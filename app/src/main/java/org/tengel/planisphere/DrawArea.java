package org.tengel.planisphere;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.Vector;

public class DrawArea extends View
{
    private Vector<ChartObject> mObjects;
    private int mSize;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;
    private double mScaleFactor = 1.f;
    private double mScrollOffsetX = 0;
    private double mScrollOffsetY = 0;

    private static int BORDER = 10;

    public DrawArea(Context context)
    {
        super(context);
        init(null, 0);
    }

    public DrawArea(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs, 0);
    }

    public DrawArea(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle)
    {
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mGestureDetector = new GestureDetector(getContext(), new GestureListener());

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.DrawArea, defStyle, 0);

        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        mSize = Math.min(getWidth(), getHeight()) - (2 * BORDER);
        if (mObjects != null)
        {
            for (ChartObject co : mObjects)
            {
                co.draw(this, canvas);
            }
        }
    }

    public void setObjects(Vector<ChartObject> objects)
    {
        mObjects = objects;
    }

    /**
     * Convert horizontal coordinates to paint area
     * Returns (x, y)
     */
    public int[] horizontal2area(double azimuth, double elevation)
    {
        int contentWidth = getWidth();
        int contentHeight = getHeight();
        double hpixel = (mSize * mScaleFactor) / 2.0 / 90.0;
        double xoff = Math.sin(Math.toRadians(azimuth)) * (90 - elevation) * hpixel;
        double yoff = Math.cos(Math.toRadians(azimuth)) * (90 - elevation) * hpixel;
        int x = (int) Math.round((contentWidth / 2.0) + xoff - mScrollOffsetX);
        int y = (int) Math.round((contentHeight / 2.0) + yoff - mScrollOffsetY);
        int[] rc = {x, y};
        return rc;
    }

    public int size()
    {
        return mSize;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        boolean result = mScaleDetector.onTouchEvent(ev);
        result = mGestureDetector.onTouchEvent(ev) || result;
        return result || super.onTouchEvent(ev);
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
    {
        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(1.0, Math.min(mScaleFactor, 10.0));
            invalidate();
            return true;
        }
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onDown(MotionEvent e)
        {
            //Log.d("SimpleOngestureListener", "onDown");
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e)
        {
            //Log.d("SimpleOngestureListener", "onDoubleTap");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY)
        {
            mScrollOffsetX += distanceX;
            mScrollOffsetX = Math.max((mSize / -2) * mScaleFactor,
                                      Math.min(mScrollOffsetX, (mSize / 2) * mScaleFactor));
            mScrollOffsetY += distanceY;
            mScrollOffsetY = Math.max((mSize / -2) * mScaleFactor,
                                      Math.min(mScrollOffsetY, (mSize / 2) * mScaleFactor));
            invalidate();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            //Log.d("SimpleOngestureListener", "onFling");
            return true;
        }
    }

}
