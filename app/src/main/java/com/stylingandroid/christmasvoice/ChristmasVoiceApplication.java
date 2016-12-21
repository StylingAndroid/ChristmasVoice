package com.stylingandroid.christmasvoice;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.stylingandroid.christmasvoice.dagger.DaggerMainComponent;
import com.stylingandroid.christmasvoice.dagger.MainComponent;
import com.stylingandroid.christmasvoice.dagger.MainModule;

import timber.log.Timber;

public class ChristmasVoiceApplication extends Application {
    private static MainComponent mainComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
    }

    private MainComponent getMainComponent() {
        if (mainComponent == null) {
            mainComponent = DaggerMainComponent.builder()
                    .mainModule(new MainModule(this))
                    .build();
        }
        return mainComponent;
    }

    @NonNull
    public static MainComponent getComponent(Context context) {
        Context application = context.getApplicationContext();
        if (!(application instanceof ChristmasVoiceApplication)) {
            throw new RuntimeException();
        }
        return ((ChristmasVoiceApplication) application).getMainComponent();
    }
}
