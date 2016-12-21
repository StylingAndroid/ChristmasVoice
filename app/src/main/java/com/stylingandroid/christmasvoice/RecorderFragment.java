package com.stylingandroid.christmasvoice;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.stylingandroid.christmasvoice.media.Player;
import com.stylingandroid.christmasvoice.media.Recorder;

import javax.inject.Inject;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

public class RecorderFragment extends Fragment {

    private static final int UPDATE_INTERVAL = 12;
    private static final float SANTA_SPEED = 0.75f;
    private static final float ELF_SPEED = 2f;

    private Unbinder unbinder;

    private long startTime = -1;
    private long clipLength = 0;

    @Inject
    protected Recorder recorder;

    @Inject
    protected Player player;

    @Inject
    protected int durationInMillis;

   @BindView(R.id.fragment_recorder)
    protected ViewGroup viewRoot;

    @BindView(R.id.floatingActionButton)
    protected FloatingActionButton record;

    @BindView(R.id.playProgress)
    protected ProgressBar playProgress;

    @BindView(R.id.santa)
    protected View santa;

    @BindView(R.id.elf)
    protected View elf;

    @BindView(R.id.attribution_author)
    protected TextView attributionAuthor;

    @BindInt(android.R.integer.config_shortAnimTime)
    protected int fabAnimationDuration;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context context = inflater.getContext();
        ChristmasVoiceApplication.getComponent(context).inject(this);
        Timber.v("onCreateView: %1$s %2$s", player.toString(), recorder.toString());
        View layout = inflater.inflate(R.layout.fragment_recorder, container, false);
        unbinder = ButterKnife.bind(this, layout);
        attributionAuthor.setMovementMethod(LinkMovementMethod.getInstance());
        playProgress.setMax(durationInMillis);
        setInitialButtonState();
        return layout;
    }

    @Override
    public void onDestroyView() {
        stopPlaying();
        stopRecording();
        unbinder.unbind();
        super.onDestroyView();
    }

    private void setInitialButtonState() {
        setInitialRecordButtonState();
        setPlayButtonState();
    }

    private void setButtonState() {
        TransitionManager.beginDelayedTransition(viewRoot);
        setRecordButtonState();
        setPlayButtonState();
    }

    private void setInitialRecordButtonState() {
        if (isRecording()) {
            record.setImageResource(R.drawable.ic_stop);
        } else {
            record.setImageResource(R.drawable.ic_mic);
        }
    }

    private void setRecordButtonState() {
        if (isRecording()) {
            startAnimation(record, R.drawable.mic2stop_animated);
        } else {
            startAnimation(record, R.drawable.stop2mic_animated);
        }
    }

    private void startAnimation(ImageView imageView, @DrawableRes int drawableRes) {
        imageView.setImageResource(drawableRes);
        Drawable drawable = record.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }

    private boolean isRecording() {
        return recorder.isRecording();
    }

    private void setPlayButtonState() {
        if (hasRecording()) {
            santa.setVisibility(View.VISIBLE);
            elf.setVisibility(View.VISIBLE);
        } else {
            santa.setVisibility(View.INVISIBLE);
            elf.setVisibility(View.INVISIBLE);
        }
    }

    private boolean hasRecording() {
        return !isRecording() && recorder.hasRecording();
    }

    @OnClick(R.id.floatingActionButton)
    protected void clickRecord() {
        if (isRecording()) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    void startRecording() {
        elf.setEnabled(false);
        santa.setEnabled(false);
        recorder.startRecording();
        startTime = System.currentTimeMillis();
        playProgress.setMax(durationInMillis);
        setButtonState();
        record.postDelayed(stopRecording, durationInMillis);
        playProgress.postDelayed(updateRecordStatus, UPDATE_INTERVAL);
    }

    void stopRecording() {
        recorder.stopRecording();
        record.removeCallbacks(stopRecording);
        playProgress.removeCallbacks(updateRecordStatus);
        clipLength = System.currentTimeMillis() - startTime;
        playProgress.setMax((int) clipLength);
        playProgress.setSecondaryProgress((int) clipLength);
        startTime = -1;
        setButtonState();
        elf.setEnabled(true);
        santa.setEnabled(true);
    }

    long getTimeOffset() {
        return System.currentTimeMillis() - startTime;
    }

    private Runnable stopRecording = new Runnable() {
        @Override
        public void run() {
            if (isRecording()) {
                stopRecording();
                playProgress.setSecondaryProgress(durationInMillis);
            }
        }
    };

    @OnClick(R.id.santa)
    protected void clickSanta() {
        if (isPlaying()) {
            stopPlaying();
        }
        startPlaying(SANTA_SPEED);
    }

    @OnClick(R.id.elf)
    protected void clickElf() {
        if (isPlaying()) {
            stopPlaying();
        }
        startPlaying(ELF_SPEED);
    }

    private boolean isPlaying() {
        return player.isPlaying();
    }

    private void stopPlaying() {
        player.stopPlaying();
        setButtonState();
        showFab();
        santa.removeCallbacks(stopRecording);
        playProgress.removeCallbacks(updateRecordStatus);
        startTime = -1;

    }

    private void showFab() {
        record.setEnabled(true);
        record.animate()
                .translationY(0)
                .setDuration(fabAnimationDuration)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void startPlaying(float speed) {
        elf.setEnabled(false);
        santa.setEnabled(false);
        hideFab();
        player.setSpeed(speed);
        player.startPlaying();
        playProgress.setMax((int) clipLength);
        startTime = System.currentTimeMillis();
        playProgress.postDelayed(updatePlayStatus, UPDATE_INTERVAL);
        record.postDelayed(stopPlaying, clipLength);
    }

    private void hideFab() {
        record.setEnabled(false);
        record.animate()
                .translationY(viewRoot.getHeight() - record.getY())
                .setDuration(fabAnimationDuration)
                .setInterpolator(new AccelerateInterpolator())
                .start();
    }

    private Runnable updateRecordStatus = new Runnable() {
        @Override
        public void run() {
            if (playProgress != null) {
                playProgress.setSecondaryProgress((int) getTimeOffset());
                playProgress.postDelayed(this, UPDATE_INTERVAL);
            }
        }
    };

    private Runnable updatePlayStatus = new Runnable() {
        @Override
        public void run() {
            if (playProgress != null) {
                playProgress.setProgress((int) getTimeOffset());
                playProgress.postDelayed(this, UPDATE_INTERVAL);
            }
        }
    };

    private Runnable stopPlaying = new Runnable() {
        @Override
        public void run() {
            elf.setEnabled(true);
            santa.setEnabled(true);
            showFab();
        }
    };
}
