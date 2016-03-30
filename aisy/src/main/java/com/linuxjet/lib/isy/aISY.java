package com.linuxjet.lib.isy;

import android.util.Base64;

import com.linuxjet.lib.isy.listeners.TaskListener;
import com.linuxjet.lib.isy.network.ConnectionManager;
import com.linuxjet.lib.isy.network.ssl.NullHostNameVerifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocket;

/**
 * Created by jamespet on 10/15/15.
 */
public class aISY {
  private static String TAG = "aisy";
  private static Thread sub_thread;


  private aISYRequest aISYRequest;
  private aISYSubscription aISYSubscription;
  //private ISYNodeList isy_node_list = null;


  private String hostAddr;
  private String userName;
  private String passWord;

  private Boolean SSLEnabled;

  public aISY(String host, String user, String pass) {
    this(host,user,pass,false);
  }

  public aISY(String host, String user, String pass, Boolean ssl) {
    hostAddr = host;
    userName = user;
    passWord = pass;
    SSLEnabled = ssl;
    HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
    aISYRequest = new aISYRequest(this);

  }

  public void Subscribe() {
    if (aISYSubscription == null) {
      aISYSubscription = new aISYSubscription(this);
    }
    if (aISYSubscription.isRunning() == null || !aISYSubscription.isRunning()) {
      aISYSubscription.setRunning(true);

      sub_thread = new Thread(new RunConnect());
      sub_thread.start();

    }

  }

  public void Stop() {
    if (aISYSubscription != null)
      aISYSubscription.setRunning(false);
    new Thread(new RunDisconnect(new TaskListener() {
      @Override
      public void onFinished(String result) {
        if (sub_thread != null)
          sub_thread.interrupt();
      }

    })).start();
  }

  private class RunDisconnect implements Runnable {

    TaskListener listener;

    public RunDisconnect(TaskListener l) {
      listener = l;
    }
    @Override
    public void run() {
      if (aISYSubscription != null)
        aISYSubscription.DisConnect();
      if (listener != null) {
        listener.onFinished("");
      }
    }
  }

  private class RunConnect implements Runnable {
    @Override
    public void run() {
      aISYSubscription.Connect();
    }
  }

  public aISYRequest getRequester() {
    return aISYRequest;
  }
  public aISYSubscription getSubscription() {
    return aISYSubscription;
  }
  //public ISYNodeList getNodeList() {
  //  return isy_node_list;
  //}
  //public void setNodeList(ISYNodeList nl) {
  //  isy_node_list = nl;
  //}

  public String getUserName() {
    return userName;
  }

  public String getHostAddr() {
    return hostAddr;
  }

  public String getPassWord() {
    return passWord;
  }

  public Boolean getSSLEnabled() {
    return SSLEnabled;
  }

  public void useSSL(Boolean ssl) {
    SSLEnabled = ssl;
  }

  public void abandon(String sID) throws IOException {

    String auth = "Basic " + Base64.encodeToString((getUserName() + ":" + getPassWord()).getBytes(), Base64.DEFAULT);

    SSLSocket isySocketSSL;
    Socket isySocket;
    try {

      InputStream reader;
      OutputStreamWriter writer;
      if (getSSLEnabled()) {
        isySocketSSL = ConnectionManager.getSSLSocket(this);
        writer = new OutputStreamWriter(isySocketSSL.getOutputStream());
        reader = isySocketSSL.getInputStream();
      } else {
        isySocket = ConnectionManager.getSocket(this);
        writer = new OutputStreamWriter(isySocket.getOutputStream());
        reader = isySocket.getInputStream();
      }

      String subreq = "<s:Envelope><s:Body>" + "<u:Unsubscribe";
      subreq += " xmlns:u='urn:udi-com:service:X_Insteon_Lighting_Service:1'>";
      subreq += "<SID>"+sID+"</SID>";
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
  }


}
