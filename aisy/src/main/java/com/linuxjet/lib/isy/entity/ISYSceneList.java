package com.linuxjet.lib.isy.entity;

import com.linuxjet.lib.isy.entity.base.ISYBaseList;
import com.linuxjet.lib.isy.entity.base.Scene;

import java.util.Vector;

/**
 * Created by jamespet on 10/19/15.
 */
public class ISYSceneList extends Vector<Scene> implements ISYBaseList<Scene> {

  public ISYSceneList() {
  }

  public Scene getByAddress(String addr) {
    for(Scene scene : subList(0,elementCount)) {
      if (scene.getAddress().equalsIgnoreCase(addr)) {
        return scene;
      }
    }
    return null;
  }

  public Scene getByIndex(int idx) {
    if (idx < elementCount) return elementAt(idx);
    return null;
  }

}
