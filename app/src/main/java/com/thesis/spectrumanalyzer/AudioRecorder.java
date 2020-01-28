package com.thesis.spectrumanalyzer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;

class AudioRecorder {

    private int bufferSize;
    private byte[] data;
    private boolean isRecording = true;
    private AudioRecord recorder;

    /**
     * Audio record with constance buffer size
     */
    AudioRecorder() {
        bufferSize = AudioRecord.getMinBufferSize(FormulasUtil._SAMPLING_RATE_, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        data = new byte[bufferSize];
    }

    /**
     * Start recording
     */
    void initRecorder(){
        recorder = new AudioRecord(AudioSource.VOICE_RECOGNITION, FormulasUtil._SAMPLING_RATE_, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        recorder.startRecording();
    }

    /**
     * Reads audio data from the audio hardware for recording into a byte array.
     * https://stackoverflow.com/questions/24270379/meaning-of-values-from-audiorecord-read
     * @return zero or the negative number means error
     */
    boolean readToBuffer(){
        return recorder.read(data, 0, bufferSize) <= 0;
    }

    byte[] getData() { return data; }

    void stop(){ recorder.stop(); }

    void release(){ recorder.release(); }

    boolean isRecording() { return isRecording; }

    /**
     * Cut thread
     */
    synchronized void stopRecording() { isRecording = false; }
}