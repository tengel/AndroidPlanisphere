package org.tengel.planisphere;

import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.ArrayList;
import java.util.Iterator;


interface ChartObjectInterface
{
    public void draw(DrawArea da, Canvas canvas);
}

abstract class ChartObject implements ChartObjectInterface
{
    protected Paint mPaint = new Paint();
    protected Paint mPaintText = new Paint();
    protected Engine mEngine;

    ChartObject(Engine e)
    {
        mEngine = e;
        mPaintText.setTextAlign(Paint.Align.CENTER);
    }
}

//-----------------------------------------------------------------------------

class GridObject extends ChartObject
{
    protected ArrayList<Double[]> mCoords = new ArrayList<Double[]>();
    protected ArrayList<Double[]> mTextCoords = new ArrayList<Double[]>();
    protected ArrayList<String> mTexts = new ArrayList<String>();

    public GridObject(Engine e)
    {
        super(e);
    }

    @Override
    public void draw(DrawArea da, Canvas canvas)
    {
        float[] lines = new float[mCoords.size() * 2];
        Iterator<Double[]> iterator = mCoords.iterator();
        int[] pxy;
        for (int i = 0; i < lines.length/2; i++)
        {
            pxy = da.horizontal2area(iterator.next());
            lines[i*2] = pxy[0];
            lines[i*2+1] = pxy[1];
        }
        canvas.drawLines(lines, mPaint);

        iterator = mTextCoords.iterator();
        Iterator<String> textIter = mTexts.iterator();
        while(iterator.hasNext() && textIter.hasNext())
        {
            pxy = da.horizontal2area(iterator.next());
            canvas.drawText(textIter.next(), pxy[0], pxy[1], mPaintText);
        }
    }
}

//-----------------------------------------------------------------------------

class AzGrid extends ChartObject
{
    public static int sColor;
    private float mAlignY;

    public AzGrid(Engine e)
    {
        super(e);
        mPaint.setColor(sColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaintText.setColor(sColor);
        mPaintText.setTextSize(e.getActivity().getResources().getDimension(R.dimen.textsize));
        mAlignY = (mPaintText.ascent() + mPaintText.descent()) / 2;
    }

    @Override
    public void draw(DrawArea da, Canvas canvas)
    {
        int[] center  = da.horizontal2area(0, 90);
        int[] pxy, pxyText;
        for (int az = 0; az < 360; az+=45)
        {
            pxy  = da.horizontal2area(az, 0);
            canvas.drawLine(center[0], center[1], pxy[0], pxy[1], mPaint);
            canvas.drawText(Integer.valueOf(az) + "°", pxy[0], pxy[1] - mAlignY,
                            mPaintText);
        }
        for (int ele = 0; ele < 90; ele+=30)
        {
            pxy  = da.horizontal2area(0, ele);
            pxyText  = da.horizontal2area(22.5, ele);
            canvas.drawCircle(center[0], center[1], (pxy[1] - center[1]), mPaint);
            canvas.drawText(Integer.valueOf(ele) + "°", pxyText[0], pxyText[1] - mAlignY, mPaintText);
        }
    }
}

//-----------------------------------------------------------------------------

class Star extends ChartObject
{
    public static int sColor;
    private Double[] mAzEle;
    private Catalog.Entry mEntry;
    private float mBaseSize;

    public Star(Engine engine, Catalog.Entry ce)
    {
        super(engine);
        mEntry = ce;
        mAzEle = mEngine.equatorial2horizontal(ce.rightAscension, ce.declination);
        mPaint.setColor(sColor);
        mBaseSize = engine.getActivity().getResources().getDimension(R.dimen.starsize);
    }

    @Override
    public void draw(DrawArea da, Canvas canvas)
    {
        int[] center = da.horizontal2area(mAzEle[0], mAzEle[1]);
        if (mEntry.apparentMagnitude <= 1.0)
        {
            canvas.drawCircle(center[0], center[1], 3 * mBaseSize, mPaint); // 0 - 1
        }
        else if (mEntry.apparentMagnitude > 1.0 && mEntry.apparentMagnitude <= 3.0)
        {
            canvas.drawCircle(center[0], center[1], 2 * mBaseSize, mPaint); // 1 - 3
        }
        else if (mEntry.apparentMagnitude > 3.0 && mEntry.apparentMagnitude <= 5.0)
        {
            canvas.drawCircle(center[0], center[1], 1 * mBaseSize, mPaint); // 3 - 5
        }
        else
        {
            canvas.drawPoint(center[0], center[1], mPaint); // >5-9
        }
    }
}

//-----------------------------------------------------------------------------

class EqGrid extends GridObject
{
    public static int sColor;

