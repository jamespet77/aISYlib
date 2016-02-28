package com.linuxjet.lib.isy.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jamespet on 10/15/15.
 */
public class ISYEvent {

  public String Control;
  public String Action;
  public String Node;
  public Map<String,String> EventInfo;
  public String FormatAct;
  public String TimeStamp;
  public int Sequence;

  public ISYEvent() {
    Control = "";
    Action  = "";
    Node    = "";
    EventInfo = new HashMap<>();
    FormatAct = "";
    TimeStamp = new Date().toString();
    Sequence = 0;
  }

  public ISYEvent(int s, String c, String a, String n, Map<String,String> e, String f) {
    Sequence = s;
    Control = c;
    Action = a;
    Node = n;
    EventInfo = e;
    FormatAct = f;
    TimeStamp = new Date().toString();
  }

  @Override
  public String toString() {
    String out = "SEQ: " + Sequence + " Control: " + Control + " Action: " + Action + " Node: " + Node + " Format: " + FormatAct + " EventInfo: ";
    for (Map.Entry<String, String> entry :  EventInfo.entrySet()) {
      out += entry.getKey() + ": " + entry.getValue() + " ";
    }
    return out;
  }
}
