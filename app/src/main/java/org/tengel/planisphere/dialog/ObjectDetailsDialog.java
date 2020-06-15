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
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import org.tengel.planisphere.R;
import java.util.ArrayList;
import androidx.fragment.app.DialogFragment;

public class ObjectDetailsDialog extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        ArrayList<String> keys = getArguments().getStringArrayList("keys");
        ArrayList<String> values = getArguments().getStringArrayList("values");
        ArrayList<String> links = getArguments().getStringArrayList("links");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        TableLayout table = new TableLayout(getContext());
        builder.setTitle(getArguments().getString("name"));
        for (int i = 0; i < keys.size(); ++i)
        {
            TableRow row = new TableRow(getContext());
            TextView t1 = new TextView(getContext());
            t1.setText(keys.get(i));
            t1.setTextIsSelectable(true);
            TextView t2 = new TextView(getContext());
            t2.setText(values.get(i));
            t2.setTextIsSelectable(true);
            row.addView(t1);
            row.addView(t2);
            table.addView(row);
        }

        for (String url : links)
        {
            TextView tv = new TextView(getContext());
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            tv.setText(Html.fromHtml(url));
            table.addView(tv);
        }

        table.setShrinkAllColumns(true);
        table.setStretchAllColumns(true);
        ScrollView sv = new ScrollView(getContext());
        sv.addView(table);
        builder.setView(sv);
        builder.setPositiveButton(R.string.ok, null);
        return builder.create();
    }
}