    public EqGrid(Engine e)
    {
        super(e);
        mPaint.setColor(sColor);
        mPaintText.setColor(sColor);
        mPaintText.setTextSize(e.getActivity().getResources().getDimension(R.dimen.textsize));
        for (int dec = -30; dec < 90; dec+=30)
        {
            for (int ra = 0; ra < 24; ra++)
            {
                mCoords.add(mEngine.equatorial2horizontal(ra, dec));
                mCoords.add(mEngine.equatorial2horizontal(ra + 1, dec));
            }
            mTextCoords.add(mEngine.equatorial2horizontal(1, dec));
            mTexts.add(Integer.valueOf(dec) + "°");
        }
        for ( int ra = 0; ra < 24; ra+=2)
        {
            for (int dec = -30; dec <90; dec+=10)
            {
                mCoords.add(mEngine.equatorial2horizontal(ra, dec));
                mCoords.add(mEngine.equatorial2horizontal(ra, dec+10));
            }
            mTextCoords.add(mEngine.equatorial2horizontal(ra, -30));
            mTexts.add(Integer.valueOf(ra) + " h");
        }

    }
}

//-----------------------------------------------------------------------------

class Horizon extends ChartObject
{
    public static int sColor = 0;
    private float mAlignY;
    private float mH;

    public Horizon(Engine e)
    {
        super(e);
        mPaint.setColor(sColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaintText.setColor(sColor);
        mPaintText.setTextSize(e.getActivity().getResources().getDimension(R.dimen.textsize));
        mH = mPaintText.ascent() + mPaintText.descent();
        mAlignY = mH / 2;
    }

    @Override
    public void draw(DrawArea da, Canvas canvas)
    {
        int[] center = da.horizontal2area(0, 90);
        int[] south = da.horizontal2area(0, 0);
        canvas.drawCircle(center[0], center[1], (south[1] - center[1]), mPaint);
        int[] west  = da.horizontal2area(90, 0);
        int[] north = da.horizontal2area(180, 0);
        int[] east  = da.horizontal2area(270, 0);
        canvas.drawText("S", south[0], south[1] - mH * 2, mPaintText);
        canvas.drawText("W", west[0] - mH * 2, west[1] - mAlignY, mPaintText);
        canvas.drawText("N", north[0], north[1] + mH, mPaintText);
        canvas.drawText("E", east[0] + mH * 2, east[1] - mAlignY, mPaintText);
    }
}

//-----------------------------------------------------------------------------

class Equator extends GridObject
{
    public static int sColor;

    public Equator(Engine e)
    {
        super(e);
        mPaint.setColor(sColor);
        for (int ra = 0; ra < 24; ra++)
        {
            mCoords.add(mEngine.equatorial2horizontal(ra, 0));
            mCoords.add(mEngine.equatorial2horizontal(ra + 1, 0));
        }
    }
}

//-----------------------------------------------------------------------------

class Ecliptic extends GridObject
{
    public static int sColor;

    public Ecliptic(Engine e)
    {
        super(e);
        mPaint.setColor(sColor);
        mPaintText.setColor(sColor);
        double[] raDec;
        for (int lon=0; lon<360; lon+=10)
        {
            raDec = Astro.geoEcl2geoEqua(0.0, lon);
            mCoords.add(mEngine.equatorial2horizontal(raDec[0] / 15, raDec[1]));
            raDec = Astro.geoEcl2geoEqua(0.0, lon + 10);
            mCoords.add(mEngine.equatorial2horizontal(raDec[0] / 15, raDec[1]));
        }
    }
}

//-----------------------------------------------------------------------------

class Planet extends ChartObject
{
    public Planet(Engine e, Object planet)
    {
        super(e);
    }

    @Override
    public void draw(DrawArea da, Canvas canvas)
    {

    }
}

//-----------------------------------------------------------------------------

class Sun extends ChartObject
{
    public Sun(Engine e)
    {
        super(e);
    }

    @Override
    public void draw(DrawArea da, Canvas canvas)
    {

    }
}

//-----------------------------------------------------------------------------
