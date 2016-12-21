package com.stylingandroid.christmasvoice.media;

public interface Player {
    boolean isPlaying();

    void startPlaying();

    void stopPlaying();

    void setSpeed(float speed);
}
