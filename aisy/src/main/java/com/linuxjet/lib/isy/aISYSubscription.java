package com.linuxjet.lib.isy;

import android.util.Base64;
import android.util.Log;

import com.linuxjet.lib.isy.entity.ISYEvent;
import com.linuxjet.lib.isy.listeners.ISYEventListener;
import com.linuxjet.lib.isy.network.ConnectionManager;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static com.linuxjet.lib.isy.util.XmlUtil.asList;
import static com.linuxjet.lib.isy.util.XmlUtil.prettyPrint;

/**
 * Created by jamespet on 10/21/15.
 */
public class aISYSubscription {
  private static String TAG = "aISYSubscription";

  aISY aisy;

  private InputStream reader = null;
  private OutputStreamWriter writer = null;
  private ISYEventListener listener;

  private String auth;
  private HttpURLConnection request;
  private Boolean hasSID;
  private Boolean InitialLoad = false;

  private Boolean running;
  private String SID;


  public aISYSubscription(aISY j) {
    aisy = j;
    hasSID = false;
    auth = "Basic " + Base64.encodeToString((aisy.getUserName() + ":" + aisy.getPassWord()).getBytes(), Base64.DEFAULT);
  }

  public void setListener(ISYEventListener l) {
    listener = l;
  }

  public ISYEventListener getListener() {
    return listener;
  }

  public Boolean hasListener() {
    return (listener != null && listener instanceof ISYEventListener);
  }

