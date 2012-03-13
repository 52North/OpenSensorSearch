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

package org.n52.sir.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.namespace.QName;

import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML;
import net.opengis.sensorML.x101.SystemType;

import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.request.SirHarvestServiceRequest;
import org.n52.sir.sml.SMLConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Nüst
 * 
 */
public class Tools {

    private static Logger log = LoggerFactory.getLogger(Tools.class);

    /**
     * 
     * @param strings
     * @return
     */
    public static boolean atLeastOneIsNotEmpty(String[] strings) {
        for (String string : strings) {
            if ( !string.isEmpty())
                return true;
        }
        return false;
    }

    /**
     * 
     * @param text
     * @return
     */
    public static String escapeSQLString(String text) {
        String s = text;
        if (text.contains("'")) {
            if (log.isDebugEnabled())
                log.debug("Text contains character that has to be escaped before database insertion, namely ' .");
            s = s.replace("'", "\\'");
        }
        return s;
    }

    /**
     * 
     * @param t
     * @return
     */
    public static String getStackTrace(Throwable t) {
        StringBuilder sb = new StringBuilder("STACKTRACE: ");
        sb.append(t.toString());
        sb.append("\n");

        for (StackTraceElement element : t.getStackTrace()) {
            sb.append(element);
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * 
     * @param strings
     * @return
     */
    public static boolean noneEmpty(String[] strings) {
        for (String string : strings) {
            if (string.isEmpty())
                return false;
        }
        return true;
    }

    /**
     * 
     * remove trailing and leading white spaces, replace newline characters with space character.
     * 
     * @param value
     * @return
     */
    public static String simplifyString(String stringToSimplify) {
        String s = stringToSimplify.trim();
        s = s.replaceAll("\n", " ");
        return s;
    }

    /**
     * @param request
     * @return
     * @throws OwsExceptionReport
     */
    public static URI url2Uri(SirHarvestServiceRequest requestP) throws OwsExceptionReport {
        URI uri;
        try {
            uri = new URL(requestP.getServiceUrl()).toURI();
        }
        catch (MalformedURLException e) {
            String msg = "Error creating URI from given service url '" + requestP.getServiceUrl()
                    + "', cannot proceed with harvesting!";
            log.error(msg, e);
            throw new OwsExceptionReport(ExceptionCode.InvalidParameterValue,
                                         "ServiceURL",
                                         "Service url is not a valid URL.");
        }
        catch (URISyntaxException e) {
            String msg = "Error creating URI from given service url '" + requestP.getServiceUrl()
                    + "', cannot proceed with harvesting!";
            log.error(msg, e);
            throw new OwsExceptionReport(ExceptionCode.InvalidParameterValue,
                                         "ServiceURL",
                                         "Service url is not a valid URL.");
        }
        return uri;
    }

    /**
     * @param stToSet
     * @return
     */
    public static SensorMLDocument wrapSystemTypeInSensorMLDocument(SystemType stToSet) {
        SensorMLDocument document;
        document = SensorMLDocument.Factory.newInstance();
        SensorML newSensorML = document.addNewSensorML();
        newSensorML.setVersion(SMLConstants.SML_VERSION);
        AbstractProcessType abstractProcess = newSensorML.addNewMember().addNewProcess();
        SystemType newSystemType = (SystemType) abstractProcess.substitute(new QName(SMLConstants.NAMESPACE, "System"),
                                                                           SystemType.type);
        newSystemType.set(stToSet);
        return document;
    }
}
