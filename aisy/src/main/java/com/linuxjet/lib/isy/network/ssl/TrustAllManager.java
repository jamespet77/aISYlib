package com.linuxjet.lib.isy.network.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class TrustAllManager implements X509TrustManager {
   public void checkClientTrusted(X509Certificate[] var1, String var2) throws CertificateException {
   }

   public void checkServerTrusted(X509Certificate[] var1, String var2) throws CertificateException {
   }

   public X509Certificate[] getAcceptedIssuers() {
      return null;
   }

}
