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
package org.n52.sor.client;

import org.n52.sor.PropertiesManager;
import org.n52.sor.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sor.x031.GetDefinitionRequestDocument;
import org.x52North.sor.x031.GetDefinitionRequestDocument.GetDefinitionRequest;

/**
 * @author Jan Schulte
 * 
 */
public class GetDefinitionBean extends AbstractBean {

    private static Logger log = LoggerFactory.getLogger(GetDefinitionBean.class);

    private String inputURI = "";

    @Override
    public void buildRequest() {
        GetDefinitionRequestDocument requestDoc = GetDefinitionRequestDocument.Factory.newInstance();
        GetDefinitionRequest request = requestDoc.addNewGetDefinitionRequest();
        request.setService(PropertiesManager.getInstance().getService());
        request.setVersion(PropertiesManager.getInstance().getServiceVersion());

        if ( !this.inputURI.isEmpty()) {
            request.setInputURI(this.inputURI);
        }
        else {
            this.requestString = "Please choose one input URI!";
            return;
        }

        if ( !requestDoc.validate()) {
            log.warn("Request is NOT valid, service may return error!\n"
                    + XmlTools.validateAndIterateErrors(requestDoc));
        }

        if ( !requestDoc.validate()) {
            log.warn("Request is NOT valid, service may return error!\n"
                    + XmlTools.validateAndIterateErrors(requestDoc));
        }

        if ( !requestDoc.validate()) {
            log.warn("Request is NOT valid, service may return error!\n"
                    + XmlTools.validateAndIterateErrors(requestDoc));
        }

        this.requestString = requestDoc.toString();
    }

    /**
     * @return the inputURI
     */
    public String getInputURI() {
        return this.inputURI;
    }

    /**
     * @param inputURI
     *        the inputURI to set
     */
    public void setInputURI(String inputURI) {
        this.inputURI = inputURI;
    }

}