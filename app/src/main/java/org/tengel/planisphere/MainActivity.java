package org.tengel.planisphere;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import org.tengel.planisphere.dialog.DisplayOptionsDialog;
import org.tengel.planisphere.dialog.LocationDialog;
import org.tengel.planisphere.dialog.MagnitudeDialog;
import org.tengel.planisphere.dialog.SetLocationListener;
import org.tengel.planisphere.dialog.ThemeDialog;
import org.tengel.planisphere.dialog.SetTimeListener;
import org.tengel.planisphere.dialog.TimeDialog;
import org.tengel.planisphere.dialog.UpdateListener;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity
        implements UpdateListener, SetTimeListener, SetLocationListener
{
    private Engine mEngine;
    private DrawArea mDrawArea;
    private Settings mSettings;
    private ConstellationDb mConstDb;
    private Catalog mCatalog;
    private LocationListener mLocListener = new LocationListener();;
    private LocationManager mLocManager = null;

    public static String LOG_TAG = "Planisphere";

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

            setTheme(mSettings.getStyle());
            mEngine = new Engine(this, mSettings, mCatalog, mConstDb);
            mEngine.setTime(new GregorianCalendar());
            setContentView(R.layout.activity_main);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            mDrawArea = findViewById(R.id.drawArea);
            if (mSettings.isGpsEnabled())
            {
                enableGps();
            }
            else
            {
                mEngine.setLocation(mSettings.getLatitude(), mSettings.getLongitude(), false);
            }

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
            theme.resolveAttribute(R.attr.planetText, typedValue, true);
            ChartPlanet.sTextColor = typedValue.data;

            update();

        } catch (Exception e)
        {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name + " failed");
            builder.setMessage("Exception in onCreate(): " + e.toString() +
                               e.getStackTrace()[0].toString());
            builder.create().show();
        }
    }

    public void update()
    {
        mEngine.update();
        mDrawArea.setObjects(mEngine.getObjects());
    }

    private void enableGps()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            Toast toast = Toast.makeText(this, R.string.gps_no_permission,
                                         Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGpsEnabled)
        {
            Toast toast = Toast.makeText(this, R.string.gps_disabled, Toast.LENGTH_LONG);
            toast.show();
        }
        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                           600000, 1000, mLocListener);
                                           // 10 min , 1 km
        Location loc = mLocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc != null)
        {
            mEngine.setLocation(loc.getLatitude(), loc.getLongitude(), true);
        }
        else if (isGpsEnabled)
        {
            Toast toast = Toast.makeText(this, R.string.gps_waiting, Toast.LENGTH_LONG);
            toast.show();
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
            TimeDialog d = new TimeDialog(mEngine.getTime());
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
        else if (id == R.id.action_about)
        {
            return true;
        }
        else if (id == R.id.action_theme)
        {
            ThemeDialog d = new ThemeDialog();
            d.show(getSupportFragmentManager(), "ThemeDialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTime(GregorianCalendar cal)
    {
        mEngine.setTime(cal);
        update();
    }

    @Override
    public void setLocation(boolean useGps, double latitude, double longitude)
    {
        if (useGps)
        {
            enableGps();
        }
        else
        {
            if (mLocManager != null)
            {
                mLocManager.removeUpdates(mLocListener);
            }
            mEngine.setLocation(latitude, longitude, false);
        }
        update();
    }


    class LocationListener implements android.location.LocationListener
    {
        @Override
        public void onLocationChanged(Location location)
        {
            Toast toast = Toast.makeText(MainActivity.this, R.string.gps_received,
                                         Toast.LENGTH_LONG);
            toast.show();
            mEngine.setLocation(location.getLatitude(), location.getLongitude(), true);
            update();
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
