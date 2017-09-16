package com.fly.camerademo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.fly.camerademo.widget.CameraView;

public class CameraActivity extends AppCompatActivity  {

    private CameraView cameraView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraView = (CameraView) findViewById(R.id.camera_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.onPause();
    }

}
