package com.linuxjet.lib.isy;

import com.linuxjet.lib.isy.listeners.TaskListener;
import com.linuxjet.lib.isy.network.ssl.NullHostNameVerifier;

import javax.net.ssl.HttpsURLConnection;

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
    aISYSubscription.setRunning(false);
    new Thread(new RunDisconnect(new TaskListener() {
      @Override
      public void onFinished(String result) {
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

}
