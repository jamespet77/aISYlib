package com.linuxjet.lib.isy.entity;

import com.linuxjet.lib.isy.entity.base.Node;

import java.util.Vector;

/**
 * Created by jamespet on 10/19/15.
 */
public class ISYNodeList extends Vector<Node> {

  public ISYNodeList() {
  }

  public Node getNodeByAddress(String addr) {
    for(Node node : subList(0,elementCount)) {
      if (node.getAddress().equalsIgnoreCase(addr)) {
        return node;
      }
    }
    return null;
  }

  public Node getNodeByIndex(int idx) {
    if (idx < elementCount) return elementAt(idx);
    return null;
  }

  //public void addNode(Node node) {
  //  data.add(node);
  //}

  //public void clear() {
  //  data.clear();
  //}

  //public int size() {
  //  return data.size();
  //}
}
