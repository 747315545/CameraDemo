package com.fly.camerademo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;

/**
 * Created by huangfei on 2017/9/12.
 */

public class CameraView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private SurfaceTexture surfaceTexture;
    private Camera mCamera;
    private CameraDrawer cameraDrawer;
    private float gray = 0.0f;
    private float light = 0.5f;
    private float saturation = 0.5f;
    private boolean isTakePicture;
    private Bitmap bmp = null;
    private Context mContext;
    private String savePath;
    private int width;
    private int height;
    private SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() {
        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            requestRender();
        }
    };

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setEGLContextClientVersion(2);
        setRenderer(this);
    }

    public CameraView(Context context) {
        super(context);
    }


    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        int mTextureID = createTextureID();
        surfaceTexture = new SurfaceTexture(mTextureID);
        cameraDrawer = new CameraDrawer(mTextureID);
        surfaceTexture.setOnFrameAvailableListener(onFrameAvailableListener);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        width = i;
        height = i1;
        try {
            mCamera = Camera.open(0);
            Camera.Parameters parameter = mCamera.getParameters();
            if (parameter.getSupportedFocusModes().contains(FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameter.setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            mCamera.setParameters(parameter);
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.startPreview();
            mCamera.cancelAutoFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        if (surfaceTexture == null) {
            return;
        }
        if (isTakePicture) {
            bmp = createBitmapFromGLSurface(width,
                    height, gl10);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(savePath);
                    compressImage(bmp, file);
                }
            }).start();
            isTakePicture = false;
        }
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        surfaceTexture.updateTexImage();

        cameraDrawer.drawSelf(gray, saturation, light);
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

    public void setGray(float gray) {
        this.gray = gray;
    }

    public void setLight(float light) {
        this.light = light;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public String tackPic() {
        savePath = getSavePath();
        isTakePicture = true;
        return savePath;
    }

    private Bitmap createBitmapFromGLSurface(int w, int h, GL10 gl) {

        int b[] = new int[(int) (w * h)];
        int bt[] = new int[(int) (w * h)];
        IntBuffer buffer = IntBuffer.wrap(b);
        buffer.position(0);
        gl.glReadPixels(0, 0, w, h, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int pix = b[i * w + j];
                int pb = (pix >> 16) & 0xff;
                int pr = (pix << 16) & 0x00ff0000;
                int pix1 = (pix & 0xff00ff00) | pr | pb;
                bt[(h - i - 1) * w + j] = pix1;
            }
        }
        Bitmap inBitmap = null;
        if (inBitmap == null || !inBitmap.isMutable()
                || inBitmap.getWidth() != w || inBitmap.getHeight() != h) {
            inBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }
        inBitmap.copyPixelsFromBuffer(buffer);
        inBitmap = Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);

        return inBitmap;
    }

    public static void compressImage(Bitmap image, File file) {
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getSavePath() {
        return Environment.getExternalStorageDirectory()
                .toString()
                + File.separator
                + System.currentTimeMillis()
                + ".jpg";
    }
}
