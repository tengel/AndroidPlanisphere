package org.tengel.planisphere.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import org.tengel.planisphere.R;
import org.tengel.planisphere.Settings;

import androidx.fragment.app.DialogFragment;

public class MagnitudeDialog extends DialogFragment
{
    private UpdateListener mListener;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try
        {
            mListener = (UpdateListener) context;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.max_magnitude);


        final NumberPicker picker = new NumberPicker(getActivity());
        picker.setMinValue(0);
        picker.setMaxValue(8);
        picker.setValue(Settings.instance().getMaxMagnitude());
        picker.setWrapSelectorWheel(false);
        FrameLayout layout = new FrameLayout(getActivity());
        layout.addView(picker,
                       new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                                    FrameLayout.LayoutParams.WRAP_CONTENT,
                                                    Gravity.CENTER));
        builder.setView(layout);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Settings.instance().setMaxMagnitude(picker.getValue());
                mListener.update();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

}