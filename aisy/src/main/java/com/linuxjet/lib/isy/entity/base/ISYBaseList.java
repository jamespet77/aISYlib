package com.linuxjet.lib.isy.entity.base;

/**
 * Created by jamespet on 10/19/15.
 */
public interface ISYBaseList<T> {

  public T getByAddress(String addr);
  public T getByIndex(int idx);

}
