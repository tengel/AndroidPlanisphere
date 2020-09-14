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

package org.tengel.planisphere.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import org.tengel.planisphere.LocationHandler;
import org.tengel.planisphere.MainActivity;
import org.tengel.planisphere.R;
import org.tengel.planisphere.Settings;
import java.util.Locale;
import androidx.fragment.app.DialogFragment;

public class LocationDialog extends DialogFragment
{
    private SetLocationListener mListener;
    private EditText mLatEdit;
    private EditText mLonEdit;
    private TextView mGpsStatus;
    private TextView mGeoUrl;
    private TextView mGpsTimestamp;
    private LinearLayout mFixedLocLayout;
    private LinearLayout mGpsLocLayout;
    private Spinner mSrcSpinner;
    private TextWatcher mLatLonWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
        { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {
            setGeoUrl(mLatEdit.getText().toString(), mLonEdit.getText().toString());
        }

        @Override
        public void afterTextChanged(Editable editable)
        {  }
    };


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try
        {
            mListener = (SetLocationListener) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString()
                    + " must implement SetLocationListener");
        }
    }

    private void setGeoUrl(String lat, String lon)
    {
        mGeoUrl.setMovementMethod(LinkMovementMethod.getInstance());
        mGeoUrl.setText(Html.fromHtml("<a href=\"geo:" + lat + "," + lon + "\">" +
                                      "geo:" + lat + "," + lon + "</a>"));
    }

    private void setVisibility(boolean useGps)
    {
        if (useGps)
        {
            mFixedLocLayout.setVisibility(View.GONE);
            mGpsLocLayout.setVisibility(View.VISIBLE);
            setGeoUrl(String.format(Locale.US, "%.4f",
                                    LocationHandler.instance().getLatitude()),
                      String.format(Locale.US, "%.4f",
                                    LocationHandler.instance().getLongitude()));
        }
        else
        {
            mFixedLocLayout.setVisibility(View.VISIBLE);
            mGpsLocLayout.setVisibility(View.GONE);
            mLatLonWatcher.onTextChanged(null, 0, 0, 0);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.action_location);
        View view = View.inflate(getContext(), R.layout.location_dialog, null);
        builder.setView(view);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                                                                             R.array.location_source,
                                                                             android.R.layout.simple_spinner_item);
        mSrcSpinner = view.findViewById(R.id.locationSource);
        mSrcSpinner.setAdapter(adapter);
        mSrcSpinner.setSelection(Settings.instance().isGpsEnabled() ? 0 : 1);
        mLatEdit = view.findViewById(R.id.editLatitude);
        mLatEdit.setText(String.valueOf(Settings.instance().getLatitude()));
        mLatEdit.addTextChangedListener(mLatLonWatcher);
        mLonEdit = view.findViewById(R.id.editLongitude);
        mLonEdit.setText(String.valueOf(Settings.instance().getLongitude()));
        mLonEdit.addTextChangedListener(mLatLonWatcher);
        mGpsStatus = view.findViewById(R.id.gpsStatus);
        mGpsStatus.setText(LocationHandler.instance().getStatus());
        mGpsTimestamp = view.findViewById(R.id.gpsTimestamp);
        mGpsTimestamp.setText(LocationHandler.instance().getGpsTimestamp());
        mGeoUrl = view.findViewById(R.id.geoUrl);
        mFixedLocLayout = view.findViewById(R.id.fixedLocationLayout);
        mGpsLocLayout = view.findViewById(R.id.gpsLocationLayout);

        setVisibility(Settings.instance().isGpsEnabled());

        mSrcSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                setVisibility(i == 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            { }
        });

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                boolean gpsEnabled = mSrcSpinner.getSelectedItemPosition() == 0;
                float lat = Float.valueOf(mLatEdit.getText().toString());
                float lon = Float.valueOf(mLonEdit.getText().toString());
                Settings.instance().setGpsEnabled(gpsEnabled);
                if (!gpsEnabled)
                {
                    Settings.instance().setLatitude(lat);
                    Settings.instance().setLongitude(lon);
                }
                mListener.changeLocationSettings(gpsEnabled, lat, lon);
            }
        });

        builder.setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

}
