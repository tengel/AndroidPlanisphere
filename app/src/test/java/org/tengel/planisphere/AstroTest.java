package org.tengel.planisphere;

import org.junit.Test;
import java.util.GregorianCalendar;
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

        GregorianCalendar gc = new GregorianCalendar();
        gc.set(1983, 1 - 1, 18, 7, 12, 0);
        assertEquals(2445352.8, Astro.julian_date(gc), 0);
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
        Double[] ah = Astro.geoEqua2geoHori(t, 48, 20);
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
        double ra, dec;
        double[] raDec;
        raDec = Astro.calcPositionSun(Astro.julian_date(2020, 1, 1, 12));
        assertEquals(281.44595, raDec[0], 0.00005);
        assertEquals(-23.0221, raDec[1], 0.00005);
    }

}
