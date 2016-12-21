package com.stylingandroid.christmasvoice.media;

import android.media.AudioRecord;
import android.media.AudioTrack;

import java.io.File;

public class AudioRecorderPlayer implements Recorder, Player {
    private final MediaToolsProvider mediaToolsProvider;
    private final File file;

    private AudioRecorder audioRecorder = null;
    private AudioPlayer audioPlayer = null;

    private float speed = 1f;

    public AudioRecorderPlayer(MediaToolsProvider mediaToolsProvider, File file) {
        this.mediaToolsProvider = mediaToolsProvider;
        this.file = file;
    }

    @Override
    public boolean isRecording() {
        return audioRecorder != null && audioRecorder.isRecording();
    }

    @Override
    public boolean hasRecording() {
        return file.exists();
    }

    @Override
    public void startRecording() {
        AudioRecord audioRecord = mediaToolsProvider.getAudioRecord();
        audioRecorder = new AudioRecorder(audioRecord, file);
        audioRecorder.startRecording();
    }

    @Override
    public void stopRecording() {
        if (audioRecorder != null) {
            audioRecorder.stopRecording();
        }
        audioRecorder = null;
    }

    @Override
    public boolean isPlaying() {
        return audioPlayer != null && audioPlayer.isPlaying();
    }

    @Override
    public void startPlaying() {
        long fileSize = file.length();
        if (fileSize <= 0) {
            return;
        }
        if (audioPlayer != null && audioPlayer.isPlaying()) {
            audioPlayer.stopPlaying();
        }
        AudioTrack audioTrack = mediaToolsProvider.getAudioTrack(fileSize);
        audioPlayer = new AudioPlayer(audioTrack, file);
        audioPlayer.setSpeed(speed);
        audioPlayer.startPlaying();
    }

    @Override
    public void stopPlaying() {
        if (audioPlayer != null && audioPlayer.isPlaying()) {
            audioPlayer.stopPlaying();
        }
        audioPlayer = null;
    }

    @Override
    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