  public void DisConnect() {
    try {
      doUnSubscribe();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void Connect() {

    while (running) {

      //TEMP CHECK TO NOT HAVE TO RELOAD
      //if (aisy.getNodeList() == null ) {
      //  ErrorNotify("Get Nodes Config");
      //  try {
      //    GetNodesConfig();
      //  } catch (IOException e) {
      //    e.printStackTrace();
      //  }
      //}
      //setDebugging(aisy.getHostAddr(), auth);

      ErrorNotify("Subscribe Subscription.");

      try {
      //  if (aisy.getNodeList() != null) {
          doSubscribe(auth);
      //  }
      } catch (IOException e) {
        e.printStackTrace();
      }

      try {
        Thread.sleep(50000);
      } catch (InterruptedException e) {
        Log.d(TAG, "Main Thread Subscription has stopped - this is probably expected");
        //listener.onConnectionFailure();
      }
    }

  }

  private void doUnSubscribe() throws IOException {

      SSLSocket isySocketSSL;
      Socket isySocket;
    try {

      if (aisy.getSSLEnabled()) {
        isySocketSSL = ConnectionManager.getSSLSocket(aisy);
        writer = new OutputStreamWriter(isySocketSSL.getOutputStream());
        reader = isySocketSSL.getInputStream();
      } else {
        isySocket = ConnectionManager.getSocket(aisy);
        writer = new OutputStreamWriter(isySocket.getOutputStream());
        reader = isySocket.getInputStream();
      }

      String subreq = "<s:Envelope><s:Body>" + "<u:Unsubscribe";
      subreq += " xmlns:u='urn:udi-com:service:X_Insteon_Lighting_Service:1'>";
      subreq += "<SID>"+SID+"</SID>";
      subreq += "</u:Unsubscribe></s:Body></s:Envelope>";

        writer.write("POST /services HTTP/1.1\n");
        writer.write("Content-Type: text/xml; charset=utf-8\n");
        writer.write("Authorization: " + auth + "\n");
        writer.write("Content-Length: " + (subreq.length()) + "\n");
        writer.write("SOAPAction: urn:udi-com:device:X_Insteon_Lighting_Service:1#Unsubscribe\r\n");
        writer.write("\r\n");
        writer.write(subreq);
        writer.write("\r\n");
        writer.flush();
    } catch (NullPointerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    listener.onUnsubscribe();
    hasSID = false;
  }


  private void doSubscribe(String auth) throws IOException {

    SSLSocket isySocketSSL;
    Socket isySocket;

    try {
      if (aisy.getSSLEnabled()) {
        isySocketSSL = ConnectionManager.getSSLSocket(aisy);
        writer = new OutputStreamWriter(isySocketSSL.getOutputStream());
        reader = isySocketSSL.getInputStream();
      } else {
        isySocket = ConnectionManager.getSocket(aisy);
        writer = new OutputStreamWriter(isySocket.getOutputStream());
        reader = isySocket.getInputStream();
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
      return;
    }

    String subreq = "<s:Envelope><s:Body>" + "<u:Subscribe";
    subreq += " xmlns:u='urn:udi-com:service:X_Insteon_Lighting_Service:1'>";
    subreq += "<reportURL>REUSE_SOCKET</reportURL>";
    subreq += "<duration>infinite</duration>";
    subreq += "</u:Subscribe></s:Body></s:Envelope>";

    try {
      writer.write("POST /services HTTP/1.1\n");
      writer.write("Content-Type: text/xml; charset=utf-8\n");
      writer.write("Authorization: " + auth + "\n");
      writer.write("Content-Length: " + (subreq.length()) + "\n");
      writer.write("SOAPAction: urn:udi-com:device:X_Insteon_Lighting_Service:1#Subscribe\r\n");
      writer.write("\r\n");
      writer.write(subreq);
      writer.write("\r\n");
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
      setRunning(false);
      return;
    }

    int content_length = 0;
    StringBuffer headerBuffer = new StringBuffer();
    int charValue;
    do { //Do once and then check for SID - REPEAT IF SID FOUND
      try {
        while ((charValue = reader.read()) != -1) { //CONTINUE READING TILL END OF INPUT
          headerBuffer.append((char) charValue);
          if (charValue == '\n') {
            int index = headerBuffer.length();
            if (index >= 4) { //Check for end of header data
              if (headerBuffer.charAt(index - 2) == '\r' &&
                  headerBuffer.charAt(index - 3) == '\n' && headerBuffer.charAt(index - 4) == '\r') {
                content_length = getContentLength(headerBuffer.toString());

                if (content_length < 0)
                  break;
                byte messageBuffer[] = new byte[content_length];
                int num_read = 0;
                do {
                  int r = reader.read(messageBuffer, num_read, content_length - num_read);
                  if (r == -1)
                    break;
                  num_read += r;
                } while (num_read < content_length);
                //View received XML
                //Log.d(TAG, new String(ca));
                if (hasSID) {
                  ParseEvent(new ByteArrayInputStream(messageBuffer));
                } else {
                  Subscribe(new ByteArrayInputStream(messageBuffer));
                }
                break;
              }
            }
          }
        }
      } catch (SocketException e) {
        Log.d(TAG,"Network Connection Lost");
        hasSID = false;
        listener.onConnectionFailure();
      } catch (SSLException e) {
        Log.d(TAG,"Network Connection Lost");
        hasSID = false;
        listener.onConnectionFailure();
      }
      headerBuffer.setLength(0);
    } while (hasSID);
    listener.onConnectionFailure();
  }

  private void Subscribe(InputStream xml) {
    Document xmld;
    NodeList xml_list;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;

    try {
      builder = factory.newDocumentBuilder();
      xmld = builder.parse(xml);
    } catch (Exception e) {
      e.printStackTrace();
      ErrorNotify("Error in XML" + xml);
      return;
    }

    if (xmld.hasChildNodes()) {
      xml_list = xmld.getChildNodes().item(0).getChildNodes().item(0).getChildNodes().item(0).getChildNodes();

      for (org.w3c.dom.Node isy_event : asList(xml_list)) {
        if (isy_event.getNodeName().equalsIgnoreCase("SID")) {
          SID = isy_event.getTextContent();
          Log.d(TAG,"Subscribing to SID: " + SID);
          hasSID = true;
          listener.onSubscriptionConnected();
          InitialLoad = true;
        }
      }
    }


  }

  private void ParseEvent(InputStream xml) {
    Document xmld;
    NodeList xml_list;
    NamedNodeMap attributes;

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;

    String Control = "";
    String Action = "";
    String Node = null;
    String EventInfo = "";
    String FormatAct = "";
    String sid = "";
    int seqid = -1;


    try {
      builder = factory.newDocumentBuilder();
      xmld = builder.parse(xml);
    } catch (Exception e) {
      e.printStackTrace();
      ErrorNotify("Error in XML" + xml);
      return;
    }

    //Log.d(TAG,getStringFromDocument(xmld));

    if (xmld.hasChildNodes()) {
      xml_list = xmld.getChildNodes();

      for (org.w3c.dom.Node isy_event : asList(xml_list)) {
        attributes = isy_event.getAttributes();
        for (org.w3c.dom.Node attr : asList(attributes)) {
          if (attr.getNodeName().toLowerCase().equals("sid")) {
            sid = attr.getNodeValue();
            if (!sid.equals(SID)) {
              hasSID = false;
              return;
            } else {

            }
          } else if (attr.getNodeName().equals("seqnum")) {
            seqid = Integer.parseInt(attr.getNodeValue());
          } else {
            //SOMETHING
          }
        }

        for (org.w3c.dom.Node event_node : asList(isy_event.getChildNodes())) {
          if (event_node.getNodeName().equals("control")) {
            Control = event_node.getTextContent();
          } else if (event_node.getNodeName().equals("action")) {
            Action = event_node.getTextContent();
          } else if (event_node.getNodeName().equals("node")) {
            Node = event_node.getTextContent();
          } else if (event_node.getNodeName().equals("eventInfo")) {
            for (org.w3c.dom.Node info : asList(event_node.getChildNodes())) {
              if (info.getNodeName().equals("status")) {
                EventInfo = info.getTextContent();
              } else if (info.getNodeName().equals("value") || info.getNodeName().equals("unit")) {
                if (!EventInfo.equals(info.getTextContent()))
                  EventInfo += info.getTextContent();
              } else if (info.getNodeName().equals("enabled")) {
                EventInfo += info.getTextContent();
              }
            }
          } else if (event_node.getNodeName().equals("fmtAct")) {
            FormatAct = event_node.getTextContent();
          } else {
            Log.d(TAG,"Unknown Param in return: " + event_node.getNodeName() + " data" + event_node.getTextContent());
          }
        }

        switch (Control) {
          case "_0":
            if (listener != null) {
              listener.onHeartbeat(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "Heartbeat", Action, Node, EventInfo);
            }
            break;
          case "_1":
            if (listener != null) {
              listener.onTriggerEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              switch (Action) {
                case "0":
                  NotifyAll(seqid, "Trigger", "Program", Node, EventInfo);
                  break;
                case "1":
                  NotifyAll(seqid, "Trigger", "Program", Node, EventInfo);
                  break;
                case "2":
                  NotifyAll(seqid, "Trigger", "Program", Node, EventInfo);
                  break;
                case "3":
                  // Button press EventInfo -> "[address] command value"
                  NotifyAll(seqid, "Trigger", "Button Press", Node, EventInfo);
                  break;
                default:
                  break;

              }
            }
            break;
          case "_2":
            if (listener != null) {
              listener.onDriverEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "Protocol", Action, Node, EventInfo);
            }
            break;
          case "_3":
            if (listener != null) {
              listener.onNodeUpdate(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "Nodes Update", Action, Node, EventInfo);
            }
            break;
          case "_4":
            if (listener != null) {
              listener.onSystemConfigUpdate(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "System Config", Action, Node, EventInfo);
            }
            break;
          case "_5":
            if (listener != null) {
              listener.onSystemStatusUpdate(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              switch (Action) {
                case "1":
                  // busy
                  NotifyAll(seqid, "System Status", "Busy", Node, EventInfo);
                  break;
                default:
                  // idle
                  NotifyAll(seqid, "System Status", "Idle", Node, EventInfo);
                  break;
              }
            }
            break;
          case "_6":
            if (listener != null) {
              listener.onInternetStatusUpdate(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "Internet", Action, Node, EventInfo);
            }
            break;
          case "_7":
            if (listener != null) {
              listener.onProgressUpdate(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "Progress", Action, Node, EventInfo);
            }
            break;
          case "_8":
            if (listener != null) {
              listener.onSecurtyEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "Security", Action, Node, EventInfo);
            }
            break;
          case "_9":
            if (listener != null) {
              listener.onAlertEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "Alert", Action, Node, EventInfo);
            }
            break;
          case "_10":
            if (listener != null) {
              listener.onADRFLEXEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "Electricity", Action, Node, EventInfo);
            }
            break;
          case "_11":
            if (listener != null) {
              listener.onClimateEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "Climate", Action, Node, EventInfo);
            }
            break;
          case "_12":
            if (listener != null) {
              listener.onAMISEPEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "AMI/SEP", Action, Node, EventInfo);
            }
            break;
          case "_13":
            if (listener != null) {
              listener.onExternalEnergyEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "External Energy Monitor", Action, Node, EventInfo);
            }
            break;
          case "_14":
            if (listener != null) {
              listener.onUPBLinkerEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "UPB Linker", Action, Node, EventInfo);
            }
            break;
          case "_15":
            if (listener != null) {
              listener.onUPBAdderEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "UPB Adder", Action, Node, EventInfo);
            }
            break;
          case "_16":
            if (listener != null) {
              listener.onUPBStatusEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "UPB Status", Action, Node, EventInfo);
            }
            break;
          case "_17":
            if (listener != null) {
              listener.onGasMeterEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "Gas Meter", Action, Node, EventInfo);
            }
            break;
          case "_18":
            if (listener != null) {
              listener.onZigbeeEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "Zigbee", Action, Node, EventInfo);
            }
            break;
          case "_19":
            if (listener != null) {
              listener.onELKEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "ELK", Action, Node, EventInfo);
            }
            break;
          case "_20":
            if (listener != null) {
              listener.onDeviceLinkerEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "Device Linker", Action, Node, EventInfo);
            }
            break;
          case "_21":
            if (listener != null) {
              listener.onZWaveEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "Z-Wave", Action, Node, EventInfo);
            }
            break;
          case "_22":
            if (listener != null) {
              listener.onBillingEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "Billing", Action, Node, EventInfo);
            }
            break;
          case "_23":
            if (listener != null) {
              listener.onPortalEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, "Portal", Action, Node, EventInfo);
            }
            break;
          case "ST":
            if (listener != null) {
              listener.onStatusUpdate(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, Control, Action, Node, EventInfo);
            }
            break;
          default:
            if (listener != null) {
              listener.onDefault(new ISYEvent(seqid, Control, Action, Node, EventInfo, FormatAct));
            } else {
              NotifyAll(seqid, Control, Action, Node, EventInfo);
            }
            break;

        }
      }
    }
  }

  private void setDebugging(String ip_address, String auth) {
    String reqxml;

    reqxml = "<?xml version='1.0' encoding='utf-8'?>";
    reqxml += "<s:Envelope><s:Body><u:SetDebugLevel";
    reqxml += " xmlns:u='urn:udi-com:service:X_Insteon_Lighting_Service:1'>";
    reqxml += "<option>1</option>";
    reqxml += "</u:SetDebugLevel></s:Body></s:Envelope>" + ("\r\n");

    try {
      request = ConnectionManager.openConnection(aisy);
      request.setRequestMethod("POST");
      request.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
      request.setRequestProperty("Authorization", auth);
      request.setRequestProperty("SoapAction", "urn:udi-com:device:X_Insteon_Lighting_Service:1#UDIService");

      writer = new OutputStreamWriter(request.getOutputStream());
      writer.write(reqxml);
      writer.flush();
      writer.close();

      request.disconnect();


    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  protected void NotifyAll(int seq, String control, String action, String node, String text) {
    Log.d(TAG, "SEQ: " + seq + " Control: " + control + " Action: " + action + " Node: " + node + " Text: " + text);
  }

  protected void ErrorNotify(String s) {
    Log.d(TAG, "Notification: " + s);
  }

  public int getContentLength(String header) {
    int contentLength = 0;

    StringTokenizer st = new StringTokenizer(header, "\r\n");
    while (st.hasMoreTokens()) {
      String line = st.nextToken();
      if (line.length() >= 15 && line.substring(0, 15).equalsIgnoreCase("CONTENT-LENGTH:")) {
        contentLength = Integer.parseInt(line.substring(15).trim());

      }
    }
    return contentLength;
  }

  public Boolean isRunning() {
    return running;
  }

  public Boolean isConnected() {
    return isRunning() && hasSID;
  }

  public void setRunning(Boolean running) {
    if (!running) listener.onConnectionFailure();
    this.running = running;
  }

  public String getStringFromDocument(Document doc)
  {
    try
    {
      DOMSource domSource = new DOMSource(doc);
      StringWriter writer = new StringWriter();
      StreamResult result = new StreamResult(writer);
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer transformer = tf.newTransformer();
      transformer.transform(domSource, result);
      return writer.toString();
    }
    catch(TransformerException ex)
    {
      ex.printStackTrace();
      return null;
    }
  }

}
