package com.smilehacker.tomato.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by kleist on 13-9-25.
 */
public class TimerView  extends View {
    private final static String TAG = "TIMER_VIEW";

    private final static int DEFAULT_HEIGHT = 500;
    private final static int DEFAULT_WIDTH = 500;

    private final static int CIRCEL_PAINT_WIDTH = 30;
    private final static int POINT_RADIUS = 30;

    private Paint mPointPaint;
    private Paint mRedPaint;
    private Paint mGrayPaint;
    private int mWidth = 0;
    private int mHeight = 0;
    private float mRadius = 0;
    private float mCenterX = 0;
    private float mCenterY = 0;
    private float mPointCenterX = 0;
    private float mPointCenterY = 0;

    private RectF mCircleRect;

    private float mStartDegree = -90;
    private float mCurrentDegree = 0;

    private Boolean canMovePoint = false;


    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDraw();
    }

    private void initDraw() {
        mPointPaint = new Paint();
        mPointPaint.setColor(Color.BLACK);
        mPointPaint.setStrokeJoin(Paint.Join.ROUND);
        mPointPaint.setStrokeCap(Paint.Cap.ROUND);
        mPointPaint.setAntiAlias(true);
        mPointPaint.setDither(true);
        mPointPaint.setStyle(Paint.Style.FILL);

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
        drawPoint(canvas);
    }



    private void drawGrayCircle(Canvas canvas) {
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mGrayPaint);
    }

    private void drawRedArc(Canvas canvas) {
        canvas.drawArc(mCircleRect, mStartDegree, mCurrentDegree, false, mRedPaint);
    }

    private void drawPoint(Canvas canvas) {
        double radians = Math.toRadians(mCurrentDegree);
        mPointCenterX = (float) (mRadius * Math.sin(radians) + mCenterX);
        mPointCenterY = (float) (mCenterY - mRadius * Math.cos(radians));
        canvas.drawCircle(mPointCenterX, mPointCenterY, POINT_RADIUS, mPointPaint);
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
        mRadius = Math.min(mWidth, mHeight) / 2 - Math.max(CIRCEL_PAINT_WIDTH, POINT_RADIUS * 2);
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
        float rectTop = mHeight / 2 - mRadius;
        float rectLeft = mWidth / 2 - mRadius;
        float rectBottom = mHeight - rectTop;
        float rectRight = mWidth - rectLeft;
        mCircleRect = new RectF(rectLeft, rectTop, rectRight, rectBottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                actionDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(event);
                break;
            case MotionEvent.ACTION_UP:
                actionUp(event);
                break;
            default:
                break;
        }

        return true;
    }

    private void actionDown(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if(isTouchPoint(x, y)) {
            float degree = getDegree(x, y);
            mCurrentDegree = degree;
            canMovePoint = true;
            invalidate();
        }
    }

    private void actionMove(MotionEvent event) {
        if (!canMovePoint) {
            return;
        }

        float x = event.getX();
        float y = event.getY();
        float degree = getDegree(x, y);
        if (mCurrentDegree > 350 && degree < 10) {
            mCurrentDegree = 360;
        } else {
            mCurrentDegree = degree;
        }

        mCurrentDegree = getDegree(x, y);
        invalidate();
    }

    private void actionUp(MotionEvent event) {
        canMovePoint = false;
    }

    private Boolean isTouchPoint(float x, float y) {
        double radius = Math.pow(x - mPointCenterX, 2) + Math.pow(y - mPointCenterY, 2);
        return radius <= Math.pow(POINT_RADIUS + 5, 2);
    }

    private float getDegree(float x, float y) {
        float cY = x - mCenterX;
        float cX = mCenterY - y;
        float R = (float) Math.sqrt(Math.pow(cX, 2) + Math.pow(cY, 2));
        //Log.i(TAG, "cY:" + cY + " R:" + R);
        double radians = Math.asin(cY/R);
        double degree = Math.toDegrees(radians);

        if (cX >=0 && cY >= 0) {

        } else if (cX <= 0 && cY >= 0) {           // 第二象限
            degree = 180 - degree;
        } else if (cX <= 0 && cY <= 0) {    // 第三象限
            degree = 180 - degree;
        } else if (cX >= 0 && cY <= 0) {     // 第四象限
            degree = 360 + degree;
        }
        //Log.i(TAG, "cX:" + cX + " cY:" + cY + " degree:" + degree + " radians:" + radians);

        return (float) degree;
    }




}
