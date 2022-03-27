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

import org.junit.Test;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import static org.junit.Assert.*;

public class AstroTest
{

    @Test
    public void sin()
    {
        assertEquals("sin 90", 90, Astro.asin(Astro.sin(90)), 0);
        assertEquals("sin 0", 0, Astro.asin(Astro.sin(0)), 0);
        assertEquals("sin 31", 31, Astro.asin(Astro.sin(31)), 0);
    }

    @Test
    public void cos()
    {
        assertEquals("cos 90", 90, Astro.acos(Astro.cos(90)), 0);
        assertEquals("cos 0", 0, Astro.acos(Astro.cos(0)), 0);
        assertEquals("cos 62", 62, Astro.acos(Astro.cos(62)), 0);
    }

    @Test
    public void tan()
    {
        assertEquals("tan 45", 45, Astro.atan(Astro.tan(45)), 0);
        assertEquals("tan 0", 0, Astro.atan(Astro.tan(0)), 0);
        assertEquals("tan 31", 31, Astro.atan(Astro.tan(31)), 0);
    }

    @Test
    public void jd()
    {
        double j = Astro.julian_date(1983, 1, 18, 7 + (12.0 / 60.0));
        assertEquals(2445352.8, j, 0);

        GregorianCalendar gc1 = new GregorianCalendar(
            TimeZone.getTimeZone("UTC"));
        gc1.set(1983, 1 - 1, 18, 7, 12, 0);
        assertEquals(2445352.8, Astro.julian_date(gc1), 0);

        GregorianCalendar gc2 = new GregorianCalendar(
            TimeZone.getTimeZone("America/Los_Angeles"));
        gc2.set(1983, 1 - 1, 18, 7, 12, 0);
        assertEquals(2445353.1333333333, Astro.julian_date(gc2), 0);

        GregorianCalendar gc3 = new GregorianCalendar(
            TimeZone.getTimeZone("America/Los_Angeles"));
        gc3.set(1983, 7 - 1, 18, 7, 12, 0);
        assertEquals(2445534.091666667, Astro.julian_date(gc3), 0);
    }

    @Test
    public void siderealTime()
    {
        double theta = Astro.sidereal_time(1982, 1, 1, 0);
        Degree hms = Astro.deg2sex(theta);
        assertEquals("mean sidereal time Greenwich h", 6, hms.d);
        assertEquals("mean sidereal time Greenwich min", 41, hms.m);
        assertEquals("mean sidereal time Greenwich sec", 17.3, hms.s, 0.1);
        double local = theta + 11.60833 / 15;
        hms = Astro.deg2sex(local);
        assertEquals("mean sidereal time Munich h", hms.d, 7);
        assertEquals("mean sidereal time Munich min", hms.m, 27);
        assertEquals("mean sidereal time Munich sec", hms.s, 43.3, 0.1);
    }

    @Test
    public void geoEqua2geoHori()
    {
        double t = (18 - 16) * 15.0;
        double[] ah = Astro.geoEqua2geoHori(t, 48, 20);
        assertEquals("azimuth", ah[0], 51.3375, 0.00005);
        assertEquals("elevation", ah[1], 53.0068, 0.00005);
    }

    @Test
    public void orbit2helioEcl()
    {
        double[] lb = Astro.orbit2helioEcl(210, 30, 20);
        assertEquals(lb[0], 238.4812, 0.00005);
        assertEquals(lb[1], -9.8466, 0.00005);
    }

    @Test
    public void helioEcl2geoEcl()
    {
        double[] v = Astro.helioEcl2geoEcl(150, 0, 1, 100, 1.3, 5);
        assertEquals("beta", 1.4692, v[0], 0.00005);
        assertEquals("lamda", 90.0258, v[1], 0.00005);
        assertEquals("delta", 4.424226, v[2], 0.0000005);
        v = Astro.geoEcl2helioEcl(150, 0, 1, v[1], v[0], v[2]);
        assertEquals("b(lat)", 1.300, v[0], 0.001);
        assertEquals("l(lon)", 100.000, v[1], 0.001);
        assertEquals("r(dist)", 5.000, v[2], 0.001);
    }

