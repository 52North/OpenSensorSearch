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

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.n52.sir.SirConfigurator;

/**
 * 
 * TODO move everything in configurator to external configuration file!
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class OpenSearchConfigurator {

    public static final String HOME_URL = "/SIR";

    private static SirConfigurator sirConfigurator = SirConfigurator.getInstance();

    private int capabilitiesCacheMaximumAgeSeconds = 60 * 60;

    /**
     * use contextually shortended urls (replacing long string identifiers with integer ids)
     */
    private boolean compressPermalinks = true;

    private String cssFile = "sir.css";

    private String feedAuthor = "Open Sensor Search by 52°North";

    // TODO move to external configuration file
    private String permalinkBaseURL = "http://sensorweb.demo.52north.org/sensorwebclient-webapp-stable/";

    private SimpleDateFormat permalinkDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    HashMap<String, String> responseFormats = new HashMap<String, String>();

    public void addResponseFormat(IOpenSearchListener listener) {
        this.responseFormats.put(listener.getMimeType(), listener.getName());
    }

    public int getCapabilitiesCacheMaximumAgeSeconds() {
        return this.capabilitiesCacheMaximumAgeSeconds;
    }

    public String getCharacterEncoding() {
        return sirConfigurator.getCharacterEncoding();
    }

    public String getCssFile() {
        return this.cssFile;
    }

    public String getFeedAuthor() {
        return this.feedAuthor;
    }

    public String getFullOpenSearchPath() {
        return getFullServicePath().toString() + getOpenSearchPath();
    }

    public URL getFullServicePath() {
        return sirConfigurator.getFullServicePath();
    }

    public String getHomeUrl() {
        return HOME_URL;
    }

    public String getOpenSearchPath() {
        return sirConfigurator.getOpenSearchPath();
    }

    public String getPermalinkBaseURL() {
        return this.permalinkBaseURL;
    }

    public SimpleDateFormat getPermalinkDateFormat() {
        return this.permalinkDateFormat;
    }

    public Map<String, String> getResponseFormats() {
        return this.responseFormats;
    }

    public boolean isCompressPermalinks() {
        return this.compressPermalinks;
    }

}
