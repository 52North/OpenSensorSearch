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
package org.n52.sor.listener;

import org.n52.sor.OwsExceptionReport;
import org.n52.sor.PhenomenonManager;
import org.n52.sor.request.ISorRequest;
import org.n52.sor.request.SorInsertDefinitionRequest;
import org.n52.sor.response.ISorResponse;
import org.n52.sor.response.SorInsertDefinitionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class InsertDefinitionListener implements IRequestListener {

    private static Logger log = LoggerFactory.getLogger(InsertDefinitionListener.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.process.ISorProcess#receiveRequest(org.n52.request.ISorRequest)
     */
    @Override
    public ISorResponse receiveRequest(ISorRequest request) throws OwsExceptionReport {
        SorInsertDefinitionRequest sorRequest = (SorInsertDefinitionRequest) request;
        SorInsertDefinitionResponse sorResponse = new SorInsertDefinitionResponse();

        // set definition URI
        sorResponse.setDefinitionURI(sorRequest.getDefinitionURI());

        // insert
        boolean b = PhenomenonManager.getInstance().insertInPhenomenonList(sorRequest.getDefinitionURI(),
                                                                           sorRequest.getPhenomenon());
        // set update successful
        sorResponse.setInsertSuccessful(b);

        if ( !b)
            log.warn("Insert NOT successful! " + sorRequest);

        return sorResponse;
    }

}