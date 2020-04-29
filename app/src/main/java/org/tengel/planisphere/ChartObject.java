package org.tengel.planisphere;

import android.graphics.Canvas;
import android.graphics.Paint;

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
    }
}


class AzGrid extends ChartObject
{
    public static int sColor;
    public static int sTextColor;

    AzGrid(Engine e)
    {
        super(e);
        mPaint.setColor(sColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaintText.setColor(sTextColor);
    }

    @Override
    public void draw(DrawArea da, Canvas canvas)
    {
        int sp1 = da.size() / 40;
        int sp2 = da.size() / 150;
        int[] center = da.horizontal2area(0, 90);
        int[] south = da.horizontal2area(0, 0);
        int[] west = da.horizontal2area(90, 0);
        int[] north = da.horizontal2area(180, 0);
        int[] east = da.horizontal2area(270, 0);
        int[] l30deg = da.horizontal2area(0, 30);
        int[] l60deg = da.horizontal2area(0, 60);
        canvas.drawLine(south[0], south[1], north[0], north[1], mPaint);
        canvas.drawLine(east[0], east[1], west[0], west[1], mPaint);
        canvas.drawCircle(center[0], center[1], (south[1] - center[1]), mPaint);
        canvas.drawCircle(center[0], center[1], (l60deg[1] - center[1]), mPaint);
        canvas.drawCircle(center[0], center[1], (l30deg[1] - center[1]), mPaint);
        canvas.drawText("S", south[0] - sp2, south[1] + sp1, mPaintText);
        canvas.drawText("W", west[0] + sp2, west[1] + sp2, mPaintText);
        canvas.drawText("N", north[0] - sp2, north[1] - sp2, mPaintText);
        canvas.drawText("E", east[0] - sp1, east[1] + sp2, mPaintText);
        canvas.drawText("0°", south[0] + sp2, south[1] - sp2, mPaintText);
        canvas.drawText("30°", l30deg[0] + sp2, l30deg[1] - sp2, mPaintText);
        canvas.drawText("60°", l60deg[0] + sp2, l60deg[1] - sp2, mPaintText);
    }
}

class Star extends ChartObject
{
    private double[] mAzEle;
    private Catalog.Entry mEntry;
    public static int sColor;

    public Star(Engine engine, Catalog.Entry ce)
    {
        super(engine);
        mEntry = ce;
        mAzEle = mEngine.equatorial2horizontal(ce.rightAscension, ce.declination);
        mPaint.setColor(sColor);
    }

    @Override
    public void draw(DrawArea da, Canvas canvas) {
        int[] center = da.horizontal2area(mAzEle[0], mAzEle[1]);
        canvas.drawCircle(center[0], center[1], 1, mPaint);
    }
}

//class EqGrid implements ChartObject
//{
//    @Override
//    public void draw(Canvas canvas) {
//
//    }
//}
//
//class Ecliptic implements ChartObject
//{
//    @Override
//    public void draw(Canvas canvas) {
//
//    }
//}
//
//class Planet implements ChartObject
//{
//    @Override
//    public void draw(Canvas canvas) {
//
//    }
//}
//
//class Sun implements ChartObject
//{
//    @Override
//    public void draw(Canvas canvas) {
//
//    }
//}
