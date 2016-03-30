/*2016 - jamespet */

package com.linuxjet.lib.isy.entity.base;

public class Variable {

  private int ID;
  private String Name;
  private int Type;
  private int Init;
  private int Value;
  private String LastChange;

  public static class TYPE {
    public static int INTEGER = 1;
    public static int STATE = 2;
  }

  public static String[] VariableTYPE = {
      "",
      "INTEGER",
      "STATE"
  };


  public Variable() {

  }

  public Variable(Variable var) {
    ID = var.getID();
    Name = var.getName();
    Type = var.getType();
    Init = var.getInit();
    Value = var.getValue();
    LastChange = var.getLastChange();
  }

  public int getID() {
    return ID;
  }

  public void setID(int ID) {
    this.ID = ID;
  }

  public int getInit() {
    return Init;
  }

  public void setInit(int init) {
    Init = init;
  }

  public String getLastChange() {
    return LastChange;
  }

  public void setLastChange(String lastChange) {
    LastChange = lastChange;
  }

  public String getName() {
    return Name;
  }

  public void setName(String name) {
    Name = name;
  }

  public int getType() {
    return Type;
  }

  public void setType(int type) {
    Type = type;
  }

  public int getValue() {
    return Value;
  }

  public void setValue(int value) {
    Value = value;
  }
}
