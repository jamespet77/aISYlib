package com.linuxjet.lib.isy.network;

import android.util.Log;

import com.linuxjet.lib.isy.aISY;
import com.linuxjet.lib.isy.network.ssl.TrustAllSSLSocketFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocket;

/**
 * Created by jamespet on 10/23/15.
 */
public class ConnectionManager {

  private static TrustAllSSLSocketFactory sslfactory;

  public static HttpURLConnection openConnection(aISY aISY) {
    URL url;
    HttpURLConnection request = null;
    try {
      if (aISY.getSSLEnabled()) {
        url = new URL("https://" + aISY.getHostAddr() + "/services");
        request = (HttpURLConnection) url.openConnection();
        try {
          sslfactory = new TrustAllSSLSocketFactory();
        } catch (KeyManagementException e) {
          e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
          e.printStackTrace();
        } catch (KeyStoreException e) {
          e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
          e.printStackTrace();
        }
        ((HttpsURLConnection) request).setSSLSocketFactory(sslfactory);
      } else {
        url = new URL("http://" + aISY.getHostAddr() + "/services");
        request = (HttpURLConnection) url.openConnection();
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return request;
  }

  public static SSLSocket getSSLSocket(aISY aISY) {
    SSLSocket isySocketSSL = null;

      try {
        sslfactory = new TrustAllSSLSocketFactory();
      } catch (KeyManagementException e) {
        e.printStackTrace();
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      } catch (KeyStoreException e) {
        e.printStackTrace();
      } catch (UnrecoverableKeyException e) {
        e.printStackTrace();
      }
      try {
        if (aISY.getHostAddr().contains(":")) {
          String[] ip = aISY.getHostAddr().split(":");
          isySocketSSL = (SSLSocket) sslfactory.createSocket(ip[0], Integer.parseInt(ip[1]));
        } else {
          isySocketSSL = (SSLSocket) sslfactory.createSocket(aISY.getHostAddr(), 80);
        }
        isySocketSSL.setSoTimeout(120000);

        isySocketSSL.startHandshake();
      } catch (IOException e) {
        e.printStackTrace();
      }

    return isySocketSSL;
  }

  public static Socket getSocket(aISY aISY) {
    Socket isySocket;
      try {
        if (aISY.getHostAddr().contains(":")) {
          String[] ip = aISY.getHostAddr().split(":");
          isySocket = new Socket(ip[0], Integer.parseInt(ip[1]));
        } else {
          isySocket = new Socket(aISY.getHostAddr(), 80);
        }
        isySocket.setSoTimeout(120000);

      } catch (IOException e) {
        e.printStackTrace();
        return null;
      }

    return isySocket;
  }

  public static void closeSocket(Socket sock) {
    try {
      if (sock != null && sock.isConnected()) sock.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
