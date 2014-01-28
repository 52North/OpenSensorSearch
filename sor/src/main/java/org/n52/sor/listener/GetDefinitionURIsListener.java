/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.sor.listener;

import java.util.ArrayList;
import java.util.List;

import org.n52.sor.ISorRequest;
import org.n52.sor.ISorResponse;
import org.n52.sor.OwsExceptionReport;
import org.n52.sor.OwsExceptionReport.ExceptionCode;
import org.n52.sor.PhenomenonManager;
import org.n52.sor.datastructures.IDictionaryEntry;
import org.n52.sor.request.SorGetDefinitionURIsRequest;
import org.n52.sor.response.SorGetDefinitionURIsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetDefinitionURIsListener implements IRequestListener {

    private static Logger log = LoggerFactory.getLogger(GetDefinitionURIsListener.class);

    /*
     * @see ISorProcess#receiveRequest(ISorRequest)
     */
    @Override
    public ISorResponse receiveRequest(ISorRequest request) throws OwsExceptionReport {
        SorGetDefinitionURIsRequest sorRequest = (SorGetDefinitionURIsRequest) request;
        SorGetDefinitionURIsResponse getDefResp = new SorGetDefinitionURIsResponse();

        ArrayList<IDictionaryEntry> phenomList = (ArrayList<IDictionaryEntry>) PhenomenonManager.getInstance().getPhenomenaList();
        List<String> defs = new ArrayList<>();

        if (sorRequest.getSearchString() != null) {
            // search string is given, check all elements and add them to ouput list if they match

            String search = sorRequest.getSearchString();
            log.debug("Searching for String '{}'", search);

            for (IDictionaryEntry de : phenomList) {
                if (de.getIdentifier().contains(search)) {
                    defs.add(de.getIdentifier());
                }
            }

            log.debug("Found search string in {} of {} dictionary entries.", defs.size(), phenomList.size());
        }
        else {
            // no search string, add all elements to output list

            for (IDictionaryEntry de : phenomList) {
                defs.add(de.getIdentifier());
            }
        }

        if (sorRequest.getStartResultElement() > defs.size()) {
            log.warn("Start result element is larger than found element list, returning empty list!");
            throw new OwsExceptionReport(ExceptionCode.InvalidParameterValue,
                                         "StartResultElement",
                                         "StartResusltElement is larger than number of found elements!");
        }

        // create sublist of maximum length and return
        defs = defs.subList(sorRequest.getStartResultElement(),
                            Math.min(sorRequest.getStartResultElement() + sorRequest.getMaxNumberOfResults(),
                                     defs.size()));

        getDefResp.setDefinitionList(defs);
        return getDefResp;
    }
}