package com.linuxjet.lib.isy.entity;

import com.linuxjet.lib.isy.entity.base.ISYBaseList;
import com.linuxjet.lib.isy.entity.base.Node;

import java.util.Vector;

/**
 * Created by jamespet on 10/19/15.
 */
public class ISYNodeList extends Vector<Node> implements ISYBaseList<Node>  {

  public ISYNodeList() {
  }

  public Node getByAddress(String addr) {
    for(Node node : subList(0,elementCount)) {
      if (node.getAddress().equalsIgnoreCase(addr)) {
        return node;
      }
    }
    return null;
  }

  public Node getByIndex(int idx) {
    if (idx < elementCount) return elementAt(idx);
    return null;
  }

}
