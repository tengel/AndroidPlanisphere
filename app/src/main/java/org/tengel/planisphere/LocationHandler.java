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
import androidx.core.content.ContextCompat;

public class LocationHandler
{
    private static LocationHandler sInstance = null;
    private LocationListener mLocListener;
    private android.location.LocationManager mLocManager = null;
    private double mLatitude;
    private double mLongitude;
    private boolean mIsGpsPosition;


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
            if (Settings.instance().isGpsEnabled())
            {
                sInstance.enableGps(mainActivity);
            }
        }
    }

    private LocationHandler()
    {
        mIsGpsPosition = false;
        mLatitude = Settings.instance().getLatitude();
        mLongitude = Settings.instance().getLongitude();
    }

    public void enableGps(MainActivity context)
    {
        mIsGpsPosition = false;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            Toast toast = Toast.makeText(context, R.string.gps_no_permission,
                                         Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        mLocListener = new LocationListener(context);
        mLocManager = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = mLocManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        if (!isGpsEnabled)
        {
            Toast toast = Toast.makeText(context, R.string.gps_disabled, Toast.LENGTH_LONG);
            toast.show();
        }
        mLocManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER,
                                           1200000, 10000, mLocListener);
                                           // 20 min , 10 km
        Location loc = mLocManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);
        if (loc != null)
        {
            Toast toast = Toast.makeText(context, R.string.gps_received, Toast.LENGTH_LONG);
            toast.show();
            mLatitude = loc.getLatitude();
            mLongitude = loc.getLongitude();
            mIsGpsPosition = true;
        }
        else if (isGpsEnabled)
        {
            Toast toast = Toast.makeText(context, R.string.gps_waiting, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void disableGps(double settingsLatitude, double settingsLongitude)
    {
        if (mLocManager != null)
        {
            mLocManager.removeUpdates(mLocListener);
        }
        mLatitude = settingsLatitude;
        mLongitude = settingsLongitude;
        mIsGpsPosition = false;
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
            if (mMainActivity.isRunning())
            {
                Toast toast = Toast.makeText(mMainActivity, R.string.gps_received_new,
                        Toast.LENGTH_LONG);
                toast.show();
            }
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            mIsGpsPosition = true;
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
