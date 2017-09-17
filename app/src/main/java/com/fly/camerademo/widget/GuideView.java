package com.fly.camerademo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.fly.camerademo.R;

/**
 * Created by huangfei on 2017/9/16.
 */

public class GuideView extends View {
    private Context mContext;

    public GuideView(Context context) {
        super(context);
    }

    public GuideView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public GuideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int x;
        int width = getWidth();
        int height = getHeight();
        if (width < height) {
            x = width / 3;
        } else {
            x = height / 3;
        }
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mContext.getResources().getColor(R.color.level_bg));
        paint.setStrokeWidth(3);
        for (int i = x; i < width; i = i + x) {
            canvas.drawLine(i, 0, i, height, paint);
        }
        for (int j = x; j < height; j = j + x) {
            canvas.drawLine(0, j, width, j, paint);
        }
        super.onDraw(canvas);
    }
}
