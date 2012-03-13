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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.datastructure.SirSearchCriteria_Phenomenon;
import org.n52.sir.datastructure.SirSensorIDInSir;
import org.n52.sir.datastructure.SirSensorIdentification;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.datastructure.SirStatusDescription;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.IGetSensorStatusDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirGetSensorStatusRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirGetSensorStatusResponse;
import org.n52.sir.util.SORTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class GetSensorStatusListener implements ISirRequestListener {

    private static Logger log = LoggerFactory.getLogger(GetSensorStatusListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.GetSensorStatus.name();

    /**
     * the data access object for the get sensor status operation
     */
    private IGetSensorStatusDAO getSensStatDao;

    public GetSensorStatusListener() throws OwsExceptionReport {
        SirConfigurator configurator = SirConfigurator.getInstance();

        IDAOFactory factory = configurator.getFactory();

        try {
            this.getSensStatDao = factory.getSensorStatusDAO();
        }
        catch (OwsExceptionReport se) {
            log.error("Error while creating the getSensorStatusDAO", se);
            throw se;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.listener.ISirRequestListener#getOperationName()
     */
    @Override
    public String getOperationName() {
        return GetSensorStatusListener.OPERATION_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.listener.ISirRequestListener#receiveRequest(org.n52.sir.request .AbstractSirRequest)
     */
    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        SirGetSensorStatusRequest getSensStatReq = (SirGetSensorStatusRequest) request;
        SirGetSensorStatusResponse response = new SirGetSensorStatusResponse();
        ArrayList<SirStatusDescription> statDescs = new ArrayList<SirStatusDescription>();

        if (getSensStatReq.getSensIdent() != null) {
            // search by sensorIdentification
            for (SirSensorIdentification sensIdent : getSensStatReq.getSensIdent()) {
                if (sensIdent instanceof SirSensorIDInSir) {
                    // sensorID in SIR
                    SirSensorIDInSir sensorId = (SirSensorIDInSir) sensIdent;
                    try {
                        statDescs = (ArrayList<SirStatusDescription>) this.getSensStatDao.getSensorStatusBySensorID(sensorId,
                                                                                                                    getSensStatReq.getPropertyFilter());
                    }
                    catch (OwsExceptionReport e) {
                        return new ExceptionResponse(e.getDocument());
                    }
                }
                else {
                    // service description
                    SirServiceReference servDesc = (SirServiceReference) sensIdent;
                    try {
                        statDescs = (ArrayList<SirStatusDescription>) this.getSensStatDao.getSensorStatusByServiceDescription(servDesc,
                                                                                                                              getSensStatReq.getPropertyFilter());
                    }
                    catch (OwsExceptionReport e) {
                        return new ExceptionResponse(e.getDocument());
                    }
                }
            }
        }
        else {
            // search by Criteria
            try {
                // utilize SOR if information is given
                if (getSensStatReq.getSearchCriteria().isUsingSOR()) {
                    // request the information from SOR and extend the search criteria with the result
                    Collection<SirSearchCriteria_Phenomenon> phenomena = getSensStatReq.getSearchCriteria().getPhenomena();

                    SORTools sor = new SORTools();
                    Collection<SirSearchCriteria_Phenomenon> newPhenomena = sor.getMatchingPhenomena(phenomena);

                    // add all found phenomena to search criteria
                    if (log.isDebugEnabled())
                        log.debug("Adding phenomena to search criteria: " + Arrays.toString(newPhenomena.toArray()));
                    phenomena.addAll(newPhenomena);
                }

                statDescs = (ArrayList<SirStatusDescription>) this.getSensStatDao.getSensorStatusBySearchCriteria(getSensStatReq.getSearchCriteria(),
                                                                                                                  getSensStatReq.getPropertyFilter());
            }
            catch (OwsExceptionReport e) {
                return new ExceptionResponse(e.getDocument());
            }
        }
        response.setStatusDescs(statDescs);
        return response;
    }

}
