/*2016 - jamespet */

package com.linuxjet.lib.isy.entity;

import com.linuxjet.lib.isy.entity.base.ISYBaseList;
import com.linuxjet.lib.isy.entity.base.Variable;

import java.util.Vector;

public class ISYVariableList extends Vector<Variable> implements ISYBaseList<Variable> {


  @Override
  public Variable getByAddress(String addr) {
    for(Variable variable : subList(0,elementCount)) {
      if (variable.getName().equalsIgnoreCase(addr)) {
        return variable;
      }
    }
    return null;
  }

  @Override
  public Variable getByIndex(int idx) {
    for(Variable variable : subList(0,elementCount)) {
      if (variable.getID() == idx) {
        return variable;
      }
    }
    return null;  }
}
