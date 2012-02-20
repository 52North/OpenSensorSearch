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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.datastructure.SirSearchCriteria_Phenomenon;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirSensorIDInSir;
import org.n52.sir.datastructure.SirSensorIdentification;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.datastructure.SirSimpleSensorDescription;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirSearchSensorRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirSearchSensorResponse;
import org.n52.sir.util.ListenersTools;
import org.n52.sir.util.SORTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class SearchSensorListener implements ISirRequestListener {

    /**
     * the logger, used to log exceptions and additionally information
     */
    private static Logger log = LoggerFactory.getLogger(SearchSensorListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.SearchSensor.name();

    private SirConfigurator configurator;

    private boolean encodeURLs = true;

    /**
     * the data access object for the searchSensor operation
     */
    private ISearchSensorDAO searchSensDao;

    /**
     * 
     * @throws OwsExceptionReport
     */
    public SearchSensorListener() throws OwsExceptionReport {
        this.configurator = SirConfigurator.getInstance();

        IDAOFactory factory = this.configurator.getFactory();
        try {
            this.searchSensDao = factory.searchSensorDAO();
        }
        catch (OwsExceptionReport se) {
            log.error("Error while creating the searchSensorDAO", se);
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
        return SearchSensorListener.OPERATION_NAME;
    }

    /**
     * @return the encodeURLs
     */
    public boolean isEncodeURLs() {
        return this.encodeURLs;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.n52.sir.ISirRequestListener#receiveRequest(org.n52.sir.request. AbstractSirRequest)
     */
    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        SirSearchSensorRequest searchSensReq = (SirSearchSensorRequest) request;
        SirSearchSensorResponse response = new SirSearchSensorResponse();
        ArrayList<SirSearchResultElement> searchResElements = new ArrayList<SirSearchResultElement>();

        if (searchSensReq.getSensIdent() != null) {
            // search by sensorIdentification
            for (SirSensorIdentification sensIdent : searchSensReq.getSensIdent()) {
                if (sensIdent instanceof SirSensorIDInSir) {
                    // sensorID in SIR
                    try {
                        SirSensorIDInSir sensorId = (SirSensorIDInSir) sensIdent;
                        SirSearchResultElement resultElement;

                        resultElement = this.searchSensDao.getSensorBySensorID(sensorId.getSensorIdInSir(),
                                                                               searchSensReq.isSimpleResponse());
                        if (resultElement != null) {
                            searchResElements.add(resultElement);
                        }
                    }
                    catch (OwsExceptionReport e) {
                        return new ExceptionResponse(e.getDocument());
                    }
                }
                else {
                    // service description
                    try {
                        SirServiceReference servDesc = (SirServiceReference) sensIdent;
                        SirSearchResultElement resultElement;
                        resultElement = this.searchSensDao.getSensorByServiceDescription(servDesc,
                                                                                         searchSensReq.isSimpleResponse());
                        if (resultElement != null) {
                            searchResElements.add(resultElement);
                        }
                    }
                    catch (OwsExceptionReport e) {
                        return new ExceptionResponse(e.getDocument());
                    }
                }
            }
        }
        else {
            // search by searchCriteria
            try {
                // utilize SOR if information is given
                if (searchSensReq.getSearchCriteria().isUsingSOR()) {
                    // request the information from SOR and extend the search criteria with the result
                    Collection<SirSearchCriteria_Phenomenon> phenomena = searchSensReq.getSearchCriteria().getPhenomena();

                    SORTools sor = new SORTools();
                    Collection<SirSearchCriteria_Phenomenon> newPhenomena = sor.getMatchingPhenomena(phenomena);

                    // add all found phenomena to search criteria
                    if (log.isDebugEnabled())
                        log.debug("Adding phenomena to search criteria: " + Arrays.toString(newPhenomena.toArray()));
                    phenomena.addAll(newPhenomena);
                }

                searchResElements = (ArrayList<SirSearchResultElement>) this.searchSensDao.searchSensor(searchSensReq.getSearchCriteria(),
                                                                                                        searchSensReq.isSimpleResponse());
            }
            catch (OwsExceptionReport e) {
                return new ExceptionResponse(e.getDocument());
            }
        }

        // if a simple response, add the corresponding GET URLs and bounding boxes
        if (searchSensReq.isSimpleResponse()) {

            // if the requested version is not 0.3.0, keep the bounding box, otherwise remove
            String version = searchSensReq.getVersion();
            boolean removeBBoxes = version.equals(SirConstants.SERVICE_VERSION_0_3_0);

            for (SirSearchResultElement sirSearchResultElement : searchResElements) {
                SirSimpleSensorDescription sensorDescription = (SirSimpleSensorDescription) sirSearchResultElement.getSensorDescription();

                String descriptionURL;
                try {
                    descriptionURL = ListenersTools.createSensorDescriptionURL(sirSearchResultElement.getSensorIdInSir());

                    if (this.encodeURLs) {
                        // must be encoded for XML:
                        descriptionURL = URLEncoder.encode(descriptionURL, this.configurator.getCharacterEncoding());
                    }
                }
                catch (UnsupportedEncodingException e) {
                    log.error("Could not encode URL", e);
                    return new ExceptionResponse(new OwsExceptionReport("Could not encode sensor description URL!", e).getDocument());
                }
                if (log.isDebugEnabled())
                    log.debug("Created description URL for sensor " + sirSearchResultElement.getSensorIdInSir() + ": "
                            + descriptionURL);

                sensorDescription.setSensorDescriptionURL(descriptionURL);

                if (removeBBoxes)
                    sensorDescription.setBoundingBox(null);
            }

        }

        response.setSearchResultElements(searchResElements);

        return response;
    }

    /**
     * @param encodeURLs
     *        the encodeURLs to set
     */
    public void setEncodeURLs(boolean encodeURLs) {
        this.encodeURLs = encodeURLs;
    }
}
