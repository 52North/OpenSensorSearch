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
package org.n52.sir.listener;

import org.apache.xmlbeans.XmlObject;
import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.IDescribeSensorDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirDescribeSensorRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirDescribeSensorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class DescribeSensorListener implements ISirRequestListener {

    /**
     * the logger, used to log exceptions and additionally information
     */
    private static Logger log = LoggerFactory.getLogger(DescribeSensorListener.class);

    /**
     * the name of the operation this listener handles
     */
    private static final String OPERATION_NAME = SirConstants.Operations.DescribeSensor.name();

    /**
     * the data access object for the describeSensor operation
     */
    private IDescribeSensorDAO descSensDao;

    /**
     * 
     * @throws OwsExceptionReport
     */
    public DescribeSensorListener() throws OwsExceptionReport {
        SirConfigurator configurator = SirConfigurator.getInstance();

        IDAOFactory factory = configurator.getFactory();
        IDescribeSensorDAO descSensDAO = null;

        try {
            descSensDAO = factory.describeSensorDAO();
        }
        catch (OwsExceptionReport se) {
            log.error("Error while creating the describeSensorDAO", se);
            throw se;
        }
        this.descSensDao = descSensDAO;
    }

    /*
     * (non-Javadoc)
     * @see org.n52.sir.listener.ISirRequestListener#getOperationName()
     */
    @Override
    public String getOperationName() {
        return DescribeSensorListener.OPERATION_NAME;
    }

    /*
     * (non-Javadoc)
     * @see org.n52.sir.listener.ISirRequestListener#receiveRequest(org.n52.sir.request.AbstractSirRequest)
     */
    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        SirDescribeSensorRequest descSensReq = (SirDescribeSensorRequest) request;

        SirDescribeSensorResponse response = new SirDescribeSensorResponse();

        try {
            XmlObject sensorML = this.descSensDao.getSensorDescription(descSensReq.getSensorIdInSir());
            if (sensorML != null) {
                response.setSensorML(sensorML);
            }
            else {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.InvalidRequest, null, "Unknown sensor ID in Sir! Given ID: "
                        + descSensReq.getSensorIdInSir());
                log.debug("Unknown sensor ID in Sir! Given ID: " + descSensReq.getSensorIdInSir());
                throw se;
            }
        }
        catch (OwsExceptionReport se) {
            return new ExceptionResponse(se.getDocument());
        }
        return response;
    }
}