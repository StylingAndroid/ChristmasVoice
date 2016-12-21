package com.stylingandroid.christmasvoice.dagger;

import com.stylingandroid.christmasvoice.MainActivity;
import com.stylingandroid.christmasvoice.RecorderFragment;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = MainModule.class)
@Singleton
public interface MainComponent {
    void inject(MainActivity mainActivity);
    void inject(RecorderFragment recorderFragment);
}
