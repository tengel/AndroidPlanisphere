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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import org.tengel.planisphere.R;
import org.tengel.planisphere.Settings;
import androidx.fragment.app.DialogFragment;

public class ThemeDialog extends DialogFragment
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
