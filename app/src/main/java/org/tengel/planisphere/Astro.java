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
 * The astro module contains basic astronomical calculations of angles, time and
 * coordinate systems.
 *
 * If not otherwise stated, the formulas are based on:
 * Montenbruck O.; Grundlagen der Ephemeridenrechnung; Spektrum Akademischer
 * Verlag, München, 7. Auflage (2005)
 *
 */

package org.tengel.planisphere;

import java.util.Calendar;

class Degree
{
    public Degree() {
        d = 0; m = 0; s = 0;
    }
    int d;
    int m;
    double s;
}

class Astro
{
    /**
     * Return the sine of x(measured in degrees).
     */
    static double sin(double x) {
        return Math.sin(Math.toRadians(x));
    }

    /**
     * Return the cosine of x (measured in degrees).
     */
    static double cos(double x)
    {
        return Math.cos(Math.toRadians(x));
    }

    /**
     * Return the tangent of x (measured in degrees).
     */
    static double tan(double x)
    {
        return Math.tan(Math.toRadians(x));
    }

    /**
     * Return the arc sine (measured in degrees) of x.
     */
    static double asin(double x)
    {
        return Math.toDegrees(Math.asin(x));
    }

    /**
     * Return the arc cosine (measured in degrees) of x.
     */
    static double acos(double x)
    {
        return Math.toDegrees(Math.acos(x));
    }

    /**
     * Return the arc tangent (measured in degrees) of x.
     */
    static double atan(double x)
    {
        return Math.toDegrees(Math.atan(x));
    }

    /**
     * Modulo, like Math.floorMod() but accepts double.
     */
    static double mod(double a, int b)
    {
        return a - b * Math.floor(a / b);
    }

    /**
     * Return the julian date. (valid after 15 Oct 1582)
     *
     * :param int sY: Year
     * :param int sM: Month
     * :param int sD: Day
     * :param float sUT: Hours in Universal Time
     * :rtype: float
     */
    static double julian_date(int sY, int sM, int sD, double sUT)
    {
        int y, m;
        double b, jd;
        if (sM <= 2)
        {
            y = sY - 1;
            m = sM + 12;
        }
        else
        {
            y = sY;
            m = sM;
        }
        b = Math.floor(y/400.0) - Math.floor(y / 100.0);
        jd = Math.floor(365.25 * y) + Math.floor(30.6001 * (m+1)) + b +
             1720996.5 + sD + sUT/24.0;
        return jd;
    }

    static double julian_date(int sY, int sM, int sD)
    {
        return julian_date(sY, sM, sD, 0);
    }

    /**
     * Return the julian date (as float) from a python datetime object.
     */
    static double julian_date(Calendar c)
    {
        return julian_date(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
                           c.get(Calendar.DAY_OF_MONTH),
                           (c.get(Calendar.HOUR_OF_DAY) +
                            (c.get(Calendar.MINUTE) / 60.0) +
                            (c.get(Calendar.SECOND) / 3600.0)));
    }

    /**
     * Return centuries since J2000.0 (as float) from a python datetime object.
     */
    static double julian_century(Calendar c)
    {
        return (julian_date(c) - 2451545.0) / 36525;
    }

    /**
     * Return the mean sidereal time of Greenwich in hours.
     *
     * :param int sY: Year
     * :param int sM: Month
     * :param int sD: Day
     * :param float sUT: Hours in Universal Time
     * :rtype: float
     */
    static double sidereal_time(int sY, int sM, int sD, double sUT)
    {
        double jd0 = julian_date(sY, sM, sD);
        double theta = (6.664520 + 0.0657098244 * (jd0 - 2451544.5) +
                (1.0027379093 * sUT));
        return mod(theta, 24);
    }

    /**
     * Convert geocentric, equatorial coordinates (right ascension, declination)
     * to horizontal coordinates (azimuth, elevation).
     *
     * :param float t: Hour angle in degree.
     *                (t=local_sidereal_time - right_ascension)
     * :param float phi: Geographical latitude of observer. (degree)
     * :param float delta: Declination (degree)
     * :return: Horizontal coordinates (azimuth, elevation) in degree
     * :rtype: list(float)
     */
    static Double[] geoEqua2geoHori(double t, double phi, double delta)
    {
        double x, y, z, r, p, beta, lambda_helper, lambda;
        x = sin(phi) * cos(delta) * cos(t) - cos(phi) * sin(delta);
        y = cos(delta) * sin(t);
        z = sin(phi) * sin(delta) + cos(phi) * cos(delta) * cos(t);
        r = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        p = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        beta = atan(z / p);
        lambda_helper = 2 * atan(y / (Math.abs(x) + Math.sqrt(Math.pow(x, 2) +
                                                              Math.pow(y, 2))));
        if (x == 0 && y == 0)
        {
            lambda = 0;
        }
        else if (x >= 0 && y >= 0)
        {
            lambda = lambda_helper;
        }
        else if (x >= 0 && y < 0)
        {
            lambda = 360 + lambda_helper;
        }
        else if (x < 0)
        {
            lambda = 180 - lambda_helper;
        }
        else
        {
            throw new RuntimeException("should not happen");
        }
        Double[] ret =  {lambda, beta};
        return ret;
    }

