package com.fly.camerademo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.fly.camerademo.R;

/**
 * Created by huangfei on 2017/9/16.
 */

public class LevelView extends View {
    private int dgreet = 0;
    private boolean needdraw = true;
    private Context mContext;
    private int width;
    private int height;
    private Paint paint;

    public LevelView(Context context) {
        super(context);
    }

    public LevelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public LevelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        width = getWidth();
        height = getHeight();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mContext.getResources().getColor(R.color.level_bg));
        RectF rectF1 = new RectF(0, height / 2 - 15, width / 3, height / 2 + 15);
        RectF rectF2 = new RectF(width * 2 / 3, height / 2 - 15, width, height / 2 + 15);
        canvas.drawRoundRect(rectF1, width / 3, width / 3, paint);
        canvas.drawRoundRect(rectF2, width / 3, width / 3, paint);
        needdraw = false;
        canvas.translate(width / 2, height / 2);
        RectF rectF = new RectF(-width / 6 + 10, -15, width / 6 - 10, 15);
        paint.setColor(mContext.getResources().getColor(R.color.level_color));
        canvas.rotate(dgreet);
        canvas.drawRoundRect(rectF, width / 3, width / 3, paint);
        super.onDraw(canvas);
    }

    public void setDgreet(int i) {
        dgreet = i;
        invalidate();
    }

}
