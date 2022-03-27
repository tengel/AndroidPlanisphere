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

package org.tengel.planisphere;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Pattern;

class Catalog
{
    static Pattern sPattern = Pattern.compile(" +");

    class Entry
    {
        int hr;
        String name;
        String bayerFlamsteed;
        double rightAscension;
        double declination;
        double apparentMagnitude;

        public Entry(String s)
        {
            hr = Integer.valueOf(substr(s, 0, 4).trim());
            name = mNames.get(hr);
            bayerFlamsteed = sPattern.matcher(substr(s, 5, 14).trim()).replaceAll(" ");
            int RAh = Integer.valueOf(substr(s, 75, 77));
            int RAm = Integer.valueOf(substr(s, 77, 79));
            double RAs = Double.valueOf(substr(s, 79, 83));
            rightAscension = RAh + (RAm / 60.0) + (RAs / 60.0 / 60.0);
            int DE_ = 1;
            if (substr(s, 83, 84).equals("-"))
            {
                DE_ = -1;
            }
            int DEd = Integer.valueOf(substr(s, 84, 86));
            int DEm = Integer.valueOf(substr(s, 86, 88));
            int DEs = Integer.valueOf(substr(s, 88, 90));
            declination = (DEd + (DEm / 60.0) + (DEs / 60.0 / 60.0)) * DE_;
            apparentMagnitude = Double.valueOf(substr(s, 102, 107));
        }

        private String substr(String s, int beginIndex, int endIndex)
        {
            if (s != null && s.length() > beginIndex && s.length() > endIndex)
            {
                return s.substring(beginIndex, endIndex);
            }
            else
            {
                return null;
            }
        }
    }

    private HashMap<Integer, Entry> mEntires = new HashMap<>();
    private HashMap<Integer, String> mNames = new HashMap<>();
    private static Catalog sInstance = null;

    public static Catalog instance() throws NullPointerException
    {
        if (sInstance == null)
        {
            throw new NullPointerException("run init() before instance()");
        }
        return sInstance;
    }

    public synchronized static void init(InputStream catalogStream,
                                         InputStream nameStream) throws IOException
    {
        if (catalogStream == null)
        {
            throw new NullPointerException("stream must not be null");
        }
        else if (sInstance == null)
        {
            sInstance = new Catalog(catalogStream, nameStream);
        }
    }

    private Catalog(InputStream catalogStream,
                    InputStream namesStream) throws IOException
    {
        BufferedReader nameReader = new BufferedReader(new InputStreamReader(
                                                           namesStream));
        while (true)
        {
            String line = nameReader.readLine();
            if (line == null)
            {
                break;
            }
            String[] lItems = line.split("\t");
            mNames.put(Integer.valueOf(lItems[1].trim()), lItems[0].trim());
        }

        BufferedReader catalogReader = new BufferedReader(new InputStreamReader(catalogStream));
        while (true)
        {
            String line = catalogReader.readLine();
            if (line == null)
            {
                break;
            }
            try
            {
                Entry e = new Entry(line);
                mEntires.put(e.hr, e);
            }
            catch(Exception e)
            {
                //Log.w(MainActivity.LOG_TAG, "Failed to parse catalog: " + e.toString() + " : " + line);
            }
        }
    }

    public final Collection<Entry> get()
    {
        return mEntires.values();
    }

    public Entry get(int id)
    {
        return mEntires.get(id);
    }
}