    /**
     * Convert coordinates of the orbital plane to heliocentric, ecliptic
     * coordinates (heliocentric ecliptic latitude, heliocentric ecliptic
     * longitude).
     *
     * :param float u: Angular position of the planet. Angle between ascending
     *                 node and planet.
     *                 (u = argument_of_periapsis + true_anomaly)
     * :param float Omega: Longitude of the ascending node (degrees).
     * :param float i: Inclination. Angle between the orbital plane and the
     *                 ecliptic.
     * :return: Heliocentric ecliptic longitude (0..360),
     *          heliocentric ecliptic latitude (-90..+90)
     */
    static double[] orbit2helioEcl(double u, double Omega, double i)
    {
        double b, l, lo1, lo2;
        b = asin(sin(u) * sin(i));
        l = acos(cos(u) / cos(b)) + Omega;
        lo1 = acos(cos(u) / cos(b));
        lo2 = asin((sin(u)*cos(i))/cos(b));
        if (lo2 < 0)
        {
            l = 360.0 - (lo1 - Omega);
        }
        if (l > 360)
        {
            l = l - 360;
        }
        double[] rt = {l, b};
        return rt;
    }

    /**
     * Convert heliocentric, ecliptic coordinates to geocentric, ecliptic
     * coordinates.
     *
     * :param float eLon: Heliocentric, ecliptic longitude of earth (degree)
     * :param float eLat: Heliocentric, ecliptic latitude of earth (degree)
     * :param float eDist: Earth distance from sun (AU)
     * :param float pLon: Heliocentric, ecliptic longitude of planet (degree)
     * :param float pLat: Heliocentric, ecliptic latitude of planet (degree)
     * :param float pDist: Distance from sun (AU)
     * :return: Geocentric ecliptic coordinates
     *          lamda (ecliptic lon), beta (ecliptic lat), Delta (earth distance)
     * :rtype: list(float)
     */
    static double[] helioEcl2geoEcl(double eLon, double eLat, double eDist,
                                    double pLon, double  pLat, double pDist)
    {
        double x, y, z;
        x = pDist * cos(pLat) * cos(pLon) - eDist * cos(eLat) * cos(eLon);
        y = pDist * cos(pLat) * sin(pLon) - eDist * cos(eLat) * sin(eLon);
        z = pDist * sin(pLat)             - eDist * sin(eLat);
        return cart2sphe(x, y, z);
    }

    /**
     * Convert spherical coordinates (beta, lambda, r) to cartesian coordinates
     * (x, y, z).
     *
     * :return: Cartesian coordinates x, y, z.
     */
    static double[] sphe2cart(double beta, double lamda, double r)
    {
        double x, y, z;
        x = r * cos(beta) * cos(lamda);
        y = r * cos(beta) * sin(lamda);
        z = r * sin(beta);
        double[] rc = {x, y, z};
        return rc;
    }

    /**
     * Convert cartesian coordinates (x, y, z) to  spherical coordinate
     * (beta, lambda, r).

     * :return: Spherical coordinates beta, lambda, r
     */
    static double[] cart2sphe(double x, double y, double z)
    {
        double r = 0, p = 0, beta = 0, phi = 0, lambda;
        r = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        p = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        if (p == 0)
        {
            if (z > 0)
            {
                beta = 90;
            }
            else if (z == 0)
            {
                beta = 0;
            }
            else if (z < 0)
            {
                beta = -90;
            }
        }
        else
        {
            beta = atan(z / p);
        }
        phi = 2 * atan(y / (Math.abs(x) + Math.sqrt(Math.pow(x, 2) +
                                                    Math.pow(y, 2))));
        if (x == 0 && y == 0)
        {
            lambda = 0;
        }
        else if (x >= 0 && y >= 0)
        {
            lambda = phi;
        }
        else if (x >= 0 && y < 0)
        {
            lambda = 360 + phi;
        }
        else if (x < 0)
        {
            lambda = 180 - phi;
        }
        else {
            throw new RuntimeException("should not happen");
        }
        double[] rc = {beta, lambda, r};
        return rc;
    }

