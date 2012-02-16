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

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.IInsertSensorStatusDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirInsertSensorStatusRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirInsertSensorStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class InsertSensorStatusListener implements ISirRequestListener {

    /**
     * the logger, used to log exceptions and additionally information
     */
    private static Logger log = LoggerFactory.getLogger(InsertSensorStatusListener.class);

    /**
     * the data access object for the insertSensorStatus operation
     */
    private IInsertSensorStatusDAO insSensStatDao;

    private static final String OPERATION_NAME = SirConstants.Operations.InsertSensorStatus.name();

    public InsertSensorStatusListener() throws OwsExceptionReport {
        SirConfigurator configurator = SirConfigurator.getInstance();

        IDAOFactory factory = configurator.getFactory();

        try {
            this.insSensStatDao = factory.insertSensorStatusDAO();
        }
        catch (OwsExceptionReport se) {
            log.error("Error while creating the insertSensorStatusDAO", se);
            throw se;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ISirRequestListener#getOperationName()
     */
    @Override
    public String getOperationName() {
        return InsertSensorStatusListener.OPERATION_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.n52.sir.ISirRequestListener#receiveRequest(org.n52.sir.request. AbstractSirRequest)
     */
    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        SirInsertSensorStatusRequest insSensStatReq = (SirInsertSensorStatusRequest) request;

        SirInsertSensorStatusResponse response = new SirInsertSensorStatusResponse();

        try {
            String sensorIdInSir = this.insSensStatDao.insertSensorStatus(insSensStatReq.getSensIdent(),
                                                                          insSensStatReq.getStatus());
            if (sensorIdInSir != null) {
                response.setSensorIdInSir(sensorIdInSir);
            }
            else {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.InvalidParameterValue, null, "Unknown identification!");
                if (log.isDebugEnabled())
                    log.debug("Unknown identification: " + insSensStatReq.getSensIdent());
                throw se;
            }
        }
        catch (OwsExceptionReport se) {
            return new ExceptionResponse(se.getDocument());
        }
        response.setStatusUpdateSuccessful(true);
        return response;
    }
}
