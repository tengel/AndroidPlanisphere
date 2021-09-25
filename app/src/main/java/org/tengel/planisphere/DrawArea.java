/*
 * Copyright (C) 2020 Timo Engel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.tengel.planisphere;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import java.util.Vector;
import androidx.appcompat.app.ActionBar;
import androidx.core.math.MathUtils;

public class DrawArea extends View
{
    private Vector<ChartObject> mObjects;
    private int mSize;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;
    private double mScaleFactor = 1.f;
    private double mScrollOffsetX = 0;
    private double mScrollOffsetY = 0;
    private ActionBar mActionBar;
    private MainActivity mMainActivity;
    private double mScrollMin = 0;
    private double mScrollMax = 0;

    private static final int BORDER = 10;
    private static final double SCALE_FACTOR_MAX = 10.0;

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
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        mSize = Math.min(getWidth(), getHeight()) - (2 * BORDER);
        mScrollMin = (mSize / -2.0) * mScaleFactor;
        mScrollMax = (mSize / 2.0) * mScaleFactor;
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
        invalidate();
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
        return new int[]{x, y};
    }

    public int[] horizontal2area(Double[] azEle)
    {
        return horizontal2area(azEle[0], azEle[1]);
    }

    /**
     * Convert paint area coordinates to horizontal coordinates
     * Returns (azimuth, elevation)
     */
    public double[] area2horizontal(float x, float y)
    {
        int contentWidth = getWidth();
        int contentHeight = getHeight();
        double hpixel = (mSize * mScaleFactor) / 2.0 / 90.0;
        double xoff = x - (contentWidth / 2.0) + mScrollOffsetX;
        double yoff = y - (contentHeight / 2.0) + mScrollOffsetY;
        double hy = Math.sqrt(xoff*xoff + yoff*yoff);
        double az = Math.toDegrees(Math.asin(xoff / hy));
        if (yoff < 0)
        {
            az = 180 - az;
        }
        else if (xoff < 0)
        {
            az = 360 + az;
        }
        double ele = (90 - (hy / hpixel));
        return new double[]{az, ele};
    }

    public int size()
    {
        return mSize;
    }

    public void setActionBar(ActionBar actionBar)
    {
        mActionBar = actionBar;
        handleToolbarVisibility();
    }

    private void handleToolbarVisibility()
    {
        if (Settings.instance().getToolbarIsVisible())
        {
            mActionBar.show();
        }
        else
        {
            mActionBar.hide();
        }
    }

    public void setMainActivity(MainActivity m)
    {
        mMainActivity = m;
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
            double factor = detector.getScaleFactor();
            mScaleFactor = MathUtils.clamp(mScaleFactor * factor,
                                           1.0, SCALE_FACTOR_MAX);
            if (mScaleFactor < SCALE_FACTOR_MAX)
            {
                mScrollOffsetX = mScrollOffsetX * factor;
                mScrollOffsetY = mScrollOffsetY * factor;
            }
            invalidate();
            return true;
        }
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            Settings.instance().toggleToolbarIsVisible();
            handleToolbarVisibility();
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e)
        {
            if (mMainActivity != null)
            {
                mMainActivity.showNearbyObjects(e.getX(), e.getY());
            }
        }

        @Override
        public boolean onDoubleTap(MotionEvent e)
        {
            //Log.d(MainActivity.LOG_TAG, "onDoubleTap");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY)
        {
            mScrollOffsetX = MathUtils.clamp(mScrollOffsetX + distanceX,
                                             mScrollMin, mScrollMax);
            mScrollOffsetY = MathUtils.clamp(mScrollOffsetY + distanceY,
                                             mScrollMin, mScrollMax);
            invalidate();
            return true;
        }
    }

}
