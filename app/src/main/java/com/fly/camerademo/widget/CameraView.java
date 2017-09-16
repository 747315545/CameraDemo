package com.fly.camerademo.widget;

import android.content.Context;
import android.hardware.Camera;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;

/**
 * Created by huangfei on 2017/9/12.
 */

public class CameraView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private SurfaceTexture surfaceTexture;
    private Camera mCamera;
    private  CameraDrawer cameraDrawer;
    private SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() {
        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            requestRender();
        }
    };

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        setRenderer(this);
    }

    public CameraView(Context context) {
        super(context);
    }


    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        Log.d("huangfei", "onSurfaceCreated");
        int mTextureID = createTextureID();
        surfaceTexture = new SurfaceTexture(mTextureID);
        cameraDrawer = new CameraDrawer(mTextureID);
        surfaceTexture.setOnFrameAvailableListener(onFrameAvailableListener);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        Log.d("huangfei", "onSurfaceChanged");
        try {
            mCamera = Camera.open(0);
            Camera.Parameters parameter = mCamera.getParameters();
            if(parameter.getSupportedFocusModes().contains(FOCUS_MODE_CONTINUOUS_PICTURE)){
                parameter.setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            mCamera.setParameters(parameter);
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.startPreview();
            mCamera.cancelAutoFocus();
        } catch (IOException e) {
            Log.d("huangfei", "ddd");
            e.printStackTrace();
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        Log.d("huangfei", "onDrawFrame");
        if (surfaceTexture == null) {
            return;
        }
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        surfaceTexture.updateTexImage();
        cameraDrawer.drawSelf();
        Log.d("huangfei", "updateTexImage");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        mCamera.stopPreview();
        mCamera.release();
    }

    private int createTextureID() {
        int[] texture = new int[1];

        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return texture[0];
    }

}
