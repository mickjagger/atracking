package com.google.tracking.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CallReceiver extends BroadcastReceiver {
    Bundle bundle;
    String state;
    String inCall, outCall;
    MediaRecorder recorder;
    File audiofile;
    public boolean wasRinging = false;
    private boolean recordstarted;

    private static final String ACTION_IN = "android.intent.action.PHONE_STATE";
    private static final String ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
        // Extract phone number reformatted by previous receivers
        String phoneNumber = getResultData();
        if (phoneNumber == null) {
            // No reformatted number, use the original
            phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        }

//        if (intent.getAction().equals(ACTION_IN))
//        {
//            if ((bundle = intent.getExtras()) != null) {
//                state = bundle.getString(TelephonyManager.EXTRA_STATE);
//                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
//                    inCall = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
//                    wasRinging = true;
//                    Toast.makeText(context, "IN : " + inCall, Toast.LENGTH_LONG).show();
//                } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
//                    if (wasRinging == true) {

//                        Toast.makeText(context, "ANSWERED", Toast.LENGTH_LONG).show();

                        String out = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(new Date());
                        File sampleDir = new File(Environment.getExternalStorageDirectory(), "/Recordings");
                        if (!sampleDir.exists()) {
                            sampleDir.mkdirs();
                        }
                        String file_name = "Record";
                        try
                        {
                            audiofile = File.createTempFile(file_name, ".amr", sampleDir);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

                        recorder = new MediaRecorder();
                          recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
//                        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        String filename = Environment.getExternalStorageDirectory().getAbsolutePath();
                        filename += "/audiorecordtest.3gp";
                        recorder.setOutputFile(filename);
                        try {
                            recorder.prepare();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        recorder.start();
                        recordstarted = true;
//                    }
//                } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
//                    wasRinging = false;
//                    Toast.makeText(context, "REJECT || DISCO", Toast.LENGTH_LONG).show();
//                    if (recordstarted) {
//                        recorder.stop();
//                        recordstarted = false;
//                    }
//                }
//            }
//        } else if (intent.getAction().equals(ACTION_OUT)) {
//            if ((bundle = intent.getExtras()) != null) {
//                outCall = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//                Toast.makeText(context, "OUT : " + outCall, Toast.LENGTH_LONG).show();
//            }
//        }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}