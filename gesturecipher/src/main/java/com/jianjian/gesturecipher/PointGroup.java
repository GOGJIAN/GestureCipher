package com.jianjian.gesturecipher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class PointGroup extends RelativeLayout {
    private PointView[] mPointViewArray;
    private float mWidth;
    private float mHeight;
    private int mPointViewWidth;
    private int mPointViewMargin;
    private int mCount;
    private int mLineWidth;
    private int mCountSum;
    private int mNoFingerColor = 0xFFD8D8D8;
    private int mFingerOnCenterColor = 0xFF6aa0ff;
    private int mFingerOnBackgroundColor = 0x896aa0ff;
    private int mIncorrectCenterColor = 0xFFFF794C;
    private int mIncorrectBackgroundColor = 0x89FDD7CA;
    private Path mPath;
    private Paint mPaint;
    private int[] answer = {1,2,3};
    private ArrayList<Integer> mChoose = new ArrayList<>();
    private float mTargetTempX;
    private float mTargetTempY;
    private float mLastX;
    private float mLastY;
    private boolean mDrawTempFlag = true;
    private boolean mCleared;
    private StateListener mStateListener;
    private int mRetryTimes = 5;
    private int mRetryTemp = mRetryTimes;
    ScheduledExecutorService mScheduledExecutorService = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());

    public PointGroup(Context context) {
        super(context);
    }

    public PointGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PointGroup);
        mCount = array.getInteger(R.styleable.PointGroup_count, 3);
        mLineWidth = array.getInteger(R.styleable.PointGroup_line_width, 20);
        mNoFingerColor = array.getColor(R.styleable.PointGroup_color_no_finger, mNoFingerColor);
        mFingerOnCenterColor = array.getColor(R.styleable.PointGroup_color_finger_on_center, mFingerOnCenterColor);
        mFingerOnBackgroundColor = array.getColor(R.styleable.PointGroup_color_finger_on_background, mFingerOnBackgroundColor);
        mIncorrectCenterColor = array.getColor(R.styleable.PointGroup_color_incorrect_center, mIncorrectCenterColor);
        mIncorrectBackgroundColor = array.getColor(R.styleable.PointGroup_color_incorrect_background, mIncorrectBackgroundColor);
        array.recycle();
        mPath = new Path();
        //画笔初始化
        mPaint = new Paint();
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mCountSum = mCount * 9 - 4;
        mPointViewWidth = (int) (mWidth / mCountSum * 5f);
        mPointViewMargin = (int) (mWidth / mCountSum * 4f);
        mPaint.setStrokeWidth(mPointViewWidth / mLineWidth);
        if (mPointViewArray == null) {
            mPointViewArray = new PointView[mCount * mCount];
            for (int i = 0; i < mPointViewArray.length; i++) {
                mPointViewArray[i] = new PointView(getContext(), mNoFingerColor,
                        mFingerOnCenterColor, mFingerOnBackgroundColor,
                        mIncorrectCenterColor, mIncorrectBackgroundColor);
                mPointViewArray[i].setId(i + 1);
                LayoutParams layoutParams = new LayoutParams(mPointViewWidth, mPointViewWidth);
                //不是第一列就设置为前面的右边
                if (i % mCount != 0) {
                    layoutParams.addRule(RelativeLayout.RIGHT_OF, mPointViewArray[i - 1].getId());
                }
                //不是第一行就设置为上面的下边
                if (i >= mCount) {
                    layoutParams.addRule(RelativeLayout.BELOW, mPointViewArray[i - mCount].getId());
                }
                int marginTop = 0;
                int marginRight = 0;
                int marginDown = 0;
                int marginLeft = 0;
                //不是最后一列则设置右间距
                if ((i + 1) % mCount != 0) {
                    marginRight = mPointViewMargin;
                }
                //不是最后一行则设置下间距
                if (i < mCount * (mCount - 1)) {
                    marginDown = mPointViewMargin;
                }
                layoutParams.setMargins(marginLeft, marginTop, marginRight, marginDown);
                mPointViewArray[i].setMode(PointView.Mode.STATUS_NO_FINGER);
                addView(mPointViewArray[i], layoutParams);
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (!mPath.isEmpty()) {
            canvas.drawPath(mPath, mPaint);
        }
        if (mChoose.size() != 0 && mLastX != 0 && mLastY != 0 && mDrawTempFlag) {
            canvas.drawLine(mLastX, mLastY, mTargetTempX, mTargetTempY, mPaint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                reset();
                mCleared = true;
                break;
            case MotionEvent.ACTION_MOVE:
                PointView cv = getChildByPos(x, y);
                if (cv != null) {
                    int id = cv.getId();
                    if (!mChoose.contains(id)) {
                        mChoose.add(id);
                        cv.setMode(PointView.Mode.STATUS_FINGER_ON);
                        mLastX = (cv.getRight() + cv.getLeft()) / 2;
                        mLastY = (cv.getBottom() + cv.getTop()) / 2;
                        if (mChoose.size() == 1) {
                            mPath.moveTo(mLastX, mLastY);
                        } else {
                            mPath.lineTo(mLastX, mLastY);
                        }
                    }
                }
                mTargetTempX = x;
                mTargetTempY = y;
                break;
            case MotionEvent.ACTION_UP:
                mDrawTempFlag = false;
                mRetryTemp--;
                if(mRetryTemp<=0){
                    mPaint.setColor(mIncorrectCenterColor);
                    for (int i : mChoose) {
                        mPointViewArray[i - 1].setMode(PointView.Mode.STATUS_INCORRECT);
                    }
                    mStateListener.onMaxRetryTimes();
                }else {
                    if (!checkAnswer()) {
                        mPaint.setColor(mIncorrectCenterColor);
                        for (int i : mChoose) {
                            mPointViewArray[i - 1].setMode(PointView.Mode.STATUS_INCORRECT);
                        }
                        if (mStateListener != null) {
                            mStateListener.onIncorrect();
                        }

                    } else {
                        mPaint.setColor(mFingerOnCenterColor);
                        for (int i : mChoose) {
                            mPointViewArray[i - 1].setMode(PointView.Mode.STATUS_FINGER_ON);
                        }
                        if (mStateListener != null) {
                            mStateListener.onCorrect();
                        }
                    }
                }
                mCleared = false;
                mScheduledExecutorService.schedule(new Runnable() {
                    @Override
                    public void run() {
                        post(new Runnable() {
                            @Override
                            public void run() {
                                reset();
                                invalidate();
                            }
                        });
                    }
                }, 1, TimeUnit.SECONDS);
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d("AAA", "onTouchEvent: CANCEL");
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }

    private boolean checkAnswer(){
        if(mChoose.size()!=answer.length) {
            return false;
        }
        for(int i = 0;i < answer.length; i++){
            if(mChoose.get(i)!=answer[i]){
                return false;
            }
        }
        return true;
    }

    private PointView getChildByPos(float x, float y) {
        for (PointView pv : mPointViewArray) {
            if (x < pv.getRight() && x > pv.getLeft() && y > pv.getTop() && y < pv.getBottom()) {
                return pv;
            }
        }
        return null;
    }

    private void reset() {
        if(!mCleared) {
            mDrawTempFlag = true;
            mPath.reset();
            mPaint.setColor(mFingerOnCenterColor);
            mChoose.clear();
            for (PointView pv : mPointViewArray) {
                pv.setMode(PointView.Mode.STATUS_NO_FINGER);
            }
        }
    }

    public void resetRetry(){
        mRetryTemp = mRetryTimes;
    }

    public void setRetryTimes(int times){
        this.mRetryTimes = times;
        this.mRetryTemp = times;
    }

    public void setAnswer(int[] answer) {
        this.answer = answer;
    }

    public void setStateListener(StateListener stateListener){
        mStateListener = stateListener;
    }

    public interface StateListener{
        void onCorrect();
        void onIncorrect();
        void onMaxRetryTimes();
    }
}
