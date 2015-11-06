package com.linuxjet.lib.isy.entity;

import java.util.Date;

/**
 * Created by jamespet on 10/15/15.
 */
public class ISYEvent {

  public String Control;
  public String Action;
  public com.linuxjet.lib.isy.entity.base.Node Node;
  public String EventInfo;
  public String TimeStamp;
  public int Sequence;

  public ISYEvent() {
    Control = "";
    Action  = "";
    Node    = null;
    EventInfo = "";
    TimeStamp = new Date().toString();
    Sequence = 0;
  }

  public ISYEvent(int s, String c, String a, com.linuxjet.lib.isy.entity.base.Node n, String e) {
    Sequence = s;
    Control = c;
    Action = a;
    Node = n;
    EventInfo = e;
    TimeStamp = new Date().toString();
  }
}
