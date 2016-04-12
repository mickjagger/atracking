package com.google.tracking.sender;

import android.os.AsyncTask;

import java.io.File;
import java.net.URL;

public class UploadTask extends AsyncTask<File, Integer, Long>{

    protected Long doInBackground(File... urls) {
        int count = urls.length;
        long totalSize = 0;
//        for (int i = 0; i < count; i++) {
//            totalSize += Downloader.downloadFile(urls[i]);
//            publishProgress((int) ((i / (float) count) * 100));
//            // Escape early if cancel() is called
//            if (isCancelled()) break;
//        }
        return totalSize;
    }

    protected void onProgressUpdate(Integer... progress) {
//        Log
    }

    protected void onPostExecute(Long result) {
//        showDialog("Downloaded " + result + " bytes");
    }
}
