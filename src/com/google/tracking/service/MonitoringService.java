package com.google.tracking.service;

import android.app.Service;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.google.tracking.sender.DataSender;
import com.google.tracking.sender.Settings;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class MonitoringService extends Service {

    private static final String TAG = "MonitoringService";
    private final IBinder serviceBinder = new MyBinder();
    private AndroidHttpClient client;
    private HttpGet getRequest;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");

        client = AndroidHttpClient.newInstance("Android");
        getRequest = new HttpGet(Settings.URL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand, intent:" + intent.toString() + ", flags : " + flags + ", startId : " + startId);

send();

        return START_STICKY;
    }

    public class MyBinder extends Binder {
        MonitoringService getService() {
            return MonitoringService.this;
        }
    }
}