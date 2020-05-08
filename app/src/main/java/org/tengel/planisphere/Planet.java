package org.tengel.planisphere;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;

class OrbitalElements
{
    double MA = 0.0; // Mean anomaly, M (degrees)
    double EC = 0.0; // Eccentricity,e
    double IN = 0.0; // Inclination w.r.t XY-plane, i(degrees)
    double OM = 0.0; // Longitude of Ascending Node, Omega (degrees)
    double W  = 0.0; // Argument of Perifocus, w(degrees)
    double om = 0.0; // longitude of perihelion,omega_bar =(OM +W)%360
    double A  = 0.0; // Semi-major axis, a (au)
    double N  = 0.0; // Mean motion, n (degrees/day)
    double JD = 0.0; // Julian Day Number, Barycentric Dynamical Time
    double dayDiff = 0.0;
}


abstract class Planet
{
    static Planet[] sPlanets;
    static Planet sEarth;

    protected String mName;
    protected OrbitalElements mOe;

    // heliocentric, ecliptical(date/time required)
    public double mHelio_lon = 0;
    public double mHelio_lat = 0;
    public double mDistance_sun = 0;

    // geocentric, ecliptical(position of earth required)
    public double mEcliptic_lat = 0;
    public double mEcliptic_lon = 0;
    public double mDistance_earth = 0;

    // geocentric,equatorial
    public double mDeclination = 0;
    public double mRa = 0;

    public double mApparentMagnitude = 0;

    Planet(String name, double apparentMagnitude)
    {
        mName = name;
        mApparentMagnitude = apparentMagnitude;
        mOe = new OrbitalElements();
    }

    /**
     * Calculate the heliocentric, ecliptical coordinates.
     * Sets helio_lon, helio_lat and distance_sun of the object.
     */
    protected void calcHeliocentricIntern()
    {
        double E, true_anomaly, omega, u;
        E = Kepler.calcEccentricAnomaly(mOe.MA, mOe.EC);
        true_anomaly = Kepler.calcTrueAnomaly(mOe.EC, E);
        mDistance_sun = Kepler.calcDistanceSun(mOe.A, mOe.EC, E);
        omega = mOe.om - mOe.OM;
        u = omega + true_anomaly;
        double[] latLon = Astro.orbit2helioEcl(u, mOe.OM, mOe.IN);
        mHelio_lon = latLon[0];
        mHelio_lat = latLon[1];
    }

    /**
     * Calculate the geocentric, ecliptical coordinates ecliptic_lat,
     * ecliptic_lon and the geocentric, equatorial coordinates
     * declination and ra.
     */
    public void calcGeocentric(Planet earth)
    {
        double[] bld = Astro.helioEcl2geoEcl(earth.mHelio_lon, earth.mHelio_lat,
                                             earth.mDistance_sun,
                                             mHelio_lon, mHelio_lat,
                                             mDistance_sun);
        mEcliptic_lat = bld[0];
        mEcliptic_lon = bld[1];
        mDistance_earth = bld[2];
        double[] ad = Astro.geoEcl2geoEqua(bld[0], bld[1]);
        mRa = ad[0];
        mDeclination = ad[1];
    }

    protected abstract void calcHeliocentric(GregorianCalendar date);
}


abstract class PlanetCsv extends Planet
{
    private ArrayList<OrbitalElements> mElements;

    private static HashMap<String, ArrayList<OrbitalElements>> sPlanetOrbitalElements = new HashMap<>();
    public static void init(InputStream jupiterStream, InputStream saturnStream,
                            InputStream uranusStream, InputStream neptuneStream) throws IOException
    {
        sPlanetOrbitalElements.put(Jupiter.sName, readElements(jupiterStream));
        sPlanetOrbitalElements.put(Saturn.sName, readElements(saturnStream));
        sPlanetOrbitalElements.put(Uranus.sName, readElements(uranusStream));
        sPlanetOrbitalElements.put(Neptune.sName, readElements(neptuneStream));

        Planet.sPlanets = new Planet[] {new Mercury(), new Venus(), new Mars(),
                                        new Jupiter(), new Saturn(), new Uranus(),
                                        new Neptune()};
        Planet.sEarth = new Earth();
    }

    PlanetCsv(String name, double apparentMagnitude)
    {
        super(name, apparentMagnitude);
        mElements = PlanetCsv.sPlanetOrbitalElements.get(name);
    }

