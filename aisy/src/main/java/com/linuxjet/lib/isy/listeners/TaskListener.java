package com.linuxjet.lib.isy.listeners;

/**
 * Created by jamespet on 10/21/15.
 */
public interface TaskListener {
  void onFinished(String result);
  void onFinished(Boolean result);

}