    /**
     * Convert geocentric, ecliptic coordinates (ecl. lat, ecl. long) to
     * geocentric equatorial coordinates (right ascension, declination).
     *
     * The inclination of the ecliptic is fixed for J2000.
     *
     * :param float beta: Geocentric ecliptic latitude beta (degree)
     * :param float lamb: Geocentric ecliptic longitude lambda (degree)
     * :return: Equatorial coordinates
     *          (right ascension alpha, declination delta) in degree
     * :rtype: list(float)
     */
    static double[] geoEcl2geoEqua(double beta, double lamb)
    {
        double epsilon = 23.4392916667;
        double x, y, z;
        x = cos(beta) * cos(lamb);
        y = cos(epsilon) * cos(beta) * sin(lamb) - sin(epsilon) * sin(beta);
        z = sin(epsilon) * cos(beta) * sin(lamb) + cos(epsilon) * sin(beta);
        double[] polar = cart2sphe(x, y, z);
        double[] rc = {polar[1], polar[0]};
        return rc;
    }

    /**
     * Calculate the geocentric, equatorial position (ra, dec) of the sun for a
     * julian date.
     * Source: https://en.wikipedia.org/wiki/Position_of_the_Sun
     *
     * :param float jd: Julian date
     * :return: Geocentric, equatorial coordinates
     *          (right ascension alpha, declination delta) in degree
     */
    static double[] calcPositionSun(double jd)
    {
        double n, L, g, lamb;
        n = jd - 2451545.0;
        L = (280.460 + 0.9856474 * n) % 360;
        g = (357.528 + 0.9856003 * n) % 360;
        lamb = L + 1.915 * sin(g) + 0.020 * sin(2 * g); // geoc., ecliptic lon
        return geoEcl2geoEqua(0, lamb); // ra, dec
    }

    /**
     * Calculate the geocentric, equatorial position (ra, dec) of the moon for a
     * julian date.
     *
     * :param float jd: Julian date
     * :return: Geocentric, equatorial coordinates
     *         (right ascension alpha, declination delta) in degree
     */
    static double[] calcPositionMoon(double jd)
    {
        double T, L0, l, l_, F, D, L1, B, lamb, beta;
        T  = (jd - 2451545.0) / 36525;
        L0 = (218.31665 + 481267.88134 * T - 0.001327 * Math.pow(T, 2)) % 360.0;
        l  = (134.96341 + 477198.86763 * T + 0.008997 * Math.pow(T, 2)) % 360.0;
        l_ = (357.52911 + 35999.05029 * T + 0.000154 * Math.pow(T, 2)) % 360.0;
        F  = (93.27210 + 483202.01753 * T - 0.003403 * Math.pow(T, 2)) % 360.0;
        D  = (297.85020 + 445267.11152 * T - 0.001630 * Math.pow(T, 2)) % 360.0;
        L1 = (22640 * sin(l) + 769 * sin(2 * l) + 36 * sin(3 * l)
              -4586 * sin(l - 2 * D)
              +2370 * sin(2 * D)
              -668  * sin(l_)
              -412  * sin(2 * F)
              -212  * sin(2 * l - 2 * D)
              -206  * sin(l + l_ - 2 * D)
              +192  * sin(l + 2 * D)
              -165  * sin(l_ - 2 * D)
              +148  * sin(l - l_)
              -125  * sin(D)
              -110  * sin(l + l_)
              -55   * sin(2 * F - 2 * D));
        lamb = L0 + (L1 / 60 / 60); // geocentric, ecliptic longitude
        B = (18520 * sin(F + lamb - L0 + 0.114 * sin(2 * F) + 0.150 * sin(l_))
             -526  * sin(+F - 2 * D)
             +44   * sin(+l + F - 2 * D)
             -31   * sin(-l + F - 2 * D)
             -25   * sin(-2 * l + F)
             -23   * sin(+l_ + F - 2 * D)
             +21   * sin(-l + F)
             +11   * sin(-l_ + F - 2 * D));
        beta = B / 60 / 60; // geocentric, ecliptic latitude
        return geoEcl2geoEqua(beta, lamb); // ra, dec
    }

    /**
     * Convert degree to sexagesimal deg, min, sec.
     */
    static Degree deg2sex(double degree)
    {
        Degree d = new Degree();
        d.d = (int) degree;
        double m = (degree - d.d) * 60.0;
        d.m = (int) m;
        d.s = (m - d.m) * 60.0;
        return d;
    }

    /**
     * Convert sexagesimal deg, min, sec to degree float.
     */
    static double sex2deg(Degree d)
    {
        return d.d + (d.m / 60.0) + (d.s / 60.0 / 60.0);
    }
}
