package com.stylingandroid.christmasvoice.dagger;

import android.content.Context;

import com.stylingandroid.christmasvoice.ChristmasVoiceApplication;
import com.stylingandroid.christmasvoice.media.AudioRecorderPlayer;
import com.stylingandroid.christmasvoice.media.MediaToolsProvider;
import com.stylingandroid.christmasvoice.media.Player;
import com.stylingandroid.christmasvoice.media.Recorder;

import javax.inject.Singleton;
import java.io.File;

import dagger.Module;
import dagger.Provides;

@Module
public class MainModule {
    private static final int DURATION_IN_SECONDS = 5;
    private static final int DURATION_IN_MILLIS = DURATION_IN_SECONDS * 1000;
    private static final String AUDIO_FILENAME = "audio";

    private final ChristmasVoiceApplication application;

    public MainModule(ChristmasVoiceApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context providesContext() {
        return application;
    }

    @Provides
    @Singleton
    MediaToolsProvider providesMediaToolsProvider() {
        return new MediaToolsProvider();
    }

    @Provides
    int providesDurationInMillis() {
        return DURATION_IN_MILLIS;
    }

    @Provides
    File providesAudioFile(Context context) {
        return new File(context.getFilesDir(), AUDIO_FILENAME);
    }

    @Provides
    @Singleton
    AudioRecorderPlayer providesAudioRecorderPlayer(MediaToolsProvider mediaToolsProvider, File audioFile) {
        return new AudioRecorderPlayer(mediaToolsProvider, audioFile);

    }

    @Provides
    Recorder providesRecorder(AudioRecorderPlayer audioRecorderPlayer) {
        return audioRecorderPlayer;
    }

    @Provides
    Player providesPlayer(AudioRecorderPlayer audioRecorderPlayer) {
        return audioRecorderPlayer;
    }
}
