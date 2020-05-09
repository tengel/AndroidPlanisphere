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
import org.tengel.planisphere.R;
import org.tengel.planisphere.Settings;
import androidx.fragment.app.DialogFragment;


public class DisplayOptionsDialog extends DialogFragment
{
    private boolean[] mSelection;
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
        builder.setTitle(R.string.display_options);
        Settings s = Settings.instance();
        mSelection = new boolean[] {s.isHorizonEnabled(), s.isEquatorEnabled(),
                      s.isEclipticEnabled(), s.isAzGridEnabled(),
                      s.isEqGridEnabled(), s.isConstLinesEnabled(),
                      s.isConstBoundEnabled(), s.isConstNamesEnabled(),
                      s.isSolarSystemEnabled(), s.isSolarNamesEnabled(),
                      s.isStarsEnabled()};

        builder.setMultiChoiceItems(R.array.display_options, mSelection,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked)
                    {
                        mSelection[which] = isChecked;
                    }
                });

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int id) {
                   Settings s = Settings.instance();
                   s.setHorizonEnabled(mSelection[0]);
                   s.setEquatorEnabled(mSelection[1]);
                   s.setEclipticEnabled(mSelection[2]);
                   s.setAzGridEnabled(mSelection[3]);
                   s.setEqGridEnabled(mSelection[4]);
                   s.setConstLinesEnabled(mSelection[5]);
                   s.setConstBoundEnabled(mSelection[6]);
                   s.setConstNamesEnabled(mSelection[7]);
                   s.setSolarSystemEnabled(mSelection[8]);
                   s.setSolarNamesEnabled(mSelection[9]);
                   s.setStarsEnabled(mSelection[10]);
                   mListener.update();
               }
           });

        builder.setNegativeButton(R.string.cancel, null);
        return builder.create();
    }
}
