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

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;
import java.util.GregorianCalendar;
import java.util.HashMap;
import androidx.core.content.ContextCompat;

enum GpsStatus {UNKNOWN, NO_PERMISSION, DISABLED, WAITING, RECEIVED}

public class LocationHandler
{
    private static LocationHandler sInstance = null;
    private LocationListener mLocListener = null;
    private android.location.LocationManager mLocManager = null;
    private double mLatitude;
    private double mLongitude;
    private boolean mIsGpsPosition;
    private GpsStatus mStatus;
    private long mGpsRxTime;
    private HashMap<GpsStatus, String> mStatusStrings = new HashMap<>();
    private boolean mStartUp = true;

    public static LocationHandler instance() throws NullPointerException
    {
        if (sInstance == null)
        {
            throw new NullPointerException("run init() before instance()");
        }
        return sInstance;
    }

    public synchronized static void init(MainActivity mainActivity)
    {
        if (mainActivity == null)
        {
            throw new NullPointerException("MainActivity must not be null");
        }
        else if (sInstance == null)
        {
            sInstance = new LocationHandler();
            sInstance.mStatusStrings.put(GpsStatus.UNKNOWN,
                                         mainActivity.getString(R.string.gps_status_unknown));
            sInstance.mStatusStrings.put(GpsStatus.NO_PERMISSION,
                                         mainActivity.getString(R.string.gps_status_nopermission));
            sInstance.mStatusStrings.put(GpsStatus.DISABLED,
                                         mainActivity.getString(R.string.gps_status_disabled));
            sInstance.mStatusStrings.put(GpsStatus.WAITING,
                                         mainActivity.getString(R.string.gps_status_waiting));
            sInstance.mStatusStrings.put(GpsStatus.RECEIVED,
                                         mainActivity.getString(R.string.gps_status_received));
        }
    }

    private LocationHandler()
    {
        mIsGpsPosition = false;
        mStatus = GpsStatus.UNKNOWN;
        mLatitude = Settings.instance().getLatitude();
        mLongitude = Settings.instance().getLongitude();
    }

    public void enableGps(MainActivity context, boolean isRestart)
    {
        mLatitude = Settings.instance().getLastGpsLatitude();
        mLongitude = Settings.instance().getLastGpsLongitude();
        mIsGpsPosition = false;
        boolean showInfo = mStartUp || isRestart;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            mStatus = GpsStatus.NO_PERMISSION;
            if (showInfo)
            {
                Toast toast = Toast.makeText(context, R.string.gps_no_permission,
                                             Toast.LENGTH_LONG);
                toast.show();
            }
            mStartUp = false;
            return;
        }

        mLocManager = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = mLocManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        if (!isGpsEnabled)
        {
            mStatus = GpsStatus.DISABLED;
            if (showInfo)
            {
                Toast toast = Toast.makeText(context, R.string.gps_disabled, Toast.LENGTH_LONG);
                toast.show();
            }
        }
        if (mLocListener == null)
        {
            mLocListener = new LocationListener(context);
            mLocManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER,
                                               1200000, 10000, mLocListener);
        }                                      //20 min, 10 km
        Location loc = mLocManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);
        if (loc != null)
        {
            if (showInfo)
            {
                Toast toast = Toast.makeText(context, R.string.gps_received, Toast.LENGTH_LONG);
                toast.show();
            }
            mLatitude = loc.getLatitude();
            mLongitude = loc.getLongitude();
            mGpsRxTime = loc.getTime();
            mIsGpsPosition = true;
            mStatus = GpsStatus.RECEIVED;
            Settings.instance().setLastGpsLatLon((float) mLatitude, (float) mLongitude);
            context.update();
        }
        else if (isGpsEnabled)
        {
            mStatus = GpsStatus.WAITING;
            if (showInfo)
            {
                Toast toast = Toast.makeText(context, R.string.gps_waiting,
                                             Toast.LENGTH_LONG);
                toast.show();
            }
        }
        mStartUp = false;
    }

    public void pauseGps()
    {
        if (mLocListener != null)
        {
            mLocManager.removeUpdates(mLocListener);
        }
    }

    public void disableGps(double settingsLatitude, double settingsLongitude)
    {
        pauseGps();
        mLocListener = null;
        mLatitude = settingsLatitude;
        mLongitude = settingsLongitude;
        mIsGpsPosition = false;
        mStatus = GpsStatus.UNKNOWN;
        mStartUp = true;
    }

    public double getLatitude()
    {
        return mLatitude;
    }

    public double getLongitude()
    {
        return mLongitude;
    }

    public boolean getIsGpsPosition()
    {
        return mIsGpsPosition;
    }

    public String getStatus()
    {
        return mStatusStrings.get(mStatus);
    }

    public String getGpsTimestamp()
    {
        if (mStatus == GpsStatus.RECEIVED)
        {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeInMillis(mGpsRxTime);
            return Astro.formatCal(gc);
        }
        else
        {
            return "-";
        }
    }

    class LocationListener implements android.location.LocationListener
    {
        private MainActivity mMainActivity;

        public LocationListener(MainActivity mainActivity)
        {
            mMainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location location)
        {
            if (mMainActivity.isRunning() && mStatus != GpsStatus.RECEIVED)
            {
                Toast toast = Toast.makeText(mMainActivity, R.string.gps_received_new,
                        Toast.LENGTH_LONG);
                toast.show();
            }
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            mGpsRxTime = location.getTime();
            Settings.instance().setLastGpsLatLon((float) mLatitude, (float) mLongitude);
            mIsGpsPosition = true;
            mStatus = GpsStatus.RECEIVED;
            mMainActivity.update();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
        }

        @Override
        public void onProviderEnabled(String provider)
        {
        }

        @Override
        public void onProviderDisabled(String provider)
        {
        }
    }
}
