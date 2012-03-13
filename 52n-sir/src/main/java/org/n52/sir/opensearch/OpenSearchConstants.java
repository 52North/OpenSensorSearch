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

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO see what is constant, and what should be in {@link OpenSearchConfigurator}.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class OpenSearchConstants {

    public static final String ACCEPT_PARAMETER = "httpAccept";

    public static final String BOX_PARAM = "box";

    public static final String CDATA_END_TAG = "]";

    public static final String CDATA_START_TAG = "![CDATA[";

    public static final double DEFAULT_RADIUS = 1000.0d;

    public static final double EARTH_RADIUS_METERS = 6.3675 * 1000000;

    public static final String GEOMETRY_PARAM = "geometry";

    public static final String LAT_PARAM = "lat";

    public static final String LON_PARAM = "lon";

    public static final int MAX_GET_URL_CHARACTER_COUNT = 2000; // http://stackoverflow.com/questions/417142/what-is-the-maximum-length-of-a-url

    public static final String NAME_PARAM = "name";

    public static final String QUERY_PARAMETER = "q";

    public static final String RADIUS_PARAM = "radius";

    public static Collection<String> TIME_SERIES_SERVICE_TYPES = new ArrayList<String>();

    public static final String X_DEFAULT_MIME_TYPE = "text/html";

    static {
        TIME_SERIES_SERVICE_TYPES.add("SOS");
        TIME_SERIES_SERVICE_TYPES.add("OGC:SOS");
    }

    public static final String MIME_TYPE_PLAIN = "text/plain";

}
