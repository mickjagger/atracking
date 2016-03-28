package com.google.tracking.runnable;


import android.os.Handler;
import android.util.Log;

public class RepeatingTask {
    private static const String TAG = "RepeatingTask";
    private static int runTaskInterval;
    private Handler mHandler;

    public RepeatingTask(int interval) {

        // your code here
        runTaskInterval = interval;
        mHandler = new Handler();
        startRepeatingTask();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d(TAG, "run()");
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
