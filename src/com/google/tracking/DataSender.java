package com.google.tracking;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by maxim.yanukovich on 06.01.2015.
 */
public class DataSender {
    // Convert the incoming data string to a URL.
    public DataSender(String msg, Context context) {
        String urlString = "http://192.168.9.71:80/SVN/receiver.php";
        Log.d(this.toString(), urlString.toString());
//        String urlString = "http://google.com/";
        try {

//            String urlParameters  = "msg=" + msg + "&param2=b&param3=c";
//            byte[] postData       = urlParameters.getBytes( Charset.forName("UTF-8"));
//            int    postDataLength = postData.length;
            URL    url            = new URL( urlString );

            URLConnection urlConnection = url.openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write("msg=" + URLEncoder.encode(msg));
            out.flush();
            out.close();


            BufferedReader in = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
            }
            in.close();

            // Handles possible exceptions
        } catch (MalformedURLException localMalformedURLException) {
            Toast.makeText(context, localMalformedURLException.getMessage(), Toast.LENGTH_SHORT).show();
            localMalformedURLException.printStackTrace();

        } catch (IOException localIOException) {
            Toast.makeText(context, localIOException.getMessage(), Toast.LENGTH_SHORT).show();
            localIOException.printStackTrace();
        }
    }
}
