package com.fly.camerademo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;


import com.fly.camerademo.widget.CameraView;
import com.fly.camerademo.widget.LevelView;

import static android.hardware.SensorManager.SENSOR_DELAY_GAME;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener, SeekBar.OnSeekBarChangeListener {

    private CameraView cameraView;
    private View guideView;
    private LevelView levelView;
    private boolean showGuideView = false;
    private boolean showLevelView = false;
    private boolean showseekBar_1 = false;
    private boolean showseekBar_2 = false;
    private boolean isGray = false;
    private SeekBar seekBar_1;
    private SeekBar seekBar_2;
    private SensorManager mySM;
    private Sensor acc_sensor;
    private Sensor mag_sensor;
    private float accValues[] = new float[3];
    private float magValues[] = new float[3];
    private float r[] = new float[9];
    private float values[] = new float[3];

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    seekBar_1.setVisibility(View.GONE);
                    showseekBar_1 = false;
                    break;
                case 2:
                    seekBar_2.setVisibility(View.GONE);
                    showseekBar_2 = false;
                    break;
            }
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraView = (CameraView) findViewById(R.id.camera_view);
        levelView = (LevelView) findViewById(R.id.level_view);
        guideView = findViewById(R.id.guide_view);
        seekBar_1 = (SeekBar) findViewById(R.id.seekbar_1);
        seekBar_2 = (SeekBar) findViewById(R.id.seekbar_2);
        seekBar_1.setOnSeekBarChangeListener(this);
        seekBar_2.setOnSeekBarChangeListener(this);
        seekBar_1.setMax(100);
        seekBar_2.setMax(100);
        seekBar_1.setProgress(50);
        seekBar_2.setProgress(50);
        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);
        findViewById(R.id.btn_5).setOnClickListener(this);
        findViewById(R.id.tack_pic).setOnClickListener(this);
        mySM = (SensorManager) getSystemService(SENSOR_SERVICE);
        mag_sensor = mySM.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        acc_sensor = mySM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (savedInstanceState != null) {
            loadConfig(savedInstanceState);
        }
    }

    public void loadConfig(Bundle savedInstanceState) {
        showGuideView = savedInstanceState.getBoolean("showGuideView");
        showLevelView = savedInstanceState.getBoolean("showLevelView");
        showseekBar_1 = savedInstanceState.getBoolean("showseekBar_1");
        showseekBar_2 = savedInstanceState.getBoolean("showseekBar_2");
        isGray = savedInstanceState.getBoolean("isGray");
        if (showLevelView) {
            levelView.setVisibility(View.VISIBLE);
            mySM.registerListener(this, mag_sensor, SENSOR_DELAY_GAME);
        }
        if (showGuideView) {
            guideView.setVisibility(View.VISIBLE);
        }
        if (showseekBar_1) {
            seekBar_1.setVisibility(View.VISIBLE);
        }
        if (showseekBar_2) {
            seekBar_2.setVisibility(View.VISIBLE);
        }
        if (isGray) {
            cameraView.setGray(1.0f);
        }

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
        mySM.unregisterListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mySM.unregisterListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_1:
                if (seekBar_1.getVisibility() == View.GONE) {
                    showseekBar_1 = true;
                    seekBar_2.setVisibility(View.GONE);
                    seekBar_1.setVisibility(View.VISIBLE);
                } else {
                    seekBar_1.setVisibility(View.GONE);
                    showseekBar_1 = false;
                }
                break;
            case R.id.btn_2:
                if (seekBar_2.getVisibility() == View.GONE) {
                    showseekBar_2 = true;
                    seekBar_1.setVisibility(View.GONE);
                    seekBar_2.setVisibility(View.VISIBLE);
                } else {
                    seekBar_2.setVisibility(View.GONE);
                    showseekBar_2 = false;
                }
                break;
            case R.id.btn_3:
                if (guideView.getVisibility() == View.GONE) {
                    showGuideView = true;
                    guideView.setVisibility(View.VISIBLE);
                } else {
                    guideView.setVisibility(View.GONE);
                    showGuideView = false;
                }
                break;
            case R.id.btn_4:
                if (levelView.getVisibility() == View.GONE) {
                    levelView.setVisibility(View.VISIBLE);
                    showLevelView = true;
                    mySM.registerListener(this, mag_sensor, SENSOR_DELAY_GAME);
                    mySM.registerListener(this, acc_sensor, SENSOR_DELAY_GAME);
                } else {
                    levelView.setVisibility(View.GONE);
                    showLevelView = false;
                    mySM.unregisterListener(this);
                }
                break;
            case R.id.btn_5:
                if (isGray) {
                    cameraView.setGray(0.0f);
                    isGray = false;
                } else {
                    cameraView.setGray(1.0f);
                    isGray = true;
                }
                break;
            case R.id.tack_pic:
                if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    Toast.makeText(CameraActivity.this, "Save patch : " + cameraView.tackPic(), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(CameraActivity.this, "Save patch : " + cameraView.tackPic(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CameraActivity.this, "Permission denied !!!", Toast.LENGTH_LONG).show();
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magValues = sensorEvent.values.clone();
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accValues = sensorEvent.values.clone();
        }
        SensorManager.getRotationMatrix(r, null, accValues, magValues);
        SensorManager.getOrientation(r, values);
        levelView.setDgreet((int) Math.toDegrees(values[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("showLevelView", showLevelView);
        outState.putBoolean("showGuideView", showGuideView);
        outState.putBoolean("showseekBar_1", showseekBar_1);
        outState.putBoolean("showseekBar_2", showseekBar_2);
        outState.putBoolean("isGray", isGray);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        int p = seekBar.getProgress();
        switch (seekBar.getId()) {
            case R.id.seekbar_1:
                cameraView.setLight(p * 0.01f);
                break;
            case R.id.seekbar_2:
                cameraView.setSaturation(p * 0.01f);
                break;
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.seekbar_1:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(1);
                    }
                }, 1000);
                break;
            case R.id.seekbar_2:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(2);
                    }
                }, 1000);
                break;
        }

    }


}
