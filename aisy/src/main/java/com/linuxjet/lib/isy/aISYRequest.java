package com.linuxjet.lib.isy;

import android.util.Log;

import com.linuxjet.lib.isy.listeners.TaskListener;
import com.linuxjet.lib.isy.methods.REST;

/**
 * Created by jamespet on 10/19/15.
 */
public class aISYRequest {
  REST restInterface;
  aISY aISY;
  int mTimeout = 5000;

  public aISYRequest(aISY j) {
    init(j);
  }

  public void init(aISY j) {
    aISY = j;
    restInterface = new REST(j);
  }
/*
  public String get(String reqstr,TaskListener l) {
    if (restInterface != null)
      return restInterface.doGet("/rest" + reqstr,mTimeout,l);
    return null;
  }

  public String post(String reqstr,TaskListener l) {
    if (restInterface != null)
      return restInterface.doPost("/rest" + reqstr,mTimeout, l);
    return null;
  }
*/
  public String get(String reqstr,int tmout, TaskListener l) {
    if (restInterface != null)
      return restInterface.doGet("/rest" + reqstr,tmout,l);
    return null;
  }

  public String post(String reqstr,int tmout,TaskListener l) {
    if (restInterface != null)
      return restInterface.doPost("/rest" + reqstr,tmout, l);
    return null;
  }

}
