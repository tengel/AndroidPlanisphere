package org.tengel.planisphere;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.io.IOException;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    private Engine mEngine;
    private DrawArea mDrawArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            mEngine = new Engine(this);
            mEngine.setLocation(53.14, 8.19);
            mEngine.setTime(new GregorianCalendar());

        } catch (IOException e)
        {
            // TODO: handle exception
            e.printStackTrace();
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawArea = findViewById(R.id.drawArea);

        update();
    }

    private void update()
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
