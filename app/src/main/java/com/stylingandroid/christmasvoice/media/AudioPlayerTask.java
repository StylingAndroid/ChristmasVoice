package com.stylingandroid.christmasvoice.media;

import android.media.AudioTrack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

class AudioPlayerTask implements Runnable {
    private static final int BUFFER_SIZE = 1024;

    private final AudioTrack audioTrack;
    private final File inputFile;

    AudioPlayerTask(AudioTrack audioTrack, File inputFile) {
        this.audioTrack = audioTrack;
        this.inputFile = inputFile;
    }

    @Override
    public void run() {
        InputStream inputStream = getInputStream();
        if (inputStream == null) {
            return;
        }
        byte[] buffer = new byte[BUFFER_SIZE];
        int read = -1;
        int total = 0;
        int size = (int) inputFile.length();
        while (total < size) {
            try {
                read = inputStream.read(buffer, 0, BUFFER_SIZE);
            } catch (IOException e) {
                Timber.e(e, "Error reading audio file");
            }
            audioTrack.write(buffer, 0, read, AudioTrack.WRITE_BLOCKING);
            total += read;
            //Timber.d("Write");
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            Timber.e(e, "Error closing audio file");
        }
        int totalFrames = audioTrack.getBufferSizeInFrames();
        audioTrack.setNotificationMarkerPosition(totalFrames);
        Timber.d("Complete");
    }

    private InputStream getInputStream() {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(inputFile);
        } catch (FileNotFoundException e) {
            Timber.e(e, "Error opening audio file for reading");
            return null;
        }
        return inputStream;
    }
}