    /**
     * Read orbital elements from CSV file oeFile and return a list of
     * OrbitalElement objects.
     */
    private static ArrayList<OrbitalElements> readElements(InputStream istream) throws IOException
    {
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(istream));
        ArrayList<OrbitalElements> oeList = new ArrayList<>();
        String line;
        while (true)
        {
            line = fileReader.readLine();
            if (line.startsWith("$$SOE"))
            {
                break;
            }
        }
        while (true)
        {
            line = fileReader.readLine();
            if (line.startsWith("$$EOE"))
            {
                break;
            }
            String[] parts = line.split(",");
            if (parts.length != 14)
            {
                throw new IOException("invalid length: " + parts.length);
            }
            OrbitalElements oe = new OrbitalElements();
            oe.MA = Double.valueOf(parts[9].trim());
            oe.EC = Double.valueOf(parts[2].trim());
            oe.IN = Double.valueOf(parts[4].trim());
            oe.OM = Double.valueOf(parts[5].trim());
            oe.W  = Double.valueOf(parts[6].trim());
            oe.om = (oe.OM + oe.W) % 360;
            oe.A  = Double.valueOf(parts[11].trim());
            oe.N  = Double.valueOf(parts[8].trim());
            oe.JD = Double.valueOf(parts[0].trim());
            oeList.add(oe);
        }
        fileReader.close();
        return oeList;
    }

    /**
     * Find closest matching orbital elements for date.
     * Returns OrbitalElement object, and time difference in days.
     */
    protected OrbitalElements findClosestOe(GregorianCalendar date)
    {
        double last_diff = Double.MAX_VALUE;
        OrbitalElements e = null, last_e = null;
        Iterator<OrbitalElements> oeIt = mElements.iterator();
        while (oeIt.hasNext())
        {
            e = oeIt.next();
            double diff = Astro.julian_date(date) - e.JD;
            if (Math.abs(last_diff) < Math.abs(diff))
            {
                e = last_e;
                e.dayDiff = last_diff;
                break;
            }
            last_e = e;
            last_diff = diff;
        }
        return e;
    }

    protected void calcHeliocentric(GregorianCalendar date)
    {
        mOe = findClosestOe(date);
        mOe.MA += (mOe.N * mOe.dayDiff);
        calcHeliocentricIntern();
    }
}


class Mercury extends Planet
{
    Mercury()
    {
        super("Mercury", -1.9);
    }

    @Override
    public void calcHeliocentric(GregorianCalendar date)
    {
        mOe.MA = 174.7947 + 149472.5153  * Astro.julian_century(date);
        mOe.EC = 0.205634 + 0.000020 * Astro.julian_century(date);
        mOe.IN = 7.0048 + 0.0019 * Astro.julian_century(date);
        mOe.OM = 48.331 + 1.185 * Astro.julian_century(date);
        mOe.om = 77.4552 + 1.5555 * Astro.julian_century(date);
        mOe.A  = 0.387099;
        calcHeliocentricIntern();
    }
}

class Venus extends Planet
{
    Venus()
    {
        super("Venus", -4.6);
    }

    @Override
    public void calcHeliocentric(GregorianCalendar date)
    {
        mOe.MA = 50.4071 + 58517.8039 * Astro.julian_century(date);
        mOe.EC = 0.006773 - 0.000048 * Astro.julian_century(date);
        mOe.IN = 3.3946 + 0.0010 * Astro.julian_century(date);
        mOe.OM = 76.680 + 0.900 * Astro.julian_century(date);
        mOe.om = 131.5718 + 1.4080 * Astro.julian_century(date);
        mOe.A = 0.723332;
        calcHeliocentricIntern();
    }
}

class Earth extends Planet
{
    Earth()
    {
        super("Earth", 0);
    }

    @Override
    protected void calcHeliocentric(GregorianCalendar date)
    {
        mOe.MA = 357.5256 + 35999.0498 * Astro.julian_century(date);
        mOe.EC = 0.016709 - 0.000042 * Astro.julian_century(date);
        mOe.IN = 0.0;
        mOe.OM = 0.0;
        mOe.om = 102.9400 + 1.7192 * Astro.julian_century(date);
        mOe.A = 1.0;
        calcHeliocentricIntern();
    }
}

class Mars extends Planet
{
    Mars()
    {
        super("Mars", -2.91);
    }

    @Override
    public void calcHeliocentric(GregorianCalendar date)
    {
        mOe.MA = 19.3879 + 19139.8585 * Astro.julian_century(date);
        mOe.EC = 0.093405 - 0.000092 * Astro.julian_century(date);
        mOe.IN = 1.8496 - 0.0007 * Astro.julian_century(date);
        mOe.OM = 49.557 + 0.771 * Astro.julian_century(date);
        mOe.om = 336.0590 + 0.4438 * Astro.julian_century(date);
        mOe.A  = 1.523692;
        calcHeliocentricIntern();
    }
}

class Jupiter extends PlanetCsv
{
    public static String sName = "Jupiter";
    public Jupiter()
    {
        super(sName, -2.94);
    }
}

class Saturn extends PlanetCsv
{
    public static String sName = "Saturn";
    public Saturn()
    {
        super(sName, -0.45);
    }
}

class Uranus extends PlanetCsv
{
    public static String sName = "Uranus";
    public Uranus()
    {
        super(sName, 5.32);
    }
}

class Neptune extends PlanetCsv
{
    public static String sName = "Neptune";
    public Neptune()
    {
        super(sName, 7.78);
    }
}
