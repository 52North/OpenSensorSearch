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

import org.n52.sor.ISorRequest;
import org.n52.sor.ISorResponse;
import org.n52.sor.OwsExceptionReport;
import org.n52.sor.OwsExceptionReport.ExceptionCode;
import org.n52.sor.PhenomenonManager;
import org.n52.sor.datastructures.IDictionaryEntry;
import org.n52.sor.request.SorGetDefinitionRequest;
import org.n52.sor.response.SorGetDefinitionResponse;

public class GetDefinitionListener implements IRequestListener {

    /*
     * @see ISorProcess#receiveRequest(ISorRequest)
     */
    @Override
    public ISorResponse receiveRequest(ISorRequest request) throws OwsExceptionReport {
        SorGetDefinitionRequest getDefReq = (SorGetDefinitionRequest) request;
        SorGetDefinitionResponse getDefResp = new SorGetDefinitionResponse();

        for (IDictionaryEntry phenom : PhenomenonManager.getInstance().getPhenomenaList()) {
            if (phenom.getIdentifier().equals(getDefReq.getInputURI())) {
                getDefResp.setPhenomenonDefinition(phenom);
                break;
            }
        }

        if (getDefResp.getPhenomenonDefinition() == null) {
            // phenomenon not found!
            throw new OwsExceptionReport(ExceptionCode.InvalidParameterValue,
                                         "InputURI",
                                         "Definition for the given input not found!");
        }

        getDefResp.setInputURI(getDefReq.getInputURI());
        return getDefResp;
    }

}