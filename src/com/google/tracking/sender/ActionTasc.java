package com.google.tracking.sender;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ActionTasc extends AsyncTask<String, Progress, Result> {
    private static final String TAG = "ActionTasc";
    private AndroidHttpClient client;
    private HttpGet getRequest;

    @Override
    // Actual download method, run in the task thread
    protected String doInBackground(String... params) {
        // params comes from the execute() call: params[0] is the url.
        return nextAction(params[0]);
    }

    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(String bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    Java Concurrency
    in Practice

    private void nextAction(String aciton) {
        try {
            HttpResponse response = client.execute(getRequest);

            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w(TAG, "Error " + statusCode + " while retrieving bitmap from " + Settings.URL);
                return;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {

                InputStream inputStream = null;

                try {
                    inputStream = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder out = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        out.append(line);
                    }
                    Log.i(TAG, "got answer " + out);

                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or IllegalStateException
            getRequest.abort();
            Log.w("ImageDownloader", "Error while retrieving bitmap from " + Settings.URL + " " + e.toString());
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return;
    }
}
