package com.stylingandroid.christmasvoice.media;

import android.media.AudioRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import timber.log.Timber;

class AudioRecorderTask implements Runnable {
    private static final int BUFFER_SIZE = 1024;

    private final AudioRecord audioRecord;
    private final File outputFile;

    AudioRecorderTask(AudioRecord audioRecord, File outputFile) {
        this.audioRecord = audioRecord;
        this.outputFile = outputFile;
    }

    @Override
    public void run() {
        OutputStream outputStream = getOutputStream();
        if (outputStream == null) {
            return;
        }
        byte[] buffer = new byte[BUFFER_SIZE];
        int read = audioRecord.read(buffer, 0, BUFFER_SIZE);
        while (read > 0) {
            try {
                outputStream.write(buffer, 0, read);
            } catch (IOException e) {
                Timber.e(e, "Error writing audio file");
            }
            read = audioRecord.read(buffer, 0, BUFFER_SIZE);
            //Timber.d("Read");
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            Timber.e(e, "Error closing audio file");
        }
    }

    private OutputStream getOutputStream() {
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            Timber.e(e, "Error opening audio file for writing");
            return null;
        }
        return outputStream;
    }
}
