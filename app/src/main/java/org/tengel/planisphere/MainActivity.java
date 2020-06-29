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

import android.app.AlertDialog;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import org.tengel.planisphere.dialog.DisplayOptionsDialog;
import org.tengel.planisphere.dialog.InfoDialog;
import org.tengel.planisphere.dialog.LocationDialog;
import org.tengel.planisphere.dialog.MagnitudeDialog;
import org.tengel.planisphere.dialog.ObjectDetailsDialog;
import org.tengel.planisphere.dialog.ObjectDetailsListener;
import org.tengel.planisphere.dialog.ObjectsNearbyDialog;
import org.tengel.planisphere.dialog.SetLocationListener;
import org.tengel.planisphere.dialog.SetTimeListener;
import org.tengel.planisphere.dialog.SettingsDialog;
import org.tengel.planisphere.dialog.ThemeDialog;
import org.tengel.planisphere.dialog.TimeDialog;
import org.tengel.planisphere.dialog.UpdateListener;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements UpdateListener, SetLocationListener, SetTimeListener,
                   ObjectDetailsListener
{
    public static String LOG_TAG = "Planisphere";
    private static long UPDATE_DELAY_MS = 60000;
    private Engine mEngine;
    private DrawArea mDrawArea;
    private Settings mSettings;
    private ConstellationDb mConstDb;
    private Catalog mCatalog;
    private LocationHandler mLocHandler;
    private boolean mIsRunningUpdateTask = false;
    private ChartObject[] mNearbyObjects = null;
    private boolean mIsRunning = false;
    private Handler mTimerHandler = new Handler(Looper.getMainLooper());
    private Runnable mAutoUpdateTask = new Runnable() {
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            Log.i(LOG_TAG, "running AutoUpdateTask");
            mSettings.setCurrentTime(new GregorianCalendar());
            update();
            mTimerHandler.postDelayed(mAutoUpdateTask, UPDATE_DELAY_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            Settings.init(getApplicationContext());
            mSettings = Settings.instance();

            Catalog.init(getResources().openRawResource(R.raw.bs_catalog));
            mCatalog = Catalog.instance();

            ConstellationDb.init(getResources().openRawResource(R.raw.constellation_lines),
                                 getResources().openRawResource(R.raw.constellation_names),
                                 getResources().openRawResource(R.raw.constellation_boundaries),
                                 mCatalog);
            mConstDb = ConstellationDb.instance();

            PlanetCsv.init(getResources().openRawResource(R.raw.horizons_jupiter),
                           getResources().openRawResource(R.raw.horizons_saturn),
                           getResources().openRawResource(R.raw.horizons_uranus),
                           getResources().openRawResource(R.raw.horizons_neptune));

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                 WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setTheme(mSettings.getStyle());
            mEngine = new Engine(this, mSettings, mCatalog, mConstDb);
            setContentView(R.layout.activity_main);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            mDrawArea = findViewById(R.id.drawArea);
            mDrawArea.setActionBar(getSupportActionBar());
            mDrawArea.setMainActivity(this);
            LocationHandler.init(this);
            mLocHandler = LocationHandler.instance();

            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = mDrawArea.getContext().getTheme();
            theme.resolveAttribute(R.attr.gridAz, typedValue, true);
            AzGrid.sColor = typedValue.data;
            theme.resolveAttribute(R.attr.star, typedValue, true);
            Star.sColor = typedValue.data;
            theme.resolveAttribute(R.attr.gridEq, typedValue, true);
            EqGrid.sColor = typedValue.data;
            theme.resolveAttribute(R.attr.horizon, typedValue, true);
            Horizon.sColor = typedValue.data;
            theme.resolveAttribute(R.attr.equator, typedValue, true);
            Equator.sColor = typedValue.data;
            theme.resolveAttribute(R.attr.ecliptic, typedValue, true);
            Ecliptic.sColor = typedValue.data;
            theme.resolveAttribute(R.attr.const_lines, typedValue, true);
            ConstLines.sColor = typedValue.data;
            theme.resolveAttribute(R.attr.const_bounds, typedValue, true);
            ConstBoundaries.sColor = typedValue.data;
            theme.resolveAttribute(R.attr.infotext, typedValue, true);
            InfoText.sColor = typedValue.data;
            theme.resolveAttribute(R.attr.planet, typedValue, true);
            ChartPlanet.sColor = typedValue.data;
            Sun.sColor = typedValue.data;
            Moon.sColor = typedValue.data;
            theme.resolveAttribute(R.attr.planetText, typedValue, true);
            ChartPlanet.sTextColor = typedValue.data;
            Sun.sTextColor = typedValue.data;
            Moon.sTextColor = typedValue.data;

            update();

            double[] nearbyAzEle = Settings.instance().getNearbyAzEle();
            if (nearbyAzEle != null)
            {
                mNearbyObjects = mEngine.findObjectsNear(nearbyAzEle);
            }
        } catch (Exception e)
        {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.app_name) + " failed");
            builder.setMessage("Exception in onCreate(): " + e.toString() +
                               e.getStackTrace()[0].toString());
            builder.create().show();
        }
    }

    public void update()
    {
        mEngine.setLocation(mLocHandler.getLatitude(), mLocHandler.getLongitude(),
                            mLocHandler.getIsGpsPosition());
        mEngine.update();
        mDrawArea.setObjects(mEngine.getObjects());
        if (mSettings.getKeepScreenOn())
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        else
        {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_display_options)
        {
            DisplayOptionsDialog d = new DisplayOptionsDialog();
            d.show(getSupportFragmentManager(), "DisplayOptionsDialog");
            return true;
        }
        else if (id == R.id.action_time)
        {
            TimeDialog d = new TimeDialog();
            d.show(getSupportFragmentManager(), "TimeDialog");
            return true;
        }
        else if (id == R.id.action_magnitude)
        {
            MagnitudeDialog d = new MagnitudeDialog();
            d.show(getSupportFragmentManager(), "MagnitudePickerDialog");
            return true;
        }
        else if (id == R.id.action_location)
        {
            LocationDialog d = new LocationDialog();
            d.show(getSupportFragmentManager(), "LocationDialog");
            return true;
        }
        else if (id == R.id.action_theme)
        {
            ThemeDialog d = new ThemeDialog();
            d.show(getSupportFragmentManager(), "ThemeDialog");
            return true;
        }
        else if (id == R.id.action_settings)
        {
            SettingsDialog d = new SettingsDialog();
            d.show(getSupportFragmentManager(), "SettingsDialog");
            return true;
        }
        else if (id == R.id.action_about)
        {
            InfoDialog d = new InfoDialog();
            d.show(getSupportFragmentManager(), "InfoDialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void changeLocationSettings(boolean useGps, double latitude, double longitude)
    {
        if (useGps)
        {
            mLocHandler.enableGps(this);
        }
        else
        {
            mLocHandler.disableGps(latitude, longitude);
            mEngine.setLocation(latitude, longitude, false);
        }
        update();
    }

    private void startTimer()
    {
        if (!mIsRunningUpdateTask)
        {
            mIsRunningUpdateTask = true;
            mTimerHandler.postDelayed(mAutoUpdateTask, UPDATE_DELAY_MS);
        }
    }

    private void stopTimer()
    {
        mTimerHandler.removeCallbacks(mAutoUpdateTask);
        mIsRunningUpdateTask = false;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mIsRunning = false;
        stopTimer();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mIsRunning = true;
        if (mSettings.getAutoUpdate())
        {
            mSettings.setCurrentTime(new GregorianCalendar());
            update();
            startTimer();
        }
    }

    @Override
    public void changeAutoUpdate(boolean enabled)
    {
        if (enabled)
        {
            startTimer();
        }
        else
        {
            stopTimer();
        }
        update();
    }

    public boolean isRunning()
    {
        return mIsRunning;
    }

    public void showNearbyObjects(double[] azEle)
    {
        Settings.instance().setNearbyAzEle(azEle);
        mNearbyObjects = mEngine.findObjectsNear(azEle);
        ArrayList<String> nameArray = new ArrayList<>();
        for (ChartObject co : mNearbyObjects)
        {
            nameArray.add(co.getText());
        }
        Bundle data = new Bundle();
        data.putStringArrayList("nameArray", nameArray);
        data.putDouble("azimuth", azEle[0]);
        data.putDouble("elevation", azEle[1]);
        ObjectsNearbyDialog d = new ObjectsNearbyDialog();
        d.setArguments(data);
        d.show(getSupportFragmentManager(), "ObjectsNearbyDialog");
    }

    @Override
    public void showObjectDetails(int idx)
    {
        ChartObject chartObject = mNearbyObjects[idx];
        Bundle data = new Bundle();
        ArrayList<String> links = new ArrayList<>();
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        keys.add(getString(R.string.type));
        values.add(String.valueOf(chartObject.getTypeString()));
        keys.add(getString(R.string.azimuth));
        values.add(String.format(Locale.getDefault(), "%.1f°", chartObject.getAzimuth()));
        keys.add(getString(R.string.elevation));
        values.add(String.format(Locale.getDefault(), "%.1f°", chartObject.getElevation()));
        keys.add(getString(R.string.apparent_magnitude));
        values.add(String.format(Locale.getDefault(), "%.1f mag", chartObject.getApparentMagnitude()));
        if (chartObject.getType() == ObjectType.STAR)
        {
            Catalog.Entry ce = ((Star) chartObject).getCatalogEntry();
            keys.add(getString(R.string.RA));
            values.add(String.format(Locale.getDefault(), "%.4f°", ce.rightAscension));
            keys.add(getString(R.string.DEC));
            values.add(String.format(Locale.getDefault(), "%.4f°", ce.declination));
            links.add(String.format(Locale.US,
                    "<a href=\"https://simbad.u-strasbg.fr/simbad/sim-id?Ident=HR+%d\">&#8599; SIMBAD</a>",
                    ce.hr));
            links.add(String.format(Locale.US,
                    "<a href=\"https://m.wikidata.org/w/index.php?search=%%22HR+%d%%22\">&#8599; Wikidata</a>",
                    ce.hr));
        }
        else if (chartObject.getType() == ObjectType.PLANET)
        {
            Planet p = ((ChartPlanet) chartObject).getPlanet();
            keys.add(getString(R.string.helio_ecliptic_lat));
            values.add(String.format(Locale.getDefault(), "%.4f°", p.mHelio_lat));
            keys.add(getString(R.string.helio_ecliptic_lon));
            values.add(String.format(Locale.getDefault(), "%.4f°", p.mHelio_lon));
            keys.add(getString(R.string.distance_sun));
            values.add(String.format(Locale.getDefault(), "%.4f AU", p.mDistance_sun));
            keys.add(getString(R.string.geo_ecliptic_lat));
            values.add(String.format(Locale.getDefault(), "%.4f°", p.mEcliptic_lat));
            keys.add(getString(R.string.geo_ecliptic_lon));
            values.add(String.format(Locale.getDefault(), "%.4f°", p.mEcliptic_lon));
            keys.add(getString(R.string.distance_earth));
            values.add(String.format(Locale.getDefault(), "%.4f AU", p.mDistance_earth));
            links.add(String.format(Locale.US,
                    "<a href=\"https://m.wikidata.org/wiki/%s\">&#8599; Wikidata</a>",
                    p.mWikidataId));
        }
        else if (chartObject.getType() == ObjectType.SUN)
        {
            links.add(String.format(Locale.US,
                    "<a href=\"https://m.wikidata.org/wiki/%s\">&#8599; Wikidata</a>",
                    Sun.sWikidataId));
        }
        else if (chartObject.getType() == ObjectType.MOON)
        {
            links.add(String.format(Locale.US,
                    "<a href=\"https://m.wikidata.org/wiki/%s\">&#8599; Wikidata</a>",
                    Moon.sWikidataId));
        }
        data.putString("name", chartObject.getText());
        data.putStringArrayList("keys", keys);
        data.putStringArrayList("values", values);
        data.putStringArrayList("links", links);
        ObjectDetailsDialog d = new ObjectDetailsDialog();
        d.setArguments(data);
        d.show(getSupportFragmentManager(), "ObjectDetailsDialog");
    }

}
