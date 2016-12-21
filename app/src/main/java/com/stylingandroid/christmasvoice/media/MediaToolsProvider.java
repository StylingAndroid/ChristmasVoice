package com.stylingandroid.christmasvoice.media;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.PresetReverb;

public class MediaToolsProvider {
    private static final int CHANNEL_SIZE = 1920;
    private PresetReverb presetReverb = null;

    AudioRecord getAudioRecord() {
        AudioFormat audioFormat = getAudioFormat(AudioFormat.CHANNEL_IN_MONO);
        int bufferSize = getInputBufferSize(audioFormat);
        return new AudioRecord.Builder()
                .setAudioSource(MediaRecorder.AudioSource.MIC)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(bufferSize)
                .build();
    }

    private AudioFormat getAudioFormat(int channelMask) {
        int sampleRate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
        return new AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setChannelMask(channelMask)
                .setEncoding(AudioFormat.ENCODING_DEFAULT)
                .build();
    }

    private int getInputBufferSize(AudioFormat audioFormat) {
        int bufferSize = AudioRecord.getMinBufferSize(
                audioFormat.getSampleRate(),
                audioFormat.getChannelCount(),
                audioFormat.getEncoding()
        );
        if (bufferSize <= 0) {
            bufferSize = CHANNEL_SIZE * audioFormat.getChannelCount();
        }
        return bufferSize;
    }

    AudioTrack getAudioTrack(long bufferSize) {
        AudioFormat audioFormat = getAudioFormat(AudioFormat.CHANNEL_OUT_MONO);
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build();
        AudioTrack track = new AudioTrack.Builder()
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes((int) bufferSize)
                .setAudioAttributes(attributes)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build();
        PresetReverb reverb = getReverb();
        track.attachAuxEffect(reverb.getId());
        track.setAuxEffectSendLevel(1.0f);
        return track;
    }

    private PresetReverb getReverb() {
        if (presetReverb == null) {
            presetReverb = createPresetReverb();
        }
        return presetReverb;
    }

    private PresetReverb createPresetReverb() {
        PresetReverb reverb = new PresetReverb(1, 0);
        reverb.setPreset(PresetReverb.PRESET_PLATE);
        reverb.setEnabled(true);
        return reverb;
    }
}
