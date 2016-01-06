package com.linuxjet.lib.isy.entity.base;

import java.util.Vector;

/**
 * Created by jamespet on 10/15/15.
 */
public class Scene implements Comparable<Scene> {

  private String Name;
  private String CustomName;
  private String Address;
  private String ElkID;
  private String Group;
  private String Flag;
  private String Folder;
  private Boolean Enabled = false;
  private Vector<String> members;

  public Scene() {
    members = new Vector<>();
  }

  public Scene(Scene scene) {
    setName(scene.getName());
    setAddress(scene.getAddress());
    setElkID(scene.getElkID());
    setEnabled(scene.getEnabled());
    setFlag(scene.getFlag());
    setGroup(scene.getGroup());
    setMembers(scene.getMembers());
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

  public String getCustomName() {
    return CustomName;
  }

  public void setCustomName(String name) {
    CustomName = name;
  }

  public Boolean getEnabled() {
    return Enabled;
  }

  public void setEnabled(Boolean enabled) {
    Enabled = enabled;
  }

  public void addMember(String memb) {
    members.add(memb);
  }

  public Vector<String> getMembers() {
    return members;
  }

  public void setMembers(Vector<String> mbrs) {
    members = mbrs;
  }

  @Override
  public String toString() {
    return "Name:" + getName() + " | Addr:" + getAddress() + " | Scene:" + getGroup() + " | Flag:" + getFlag() + " | ElkID:" + getElkID();
  }

  @Override
  public int compareTo(Scene another) {
    if (getName() == null || getName().equals("")) return 1;
    if (another.getName() == null || another.getName().equals("")) return -1;
    if (getName().substring(0,getName().indexOf(".") + 1).compareTo(another.getName().substring(0,another.getName().indexOf(".") + 1)) == 0) {
      return getName().compareTo(another.getName());
    }
    return getName().compareTo(another.getName());

  }
}
