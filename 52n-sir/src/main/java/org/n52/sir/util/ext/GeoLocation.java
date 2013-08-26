/**
 * http://creativecommons.org/licenses/by/3.0/
 * 
 * Jan Philip Matuschek
 */

package org.n52.sir.util.ext;

/**
 * <p>
 * Represents a point on the surface of a sphere. (The Earth is almost spherical.)
 * </p>
 * 
 * <p>
 * To create an instance, call one of the static methods fromDegrees() or fromRadians().
 * </p>
 * 
 * <p>
 * This code was originally published at <a
 * href="http://JanMatuschek.de/LatitudeLongitudeBoundingCoordinates#Java">
 * http://JanMatuschek.de/LatitudeLongitudeBoundingCoordinates#Java</a>.
 * </p>
 * 
 * @author Jan Philip Matuschek
 * @version 22 September 2010
 */
public class GeoLocation {

    private static final double MAX_LAT = Math.toRadians(90d); // PI/2
    private static final double MAX_LON = Math.toRadians(180d); // PI

    private static final double MIN_LAT = Math.toRadians( -90d); // -PI/2
    private static final double MIN_LON = Math.toRadians( -180d); // -PI

    /**
     * @param latitude
     *        the latitude, in degrees.
     * @param longitude
     *        the longitude, in degrees.
     */
    public static GeoLocation fromDegrees(double latitude, double longitude) {
        GeoLocation result = new GeoLocation();
        result.radLat = Math.toRadians(latitude);
        result.radLon = Math.toRadians(longitude);
        result.degLat = latitude;
        result.degLon = longitude;
        result.checkBounds();
        return result;
    }

    /**
     * @param latitude
     *        the latitude, in radians.
     * @param longitude
     *        the longitude, in radians.
     */
    public static GeoLocation fromRadians(double latitude, double longitude) {
        GeoLocation result = new GeoLocation();
        result.radLat = latitude;
        result.radLon = longitude;
        result.degLat = Math.toDegrees(latitude);
        result.degLon = Math.toDegrees(longitude);
        result.checkBounds();
        return result;
    }

    private double degLat; // latitude in degrees
    private double degLon; // longitude in degrees

    private double radLat; // latitude in radians

    private double radLon; // longitude in radians

    private GeoLocation() {
    }

    /**
     * <p>
     * Computes the bounding coordinates of all points on the surface of a sphere that have a great circle
     * distance to the point represented by this GeoLocation instance that is less or equal to the distance
     * argument.
     * </p>
     * <p>
     * For more information about the formulae used in this method visit <a
     * href="http://JanMatuschek.de/LatitudeLongitudeBoundingCoordinates">
     * http://JanMatuschek.de/LatitudeLongitudeBoundingCoordinates</a>.
     * </p>
     * 
     * @param distance
     *        the distance from the point represented by this GeoLocation instance. Must me measured in the
     *        same unit as the radius argument.
     * @param radius
     *        the radius of the sphere, e.g. the average radius for a spherical approximation of the figure of
     *        the Earth is approximately 6371.01 kilometers.
     * @return an array of two GeoLocation objects such that:
     *         <ul>
     *         <li>The latitude of any point within the specified distance is greater or equal to the latitude
     *         of the first array element and smaller or equal to the latitude of the second array element.</li>
     *         <li>If the longitude of the first array element is smaller or equal to the longitude of the
     *         second element, then the longitude of any point within the specified distance is greater or
     *         equal to the longitude of the first array element and smaller or equal to the longitude of the
     *         second array element.</li>
     *         <li>If the longitude of the first array element is greater than the longitude of the second
     *         element (this is the case if the 180th meridian is within the distance), then the longitude of
     *         any point within the specified distance is greater or equal to the longitude of the first array
     *         element <strong>or</strong> smaller or equal to the longitude of the second array element.</li>
     *         </ul>
     */
    public GeoLocation[] boundingCoordinates(double distance, double radius) {

        if (radius < 0d || distance < 0d)
            throw new IllegalArgumentException();

        // angular distance in radians on a great circle
        double radDist = distance / radius;

        double minLat = this.radLat - radDist;
        double maxLat = this.radLat + radDist;

        double minLon, maxLon;
        if (minLat > MIN_LAT && maxLat < MAX_LAT) {
            double deltaLon = Math.asin(Math.sin(radDist) / Math.cos(this.radLat));
            minLon = this.radLon - deltaLon;
            if (minLon < MIN_LON)
                minLon += 2d * Math.PI;
            maxLon = this.radLon + deltaLon;
            if (maxLon > MAX_LON)
                maxLon -= 2d * Math.PI;
        }
        else {
            // a pole is within the distance
            minLat = Math.max(minLat, MIN_LAT);
            maxLat = Math.min(maxLat, MAX_LAT);
            minLon = MIN_LON;
            maxLon = MAX_LON;
        }

        return new GeoLocation[] {fromRadians(minLat, minLon), fromRadians(maxLat, maxLon)};
    }

    private void checkBounds() {
        if (this.radLat < MIN_LAT || this.radLat > MAX_LAT || this.radLon < MIN_LON || this.radLon > MAX_LON)
            throw new IllegalArgumentException();
    }

    /**
     * Computes the great circle distance between this GeoLocation instance and the location argument.
     * 
     * @param radius
     *        the radius of the sphere, e.g. the average radius for a spherical approximation of the figure of
     *        the Earth is approximately 6371.01 kilometers.
     * @return the distance, measured in the same unit as the radius argument.
     */
    public double distanceTo(GeoLocation location, double radius) {
        return Math.acos(Math.sin(this.radLat) * Math.sin(location.radLat) + Math.cos(this.radLat)
                * Math.cos(location.radLat) * Math.cos(this.radLon - location.radLon))
                * radius;
    }

    /**
     * @return the latitude, in degrees.
     */
    public double getLatitudeInDegrees() {
        return this.degLat;
    }

    /**
     * @return the latitude, in radians.
     */
    public double getLatitudeInRadians() {
        return this.radLat;
    }

    /**
     * @return the longitude, in degrees.
     */
    public double getLongitudeInDegrees() {
        return this.degLon;
    }

    /**
     * @return the longitude, in radians.
     */
    public double getLongitudeInRadians() {
        return this.radLon;
    }

    @Override
    public String toString() {
        return "(" + this.degLat + "\u00B0, " + this.degLon + "\u00B0) = (" + this.radLat + " rad, " + this.radLon
                + " rad)";
    }

}
