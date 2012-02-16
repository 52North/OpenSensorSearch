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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class ListenersTools {

    private static Logger log = LoggerFactory.getLogger(ListenersTools.class);

    /**
     * 
     * @param version
     * @throws OwsExceptionReport
     */
    public static void checkVersionParameter(String version) throws OwsExceptionReport {
        String[] acceptedServiceVersions = SirConfigurator.getInstance().getAcceptedServiceVersions();
        List<String> versions = Arrays.asList(acceptedServiceVersions);
        
        if (version == null || !versions.contains(version)) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 "version",
                                 "The Parameter 'version' does not contain the version of this SIR: '"
                                         + SirConfigurator.getInstance().getServiceVersion() + "'");
            log.error("The accepted versions parameter is incorrect.", se);
            throw se;
        }
    }

    /**
     * 
     * @param service
     * @throws OwsExceptionReport
     */
    public static void checkServiceParameter(String service) throws OwsExceptionReport {
        if (service == null || service.equalsIgnoreCase("NOT_SET")) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.MissingParameterValue,
                                 "service",
                                 "The mandatory parameter 'service' was not found in the request!");
            log.error("Service parameter was not found in the request");
            throw se;
        }

        if ( !service.equalsIgnoreCase(SirConstants.SERVICE_NAME)) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 "service",
                                 "The value of the mandatory parameter 'service' must be '" + SirConstants.SERVICE_NAME
                                         + "'. Delivered value was: " + service);
            log.error("Service parameter was wrong");
            throw se;
        }
    }

    /**
     * 
     * creates a GET request to retrieve the sensor description of the given sensor,
     * 
     * @param sensorIdInSir
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String createSensorDescriptionURL(String sensorIdInSir) throws UnsupportedEncodingException {
        SirConfigurator sc = SirConfigurator.getInstance();

        StringBuilder sb = new StringBuilder();
        sb.append(sc.getServiceUrl());
        sb.append("?");
        sb.append("service=");
        sb.append(SirConstants.SERVICE_NAME);
        sb.append("&amp;");
        sb.append(SirConstants.GETVERSIONPARAM);
        sb.append("=");
        sb.append(sc.getServiceVersion());
        sb.append("&amp;");
        sb.append(SirConstants.GETREQUESTPARAM);
        sb.append("=");
        sb.append(SirConstants.Operations.DescribeSensor.name());
        sb.append("&amp;");
        sb.append(SirConstants.GetDescSensorParams.SENSORIDINSIR.name());
        sb.append("=");
        sb.append(sensorIdInSir);

        return URLEncoder.encode(sb.toString(), sc.getCharacterEncoding());
    }
}
