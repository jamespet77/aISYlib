package com.linuxjet.lib.isy;

import android.util.Base64;
import android.util.Log;

import com.linuxjet.lib.isy.entity.ISYEvent;
import com.linuxjet.lib.isy.listeners.ISYEventListener;
import com.linuxjet.lib.isy.network.ConnectionManager;
import com.linuxjet.lib.isy.entity.base.Node;
import com.linuxjet.lib.isy.entity.ISYNodeList;
import com.linuxjet.lib.isy.entity.ISYNodeProperty;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.net.ssl.SSLSocket;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.linuxjet.lib.isy.util.XmlUtil.asList;

/**
 * Created by jamespet on 10/21/15.
 */
public class aISYSubscription {
  private static String TAG = "aISYSubscription";

  aISY aISY;

  private InputStream reader = null;
  private OutputStreamWriter writer = null;
  private ISYEventListener listener;

  private String auth;
  private HttpURLConnection request;
  public Boolean hasSID;
  public Boolean running;
  public String SID;


  public aISYSubscription(aISY j) {
    aISY = j;
    hasSID = false;
    auth = "Basic " + Base64.encodeToString((aISY.getUserName() + ":" + aISY.getPassWord()).getBytes(), Base64.DEFAULT);
  }


  public void setListener(ISYEventListener l) {
    listener = l;
  }

  public Boolean hasListener() {
    return (listener != null && listener instanceof ISYEventListener);
  }


