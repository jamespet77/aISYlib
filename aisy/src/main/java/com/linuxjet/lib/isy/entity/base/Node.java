package com.linuxjet.lib.isy.entity.base;

import java.util.Vector;

/**
 * Created by jamespet on 10/15/15.
 */
public class Node implements Comparable<Node> {

  private String Name;
  private String CustomName;
  private String Address;
  private String Type;
  private String ElkID;
  private String Flag;
  private String Folder;
  private String Primary;
  private int Family;
  private Boolean Enabled = false;
  private Vector<NodeProperty> properties;

  public Node() {
    properties = new Vector<>();
    setFamily(1);
  }

  public Node(Node node) {
    setName(node.getName());
    setAddress(node.getAddress());
    setType(node.getType());
    setElkID(node.getElkID());
    setEnabled(node.getEnabled());
    setFlag(node.getFlag());
    setFolder(node.getFolder());
    setPrimary(node.getPrimary());
    setProperties(node.getProperties());
    setFamily(node.getFamily());
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

  public void setFolder(String folder) {
    Folder = folder;
  }

  public String getFolder() {
    return Folder;
  }

  public String getName() {
    return Name;
  }

  public void setName(String name) {
    Name = name;
  }

  public String getCustomName() {
    return CustomName;
  }

  public void setCustomName(String name) {
    CustomName = name;
  }

  public String getType() {
    if (Type != null)
      return Type;
    return "";
  }

  public void setType(String type) {
    Type = type;
  }

  public int getFamily() {
    if (Family > 1)
      return Family;
    return 1;
  }

  public void setFamily(int fam) {
    Family = fam;
  }


  public String getPrimary() {
    return Primary;
  }

  public void setPrimary(String pri) {
    Primary = pri;
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

  public void addProperty(NodeProperty property) {
    properties.add(property);
  }

  public NodeProperty getProperty(String id) {
    if (properties != null && properties.size() > 0) {
      for(NodeProperty p : properties) {
        if (p != null && p.getId() != null  && p.getId().equals(id)) return p;
      }
    }
    return null;
  }

  public NodeProperty getProperty(int idx) {
    if (properties.size() > 0 && properties.size() > idx) return properties.get(idx);
    return null;
  }

  public Vector<NodeProperty> getProperties() {
    return properties;
  }

  public void setProperties(Vector<NodeProperty> nodeProperties) {
    properties = nodeProperties;
  }

  @Override
  public String toString() {
    return "Name:" + getName() + " | Addr:" + getAddress() + " | Type: " + getType() + " | Flag:" + getFlag() + " | ElkID:" + getElkID();
  }

  @Override
  public int compareTo(Node another) {
    if (getType() == null || getType().equals("")) return 1;
    if (another.getType() == null || another.getType().equals("")) return -1;
    if (getType().substring(0,getType().indexOf(".") + 1).compareTo(another.getType().substring(0,another.getType().indexOf(".") + 1)) == 0) {
      return getName().compareTo(another.getName());
    }
    return getType().compareTo(another.getType());

  }

}
