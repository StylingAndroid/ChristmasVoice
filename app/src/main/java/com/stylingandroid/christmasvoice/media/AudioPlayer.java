package com.stylingandroid.christmasvoice.media;

import android.media.AudioTrack;
import android.media.PlaybackParams;

import java.io.File;

import timber.log.Timber;

class AudioPlayer implements Player {
    private final AudioTrack audioTrack;
    private final File file;

    private float speed = 1f;

    private Thread playerThread;

    AudioPlayer(AudioTrack audioTrack, File file) {
        this.audioTrack = audioTrack;
        this.file = file;
    }

    @Override
    public boolean isPlaying() {
        return audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING;
    }

    @Override
    public void startPlaying() {
        if (isPlaying()) {
            audioTrack.stop();
        }
        PlaybackParams playbackParams = audioTrack.getPlaybackParams();
        playbackParams.setPitch(speed);
        audioTrack.setPlaybackParams(playbackParams);
        audioTrack.setPlaybackPositionUpdateListener(positionListener);
        audioTrack.play();
        AudioPlayerTask playerTask = new AudioPlayerTask(audioTrack, file);
        playerThread = new Thread(playerTask);
        playerThread.start();
    }

    @Override
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public void stopPlaying() {
        audioTrack.flush();
        audioTrack.stop();
        audioTrack.release();
        playerThread = null;
    }

    private AudioTrack.OnPlaybackPositionUpdateListener positionListener = new AudioTrack.OnPlaybackPositionUpdateListener() {
        @Override
        public void onMarkerReached(AudioTrack track) {
            track.flush();
            track.release();
            Timber.d("Playback Complete");
        }

        @Override
        public void onPeriodicNotification(AudioTrack track) {
            //NO-OP
        }
    };
}
