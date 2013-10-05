package com.smilehacker.tomato.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by kleist on 13-9-28.
 */
public class TimeCountView extends View {


    private final static String TAG = "TIMER_VIEW";

    private final static int DEFAULT_HEIGHT = 500;
    private final static int DEFAULT_WIDTH = 500;
    private final static int CIRCEL_PAINT_WIDTH = 30;

    private Paint mRedPaint;
    private Paint mGrayPaint;
    private int mWidth = 0;
    private int mHeight = 0;
    private float mRadius = 0;
    private float mCenterX = 0;
    private float mCenterY = 0;

    private RectF mCircleRect;

    private float mStartDegree = -90;
    private float mCurrentDegree = 0;



    public TimeCountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDraw();
    }

    private void initDraw() {

        mRedPaint = new Paint();
        mRedPaint.setColor(Color.RED);
        mRedPaint.setStrokeJoin(Paint.Join.BEVEL);
        mRedPaint.setStrokeCap(Paint.Cap.SQUARE);
        mRedPaint.setAntiAlias(true);
        mRedPaint.setDither(true);
        mRedPaint.setStyle(Paint.Style.STROKE);
        mRedPaint.setStrokeWidth(CIRCEL_PAINT_WIDTH);

        mGrayPaint = new Paint();
        mGrayPaint.setColor(Color.GRAY);
        mGrayPaint.setStrokeJoin(Paint.Join.ROUND);
        mGrayPaint.setStrokeCap(Paint.Cap.ROUND);
        mGrayPaint.setAntiAlias(true);
        mGrayPaint.setDither(true);
        mGrayPaint.setStrokeWidth(CIRCEL_PAINT_WIDTH);
        mGrayPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawGrayCircle(canvas);
        drawRedArc(canvas);
    }

    private void drawGrayCircle(Canvas canvas) {
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mGrayPaint);
    }

    private void drawRedArc(Canvas canvas) {
        canvas.drawArc(mCircleRect, mStartDegree, mCurrentDegree, false, mRedPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        mWidth = getMeasuredLength(widthMeasureSpec, true);
        mHeight = getMeasuredLength(heightMeasureSpec, false);
        setMeasuredDimension( mWidth, mHeight);
        getCircleParams();
    }

    private int getMeasuredLength(int length, boolean isWidth) {
        int specMode = MeasureSpec.getMode(length);
        int specSize = MeasureSpec.getSize(length);
        int size;
        int padding = isWidth ? getPaddingLeft() + getPaddingRight()
                : getPaddingTop() + getPaddingBottom();
        if (specMode == MeasureSpec.EXACTLY) {
            size = specSize;
        } else {
            size = isWidth ? padding + DEFAULT_WIDTH : DEFAULT_HEIGHT
                    + padding;
            if (specMode == MeasureSpec.AT_MOST) {
                size = Math.min(size, specSize);
            }
        }
        return size;
    }

    private void getCircleParams() {
        mRadius = Math.min(mWidth, mHeight) / 2 - CIRCEL_PAINT_WIDTH;
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
        float rectTop = mHeight / 2 - mRadius;
        float rectLeft = mWidth / 2 - mRadius;
        float rectBottom = mHeight - rectTop;
        float rectRight = mWidth - rectLeft;
        mCircleRect = new RectF(rectLeft, rectTop, rectRight, rectBottom);
    }

    public void setDegree(float degree) {
        mCurrentDegree = degree;
        invalidate();
    }

}
