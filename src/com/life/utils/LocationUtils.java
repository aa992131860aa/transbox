package com.life.utils;

public class LocationUtils {
    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 通过经纬度获取距离(单位：千米)
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double getDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        try {
            double radLat1 = rad(lat1);
            double radLat2 = rad(lat2);
            double a = radLat1 - radLat2;
            double b = rad(lng1) - rad(lng2);
            double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                    + Math.cos(radLat1) * Math.cos(radLat2)
                    * Math.pow(Math.sin(b / 2), 2)));
            s = s * EARTH_RADIUS;
            s = Math.round(s * 10000d) / 10000d;

            return s;
        } catch (Exception e) {

        }
        return 0;
    }

    /**
     * 通过经纬度获取距离(单位：千米)
     *
     * @return
     */
    public static double getDistance(String lat1Str, String lng1Str, String lat2Str,
                                     String lng2Str) {
        try {
            double lat1 = Double.parseDouble(lat1Str);
            double lng1 = Double.parseDouble(lng1Str);
            double lat2 = Double.parseDouble(lat2Str);
            double lng2 = Double.parseDouble(lng2Str);

            double radLat1 = rad(lat1);
            double radLat2 = rad(lat2);
            double a = radLat1 - radLat2;
            double b = rad(lng1) - rad(lng2);
            double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                    + Math.cos(radLat1) * Math.cos(radLat2)
                    * Math.pow(Math.sin(b / 2), 2)));
            s = s * EARTH_RADIUS;
            s = Math.round(s * 10000d) / 10000d;

            return s;
        } catch (Exception e) {

        }
        return 0;
    }
}  