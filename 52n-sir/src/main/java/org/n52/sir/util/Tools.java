/**
 * ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.sir.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    /**
     * 
     * @param is
     * @return
     * @throws Exception
     */
    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ( (line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        is.close();
        return sb.toString();
    }
}
