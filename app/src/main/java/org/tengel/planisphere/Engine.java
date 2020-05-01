package org.tengel.planisphere;

import android.app.Activity;
import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;

public class Engine {
    private Catalog mCatalog;
    private Vector<ChartObject> mObjects = new Vector<ChartObject>();
    private Activity mActivity;
    private Settings mSettings;
    private double mLatitude;
    private double mLongitude;
    private Calendar mTime;
    private double mLocalSiderealTime;


    public Engine(Activity activity, Settings settings) throws IOException
    {
        mActivity = activity;
        mSettings = settings;
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
    public Double[] equatorial2horizontal(double rightAscension, double declination)
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
        int maxMagnitude = Settings.instance().getMaxMagnitude();

        if (mSettings.isStarsEnabled())
        {
            for (Catalog.Entry e : mCatalog.get())
            {
                if (e.apparentMagnitude > maxMagnitude)
                {
                    continue;
                }
                mObjects.add(new Star(this, e));
            }
        }
        if (mSettings.isAzGridEnabled())
        {
            mObjects.add(new AzGrid(this));
        }
        if (mSettings.isEqGridEnabled())
        {
            mObjects.add(new EqGrid(this));
        }
        if (mSettings.isHorizonEnabled())
        {
            mObjects.add(new Horizon(this));
        }
        if (mSettings.isEquatorEnabled())
        {
            mObjects.add(new Equator(this));
        }
        if (mSettings.isEclipticEnabled())
        {
            mObjects.add(new Ecliptic(this));
        }

    }

    public Vector<ChartObject> getObjects()
    {
        return mObjects;
    }

    public Activity getActivity()
    {
        return mActivity;
    }

}
