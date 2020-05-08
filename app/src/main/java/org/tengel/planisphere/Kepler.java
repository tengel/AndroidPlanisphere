/**
 * The kepler module calculates the position of the planets by solving the two-body
 * problem.
 *
 * The calculations are based on:
 * Montenbruck O.; Grundlagen der Ephemeridenrechnung; Spektrum Akademischer
 * Verlag, München, 7. Auflage (2005)
 *
 * ----
 */

package org.tengel.planisphere;

public class Kepler
{
    /**
     * Calculates the eccentric anomaly in degrees from mean anomaly and
     * eccentricity. Calculation is done iteratively until the precision of
     * epsilon is reached.
     */
    public static double calcEccentricAnomaly(double meanAnomaly, double eccentricity)
    {
        double epsilon = 0.00001;
        double E = meanAnomaly;
        double Enext;
        while(true)
        {
            Enext = (E -
                     ((meanAnomaly - E + (180/Math.PI) * eccentricity * Astro.sin(E)) /
                      (eccentricity * Astro.cos(E) - 1)));
            if (Math.abs(E - Enext) < epsilon)
            {
                return Enext;
            }
            E = Enext;
        }
    }


    /**
     * Calculate the true anomaly.
     */
    public static double calcTrueAnomaly(double eccentricity, double eccentricAnomaly)
    {
        double t, v;
        t = (Math.sqrt((1 + eccentricity) / (1 - eccentricity)) *
             Astro.tan(eccentricAnomaly / 2));
        v = Astro.atan(t) * 2;
        if (v < 0)
        {
            return 360 + v;
        }
        else
        {
            return v;
        }
    }


    /**
     * Calculate the distance from the planet to the sun in AU.
     */
    public static double calcDistanceSun(double semiMajorAxis, double eccentricity,
                                         double eccentricAnomaly)
    {
        return semiMajorAxis * (1 - eccentricity * Astro.cos(eccentricAnomaly));
    }
}
