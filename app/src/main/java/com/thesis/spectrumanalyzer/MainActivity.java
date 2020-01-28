package com.thesis.spectrumanalyzer;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import androidx.annotation.*;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private AudioRecorder recorder;
    private RecordingHandler handler;

    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        checkPermissions();
        setContentView(R.layout.activity_main);
        setSystemBar();
        GraphicComponents graphicComponents = new GraphicComponents(this);
        graphicComponents.setGraphicComponents();
        handler = new RecordingHandler(this, graphicComponents);
    }

    public void setSystemBar(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |  View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN );
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
    }

    public void startRecording() {
        Thread recordingThread = new Thread() {
            @Override
            public void run() {
                try {
                    SignalConverter converter = new SignalConverter();
                    recorder = new AudioRecorder();
                    recorder.initRecorder();
                    while (recorder.isRecording()) {
                        if (recorder.readToBuffer()) continue;
                        converter.convertToComplex(recorder.getData());
                        converter.fft();
                        if (handler != null) handler.dataChangeNotify(converter.divideToThirds());
                        sleep(85);
                    }
                    recorder.stop();
                    recorder.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        recordingThread.start();
    }

    public RecordingHandler getHandler() {
        return handler;
    }

    private void checkPermissions() {
        int result;
        ArrayList<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startRecording();
    }

    @Override
    protected void onPause() {
        super.onPause();
        recorder.stopRecording();
    }
}