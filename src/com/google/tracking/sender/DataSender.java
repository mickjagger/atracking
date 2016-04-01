package com.google.tracking.sender;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import com.google.tracking.constants.TrackingConstants;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PortUnreachableException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by maxim.yanukovich on 06.01.2015.
 */
public class DataSender extends AsyncTask<File, Integer, Long> implements IDataSender {
    private static final String TAG = "DataSender";

    private static String SMS = "sms";
    private static String COMMAND = "command";
    private static String SAVE_FILE = "save_file";

//    public DataSender() {
//        Log.d(TAG, urlString().toString());
//
//        disableConnectionReuseIfNecessary();
//    }

    protected Long doInBackground(File... file) {
        Log.d(TAG, "doInBackground " + file[0].getName());
//        int count = urls.length;
        long totalSize = 0;
//        for (int i = 0; i < count; i++) {
//            totalSize += Downloader.downloadFile(urls[i]);
//            publishProgress((int) ((i / (float) count) * 100));
//            // Escape early if cancel() is called
//            if (isCancelled()) break;
//        }

        InputStream in = null;
        byte[] buffer;
        try {
            in = new BufferedInputStream(new FileInputStream(file[0]));
            totalSize = in.available();
            buffer = new byte[in.available()];
            in.read(buffer);


            sendFile(buffer, file[0].getName());
            postBinary2(buffer);
        } catch (Exception e) {
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return totalSize;
    }

    protected void onProgressUpdate(Integer... progress) {
        Log.d(TAG, "onProgressUpdate:");
    }

    protected void onPostExecute(Long result) {
        Log.d(TAG, "onPostExecute:" + result + " bytes");
    }

    public String urlString(String action) {
        return TrackingConstants.WEB_SERVICE_URL + "?action=" + action;
    }

    public void getNextAction() {
//        postHttp(COMMAND, "message");
    }

//    public void sendSMS(String msg) {
//        postHttp(SMS, msg.getBytes());
//    }

    public void sendFile(byte[] data, String name) {
        postBinary(SAVE_FILE, data, name);
        postHttp(SMS, "my test message");
    }

    private void postBinary2(byte[] data) {
        try {
            String CRLF = "\r\n";
            String boundary = Long.toHexString(System.currentTimeMillis());
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString(SAVE_FILE)).openConnection();
// set some connection properties
            OutputStream output = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);
// set some headers with writer
            InputStream file = new ByteArrayInputStream(data);
            System.out.println("Size: " + file.available());

            byte[] buffer = new byte[4096];
            int length;
            while ((length = file.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.flush();
            writer.append(CRLF).flush();
            writer.append("--" + boundary + "--").append(CRLF).flush();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
// catch and close streams
    }

    private void postBinary(String action, byte[] postData, String fileName) {

        try {
            int postDataLength = postData.length;
            Log.d(TAG, "postBinary:" + postDataLength);
            URL url = new URL(urlString(action));
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setInstanceFollowRedirects(false);
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Content-Type", "application/octet-stream");
            httpConn.setRequestProperty("Content-Disposition", "form-data; name=\"binaryFile\";filename=\"" + fileName+"\"");
            httpConn.setRequestProperty("Content-Transfer-Encoding", "binary");
            httpConn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            httpConn.setUseCaches(false);
            httpConn.connect();
            try {

                DataOutputStream os = new DataOutputStream(httpConn.getOutputStream());
                os.write(postData);
                os.writeBytes("12341234");

                Log.d(TAG, " getResponseCode:" + String.valueOf(httpConn.getResponseCode()));

//                DataInputStream is = new DataInputStream(httpConn.getInputStream());
//                Log.d(TAG, "input : " + is.readUTF());
//                os.close();
//                os.flush();
            } catch (Exception ex) {
                Log.d(TAG, "Exception : " + ex.getStackTrace());
            }

//            httpConn.disconnect();
            // Handles possible exceptions
        } catch (MalformedURLException localMalformedURLException) {
            Log.d(TAG, localMalformedURLException.getMessage());

        } catch (IOException localIOException) {
            Log.d(TAG, localIOException.getMessage());
        }


    }

    private void postHttp(String action, String msg) {
        Log.d(TAG, "postHttp");
        try {

            String urlParameters = "action=" + action + "&msg=" + msg + "&model=" + Build.MODEL;
            byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
            int postDataLength = postData.length;

            URL url = new URL(urlString(action));
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

//               DataInputStream is = new DataInputStream(httpConn.getInputStream());
//               Log.d(TAG, "input : " + is.readUTF());

            } catch (Exception ex) {
                Log.d(TAG, "Exception : " + ex.getStackTrace());
            }

            httpConn.disconnect();
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
