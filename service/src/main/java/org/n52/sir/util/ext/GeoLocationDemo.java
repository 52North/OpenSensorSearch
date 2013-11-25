/**
 * http://creativecommons.org/licenses/by/3.0/
 * 
 * Jan Philip Matuschek
 */

package org.n52.sir.util.ext;

/**
 * <p>
 * See <a href="http://JanMatuschek.de/LatitudeLongitudeBoundingCoordinates#Java">
 * http://JanMatuschek.de/LatitudeLongitudeBoundingCoordinates#Java</a> for the GeoLocation class referenced
 * from this code.
 * </p>
 * 
 * @author Jan Philip Matuschek
 * @version 26 May 2010
 */
public class GeoLocationDemo {

    /**
     * @param radius
     *        radius of the sphere.
     * @param location
     *        center of the query circle.
     * @param distance
     *        radius of the query circle.
     * @param connection
     *        an SQL connection.
     * @return places within the specified distance from location.
     */
    public static java.sql.ResultSet findPlacesWithinDistance(double radius,
                                                              GeoLocation location,
                                                              double distance,
                                                              java.sql.Connection connection) throws java.sql.SQLException {

        GeoLocation[] boundingCoordinates = location.boundingCoordinates(distance, radius);
        boolean meridian180WithinDistance = boundingCoordinates[0].getLongitudeInRadians() > boundingCoordinates[1].getLongitudeInRadians();

        java.sql.PreparedStatement statement = connection.prepareStatement("SELECT * FROM Places WHERE (Lat >= ? AND Lat <= ?) AND (Lon >= ? "
                + (meridian180WithinDistance ? "OR" : "AND")
                + " Lon <= ?) AND "
                + "acos(sin(?) * sin(Lat) + cos(?) * cos(Lat) * cos(Lon - ?)) <= ?");
        statement.setDouble(1, boundingCoordinates[0].getLatitudeInRadians());
        statement.setDouble(2, boundingCoordinates[1].getLatitudeInRadians());
        statement.setDouble(3, boundingCoordinates[0].getLongitudeInRadians());
        statement.setDouble(4, boundingCoordinates[1].getLongitudeInRadians());
        statement.setDouble(5, location.getLatitudeInRadians());
        statement.setDouble(6, location.getLatitudeInRadians());
        statement.setDouble(7, location.getLongitudeInRadians());
        statement.setDouble(8, distance / radius);
        return statement.executeQuery();
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) {

        double earthRadius = 6371.01;
        GeoLocation myLocation = GeoLocation.fromRadians(1.3963, -0.6981);
        double distance = 1000;
        // java.sql.Connection connection = ...;
        //
        // java.sql.ResultSet resultSet = findPlacesWithinDistance(
        // earthRadius, myLocation, distance, connection);
        //
        // ...;
    }
}
