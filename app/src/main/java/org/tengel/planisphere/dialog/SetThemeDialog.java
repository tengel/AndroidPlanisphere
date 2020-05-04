package org.tengel.planisphere.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import org.tengel.planisphere.R;
import org.tengel.planisphere.Settings;
import androidx.fragment.app.DialogFragment;

public class SetThemeDialog extends DialogFragment
{
    class Listener implements DialogInterface.OnClickListener
    {
        private Activity mActivity;

        Listener(Activity a)
        {
            mActivity = a;
        }

        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            Settings.instance().setStyleIndex(which);
            dialog.dismiss();
            mActivity.recreate();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.theme);
        Listener listener = new Listener(this.getActivity());
        builder.setSingleChoiceItems(R.array.themes, Settings.instance().getStyleIndex(), listener);
        return builder.create();
    }
}
