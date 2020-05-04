package org.tengel.planisphere;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings
{
    private static Settings sInstance = null;
    private Context mContext;
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
    private int mMaxMagnitude;

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
        mContext = context;
        mPref = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
        mStyle = mPref.getInt("style", R.style.AppThemeLight);
        mHorizonEnabled = mPref.getBoolean("horizon-enabled", true);
        mEquatorEnabled = mPref.getBoolean("equator-enabled", true);
        mEclipticEnabled = mPref.getBoolean("ecliptic-enabled", true);
        mAzGridEnabled = mPref.getBoolean("azGrid-enabled", true);
        mEqGridEnabled = mPref.getBoolean("eqGrid-enabled", true);
        mConstLinesEnabled = mPref.getBoolean("constLines-enabled", true);
        mConstBoundEnabled = mPref.getBoolean("constBound-enabled", true);
        mConstNamesEnabled = mPref.getBoolean("constNames-enabled", true);
        mSolarSystemEnabled = mPref.getBoolean("solarSystem-enabled", true);
        mSolarNamesEnabled = mPref.getBoolean("solarNames-enabled", true);
        mStarsEnabled = mPref.getBoolean("stars-enabled", true);
        mMaxMagnitude = mPref.getInt("magnitude-max", 8);
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
        spe.putInt("magnitude-max", mMaxMagnitude);
        spe.commit();
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
}
