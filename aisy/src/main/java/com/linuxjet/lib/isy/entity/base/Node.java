package com.linuxjet.lib.isy.entity.base;

import com.linuxjet.lib.isy.entity.ISYNodeProperty;

import java.util.Vector;

/**
 * Created by jamespet on 10/15/15.
 */
public class Node implements Comparable<Node> {

  private String Name;
  private String Address;
  private String Type;
  private String ElkID;
  private String Group;
  private String Flag;
  private Boolean Enabled = false;
  private Vector<ISYNodeProperty> properties;

  public Node() {
    properties = new Vector<>();
  }

  public Node(Node node) {
    setName(node.getName());
    setAddress(node.getAddress());
    setElkID(node.getElkID());
    setEnabled(node.getEnabled());
    setFlag(node.getFlag());
    setGroup(node.getGroup());
    setType(node.getType());
    setProperties(node.getProperties());
  }

  public String getAddress() {
    return Address;
  }

  public void setAddress(String address) {
    Address = address;
  }

  public String getElkID() {
    return ElkID;
  }

  public void setElkID(String elkID) {
    ElkID = elkID;
  }

  public String getFlag() {
    return Flag;
  }

  public void setFlag(String flag) {
    Flag = flag;
  }

  public String getGroup() {
    return Group;
  }

  public void setGroup(String group) {
    Group = group;
  }

  public String getName() {
    return Name;
  }

  public void setName(String name) {
    Name = name;
  }

  public String getType() {
    return Type;
  }

  public void setType(String type) {
    Type = type;
  }

  public Boolean getEnabled() {
    return Enabled;
  }

  public void setEnabled(Boolean enabled) {
    Enabled = enabled;
  }

  public Boolean isDimmable() {
    if (getType() != null && getType().startsWith("1.")) return true;
    return false;
  }

  public void addProperty(ISYNodeProperty property) {
    properties.add(property);
  }

  public ISYNodeProperty getProperty(String id) {
    if (properties.size() > 0) {
      for(ISYNodeProperty p : properties) {
        if (p.getId().equals(id)) return p;
      }
    }
    return null;
  }

  public ISYNodeProperty getProperty(int idx) {
    if (properties.size() > 0 && properties.size() > idx) return properties.get(idx);
    return null;
  }

  public Vector<ISYNodeProperty> getProperties() {
    return properties;
  }

  public void setProperties(Vector<ISYNodeProperty> nodeProperties) {
    properties = nodeProperties;
  }


  @Override
  public String toString() {
    return "Name:" + getName() + " | Addr:" + getAddress() + " | Type: " + getType() + " | Group:" + getGroup() + " | Flag:" + getFlag() + " | ElkID:" + getElkID();
  }

  @Override
  public int compareTo(Node another) {
    if (getType() == null) return 1;
    if (another.getType() == null) return -1;
    if (getType().substring(0,getType().indexOf(".") + 1).compareTo(another.getType().substring(0,another.getType().indexOf(".") + 1)) == 0) {
      return getName().compareTo(another.getName());
    }
    return getType().compareTo(another.getType());

  }
}
