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
import org.x52North.sor.x031.GetDefinitionURIsRequestDocument;
import org.x52North.sor.x031.GetDefinitionURIsRequestDocument.GetDefinitionURIsRequest;

/**
 * @author Jan Schulte
 * 
 */
public class GetDefinitionURIsBean extends AbstractBean {

    private static Logger log = LoggerFactory.getLogger(GetDefinitionURIsBean.class);

    private String maxNumberOfResults = "";

    private String startResultElement = "";

    private String searchString = "";

    @Override
    public void buildRequest() {
        GetDefinitionURIsRequestDocument requestDoc = GetDefinitionURIsRequestDocument.Factory.newInstance();
        GetDefinitionURIsRequest request = requestDoc.addNewGetDefinitionURIsRequest();
        request.setService(PropertiesManager.getInstance().getService());
        request.setVersion(PropertiesManager.getInstance().getServiceVersion());

        // maxNumberOfResults
        if ( !this.maxNumberOfResults.isEmpty()) {
            try {
                request.setMaxNumberOfResults(Integer.parseInt(this.maxNumberOfResults));
            }
            catch (NumberFormatException nfe) {
                this.requestString = "Please enter numbers into the field 'Maximum number of elements'!";
                return;
            }
        }
        // startResultElement
        if ( !this.startResultElement.isEmpty()) {
            try {
                request.setStartResultElement(Integer.parseInt(this.startResultElement));
            }
            catch (NumberFormatException nfe) {
                this.requestString = "Please enter numbers into the fields 'Start result element'!";
                return;
            }
        }

        // searchString
        if ( !this.searchString.isEmpty()) {
            request.setSearchString(this.searchString);
        }

        if ( !requestDoc.validate()) {
            log.warn("Request is NOT valid, service may return error!\n"
                    + XmlTools.validateAndIterateErrors(requestDoc));
        }

        this.requestString = requestDoc.toString();
    }

    /**
     * @return the maxNumberOfResults
     */
    public String getMaxNumberOfResults() {
        return this.maxNumberOfResults;
    }

    /**
     * @return the searchString
     */
    public String getSearchString() {
        return this.searchString;
    }

    /**
     * @return the startResultElement
     */
    public String getStartResultElement() {
        return this.startResultElement;
    }

    /**
     * @param maxNumberOfResults
     *        the maxNumberOfResults to set
     */
    public void setMaxNumberOfResults(String maxNumberOfResults) {
        this.maxNumberOfResults = maxNumberOfResults;
    }

    /**
     * @param searchString
     *        the searchString to set
     */
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    /**
     * @param startResultElement
     *        the startResultElement to set
     */
    public void setStartResultElement(String startResultElement) {
        this.startResultElement = startResultElement;
    }
}