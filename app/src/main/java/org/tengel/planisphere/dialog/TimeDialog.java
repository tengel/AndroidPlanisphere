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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TimePicker;
import org.tengel.planisphere.R;
import org.tengel.planisphere.Settings;
import java.util.Calendar;
import java.util.GregorianCalendar;
import androidx.fragment.app.DialogFragment;

public class TimeDialog extends DialogFragment
{
    private SetTimeListener mListener;
    private TimePicker mTp;
    private DatePicker mDp;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try
        {
            mListener = (SetTimeListener) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString()
                    + " must implement SetTimeListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.action_time);
        View view = View.inflate(getContext(), R.layout.time_dialog, null);
        builder.setView(view);
        GregorianCalendar currTime = Settings.instance().getCurrentTime();
        mTp = view.findViewById(R.id.timePicker);
        mDp = view.findViewById(R.id.datePicker);
        final CheckBox autoUpdateCb = view.findViewById(R.id.timeAutoUpdate);
        mTp.setIs24HourView(true);
        mTp.setCurrentHour(currTime.get(Calendar.HOUR_OF_DAY));
        mTp.setCurrentMinute(currTime.get(Calendar.MINUTE));
        mDp.init(currTime.get(Calendar.YEAR), currTime.get(Calendar.MONTH),
                 currTime.get(Calendar.DAY_OF_MONTH), null);
        final Button setNowBtn = view.findViewById(R.id.setToNow);
        setNowBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setToNow();
            }
        });

        autoUpdateCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                mTp.setEnabled(!isChecked);
                mDp.setEnabled(!isChecked);
                setNowBtn.setEnabled(!isChecked);
                if (isChecked)
                {
                   setToNow();
                }
            }
        });
        autoUpdateCb.setChecked(Settings.instance().getAutoUpdate());

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Settings.instance().setCurrentTime(new GregorianCalendar(
                        mDp.getYear(), mDp.getMonth(), mDp.getDayOfMonth(),
                        mTp.getCurrentHour(), mTp.getCurrentMinute()));
                Settings.instance().setAutoUpdate(autoUpdateCb.isChecked());
                mListener.changeAutoUpdate(autoUpdateCb.isChecked());
            }
        });

        builder.setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

    private void setToNow()
    {
        GregorianCalendar now = new GregorianCalendar();
        mTp.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
        mTp.setCurrentMinute(now.get(Calendar.MINUTE));
        mDp.init(now.get(Calendar.YEAR), now.get(Calendar.MONTH),
                 now.get(Calendar.DAY_OF_MONTH), null);
    }
}
