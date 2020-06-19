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
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import org.tengel.planisphere.R;
import java.util.ArrayList;
import androidx.fragment.app.DialogFragment;

public class ObjectsNearbyDialog extends DialogFragment
{
    private ObjectDetailsListener mListener;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try
        {
            mListener = (ObjectDetailsListener) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString()
                    + " must implement ObjectDetailsListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        double azimuth = getArguments().getDouble("azimuth");
        double elevation = getArguments().getDouble("elevation");
        final ArrayList<String> nameArray = getArguments().getStringArrayList("nameArray");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.objects_nearby);
        TextView tv = new TextView(getContext());
        tv.setText(String.format(" " + getString(R.string.azimuth) + ": %.1f°   " +
                   getString(R.string.elevation) + ": %.1f°", azimuth, elevation));
        ListView lv = new ListView(getContext());
        ArrayAdapter<String> lvAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, nameArray);
        lv.setAdapter(lvAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                mListener.showObjectDetails(position);
            }
        });

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(tv);
        layout.addView(lv);
        builder.setView(layout);
        builder.setPositiveButton(R.string.ok, null);
        return builder.create();
    }

}
