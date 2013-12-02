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

package org.n52.sir.listener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.n52.oss.sir.SirClient;
import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.api.InternalSensorID;
import org.n52.oss.sir.api.SirSearchCriteria_Phenomenon;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.api.SirSensorIdentification;
import org.n52.oss.sir.api.SirServiceReference;
import org.n52.oss.sir.api.SirSimpleSensorDescription;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirSearchSensorRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirSearchSensorResponse;
import org.n52.sir.util.SORTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class SearchSensorListener implements ISirRequestListener {

    private static Logger log = LoggerFactory.getLogger(SearchSensorListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.SearchSensor.name();

    private boolean encodeURLs = true;

    private ISearchSensorDAO searchSensDao;

    private String sirVersion;

    private ISearchSensorDAO autocompleteDao;

    private SirClient client;

    /**
     * TODO implement injection mechanism for search DAO so that only that what needed is injected, not the
     * complete configurator
     * 
     * @throws OwsExceptionReport
     */
    @Inject
    public SearchSensorListener(@Named("full")
    ISearchSensorDAO dao, @Named("autocomplete")
    ISearchSensorDAO autocompleteDao, SirClient client) throws OwsExceptionReport {
        this.client = client;
        this.searchSensDao = dao;
        this.autocompleteDao = autocompleteDao;

        log.debug("NEW {}", this);
    }

    @Override
    public String getOperationName() {
        return SearchSensorListener.OPERATION_NAME;
    }

    public boolean isEncodeURLs() {
        return this.encodeURLs;
    }

    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        return receiveRequest(request, false);
    }

    public ISirResponse receiveRequest(AbstractSirRequest request, boolean autocompleteEngineOnly) {
        SirSearchSensorRequest searchSensReq = (SirSearchSensorRequest) request;
        // SirSearchCriteria crit = searchSensReq.getSearchCriteria();
        // String lat = crit.getLat();
        // String lng = crit.getLng();

        SirSearchSensorResponse response = new SirSearchSensorResponse();
        ArrayList<SirSearchResultElement> searchResElements = null;

        try {
            if (searchSensReq.getSensIdent() != null)
                searchResElements = searchByIdentification(searchSensReq);
            else
                searchResElements = searchBySearchCriteria(searchSensReq, autocompleteEngineOnly);

            // FIXME moh-yakoub: why do you query by id manually here, there is one happening above!
            // Object resultElement = this.searchSensDao.getSensorBySensorID(sensorId.getSensorId(),
            // searchSensReq.isSimpleResponse());
            // if (resultElement != null) {
            // searchResElements.add(resultElement);
        }

        catch (OwsExceptionReport e) {
            return new ExceptionResponse(e);
        }

        // if a simple response, add the corresponding GET URLs and bounding boxes
        if (searchSensReq.isSimpleResponse()) {
            processForSimpleResponse(searchSensReq, searchResElements);
        }

        response.setSearchResultElements(searchResElements);

        return response;
    }

    private void processForSimpleResponse(SirSearchSensorRequest searchSensReq,
                                          ArrayList<SirSearchResultElement> searchResElements) {
        // if the requested version is not 0.3.0, keep the bounding box, otherwise remove
        String version = searchSensReq.getVersion();
        boolean removeBBoxes = version.equals(SirConstants.SERVICE_VERSION_0_3_0);

        for (SirSearchResultElement sirSearchResultElement : searchResElements) {
            SirSimpleSensorDescription sensorDescription = (SirSimpleSensorDescription) sirSearchResultElement.getSensorDescription();

            String descriptionURL;
            try {
                descriptionURL = this.client.createDescribeSensorURL(sirSearchResultElement.getSensorId(), false);
            }
            catch (UnsupportedEncodingException e) {
                log.error("Could not encode URL", e);
                descriptionURL = "ERROR ENCODING URL: " + e.getMessage();
                // return new ExceptionResponse(new
                // OwsExceptionReport("Could not encode sensor description URL!", e).getDocument());
            }

            sensorDescription.setSensorDescriptionURL(descriptionURL);

            if (removeBBoxes)
                sensorDescription.setBoundingBox(null);
        }
    }

    private ArrayList<SirSearchResultElement> searchBySearchCriteria(SirSearchSensorRequest searchSensReq,
                                                                     boolean autocompleteOnly) {
        log.debug("Searching with criteria {} using only the autocomplete engine: {}",
                  searchSensReq.getSearchCriteria(),
                  autocompleteOnly);

        ArrayList<SirSearchResultElement> searchResElements = new ArrayList<>();

        // utilize SOR if information is given
        if (searchSensReq.getSearchCriteria().isUsingSOR()) {
            // request the information from SOR and extend the search criteria with the result
            Collection<SirSearchCriteria_Phenomenon> phenomena = searchSensReq.getSearchCriteria().getPhenomena();

            SORTools sor = new SORTools();
            Collection<SirSearchCriteria_Phenomenon> newPhenomena = sor.getMatchingPhenomena(phenomena);

            // add all found phenomena to search criteria
            log.debug("Adding phenomena to search criteria: {}", Arrays.toString(newPhenomena.toArray()));
            phenomena.addAll(newPhenomena);
        }

        Collection<SirSearchResultElement> searchResElementsSolr = null;
        Collection<SirSearchResultElement> searchResElementsPgSQL = null;

        // search autocomplete database
        try {
            searchResElementsSolr = this.autocompleteDao.searchSensor(searchSensReq.getSearchCriteria(),
                                                                      searchSensReq.isSimpleResponse());
        }
        catch (OwsExceptionReport e) {
            log.error("Could not query data from search backend.", e);
            searchResElementsSolr = new ArrayList<>();
            // return new ExceptionResponse(e);
        }

        if ( !autocompleteOnly) {
            // search PostGreSQL
            try {
                searchResElementsPgSQL = this.searchSensDao.searchSensor(searchSensReq.getSearchCriteria(),
                                                                         searchSensReq.isSimpleResponse());
            }
            catch (OwsExceptionReport e) {
                log.error("Could not query data from search backend.", e);
                searchResElementsPgSQL = new ArrayList<>();
            }
        }

        // union the searches
        Collections.addAll(searchResElements, searchResElementsSolr.toArray(new SirSearchResultElement[] {}));
        Collections.addAll(searchResElements, searchResElementsPgSQL.toArray(new SirSearchResultElement[] {}));
        log.debug("Found {} results in Solr, {} in Postgres, so {} in total.",
                  searchResElementsSolr.size(),
                  searchResElementsPgSQL.size(),
                  searchResElements.size());

        return searchResElements;
    }

    private ArrayList<SirSearchResultElement> searchByIdentification(SirSearchSensorRequest searchSensReq) throws OwsExceptionReport {
        ArrayList<SirSearchResultElement> searchResElements = new ArrayList<>();

        for (SirSensorIdentification sensIdent : searchSensReq.getSensIdent()) {
            if (sensIdent instanceof InternalSensorID) {
                // sensorID in SIR
                InternalSensorID sensorId = (InternalSensorID) sensIdent;
                SirSearchResultElement resultElement;

                resultElement = this.searchSensDao.getSensorBySensorID(sensorId.getId(),
                                                                       searchSensReq.isSimpleResponse());
                if (resultElement != null) {
                    searchResElements.add(resultElement);
                }
            }
            else {
                // service description
                SirServiceReference servDesc = (SirServiceReference) sensIdent;
                SirSearchResultElement resultElement;
                resultElement = this.searchSensDao.getSensorByServiceDescription(servDesc,
                                                                                 searchSensReq.isSimpleResponse());
                if (resultElement != null) {
                    searchResElements.add(resultElement);
                }
            }
        }

        return searchResElements;
    }

    public void setEncodeURLs(boolean encodeURLs) {
        this.encodeURLs = encodeURLs;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SearchSensorListener [encodeURLs=");
        builder.append(this.encodeURLs);
        builder.append(", sirUrl=");
        // builder.append(this.sirUrl);
        builder.append(", sirVersion=");
        builder.append(this.sirVersion);
        builder.append(", searchSensDao=");
        builder.append(this.searchSensDao);
        builder.append(", autocompleteDao=");
        builder.append(this.autocompleteDao);
        builder.append("]");
        return builder.toString();
    }

}
