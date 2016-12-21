package com.stylingandroid.christmasvoice.media;

import android.media.AudioRecord;

import java.io.File;

class AudioRecorder implements Recorder {
    private final AudioRecord audioRecord;
    private final File file;

    private Thread recorderThread;
    private AudioRecorderTask recorderTask;

    AudioRecorder(AudioRecord audioRecord, File file) {
        this.audioRecord = audioRecord;
        this.file = file;
    }

    @Override
    public boolean isRecording() {
        return recorderThread != null && recorderThread.isAlive();
    }

    @Override
    public boolean hasRecording() {
        return file.exists() && file.length() > 0;
    }

    @Override
    public void startRecording() {
        recorderTask = new AudioRecorderTask(audioRecord, file);
        recorderThread = new Thread(recorderTask);
        recorderThread.start();
        audioRecord.startRecording();
    }

    @Override
    public void stopRecording() {
        audioRecord.stop();
        audioRecord.release();
        recorderThread = null;
        recorderTask = null;
    }
}