    @Test
    public void sphe2cart()
    {
        double beta = 60;
        double lam = 120;
        double r = 5;
        double[] xyz = Astro.sphe2cart(beta, lam, r);
        assertEquals("x", -1.25, xyz[0], 0.005);
        assertEquals("y", 2.165064, xyz[1], 0.0000005);
        assertEquals("z", 4.330127, xyz[2], 0.0000005);
        double[] s = Astro.cart2sphe(xyz[0], xyz[1], xyz[2]);
        assertEquals(s[0], beta, 1e-10);
        assertEquals(s[1], lam, 1e-10);
        assertEquals(s[2], r, 0);
    }

    @Test
    public void cart2sphe()
    {
        double x = -1.0;
        double y = -2.5;
        double z = 0.5;
        double[] s = Astro.cart2sphe(x, y, z);
        assertEquals("beta", 10.5197, s[0], 0.00005);
        assertEquals("lamda", 248.1986, s[1], 0.00005);
        assertEquals("r", 2.738613, s[2], 0.0000005);
        double[] c = Astro.sphe2cart(s[0], s[1], s[2]);
        assertEquals(c[0], x, 1e-10);
        assertEquals(c[1], y, 1e-10);
        assertEquals(c[2], z, 1e-10);
    }

    @Test
    public void geoEcl2geoEqua()
    {
        double[] v = Astro.geoEcl2geoEqua(50, 290);
        assertEquals("alpha", 284.357, v[0], 0.0005);
        assertEquals("delta", 27.55, v[1], 0.005);
    }

    @Test
    public void deg2sec()
    {
        double d = 121.135;
        Degree dms = Astro.deg2sex(d);
        assertEquals(dms.d, 121);
        assertEquals(dms.m, 8);
        assertEquals(dms.s, 6.0, 1e-10);
        assertEquals(Astro.sex2deg(dms), d, 0);
    }

    @Test
    public void positionSun()
    {
        double[] raDec;
        raDec = Astro.calcPositionSun(Astro.julian_date(2020, 1, 1, 12));
        assertEquals(281.44595, raDec[0], 0.00005);
        assertEquals(-23.0221, raDec[1], 0.00005);
    }

    @Test
    public void positionMoon()
    {
        double[] bld, raDec;
        bld = Astro.calcPositionMoon(Astro.julian_date(2020, 1, 1, 18));
        raDec = Astro.geoEcl2geoEqua(bld[0], bld[1]);
        assertEquals(357.511, raDec[0],0.1);
        assertEquals(-6.686, raDec[1], 0.1);
        bld = Astro.calcPositionMoon(Astro.julian_date(2005, 8, 2, 0));
        assertEquals(4.9504, bld[0], 0.0001);
        assertEquals(95.5624, bld[1], 0.0001);
        assertEquals(0.002697, bld[2], 0.000001);
    }

