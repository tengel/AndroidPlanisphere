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
import java.util.Calendar;
import java.util.GregorianCalendar;
import androidx.fragment.app.DialogFragment;

public class TimeDialog extends DialogFragment
{
    private SetTimeListener mListener;
    private GregorianCalendar mCurrSetting;

    public TimeDialog(GregorianCalendar currTime)
    {
        mCurrSetting = currTime;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try
        {
            mListener = (SetTimeListener) context;
        } catch (ClassCastException e)
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
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.time_dialog, null);
        builder.setView(view);
        final TimePicker tp = view.findViewById(R.id.timePicker);
        final DatePicker dp = view.findViewById(R.id.datePicker);
        tp.setIs24HourView(true);
        tp.setCurrentHour(mCurrSetting.get(Calendar.HOUR_OF_DAY));
        tp.setCurrentMinute(mCurrSetting.get(Calendar.MINUTE));
        dp.init(mCurrSetting.get(Calendar.YEAR), mCurrSetting.get(Calendar.MONTH),
                mCurrSetting.get(Calendar.DAY_OF_MONTH), null);
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
                mListener.setTime(new GregorianCalendar(dp.getYear(), dp.getMonth(),
                                                        dp.getDayOfMonth(),
                                                        tp.getCurrentHour(), tp.getCurrentMinute()));
            }
        });

        builder.setNegativeButton(R.string.cancel, null);
        return builder.create();
    }
}
