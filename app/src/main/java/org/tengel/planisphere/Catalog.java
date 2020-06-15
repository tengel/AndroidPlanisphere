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

class Catalog
{

    class Entry
    {
        int hr;
        String name;
        String DM;
        String HD;
        String SAO;
        String FK5;
        String IRflag;
        String r_IRflag;
        String Multiple;
        String ADS;
        String ADScomp;
        String VarID;
        String RAh1900;
        String RAm1900;
        String RAs1900;
        String DE_1900;
        String DEd1900;
        String DEm1900;
        String DEs1900;
        double rightAscension;
        double declination;
        String GLON;
        String GLAT;
        double apparentMagnitude;
        String n_Vmag;
        String u_Vmag;
        String B_V;
        String u_B_V;
        String U_B;
        String u_U_B;
        String R_I;
        String n_R_I;
        String SpType;
        String n_SpType;
        String pmRA;
        String pmDE;
        String n_Parallax;
        String Parallax;
        String RadVel;
        String n_RadVel;
        String l_RotVel;
        String RotVel;
        String u_RotVel;
        String Dmag;
        String Sep;
        String MultID;
        String MultCnt;
        String NoteFlag;

        public Entry(String s)
        {
            hr = Integer.valueOf(substr(s, 0, 4).trim());
            name = substr(s, 5, 14).trim().replaceAll(" +", " ");
            DM = substr(s, 14, 25);
            HD = substr(s, 25, 31);
            SAO = substr(s, 31 ,37);
            FK5 = substr(s, 37, 41);
            IRflag = substr(s, 41, 42);
            r_IRflag = substr(s, 42, 43);
            Multiple = substr(s, 43, 44);
            ADS = substr(s, 44, 49);
            ADScomp = substr(s, 49, 51);
            VarID = substr(s, 51, 60);
            RAh1900 = substr(s, 60, 62);
            RAm1900 = substr(s, 62, 64);
            RAs1900 = substr(s, 64, 68);
            DE_1900 = substr(s, 68, 69);
            DEd1900 = substr(s, 69, 71);
            DEm1900 = substr(s, 71, 73);
            DEs1900 = substr(s, 73, 75);
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
            GLON = substr(s, 90, 96);
            GLAT = substr(s, 96, 102);
            apparentMagnitude = Double.valueOf(substr(s, 102, 107));
            n_Vmag = substr(s, 107, 108);
            u_Vmag = substr(s, 108, 109);
            B_V = substr(s, 109, 114);
            u_B_V = substr(s, 114, 115);
            U_B = substr(s, 115, 120);
            u_U_B = substr(s, 120, 121);
            R_I = substr(s, 121, 126);
            n_R_I = substr(s, 126, 127);
            SpType = substr(s, 127, 147);
            n_SpType = substr(s, 147, 148);
            pmRA = substr(s, 148, 154);
            pmDE = substr(s, 154, 160);
            n_Parallax = substr(s, 160, 161);
            Parallax = substr(s, 161, 166);
            RadVel = substr(s, 166, 170);
            n_RadVel = substr(s, 170, 174);
            l_RotVel = substr(s, 174, 176);
            RotVel = substr(s, 176, 179);
            u_RotVel = substr(s, 179, 180);
            Dmag = substr(s, 180, 184);
            Sep = substr(s, 184, 190);
            MultID = substr(s, 190, 194);
            MultCnt = substr(s, 194, 196);
            NoteFlag = substr(s, 196, 197);
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
    private static Catalog sInstance = null;

    public static Catalog instance() throws NullPointerException
    {
        if (sInstance == null)
        {
            throw new NullPointerException("run init() before instance()");
        }
        return sInstance;
    }

    public synchronized static void init(InputStream catalogStream) throws IOException
    {
        if (catalogStream == null)
        {
            throw new NullPointerException("stream must not be null");
        }
        else if (sInstance == null)
        {
            sInstance = new Catalog(catalogStream);
        }
    }

    private Catalog(InputStream catalogStream) throws IOException
    {
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
