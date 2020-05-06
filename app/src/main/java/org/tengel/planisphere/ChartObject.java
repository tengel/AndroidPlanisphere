package org.tengel.planisphere;

import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


interface ChartObjectInterface
{
    void draw(DrawArea da, Canvas canvas);
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

class InfoText extends ChartObject
{
    public static int sColor = 0;
    private String mText;

    public InfoText(Engine e, String text)
    {
        super(e);
        mText = text;
        mPaintText.setColor(sColor);
        mPaintText.setTextSize(e.getActivity().getResources().getDimension(R.dimen.textsizeSmall));
        mPaintText.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    public void draw(DrawArea da, Canvas canvas)
    {
        canvas.drawText(mText, 0, mPaintText.ascent() * -1, mPaintText);
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

//=============================================================================

class LineObject extends ChartObject
{
    protected ArrayList<ArrayList<Double[]>> mLines = new ArrayList<>();
    protected ArrayList<Double[]> mTextCoords = new ArrayList<Double[]>();
    protected ArrayList<String> mTexts = new ArrayList<String>();

    public LineObject(Engine e)
    {
        super(e);
    }

    @Override
    public void draw(DrawArea da, Canvas canvas)
    {
        for (ArrayList<Double[]> line : mLines)
        {
            drawLine(da, canvas, line);
        }

        int[] pxy;
        Iterator<Double[]> coordIter = mTextCoords.iterator();
        Iterator<String> textIter = mTexts.iterator();
        while(coordIter.hasNext() && textIter.hasNext())
        {
            pxy = da.horizontal2area(coordIter.next());
            canvas.drawText(textIter.next(), pxy[0], pxy[1], mPaintText);
        }
    }

    private void drawLine(DrawArea da, Canvas canvas, ArrayList<Double[]> line)
    {
        int[] pFrom;
        int[] pTo;
        Iterator<Double[]> iterator = line.iterator();
        pFrom = da.horizontal2area(iterator.next());
        while (iterator.hasNext())
        {
            pTo = da.horizontal2area(iterator.next());
            canvas.drawLine(pFrom[0], pFrom[1], pTo[0], pTo[1], mPaint);
            pFrom = pTo;
        }
    }
}

//-----------------------------------------------------------------------------

class EqGrid extends LineObject
{
    public static int sColor;

    public EqGrid(Engine e)
    {
        super(e);
        mPaint.setColor(sColor);
        mPaintText.setColor(sColor);
        mPaintText.setTextSize(e.getActivity().getResources().getDimension(R.dimen.textsize));
        ArrayList<Double[]> line;
        for (int dec = -30; dec < 90; dec+=30)
        {
            line = new ArrayList<>();
            for (int ra = 0; ra <= 24; ++ra)
            {
                line.add(mEngine.equatorial2horizontal(ra, dec));
            }
            mLines.add(line);
            mTextCoords.add(mEngine.equatorial2horizontal(1, dec));
            mTexts.add(Integer.valueOf(dec) + "°");
        }
        for ( int ra = 0; ra < 24; ra+=2)
        {
            line = new ArrayList<>();
            for (int dec = -30; dec <= 90; dec+=10)
            {
                line.add(mEngine.equatorial2horizontal(ra, dec));
            }
            mLines.add(line);
            mTextCoords.add(mEngine.equatorial2horizontal(ra, -30));
            mTexts.add(Integer.valueOf(ra) + " h");
        }
    }
}

//-----------------------------------------------------------------------------

class Equator extends LineObject
{
    public static int sColor;

    public Equator(Engine e)
    {
        super(e);
        mPaint.setColor(sColor);
        ArrayList<Double[]> line = new ArrayList<>();
        for (int ra = 0; ra <= 24; ra++)
        {
            line.add(mEngine.equatorial2horizontal(ra, 0));
        }
        mLines.add(line);
    }
}
//-----------------------------------------------------------------------------

class Ecliptic extends LineObject
{
    public static int sColor;

    public Ecliptic(Engine e)
    {
        super(e);
        mPaint.setColor(sColor);
        mPaintText.setColor(sColor);
        double[] raDec;
        ArrayList<Double[]> line = new ArrayList<>();
        for (int lon = 0; lon <= 360; lon+=10)
        {
            raDec = Astro.geoEcl2geoEqua(0.0, lon);
            line.add(mEngine.equatorial2horizontal(raDec[0] / 15, raDec[1]));
        }
        mLines.add(line);
    }
}

//-----------------------------------------------------------------------------

class ConstLines extends LineObject
{
    public static int sColor;

    public ConstLines(Engine e, ConstellationDb db, ConstBoundaries boundaries,
                      boolean isLinesEnabled, boolean isNamesEnabled)
    {
        super(e);
        mPaint.setColor(sColor);
        mPaintText.setColor(sColor);
        mPaintText.setTextSize(e.getActivity().getResources().getDimension(R.dimen.textsize));
        Double[] azEle;
        boolean isVisible;
        for (ConstellationDb.Constellation constellation : db.get())
        {
            if (!boundaries.isVisible(constellation.mName))
            {
                continue;
            }
            ArrayList<Double[]> constLine = new ArrayList<>();
            for (Catalog.Entry ce : constellation.mLine)
            {
                constLine.add(mEngine.equatorial2horizontal(ce.rightAscension, ce.declination));
            }
            if (isLinesEnabled)
            {
                mLines.add(constLine);
            }
            if (isNamesEnabled)
            {
                mTextCoords.add(constLine.get(1));
                mTexts.add(db.getName(constellation.mName));
            }
        }
    }
}

//-----------------------------------------------------------------------------

class ConstBoundaries extends LineObject
{
    public static int sColor;
    private HashMap<String, Boolean> mVisibility = new HashMap<>();

    public ConstBoundaries(Engine e, ConstellationDb db, boolean isBoundEnabled)
    {
        super(e);
        mPaint.setColor(sColor);
        Double[] azEle;
        Double[] azEleFirst = null;
        boolean isVisible;
        for (ConstellationDb.Constellation constellation : db.get())
        {
            ArrayList<Double[]> boundLine = new ArrayList<>();
            isVisible = false;
            azEleFirst = null;
            for (Double[] raDec : db.getBoundary(constellation.mName))
            {
                azEle = mEngine.equatorial2horizontal(raDec[0], raDec[1]);
                boundLine.add(azEle);
                if (azEleFirst == null)
                {
                    azEleFirst = azEle;
                }
                if (azEle[1] > 0)
                {
                    isVisible = true;
                }
            }
            boundLine.add(azEleFirst);
            if (isVisible && isBoundEnabled)
            {
                mLines.add(boundLine);
            }
            mVisibility.put(constellation.mName, isVisible);
        }
    }

    public boolean isVisible(String name)
    {
        return mVisibility.get(name);
    }
}

