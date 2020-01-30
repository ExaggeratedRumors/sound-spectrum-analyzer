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
    private FormulasUtil.State state;


    String[] permissions = new String[]{
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        setSystemWindow();
        setContentView(R.layout.activity_main);
        GraphicComponents graphicComponents = new GraphicComponents(this);
        graphicComponents.setGraphicComponents();
        state = FormulasUtil.State.A_WEIGHTING;
        handler = new RecordingHandler(this, graphicComponents);
    }

    public void setSystemWindow(){
        Window window = getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |  View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN );
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
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

    public void startRecording() {
        Thread recordingThread = new Thread() {
            @Override
            public void run() {
                try {
                    SignalConverter converter = new SignalConverter();
                    recorder = new AudioRecorder();
                    recorder.initRecorder();
                    while (recorder.isRecording()) {
                        if (recorder.readRecordedData()) continue;
                        converter.convertToComplex(recorder.getData());
                        converter.fft();
                        converter.divideToThirds();
                        if (handler != null) handler.dataChangeNotify(converter.getDbSignalForm(state));
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

    public void changeState(FormulasUtil.State state){
        this.state = state;
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
}