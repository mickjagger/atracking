package com.google.tracking.call;

import android.app.Application;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecorderController {
    private static String log_tag = "CallReceiver";
    //settings
    private String save_folder;
    private String date_format;

    private MediaRecorder recorder;
    private boolean recordstarted = false;
    File audiofile;

    public RecorderController()
    {
        save_folder = "/DCIM/fls";
        date_format = "yy.MM.dd_HH.mm.ss";
    }

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

        File sampleDir = new File(Environment.getExternalStorageDirectory(), save_folder);
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }

        phoneNumber = phoneNumber.replace("+375", "");
        String time_stamp = new SimpleDateFormat(date_format).format(new Date());
        String file_name = time_stamp + "_n_";
        file_name += (phoneNumber != null) ? (phoneNumber + "_") : "unknown_phone_number_";


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
