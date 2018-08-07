package com.jianjian.gesturecipher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.View;

public class PointView extends View {

    private Mode mMode;
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private int mNoFingerColor = 0xFFD8D8D8;
    private int mFingerOnCenterColor = 0xFF6aa0ff;
    private int mFingerOnBackgroundColor = 0x896aa0ff;
    private int mIncorrectCenterColor = 0xFFFF794C;
    private int mIncorrectBackgroundColor = 0x89FDD7CA;

    enum Mode {
        STATUS_NO_FINGER, STATUS_FINGER_ON, STATUS_INCORRECT
    }

    public PointView(Context context) {
        super(context);
        mPaint = new Paint();
    }

    public PointView(Context context, int noFingerColor, int fingerOnCenterColor, int fingerOnBackgroundColor, int incorrectCenterColor, int incorrectBackgroundColor) {
        super(context);
        mPaint = new Paint();
        mNoFingerColor = noFingerColor;
        mFingerOnCenterColor = fingerOnCenterColor;
        mFingerOnBackgroundColor = fingerOnBackgroundColor;
        mIncorrectCenterColor = incorrectCenterColor;
        mIncorrectBackgroundColor = incorrectBackgroundColor;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (mMode) {
            case STATUS_NO_FINGER:
                mPaint.setColor(mNoFingerColor);
                drawPoint(canvas, mPaint);
                break;
            case STATUS_FINGER_ON:
                mPaint.setColor(mFingerOnBackgroundColor);
                drawPointBackGround(canvas, mPaint);
                mPaint.setColor(mFingerOnCenterColor);
                drawPoint(canvas, mPaint);
                break;
            case STATUS_INCORRECT:
                mPaint.setColor(mIncorrectBackgroundColor);
                drawPointBackGround(canvas, mPaint);
                mPaint.setColor(mIncorrectCenterColor);
                drawPoint(canvas, mPaint);
                break;
            default:
                break;
        }

    }

    private void drawPoint(Canvas canvas, Paint paint) {
        RectF rectF = new RectF(this.mWidth * 0.3f,
                this.mHeight * 0.3f,
                this.mWidth * 0.7f,
                this.mHeight * 0.7f);
        canvas.drawOval(rectF, paint);
    }
    private void drawPointBackGround(Canvas canvas, Paint paint) {
        RectF rectF = new RectF(0, 0, this.mWidth, this.mHeight);
        canvas.drawOval(rectF, paint);
    }

    public void setMode(Mode mode) {
        this.mMode = mode;
        invalidate();
    }
}
