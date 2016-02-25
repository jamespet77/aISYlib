package com.linuxjet.lib.isy.methods;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.linuxjet.lib.isy.aISY;
import com.linuxjet.lib.isy.listeners.TaskListener;
import com.linuxjet.lib.isy.network.ssl.TrustAllSSLSocketFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jamespet on 10/19/15.
 */
public class REST {
  private static String TAG = "REST";

  aISY aISY;

  private String auth;
  protected HttpURLConnection request;
  protected URL url;
  protected TrustAllSSLSocketFactory sslfactory;
  protected BufferedReader reader = null;

  public REST(aISY j)
  {
    aISY = j;
    auth = "Basic " + Base64.encodeToString((aISY.getUserName() + ":" + aISY.getPassWord()).getBytes(), Base64.DEFAULT);
  }

  public String doGet(String str,TaskListener l) {
    final RequestTask task = new RequestTask(str,false,l);
    if (l != null) {

      task.execute();
      return null;
    }
    try {
      return task.execute().get(5000,TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    } catch (TimeoutException e) {
      e.printStackTrace();
    }
    return null;
  }

  public String doPost(String str,TaskListener l) {
    RequestTask task = new RequestTask(str,true,l);
    if (l != null) {
      task.execute();
      return null;
    }
    try {
      return task.execute().get(5000, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    } catch (TimeoutException e) {
      e.printStackTrace();
    }
    return null;
  }

  class RequestTask extends AsyncTask<Void, Void, String> {

    // This is the reference to the associated listener
    private final TaskListener taskListener;
    private final String mCmd;
    private final Boolean isPost;

    public RequestTask(String cmd, Boolean mpost, TaskListener listener) {
      // The listener reference is passed in through the constructor
      this.taskListener = listener;
      this.mCmd = cmd;
      this.isPost = mpost;
    }

    @Override
    protected String doInBackground(Void... params) {
      StringBuilder tmp = request(mCmd,isPost);
      if (tmp != null) return tmp.toString();
      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);

      // In onPostExecute we check if the listener is valid
      if(this.taskListener != null) {
        // And if it is we call the callback function on it.
        this.taskListener.onFinished(result);
      } else {
        //Log.d(TAG,"TaskListener not implemented in RequestTask: " + result);
      }
    }
  }

  public StringBuilder request(String cmd, Boolean usePOST) {
    StringBuilder retVal = null;
    try {
      if (aISY.getSSLEnabled()) {
        url = new URL("https://" + aISY.getHostAddr() + (cmd.replace(" ","%20")));
        request = (HttpURLConnection) url.openConnection();
        request.setConnectTimeout(1000);
        request.setReadTimeout(5000);
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
        url = new URL("http://" + aISY.getHostAddr() + (cmd.replace(" ","%20")));
        request = (HttpURLConnection) url.openConnection();
        request.setConnectTimeout(1000);
        request.setReadTimeout(5000);

      }
      if (usePOST) {
        request.setRequestMethod("POST");
      } else {
        request.setRequestMethod("GET");
      }
      request.setRequestProperty("Accept", "application/xml");
      request.setRequestProperty("Authorization", auth);

      if (request.getResponseCode() == HttpURLConnection.HTTP_OK) {
          reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
          retVal = new StringBuilder();
          String tmpStr;
          while((tmpStr = reader.readLine()) != null) {
            retVal.append(tmpStr);
          }
      }
      request.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return retVal;
  }

}
