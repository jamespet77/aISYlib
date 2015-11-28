package com.linuxjet.lib.isy.network.ssl;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class TrustAllSSLSocketFactory extends SSLSocketFactory {
   private javax.net.ssl.SSLSocketFactory factory;

   public TrustAllSSLSocketFactory() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
      super();

      try {
         SSLContext ctx = SSLContext.getInstance("TLS");
         ctx.init((KeyManager[])null, new TrustManager[]{new TrustAllManager()}, (SecureRandom)null);
         this.factory = ctx.getSocketFactory();
      } catch (Exception exp) {
         Log.d("SSLSocketFactory","failed to create SSL Context");
      }
   }

   public Socket createSocket() throws IOException {
      return this.factory.createSocket();
   }

   public Socket createSocket(String addr, int port) throws IOException {
      return this.factory.createSocket(addr, port);
   }

   public Socket createSocket(String host, int port, InetAddress localhost, int localport) throws IOException {
      return this.factory.createSocket(host, port, localhost, localport);
   }

   public Socket createSocket(InetAddress host, int port) throws IOException {
      return this.factory.createSocket(host, port);
   }

   public Socket createSocket(InetAddress host, int port, InetAddress localhost, int localport) throws IOException {
      return this.factory.createSocket(host, port, localhost, localport);
   }

   public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
      return this.factory.createSocket(socket, host, port, autoClose);
   }

   public String[] getDefaultCipherSuites() {
      return this.factory.getDefaultCipherSuites();
   }

   public String[] getSupportedCipherSuites() {
      return this.factory.getSupportedCipherSuites();
   }
}
