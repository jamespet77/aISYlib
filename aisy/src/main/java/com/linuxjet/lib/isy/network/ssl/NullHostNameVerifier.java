package com.linuxjet.lib.isy.network.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by jamespet on 10/16/15.
 */
public class NullHostNameVerifier implements HostnameVerifier {

  @Override
  public boolean verify(String hostname, SSLSession session) {
    //Log.i("RestUtilImpl", "Approving certificate for " + hostname);
    return true;
  }

}
