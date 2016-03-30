/*2016 - jamespet */

package com.linuxjet.lib.isy.entity;

import com.linuxjet.lib.isy.entity.base.ISYBaseList;
import com.linuxjet.lib.isy.entity.base.Program;

import java.util.Vector;

public class ISYProgramList extends Vector<Program> implements ISYBaseList<Program> {


  @Override
  public Program getByAddress(String addr) {
    for(Program program : subList(0,elementCount)) {
      if (program.getFID().equalsIgnoreCase(addr)) {
        return program;
      }
    }
    return null;
  }

  @Override
  public Program getByIndex(int idx) {
    if (idx < elementCount) return elementAt(idx);
    return null;  }
}
