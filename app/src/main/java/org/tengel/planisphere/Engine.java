package org.tengel.planisphere;

import android.app.Activity;
import android.util.Log;
import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;

public class Engine {
    private Catalog mCatalog;
    private boolean isEnabledAzGrid = true;
    private boolean isEnabledEqGrid = true;
    private boolean isEnabledEcl = true;
    private Vector<ChartObject> mObjects = new Vector<ChartObject>();
    private Activity mActivity;
    private double mLatitude;
    private double mLongitude;
    private Calendar mTime;
    private double mMaxMagnitude = 11;
    private double mLocalSiderealTime;


    public Engine(Activity activity) throws IOException
    {
        mActivity = activity;
        mCatalog = new Catalog(mActivity.getResources().openRawResource(R.raw.bs_catalog));
    }

    public void setLocation(double lat, double lon)
    {
        mLatitude = lat;
        mLongitude = lon;
    }

    public void setTime(Calendar c)
    {
        mTime = c;
    }

    /**
     * Return azimut, elevation
     */
    public double[] equatorial2horizontal(double rightAscension, double declination)
    {
        double hourAngle = (mLocalSiderealTime - rightAscension) * 15.0;
        return Astro.geoEqua2geoHori(hourAngle, mLatitude, declination);
    }

    public void update()
    {
        //TODO: convert to UTC
        double utcHour = mTime.get(Calendar.HOUR_OF_DAY) + (mTime.get(Calendar.MINUTE) / 60.0);
        double siderealTime = Astro.sidereal_time(mTime.get(Calendar.YEAR), mTime.get(Calendar.MONTH) + 1,
                                                  mTime.get(Calendar.DAY_OF_MONTH), utcHour);
        mLocalSiderealTime = siderealTime + (mLongitude / 15.0); // in h
        mObjects.clear();
        if (isEnabledAzGrid)
        {
            mObjects.add(new AzGrid(this));
        }
        for (Catalog.Entry e : mCatalog.get())
        {
            if (e.apparentMagnitude > mMaxMagnitude)
            {
                continue;
            }
            mObjects.add(new Star(this, e));
        }
    }

    public Vector<ChartObject> getObjects()
    {
        return mObjects;
    }

}
