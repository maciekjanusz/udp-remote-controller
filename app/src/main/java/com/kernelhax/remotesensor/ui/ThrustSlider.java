package com.kernelhax.remotesensor.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ThrustSlider extends View {

    private static final String TAG = "ThrustSlider";
    private OnThrustLevelChangedListener listener;

    int height, width;
    int minThrustHeight, maxThrustHeight, thrustInterval;

    private Paint thumbPaint;

    private float thrustLevel = 0.5f;

    public ThrustSlider(Context context) {
        super(context);
        init();
    }

    public ThrustSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ThrustSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ThrustSlider(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        thumbPaint = new Paint();
        thumbPaint.setColor(Color.BLACK);
        thumbPaint.setAntiAlias(true);
        thumbPaint.setStrokeWidth(6);
        thumbPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.height = h;
        this.width = w;

        int thrustBorder = h/10;
        this.minThrustHeight = thrustBorder;
        this.maxThrustHeight = height - thrustBorder;
        this.thrustInterval = maxThrustHeight - minThrustHeight;

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float thumbPosition = minThrustHeight + thrustLevel * thrustInterval;
        canvas.drawLine(10, thumbPosition, width-10, thumbPosition, thumbPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                changeThumbPosition(event.getY());
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "Thrust = " + thrustLevel);
                break;
        }

        return true;
    }

    private void changeThumbPosition(float y) {
        thrustLevel = y/thrustInterval;
        thrustLevel = thrustLevel < 0 ? 0 : thrustLevel;
        thrustLevel = thrustLevel > 1 ? 1 : thrustLevel;
        if(listener != null) {
            listener.onThrustChanged(1-thrustLevel);
        }

        invalidate();
    }

    public void setListener(OnThrustLevelChangedListener listener) {
        this.listener = listener;
    }

    public interface OnThrustLevelChangedListener {
        void onThrustChanged(float thrustLevel);
    }
}
