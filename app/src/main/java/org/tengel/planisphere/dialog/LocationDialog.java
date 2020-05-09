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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import org.tengel.planisphere.R;
import org.tengel.planisphere.Settings;
import androidx.fragment.app.DialogFragment;

public class LocationDialog extends DialogFragment
{
    private SetLocationListener mListener;

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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.action_time);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.location_dialog, null);
        builder.setView(view);
        final EditText latEdit = view.findViewById(R.id.editLatitude);
        latEdit.setText(String.valueOf(Settings.instance().getLatitude()));
        final EditText lonEdit = view.findViewById(R.id.editLongitude);
        lonEdit.setText(String.valueOf(Settings.instance().getLongitude()));
        final CheckBox gpsBox = view.findViewById(R.id.useGps);
        boolean isGpsEnabled = Settings.instance().isGpsEnabled();
        gpsBox.setChecked(isGpsEnabled);
        latEdit.setEnabled(!isGpsEnabled);
        lonEdit.setEnabled(!isGpsEnabled);

        gpsBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                latEdit.setEnabled(!isChecked);
                lonEdit.setEnabled(!isChecked);
            }
        });

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                boolean isEnabled = gpsBox.isChecked();
                float lat = Float.valueOf(latEdit.getText().toString());
                float lon = Float.valueOf(lonEdit.getText().toString());
                Settings.instance().setGpsEnabled(isEnabled);
                if (!isEnabled)
                {
                    Settings.instance().setLatitude(lat);
                    Settings.instance().setLongitude(lon);
                }
                mListener.setLocation(isEnabled, lat, lon);
            }
        });

        builder.setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

}