    @Test
    public void riseSetStar()
    {
        Calendar rise, set;
        final double rigelRa = 78.63458333333332 / 15;
        final double rigelDec = -8.201666666666666;

        // date is UTC
        GregorianCalendar date1 = new GregorianCalendar(
            TimeZone.getTimeZone("UTC"));
        date1.clear();
        date1.set(Calendar.YEAR, 2020);
        date1.set(Calendar.MONTH, 0); // 0 = Jan
        date1.set(Calendar.DAY_OF_MONTH, 1);
        rise = Astro.calcRiseSet_star(15, 50, date1, rigelRa, rigelDec, true);
        assertEquals(2020, rise.get(Calendar.YEAR));
        assertEquals(0,    rise.get(Calendar.MONTH));
        assertEquals(1,    rise.get(Calendar.DAY_OF_MONTH));
        assertEquals(16,   rise.get(Calendar.HOUR_OF_DAY));
        assertEquals(7,    rise.get(Calendar.MINUTE));
        assertEquals(21,   rise.get(Calendar.SECOND));
        set = Astro.calcRiseSet_star(15, 50, date1, rigelRa, rigelDec, false);
        assertEquals(2020, set.get(Calendar.YEAR));
        assertEquals(0,    set.get(Calendar.MONTH));
        assertEquals(2,    set.get(Calendar.DAY_OF_MONTH));
        assertEquals(2,    set.get(Calendar.HOUR_OF_DAY));
        assertEquals(53,   set.get(Calendar.MINUTE));
        assertEquals(40,   set.get(Calendar.SECOND));

        // date with timezone and DST
        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
        GregorianCalendar date2 = new GregorianCalendar();
        date2.set(2020, 7 - 1, 15, 0, 0, 0);
        rise = Astro.calcRiseSet_star(-118, 34, date2, rigelRa, rigelDec, true);
        assertEquals(2020, rise.get(Calendar.YEAR));
        assertEquals(7 -1, rise.get(Calendar.MONTH));
        assertEquals(15,   rise.get(Calendar.DAY_OF_MONTH));
        assertEquals(11,   rise.get(Calendar.HOUR_OF_DAY));
        assertEquals(50,   rise.get(Calendar.MINUTE));
        assertEquals(53,   rise.get(Calendar.SECOND));
        set = Astro.calcRiseSet_star(-118, 34, date2, rigelRa, rigelDec, false);
        assertEquals(2020, set.get(Calendar.YEAR));
        assertEquals(7 -1, set.get(Calendar.MONTH));
        assertEquals(15,   set.get(Calendar.DAY_OF_MONTH));
        assertEquals(23,   set.get(Calendar.HOUR_OF_DAY));
        assertEquals(9,    set.get(Calendar.MINUTE));
        assertEquals(57,   set.get(Calendar.SECOND));

        // convert UTC to default timezone and DST
        rise.setTimeZone(TimeZone.getDefault());
        assertEquals(2020, rise.get(Calendar.YEAR));
        assertEquals(7 -1, rise.get(Calendar.MONTH));
        assertEquals(15,   rise.get(Calendar.DAY_OF_MONTH));
        assertEquals(4,    rise.get(Calendar.HOUR_OF_DAY));
        assertEquals(50,   rise.get(Calendar.MINUTE));
        assertEquals(53,   rise.get(Calendar.SECOND));
    }

    @Test
    public void riseSetSun()
    {
        Calendar rise, set;
        GregorianCalendar date1 = new GregorianCalendar(
                TimeZone.getTimeZone("UTC"));
        date1.set(2020, 7 - 1, 3, 18, 0, 0);

        rise = Astro.calcRiseSet_sun(9.49, 51.31, date1, RiseSetType.RISE);
        assertEquals(2020, rise.get(Calendar.YEAR));
        assertEquals(7 -1, rise.get(Calendar.MONTH));
        assertEquals(3,    rise.get(Calendar.DAY_OF_MONTH));
        assertEquals(3,    rise.get(Calendar.HOUR_OF_DAY));
        assertEquals(11,   rise.get(Calendar.MINUTE));
        assertEquals(55,   rise.get(Calendar.SECOND));

        set  = Astro.calcRiseSet_sun(9.49, 51.31, date1, RiseSetType.SET);
        assertEquals(2020, set.get(Calendar.YEAR));
        assertEquals(7 -1, set.get(Calendar.MONTH));
        assertEquals(3,    set.get(Calendar.DAY_OF_MONTH));
        assertEquals(19,   set.get(Calendar.HOUR_OF_DAY));
        assertEquals(40,   set.get(Calendar.MINUTE));
        assertEquals(26,   set.get(Calendar.SECOND));
    }

    @Test
    public void testPhase()
    {
        double delta = 1.3135;
        double r = 1.4030;
        double R = 0.9852;
        double i = Astro.calcPhase(delta, r, R);
        assertEquals(87, i, 0.1);
    }
}
