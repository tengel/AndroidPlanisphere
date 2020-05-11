package org.tengel.planisphere.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import org.tengel.planisphere.R;
import org.tengel.planisphere.Settings;
import androidx.fragment.app.DialogFragment;

public class SettingsDialog extends DialogFragment
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
        builder.setTitle(R.string.action_settings);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.settings_dialog, null);
        builder.setView(view);
        final Spinner spinner = (Spinner) view.findViewById(R.id.const_langauge);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                                                                             R.array.const_language,
                                                                             android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(Settings.instance().getConstLanguage());

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                Settings.instance().setConstLanguage(spinner.getSelectedItemPosition());
                mListener.update();
            }
        });

        builder.setNegativeButton(R.string.cancel, null);
        return builder.create();
    }
}
