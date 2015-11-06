package com.linuxjet.lib.isy.entity;

import com.linuxjet.lib.isy.entity.base.Node;

import java.util.Vector;

/**
 * Created by jamespet on 10/19/15.
 */
public class ISYNodeList {
  private Vector<Node> data;

  public ISYNodeList() {
    data = new Vector<>();
  }

  public Node getNodeByAddress(String addr) {
    for(Node node : data) {
      if (node.getAddress().equalsIgnoreCase(addr));
    }
    return null;
  }

  public Node getNodeByIndex(int idx) {
    if (idx < data.size()) return data.get(idx);
    return null;
  }

  public void addNode(Node node) {
    data.add(node);
  }

  public void clear() {
    data.clear();
  }

  public int size() {
    return data.size();
  }
}