  public void Connect() {

    while (running) {

      //TEMP CHECK TO NOT HAVE TO RELOAD
      if (aISY.getNodeList() == null ) {
        ErrorNotify("Get Nodes Config");
        try {
          GetNodesConfig();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      setDebugging(aISY.getHostAddr(), auth);

      ErrorNotify("Subscribe Subscription.");

      try {
        if (aISY.getNodeList() != null) {
          doSubscribe(auth);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

      try {
        Thread.sleep(50000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }


  private void doSubscribe(String auth) throws IOException {

    SSLSocket isySocketSSL = null;
    Socket isySocket = null;

    if (aISY.getSSLEnabled()) {
      isySocketSSL = ConnectionManager.getSSLSocket(aISY);
      writer = new OutputStreamWriter(isySocketSSL.getOutputStream());
      reader = isySocketSSL.getInputStream();
    } else {
      isySocket = ConnectionManager.getSocket(aISY);
      writer = new OutputStreamWriter(isySocket.getOutputStream());
      reader = isySocket.getInputStream();
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
      return;
    }

    int content_length = 0;
    StringBuffer headerBuffer = new StringBuffer();
    int charValue;
    do { //Do once and then check for SID - REPEAT IF SID FOUND
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
      headerBuffer.setLength(0);
    } while (hasSID);
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
    Node Node = null;
    String EventInfo = "";
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

    if (xmld.hasChildNodes()) {
      xml_list = xmld.getChildNodes();

      for (org.w3c.dom.Node isy_event : asList(xml_list)) {

        attributes = isy_event.getAttributes();
        for (org.w3c.dom.Node attr : asList(attributes)) {
          if (attr.getNodeName().toLowerCase().equals("sid")) {
            sid = attr.getNodeValue();
            if (!sid.equals(SID)) hasSID = false;
          } else if (attr.getNodeName().equals("seqnum")) {
            seqid = Integer.parseInt(attr.getNodeValue());
          } else {
            //SOMETHING
          }
        }

        for (org.w3c.dom.Node evt_info : asList(isy_event.getChildNodes())) {
          if (evt_info.getNodeName().equals("control")) {
            Control = evt_info.getTextContent();
          } else if (evt_info.getNodeName().equals("action")) {
            Action = evt_info.getTextContent();
          } else if (evt_info.getNodeName().equals("node")) {
            Node = aISY.getNodeList().getNodeByAddress(evt_info.getTextContent());
          } else if (evt_info.getNodeName().equals("eventInfo")) {
            EventInfo = evt_info.getTextContent();
          } else {
            //Unknown data
          }
        }

        switch (Control) {
          case "_0":
            if (listener != null) {
              listener.onHeartbeat(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "Heartbeat", Action, Node, EventInfo);
            }
            break;
          case "_1":
            if (listener != null) {
              listener.onTriggerEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo));
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
              listener.onDriverEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "Protocol", Action, Node, EventInfo);
            }
            break;
          case "_3":
            if (listener != null) {
              listener.onNodeUpdate(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "Nodes Update", Action, Node, EventInfo);
            }
            break;
          case "_4":
            if (listener != null) {
              listener.onSystemConfigUpdate(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "System Config", Action, Node, EventInfo);
            }
            break;
          case "_5":
            if (listener != null) {
              listener.onSystemStatusUpdate(new ISYEvent(seqid, Control, Action, Node, EventInfo));
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
              listener.onInternetStatusUpdate(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "Internet", Action, Node, EventInfo);
            }
            break;
          case "_7":
            if (listener != null) {
              listener.onProgressUpdate(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "Progress", Action, Node, EventInfo);
            }
            break;
          case "_8":
            if (listener != null) {
              listener.onSecurtyEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "Security", Action, Node, EventInfo);
            }
            break;
          case "_9":
            if (listener != null) {
              listener.onAlertEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "Alert", Action, Node, EventInfo);
            }
            break;
          case "_10":
            if (listener != null) {
              listener.onADRFLEXEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "Electricity", Action, Node, EventInfo);
            }
            break;
          case "_11":
            if (listener != null) {
              listener.onClimateEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "Climate", Action, Node, EventInfo);
            }
            break;
          case "_12":
            if (listener != null) {
              listener.onAMISEPEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "AMI/SEP", Action, Node, EventInfo);
            }
            break;
          case "_13":
            if (listener != null) {
              listener.onExternalEnergyEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "External Energy Monitor", Action, Node, EventInfo);
            }
            break;
          case "_14":
            if (listener != null) {
              listener.onUPBLinkerEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "UPB Linker", Action, Node, EventInfo);
            }
            break;
          case "_15":
            if (listener != null) {
              listener.onUPBAdderEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "UPB Adder", Action, Node, EventInfo);
            }
            break;
          case "_16":
            if (listener != null) {
              listener.onUPBStatusEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "UPB Status", Action, Node, EventInfo);
            }
            break;
          case "_17":
            if (listener != null) {
              listener.onGasMeterEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "Gas Meter", Action, Node, EventInfo);
            }
            break;
          case "_18":
            if (listener != null) {
              listener.onZigbeeEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "Zigbee", Action, Node, EventInfo);
            }
            break;
          case "_19":
            if (listener != null) {
              listener.onELKEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "ELK", Action, Node, EventInfo);
            }
            break;
          case "_20":
            if (listener != null) {
              listener.onDeviceLinkerEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "Device Linker", Action, Node, EventInfo);
            }
            break;
          case "_21":
            if (listener != null) {
              listener.onZWaveEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "Z-Wave", Action, Node, EventInfo);
            }
            break;
          case "_22":
            if (listener != null) {
              listener.onBillingEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "Billing", Action, Node, EventInfo);
            }
            break;
          case "_23":
            if (listener != null) {
              listener.onPortalEvent(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "Portal", Action, Node, EventInfo);
            }
            break;
          case "ST":
            if (listener != null) {
              listener.onStatusUpdate(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, "", Action, Node, "");
            }
            break;
          default:
            if (listener != null) {
              listener.onDefault(new ISYEvent(seqid, Control, Action, Node, EventInfo));
            } else {
              NotifyAll(seqid, Control, Action, Node, EventInfo);
            }
            break;

        }
      }
    }
  }


  private void GetNodesConfig() throws IOException {

    Node new_node = null;
    String result = aISY.getRequester().get("/nodes",null);
    if (result == null) return;
    XmlPullParser parser = null;
    XmlPullParserFactory factory;
    try {
      factory = XmlPullParserFactory.newInstance();
      parser = factory.newPullParser();
    } catch (XmlPullParserException e) {
      e.printStackTrace();
    }
    if (parser == null) {
      ErrorNotify("GetNodesConfig: Failed to create parser");
      return;
    }
    try {
      parser.setInput(new StringReader(result));
    } catch (XmlPullParserException e) {
      e.printStackTrace();
    }

    try {
      int eventType = parser.getEventType();

      while (eventType != XmlPullParser.END_DOCUMENT) {
        String name = null;
        switch (eventType) {
          case XmlPullParser.START_DOCUMENT:
            aISY.setNodeList(new ISYNodeList());
            break;
          case XmlPullParser.START_TAG:
            name = parser.getName();
            if (name.equalsIgnoreCase("node") || name.equalsIgnoreCase("group")){
              new_node = new Node();
              new_node.setFlag(parser.getAttributeValue(null, "flag"));
            } else if (new_node != null) {
              if (name.equals("address")) {
                new_node.setAddress(parser.nextText());
              } else if (name.equals("name")) {
                new_node.setName(parser.nextText());
              } else if (name.equals("type")) {
                new_node.setType(parser.nextText());
              } else if (name.equalsIgnoreCase("elk_id")) {
                new_node.setElkID(parser.nextText());
              } else if (name.equalsIgnoreCase("devicegroup")) {
                new_node.setGroup(parser.nextText());
              } else if (name.equalsIgnoreCase("property")) {
                ISYNodeProperty property = new ISYNodeProperty();
                property.setId(parser.getAttributeValue(null,"id"));
                try {
                  property.setValue(Integer.parseInt(parser.getAttributeValue(null, "value")));
                } catch (NumberFormatException e) {
                  property.setValue(0);
                }
                property.setFormattedValue(parser.getAttributeValue(null,"formatted"));
                property.setUOM(parser.getAttributeValue(null,"uom"));
                new_node.addProperty(property);
              } else if (name.equalsIgnoreCase("enabled")) {
                new_node.setEnabled(Boolean.parseBoolean(parser.nextText()));
              }
            }
            break;
          case XmlPullParser.END_TAG:
            name = parser.getName();
            if (name.equalsIgnoreCase("node") || name.equalsIgnoreCase("group")) {
              if (new_node != null) {
                aISY.getNodeList().addNode(new_node);
              }
              new_node = null;
            }
            break;

        }
        eventType = parser.nextToken();

      }
    } catch (XmlPullParserException e) {
      e.printStackTrace();
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
      request = ConnectionManager.openConnection(aISY);
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


  protected void NotifyAll(int seq, String control, String action, Node node, String text) {
    Log.d(TAG, "SEQ: " + seq + " Control: " + control + " Action: " + action + " Node: " + (node != null ? node.getName() : "") + " Text: " + text);
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

}
