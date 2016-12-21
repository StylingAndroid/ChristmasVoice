package com.stylingandroid.christmasvoice.media;

public interface Recorder {
    boolean isRecording();

    boolean hasRecording();

    void stopRecording();

    void startRecording();
}
