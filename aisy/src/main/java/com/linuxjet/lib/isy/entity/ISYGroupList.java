package com.linuxjet.lib.isy.entity;

import com.linuxjet.lib.isy.entity.base.Group;
import com.linuxjet.lib.isy.entity.base.ISYBaseList;

import java.util.Vector;

/**
 * Created by jamespet on 10/19/15.
 */
public class ISYGroupList extends Vector<Group> implements ISYBaseList<Group> {

  public ISYGroupList() {
  }

  public Group getByAddress(String addr) {
    for(Group group : subList(0,elementCount)) {
      if (group.getAddress().equalsIgnoreCase(addr)) {
        return group;
      }
    }
    return null;
  }

  public Group getByIndex(int idx) {
    if (idx < elementCount) return elementAt(idx);
    return null;
  }

}
