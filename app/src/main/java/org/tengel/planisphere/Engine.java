package org.tengel.planisphere;

import android.app.Activity;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

public class Engine {
    private Catalog mCatalog;
    private Vector<ChartObject> mObjects = new Vector<ChartObject>();
    private Activity mActivity;
    private Settings mSettings;
    private double mLatitude;
    private double mLongitude;
    private boolean mIsGpsPos;
    private GregorianCalendar mTime;
    private double mLocalSiderealTime;
    private ConstellationDb mConstDb;


    public Engine(Activity activity, Settings settings, Catalog catalog,
                  ConstellationDb constDb)
    {
        mActivity = activity;
        mSettings = settings;
        mCatalog = catalog;
        mConstDb = constDb;
    }

    public void setLocation(double lat, double lon, boolean isGpsPos)
    {
        mLatitude = lat;
        mLongitude = lon;
        mIsGpsPos = isGpsPos;
    }

    public void setTime(GregorianCalendar c)
    {
        mTime = c;
    }

    public GregorianCalendar getTime()
    {
        return mTime;
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
        double utcHour = mTime.get(Calendar.HOUR_OF_DAY) +
                         (mTime.get(Calendar.MINUTE) / 60.0) +
                         (mTime.get(Calendar.SECOND) / 60.0 / 60.0) -
                         (mTime.get(Calendar.DST_OFFSET) / 1000.0 / 60.0 / 60.0) -
                         (mTime.get(Calendar.ZONE_OFFSET) / 1000.0 / 60.0 / 60.0);
        double siderealTime = Astro.sidereal_time(mTime.get(Calendar.YEAR), mTime.get(Calendar.MONTH) + 1,
                                                  mTime.get(Calendar.DAY_OF_MONTH), utcHour);
        mLocalSiderealTime = siderealTime + (mLongitude / 15.0); // in h
        mObjects.clear();
        int maxMagnitude = Settings.instance().getMaxMagnitude();
        ConstBoundaries boundaries = null;

        if (mSettings.isConstLinesEnabled() || mSettings.isConstNamesEnabled() ||
            mSettings.isConstBoundEnabled())
        {
            boundaries = new ConstBoundaries(this, mConstDb, mSettings.isConstBoundEnabled());
            mObjects.add(boundaries);
        }
        if (mSettings.isConstLinesEnabled() || mSettings.isConstNamesEnabled())
        {
            mObjects.add(new ConstLines(this, mConstDb, boundaries, mSettings.isConstLinesEnabled(),
                                        mSettings.isConstNamesEnabled()));
        }
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
        String s = String.format("%04d-%02d-%02d  %02d:%02d  %s  %.2f; %.2f GPS: %d  Mag: %d",
                mTime.get(Calendar.YEAR), (mTime.get(Calendar.MONTH) + 1),
                mTime.get(Calendar.DAY_OF_MONTH), mTime.get(Calendar.HOUR_OF_DAY),
                mTime.get(Calendar.MINUTE), mTime.getTimeZone().getID(),
                mLatitude, mLongitude, mIsGpsPos? 1 : 0, maxMagnitude);
        mObjects.add(new InfoText(this, s));
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
