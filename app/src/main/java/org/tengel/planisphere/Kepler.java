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
