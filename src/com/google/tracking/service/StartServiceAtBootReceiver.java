package com.google.tracking.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.google.tracking.TService;

public class StartServiceAtBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, TService.class);
        context.startService(startServiceIntent);
    }
}
