package com.linuxjet.lib.isy;

import android.util.Base64;

import com.linuxjet.lib.isy.listeners.ISYEventListener;
import com.linuxjet.lib.isy.network.ssl.NullHostNameVerifier;
import com.linuxjet.lib.isy.entity.ISYNodeList;

import javax.net.ssl.HttpsURLConnection;

import static com.linuxjet.lib.isy.util.XmlUtil.asList;

/**
 * Created by jamespet on 10/15/15.
 */
public class aISY {
  private static String TAG = "aISY";
  private static Thread sub_thread;


  private aISYRequest aISYRequest;
  private aISYSubscription aISYSubscription;
  private ISYNodeList isy_node_list = null;


  private String hostAddr;
  private String userName;
  private String passWord;
  private String auth;

  private Boolean SSLEnabled;

  public aISY(String host, String user, String pass) {
    this(host,user,pass,false);
  }

  public aISY(String host, String user, String pass, Boolean ssl) {
    hostAddr = host;
    userName = user;
    passWord = pass;
    SSLEnabled = ssl;
    auth = "Basic " + Base64.encodeToString((userName + ":" + passWord).getBytes(), Base64.DEFAULT);
    HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
    aISYRequest = new aISYRequest(this);
  }

  public aISY(String host, String user, String pass, Boolean ssl, ISYEventListener l) {
    this(host, user, pass, ssl);
  }

  public void Subscribe() {
    aISYSubscription = new aISYSubscription(this);
    if (aISYSubscription.running == null || !aISYSubscription.running) {
      aISYSubscription.running = true;

      sub_thread = new Thread(new RunConnect());
      sub_thread.start();
    }

  }

  public void Stop() {
    aISYSubscription.running = false;
    sub_thread.interrupt();
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
  public ISYNodeList getNodeList() {
    return isy_node_list;
  }
  public void setNodeList(ISYNodeList nl) {
    isy_node_list = nl;
  }

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

}
