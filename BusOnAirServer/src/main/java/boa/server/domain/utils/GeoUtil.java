package boa.server.domain.utils;

import com.vividsolutions.jts.geom.Coordinate;
import org.geotools.referencing.datum.DefaultEllipsoid;

/**
 * The DateUtil is used as a Utility Class for Dates.
 *
 * @author Robin Sharp
 */

/**
 * @return Distance in meters
 */


public class GeoUtil {


    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        // credo che non funziona bene, utilizzare getDistance2 per calcoli più accurati

        return DefaultEllipsoid.WGS84.orthodromicDistance(lon1, lat1, lon2, lat2);
    }


    public static double getDistance2(Coordinate c1, Coordinate c2) {
        return getDistance2(c1.y, c1.x, c2.y, c2.x);
    }

    public static double getDistance2(double lat1, double lon1, double lat2, double lon2) {
        // ritorna la distanza ortodromica in km

        if ((lat1 == lat2) && (lon1 == lon2))
            return 0;

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 1.609344;

        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
