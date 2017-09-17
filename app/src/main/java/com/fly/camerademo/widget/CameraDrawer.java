package com.fly.camerademo.widget;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

public class CameraDrawer {
    private final String vertexShaderCode =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec2 inputTextureCoordinate;\n" +
                    "attribute float inputSaturation;\n" +
                    "attribute float inputLight;\n" +
                    "attribute float inputGray;\n" +
                    "varying vec2 textureCoordinate;\n" +
                    "varying float saturation;\n" +
                    "varying float light;\n" +
                    "varying float gray;\n" +
                    "void main()" +
                    "{" +
                    "gl_Position = vPosition;\n" +
                    "textureCoordinate = inputTextureCoordinate;\n" +
                    "saturation = inputSaturation;" +
                    "light = inputLight;" +
                    "gray = inputGray;" +
                    "}";

    private final String fragmentShaderCode =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;" +
                    "varying vec2 textureCoordinate;\n" +
                    "varying float saturation ;" +
                    "varying float light;" +
                    "varying float gray;" +
                    "uniform samplerExternalOES s_texture;\n" +
                    "vec3 target = vec3(0.2125,0.7154,0.0721);\n" +
                    "vec3 monoMultiplier = vec3(0.299, 0.587, 0.114);\n" +


                    "void main() {" +
                    "  vec4 color = texture2D( s_texture, textureCoordinate );\n" +
                    "  if(gray >0.0){" +
                    "  float monoColor = dot(color.rgb,monoMultiplier);\n" +
                    "  gl_FragColor = vec4(monoColor,monoColor,monoColor,1.0);\n" +
                    "  }else{" +
                    "  vec3 mColor = vec3 (dot(color.rgb,target));\n" +
                    "  gl_FragColor = vec4(mix(mColor,color.rgb,saturation),1.0)*light;\n" +
                    "  }" +
                    "}";

    private FloatBuffer vertexBuffer, textureVerticesBuffer;
    private ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mTextureCoordHandle;
    private int saturationHandle;
    private int lightHandle;
    private int grayHandle;

    private short drawOrder[] = {0, 1, 2, 0, 2, 3};

    private static final int COORDS_PER_VERTEX = 2;

    private final int vertexStride = COORDS_PER_VERTEX * 4;

    static float squareCoords[] = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f,
            1.0f, 1.0f,
    };

    static float textureVertices[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
    };

    private int texture;

    public CameraDrawer(int texture) {
        this.texture = texture;
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        ByteBuffer bb2 = ByteBuffer.allocateDirect(textureVertices.length * 4);
        bb2.order(ByteOrder.nativeOrder());
        textureVerticesBuffer = bb2.asFloatBuffer();
        textureVerticesBuffer.put(textureVertices);
        textureVerticesBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    public void drawSelf(float gray, float saturation, float light) {
        GLES20.glUseProgram(mProgram);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);
        GLES20.glVertexAttribPointer(mTextureCoordHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, textureVerticesBuffer);

        saturationHandle = GLES20.glGetAttribLocation(mProgram, "inputSaturation");
        GLES20.glEnable(saturationHandle);
        GLES20.glVertexAttrib1f(saturationHandle, saturation);

        lightHandle = GLES20.glGetAttribLocation(mProgram, "inputLight");
        GLES20.glEnable(lightHandle);
        GLES20.glVertexAttrib1f(lightHandle, light);

        grayHandle = GLES20.glGetAttribLocation(mProgram, "inputGray");
        GLES20.glEnable(grayHandle);
        GLES20.glVertexAttrib1f(grayHandle, gray);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        GLES20.glDisable(saturationHandle);
        GLES20.glDisable(lightHandle);
        GLES20.glDisable(grayHandle);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
    }

    private int loadShader(int type, String shaderCode) {

        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }


}