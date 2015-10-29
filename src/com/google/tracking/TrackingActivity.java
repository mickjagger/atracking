package com.google.tracking;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.tracking.service.MonitoringService;


public class TrackingActivity extends Activity {
    Button btnSendSMS;
    EditText txtPhoneNo;
    EditText txtMessage;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TO TEST SMS
        initSendSMS();



        Intent intent = new Intent(this, MonitoringService.class);
        startService(intent);
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

//    private ServiceConnection mConnection = new ServiceConnection() {
//        private MonitoringService s;
//
//        public void onServiceConnected(ComponentName className,
//                                       IBinder binder) {
//            MonitoringService.MyBinder b = (MonitoringService.MyBinder) binder;
//            s = b.getService();
//            Toast.makeText(TrackingActivity.this, "Connected", Toast.LENGTH_SHORT)
//                    .show();
//        }
//
//        public void onServiceDisconnected(ComponentName className) {
//            s = null;
//        }
//    };

}
