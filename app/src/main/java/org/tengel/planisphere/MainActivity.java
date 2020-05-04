package org.tengel.planisphere;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import org.tengel.planisphere.dialog.DisplayOptionsDialog;
import org.tengel.planisphere.dialog.MagnitudeDialog;
import org.tengel.planisphere.dialog.SetThemeDialog;
import org.tengel.planisphere.dialog.SetTimeListener;
import org.tengel.planisphere.dialog.TimeDialog;
import org.tengel.planisphere.dialog.UpdateListener;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity
                          implements UpdateListener, SetTimeListener
{
    private Engine mEngine;
    private DrawArea mDrawArea;
    private Settings mSettings;
    private ConstellationDb mConstDb;
    private Catalog mCatalog;
    public static String LOG_TAG = "Planisphere";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            Settings.init(getApplicationContext());
            mSettings = Settings.instance();

            Catalog.init(getResources().openRawResource(R.raw.bs_catalog));
            mCatalog = Catalog.instance();;

            ConstellationDb.init(getResources().openRawResource(R.raw.constellation_lines),
                                 getResources().openRawResource(R.raw.constellation_names),
                                 mCatalog);
            mConstDb = ConstellationDb.instance();

            setTheme(mSettings.getStyle());
            mEngine = new Engine(this, mSettings, mCatalog, mConstDb);
            mEngine.setLocation(53.14, 8.19);
            mEngine.setTime(new GregorianCalendar());

            setContentView(R.layout.activity_main);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            mDrawArea = findViewById(R.id.drawArea);

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

            update();

        } catch (Exception e)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
            return true;
        }
        else if (id == R.id.action_about)
        {
            return true;
        }
        else if (id == R.id.action_theme)
        {
            SetThemeDialog d = new SetThemeDialog();
            d.show(getSupportFragmentManager(), "SetThemeDialog");
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
}
