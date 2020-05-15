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
        int[] rc = {x, y};
        return rc;
    }

    public int[] horizontal2area(Double[] azEle)
    {
        return horizontal2area(azEle[0], azEle[1]);
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
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            Settings.instance().toggleToolbarIsVisible();
            handleToolbarVisibility();
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e)
        {
            //Log.d(MainActivity.LOG_TAG, "onLongPress");
            super.onLongPress(e);
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
            mScrollOffsetX += distanceX;
            mScrollOffsetX = Math.max((mSize / -2) * mScaleFactor,
                                      Math.min(mScrollOffsetX, (mSize / 2) * mScaleFactor));
            mScrollOffsetY += distanceY;
            mScrollOffsetY = Math.max((mSize / -2) * mScaleFactor,
                                      Math.min(mScrollOffsetY, (mSize / 2) * mScaleFactor));
            invalidate();
            return true;
        }
    }

}
