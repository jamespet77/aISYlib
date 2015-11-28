package com.linuxjet.lib.isy;

import android.util.Log;

import com.linuxjet.lib.isy.entity.ISYNodeList;
import com.linuxjet.lib.isy.listeners.TaskListener;
import com.linuxjet.lib.isy.methods.REST;

/**
 * Created by jamespet on 10/19/15.
 */
public class aISYRequest {
  REST restInterface;
  aISY aISY;

  public aISYRequest(aISY j) {
    init(j);
  }

  public void init(aISY j) {
    aISY = j;
    restInterface = new REST(j);
  }

  public String get(String reqstr,TaskListener l) {
    if (restInterface != null)
      return restInterface.doGet("/rest" + reqstr,l);
    return null;
  }

  public String post(String reqstr,TaskListener l) {
    if (restInterface != null)
      return restInterface.doPost("/rest" + reqstr, l);
    return null;
  }

}
