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
import android.content.SharedPreferences;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class Settings
{
    private static Settings sInstance = null;
    private SharedPreferences mPref;
    private int mStyle;
    private boolean mHorizonEnabled;
    private boolean mEquatorEnabled;
    private boolean mEclipticEnabled;
    private boolean mAzGridEnabled;
    private boolean mEqGridEnabled;
    private boolean mConstLinesEnabled;
    private boolean mConstBoundEnabled;
    private boolean mConstNamesEnabled;
    private boolean mSolarSystemEnabled;
    private boolean mSolarNamesEnabled;
    private boolean mStarsEnabled;
    private boolean mStarNamesEnabled;
    private int mMaxMagnitude;
    private boolean mGpsEnabled;
    private float mLatitude;
    private float mLongitude;
    private float mLastGpsLatitude;
    private float mLastGpsLongitude;
    private HashMap<String, String> mTranslations = new HashMap<>();
    private String mLanguage;
    private int mConstLanguage;
    private GregorianCalendar mCurrentTime;
    private boolean mToolbarIsVisible = true;
    private boolean mKeepScreenOn;
    private boolean mAutoUpdate;
    private ChartObject[] mNearbyObjects = null;
    private boolean mOnlyVisiblePlanets;
    private float mFontScale;
    private static final int LANG_SYSDEFAULT_INTERNAL = 9999; // stored in SharedPreferences
    private static final int LANG_SYSDEFAULT_IDX = 9;

    public static Settings instance() throws NullPointerException
    {
        if (sInstance == null)
        {
            throw new NullPointerException("run init() before instance()");
        }
        return sInstance;
    }

    public synchronized static void init(Context context)
    {
        if (context == null)
        {
            throw new NullPointerException("context must not be null");
        }
        else if (sInstance == null)
        {
            sInstance = new Settings(context);
        }
    }

    private Settings(Context context)
    {
        mPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        mStyle = mPref.getInt("style", R.style.AppThemeLight);
        mHorizonEnabled = mPref.getBoolean("horizon-enabled", true);
        mEquatorEnabled = mPref.getBoolean("equator-enabled", false);
        mEclipticEnabled = mPref.getBoolean("ecliptic-enabled", true);
        mAzGridEnabled = mPref.getBoolean("azGrid-enabled", true);
        mEqGridEnabled = mPref.getBoolean("eqGrid-enabled", false);
        mConstLinesEnabled = mPref.getBoolean("constLines-enabled", true);
        mConstBoundEnabled = mPref.getBoolean("constBound-enabled", false);
        mConstNamesEnabled = mPref.getBoolean("constNames-enabled", true);
        mSolarSystemEnabled = mPref.getBoolean("solarSystem-enabled", true);
        mSolarNamesEnabled = mPref.getBoolean("solarNames-enabled", true);
        mStarsEnabled = mPref.getBoolean("stars-enabled", true);
        mStarNamesEnabled = mPref.getBoolean("starNames-enabled", false);
        mMaxMagnitude = mPref.getInt("magnitude-max", 6);
        mGpsEnabled = mPref.getBoolean("gps-enabled", true);
        mLatitude = mPref.getFloat("latitude", 51.31f);
        mLongitude = mPref.getFloat("longitude", 9.49f);
        mLastGpsLatitude = mPref.getFloat("gps-latitude", 51.31f);
        mLastGpsLongitude = mPref.getFloat("gps-longitude", 9.49f);
        mConstLanguage = mPref.getInt("constLanguage", LANG_SYSDEFAULT_INTERNAL);
        mKeepScreenOn = mPref.getBoolean("keepScreenOn", false);
        mAutoUpdate = mPref.getBoolean("autoUpdate", true);
        mOnlyVisiblePlanets = mPref.getBoolean("onlyVisiblePlanets", false);
        mFontScale = mPref.getFloat("fontScale", 1.0f);

        String storedVersion = mPref.getString("version", "");
        String currentVersion = BuildConfig.VERSION_NAME;
        if (!storedVersion.equals(currentVersion))
        {
        }

        mTranslations.put(Mercury.sName, context.getString(R.string.planet_mercury));
        mTranslations.put(Venus.sName, context.getString(R.string.planet_venus));
        mTranslations.put(Earth.sName, context.getString(R.string.planet_earth));
        mTranslations.put(Mars.sName, context.getString(R.string.planet_mars));
        mTranslations.put(Jupiter.sName, context.getString(R.string.planet_jupiter));
        mTranslations.put(Saturn.sName, context.getString(R.string.planet_saturn));
        mTranslations.put(Uranus.sName, context.getString(R.string.planet_uranus));
        mTranslations.put(Neptune.sName, context.getString(R.string.planet_neptune));
        mTranslations.put("Sun", context.getString(R.string.sun));
        mTranslations.put("Moon", context.getString(R.string.moon));

        mLanguage = context.getResources().getConfiguration().locale.getLanguage();
        mCurrentTime = new GregorianCalendar();
    }

    private void store()
    {
        SharedPreferences.Editor spe = mPref.edit();
        spe.putInt("style", mStyle);
        spe.putBoolean("horizon-enabled", mHorizonEnabled);
        spe.putBoolean("equator-enabled", mEquatorEnabled);
        spe.putBoolean("ecliptic-enabled", mEclipticEnabled);
        spe.putBoolean("azGrid-enabled", mAzGridEnabled);
        spe.putBoolean("eqGrid-enabled", mEqGridEnabled);
        spe.putBoolean("constLines-enabled", mConstLinesEnabled);
        spe.putBoolean("constBound-enabled", mConstBoundEnabled);
        spe.putBoolean("constNames-enabled", mConstNamesEnabled);
        spe.putBoolean("solarSystem-enabled", mSolarSystemEnabled);
        spe.putBoolean("solarNames-enabled", mSolarNamesEnabled);
        spe.putBoolean("stars-enabled", mStarsEnabled);
        spe.putBoolean("starNames-enabled", mStarNamesEnabled);
        spe.putInt("magnitude-max", mMaxMagnitude);
        spe.putBoolean("gps-enabled", mGpsEnabled);
        spe.putFloat("latitude", mLatitude);
        spe.putFloat("longitude", mLongitude);
        spe.putFloat("gps-latitude", mLastGpsLatitude);
        spe.putFloat("gps-longitude", mLastGpsLongitude);
        spe.putInt("constLanguage", mConstLanguage);
        spe.putBoolean("keepScreenOn", mKeepScreenOn);
        spe.putBoolean("autoUpdate", mAutoUpdate);
        spe.putBoolean("onlyVisiblePlanets", mOnlyVisiblePlanets);
        spe.putFloat("fontScale", mFontScale);
        spe.putString("version", BuildConfig.VERSION_NAME);
        spe.apply();
    }

    public int getStyle()
    {
        return mStyle;
    }

    public int getStyleIndex()
    {
        if (mStyle == R.style.AppThemeLight)
        {
            return 0;
        }
        else if (mStyle == R.style.AppThemeDark)
        {
            return 1;
        }
        else if (mStyle == R.style.AppThemeNight)
        {
            return 2;
        }
        return 0;
    }

    public void setStyle(int style)
    {
        mStyle = style;
        store();
    }

    public void setStyleIndex(int styleIndex)
    {
        if (styleIndex == 0)
        {
            mStyle = R.style.AppThemeLight;
        }
        else if (styleIndex == 1)
        {
            mStyle = R.style.AppThemeDark;
        }
        else if (styleIndex == 2)
        {
            mStyle = R.style.AppThemeNight;
        }
        store();
    }

    public boolean isHorizonEnabled()
    {
        return mHorizonEnabled;
    }

    public void setHorizonEnabled(boolean horizonEnabled)
    {
        mHorizonEnabled = horizonEnabled;
        store();
    }

    public boolean isEquatorEnabled()
    {
        return mEquatorEnabled;
    }

    public void setEquatorEnabled(boolean equatorEnabled)
    {
        mEquatorEnabled = equatorEnabled;
        store();
    }

    public boolean isEclipticEnabled()
    {
        return mEclipticEnabled;
    }

    public void setEclipticEnabled(boolean eclipticEnabled)
    {
        mEclipticEnabled = eclipticEnabled;
        store();
    }

    public boolean isAzGridEnabled()
    {
        return mAzGridEnabled;
    }

    public void setAzGridEnabled(boolean azGridEnabled)
    {
        mAzGridEnabled = azGridEnabled;
        store();
    }

    public boolean isEqGridEnabled()
    {
        return mEqGridEnabled;
    }

    public void setEqGridEnabled(boolean eqGridEnabled)
    {
        mEqGridEnabled = eqGridEnabled;
        store();
    }

    public boolean isConstLinesEnabled()
    {
        return mConstLinesEnabled;
    }

    public void setConstLinesEnabled(boolean constLinesEnabled)
    {
        mConstLinesEnabled = constLinesEnabled;
        store();
    }

    public boolean isConstBoundEnabled()
    {
        return mConstBoundEnabled;
    }

    public void setConstBoundEnabled(boolean constBoundEnabled)
    {
        mConstBoundEnabled = constBoundEnabled;
        store();
    }

    public boolean isConstNamesEnabled()
    {
        return mConstNamesEnabled;
    }

    public void setConstNamesEnabled(boolean constNamesEnabled)
    {
        mConstNamesEnabled = constNamesEnabled;
        store();
    }

    public boolean isSolarSystemEnabled()
    {
        return mSolarSystemEnabled;
    }

    public void setSolarSystemEnabled(boolean solarSystemEnabled)
    {
        mSolarSystemEnabled = solarSystemEnabled;
        store();
    }

    public boolean isSolarNamesEnabled()
    {
        return mSolarNamesEnabled;
    }

    public void setSolarNamesEnabled(boolean solarNamesEnabled)
    {
        mSolarNamesEnabled = solarNamesEnabled;
        store();
    }

    public boolean isStarsEnabled()
    {
        return mStarsEnabled;
    }

    public void setStarsEnabled(boolean starsEnabled)
    {
        mStarsEnabled = starsEnabled;
        store();
    }

    public int getMaxMagnitude()
    {
        return mMaxMagnitude;
    }

    public void setMaxMagnitude(int maxMagnitude)
    {
        mMaxMagnitude = maxMagnitude;
        store();
    }

    public boolean isGpsEnabled()
    {
        return mGpsEnabled;
    }

    public void setGpsEnabled(boolean gpsEnabled)
    {
        mGpsEnabled = gpsEnabled;
        store();
    }

    public float getLatitude()
    {
        return mLatitude;
    }

    public void setLatitude(float latitude)
    {
        mLatitude = latitude;
        store();
    }

    public float getLongitude()
    {
        return mLongitude;
    }

    public void setLongitude(float longitude)
    {
        mLongitude = longitude;
        store();
    }

    public int getConstLanguage()
    {
        if (mConstLanguage == LANG_SYSDEFAULT_INTERNAL)
        {
            return LANG_SYSDEFAULT_IDX;
        }
        else
        {
            return mConstLanguage;
        }
    }

    public void setConstLanguage(int constLanguage)
    {
        if (constLanguage == LANG_SYSDEFAULT_IDX)
        {
            mConstLanguage = LANG_SYSDEFAULT_INTERNAL;
        }
        else
        {
            mConstLanguage = constLanguage;
        }
        store();
    }

    public String translateName(String s)
    {
        return mTranslations.get(s);
    }

    public String getLanguage()
    {
        return mLanguage;
    }

    public GregorianCalendar getCurrentTime()
    {
        return mCurrentTime;
    }

    public void setCurrentTime(GregorianCalendar currentTime)
    {
        mCurrentTime = currentTime;
    }

    public boolean getToolbarIsVisible()
    {
        return mToolbarIsVisible;
    }

    public void toggleToolbarIsVisible()
    {
        mToolbarIsVisible = !mToolbarIsVisible;
    }

    public boolean getKeepScreenOn()
    {
        return mKeepScreenOn;
    }

    public void setKeepScreenOn(boolean keepScreenOn)
    {
        mKeepScreenOn = keepScreenOn;
        store();
    }

    public boolean getAutoUpdate()
    {
        return mAutoUpdate;
    }

    public void setAutoUpdate(boolean enabled)
    {
        mAutoUpdate = enabled;
        store();
    }

    public ChartObject[] getNearbyObjects()
    {
        return mNearbyObjects;
    }

    public void setNearbyObjects(ChartObject[] objects)
    {
        mNearbyObjects = objects;
    }

    public boolean getOnlyVisiblePlanets()
    {
        return mOnlyVisiblePlanets;
    }

    public void setOnlyVisiblePlanets(boolean onlyVisiblePlanets)
    {
        mOnlyVisiblePlanets = onlyVisiblePlanets;
        store();
    }

    public void setLastGpsLatLon(float latitude, float longitude)
    {
        mLastGpsLatitude = latitude;
        mLastGpsLongitude = longitude;
        store();
    }

    public float getLastGpsLatitude()
    {
        return mLastGpsLatitude;
    }

    public float getLastGpsLongitude()
    {
        return mLastGpsLongitude;
    }

    public float getFontScale()
    {
        return mFontScale;
    }

    public void setFontScale(float fontScale)
    {
        mFontScale = fontScale;
        store();
    }

    public boolean isStarNamesEnabled()
    {
        return mStarNamesEnabled;
    }

    public void setStarNamesEnabled(boolean starNamesEnabled)
    {
        mStarNamesEnabled = starNamesEnabled;
        store();
    }
}
