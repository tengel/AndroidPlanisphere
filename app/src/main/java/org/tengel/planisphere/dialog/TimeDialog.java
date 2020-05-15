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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import org.tengel.planisphere.R;
import org.tengel.planisphere.Settings;
import java.util.Calendar;
import java.util.GregorianCalendar;
import androidx.fragment.app.DialogFragment;

public class TimeDialog extends DialogFragment
{
    private UpdateListener mListener;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try
        {
            mListener = (UpdateListener) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString()
                    + " must implement UpdateListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.action_time);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.time_dialog, null);
        builder.setView(view);
        GregorianCalendar currTime = Settings.instance().getCurrentTime();
        final TimePicker tp = view.findViewById(R.id.timePicker);
        final DatePicker dp = view.findViewById(R.id.datePicker);
        tp.setIs24HourView(true);
        tp.setCurrentHour(currTime.get(Calendar.HOUR_OF_DAY));
        tp.setCurrentMinute(currTime.get(Calendar.MINUTE));
        dp.init(currTime.get(Calendar.YEAR), currTime.get(Calendar.MONTH),
                currTime.get(Calendar.DAY_OF_MONTH), null);
        Button setNow = view.findViewById(R.id.setToNow);
        setNow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GregorianCalendar now = new GregorianCalendar();
                tp.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
                tp.setCurrentMinute(now.get(Calendar.MINUTE));
                dp.init(now.get(Calendar.YEAR), now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH), null);
            }
        });

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Settings.instance().setCurrentTime(new GregorianCalendar(
                        dp.getYear(), dp.getMonth(), dp.getDayOfMonth(),
                        tp.getCurrentHour(), tp.getCurrentMinute()));
                mListener.update();
            }
        });

        builder.setNegativeButton(R.string.cancel, null);
        return builder.create();
    }
}
