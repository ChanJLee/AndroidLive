package com.wenyu.rtmp.audio;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.wenyu.rtmp.configuration.AudioConfiguration;

public class AudioUtils {
    public static boolean checkMicSupport(AudioConfiguration audioConfiguration) {
        boolean result;
        int recordBufferSize = getRecordBufferSize(audioConfiguration);
        byte[] mRecordBuffer = new byte[recordBufferSize];
        AudioRecord audioRecord = getAudioRecord(audioConfiguration);
        try {
            audioRecord.startRecording();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int readLen = audioRecord.read(mRecordBuffer, 0, recordBufferSize);
        result = readLen >= 0;
        try {
            audioRecord.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int getRecordBufferSize(AudioConfiguration audioConfiguration) {
        int frequency = audioConfiguration.frequency;
        if(audioConfiguration.aec && audioConfiguration.frequency == 48000) {
            frequency = 16000;
        }
        int audioEncoding = audioConfiguration.encoding;
        int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        if(audioConfiguration.channelCount == 2) {
            channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
        }
        int size = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
        if(audioConfiguration.aec && audioConfiguration.frequency == 48000) {
            return size > 3200 ? 6400 : 3200;
        } else {
            return size;
        }
    }

    @TargetApi(18)
    public static AudioRecord getAudioRecord(AudioConfiguration audioConfiguration) {
        int frequency = audioConfiguration.frequency;
        if(audioConfiguration.aec && audioConfiguration.frequency == 48000) {
            frequency = 16000;
        }
        int audioEncoding = audioConfiguration.encoding;
        int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        if(audioConfiguration.channelCount == 2) {
            channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
        }
        int audioSource = MediaRecorder.AudioSource.MIC;
        if(audioConfiguration.aec) {
            audioSource = MediaRecorder.AudioSource.VOICE_COMMUNICATION;
        }
        AudioRecord audioRecord = new AudioRecord(audioSource, frequency,
                channelConfiguration, audioEncoding, getRecordBufferSize(audioConfiguration));
        return audioRecord;
    }
}
