/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.sir.opensearch;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.n52.sir.datastructure.SirBoundingBox;
import org.n52.sir.util.ext.GeoLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestDismantler {

    private static final Logger log = LoggerFactory.getLogger(RequestDismantler.class);

    public SirBoundingBox getBoundingBox(HttpServletRequest req) {
        Set< ? > keySet = req.getParameterMap().keySet();
        boolean containsName = keySet.contains(OpenSearchConstants.NAME_PARAM);
        boolean containsLatLon = keySet.contains(OpenSearchConstants.LAT_PARAM)
                && keySet.contains(OpenSearchConstants.LON_PARAM);
        boolean containsRadius = keySet.contains(OpenSearchConstants.RADIUS_PARAM);
        // boolean containsBox = keySet.contains("box");
        // boolean containsGeometry = keySet.contains("geometry");

        if (containsLatLon && containsName) {
            log.warn("More than one location definition, using latlon");
            containsName = false;
        }

        if (containsName) {
            return getBoundingBoxFromGazetteer(req.getParameter(OpenSearchConstants.NAME_PARAM));
        }
        else if (containsLatLon) {
            double radius, lat, lon;

            try {
                if ( !containsRadius) {
                    radius = OpenSearchConstants.DEFAULT_RADIUS;
                    log.debug("No radius given, falling back to default {}",
                              Double.valueOf(OpenSearchConstants.DEFAULT_RADIUS));
                }
                else
                    radius = Double.parseDouble(req.getParameter(OpenSearchConstants.RADIUS_PARAM));

                lat = Double.parseDouble(req.getParameter(OpenSearchConstants.LAT_PARAM));
                lon = Double.parseDouble(req.getParameter(OpenSearchConstants.LON_PARAM));
            }
            catch (NumberFormatException e) {
                log.error("Could not parse lat, lon or radius from request paramters: "
                                  + Arrays.deepToString(req.getParameterMap().values().toArray()),
                          e);
                return null;
            }

            return getBoundingBoxFromLatLon(lat, lon, radius);
        }

        return null;
    }

    private SirBoundingBox getBoundingBoxFromGazetteer(String parameter) {
        log.error("gazetteer not implemented yet, discarding parameter {}", parameter);
        return null;
    }

    /**
     * 
     * map lat lon und radius to a bouding box
     * 
     * @param lat
     * @param lon
     * @param radius
     * @return
     */
    public SirBoundingBox getBoundingBoxFromLatLon(double lat, double lon, double radius) {
        // stackoverflow.com/questions/1689096/calculating-bounding-box-a-certain-distance-away-from-a-lat-long-coordinate-in-j

        GeoLocation loc = GeoLocation.fromDegrees(lat, lon);
        GeoLocation[] boundingCoordinates = loc.boundingCoordinates(radius, OpenSearchConstants.EARTH_RADIUS_METERS);

        double east = boundingCoordinates[1].getLongitudeInDegrees();
        double south = boundingCoordinates[0].getLatitudeInDegrees();
        double west = boundingCoordinates[0].getLongitudeInDegrees();
        double north = boundingCoordinates[1].getLatitudeInDegrees();
        SirBoundingBox box = new SirBoundingBox(east, south, west, north);

        return box;
    }

    public Calendar[] getStartEnd(HttpServletRequest req) {
        log.error("start-end not implemented yet, discarding request {}", req);
        return null;
    }

    public boolean requestContainsGeoParameters(HttpServletRequest req) {
        Set< ? > keySet = req.getParameterMap().keySet();
        boolean containsName = keySet.contains(OpenSearchConstants.NAME_PARAM);
        boolean containsLatLon = keySet.contains(OpenSearchConstants.LAT_PARAM)
                && keySet.contains(OpenSearchConstants.LON_PARAM);
        boolean containsRadius = keySet.contains(OpenSearchConstants.RADIUS_PARAM);
        boolean containsBox = keySet.contains(OpenSearchConstants.BOX_PARAM);
        boolean containsGeometry = keySet.contains(OpenSearchConstants.GEOMETRY_PARAM);

        return containsName | containsLatLon | containsRadius | containsBox | containsGeometry;
    }

    public boolean requestContainsTime(HttpServletRequest req) {
        log.error("time not implemented yet, discarding req {}", req);
        return false;
    }

}
