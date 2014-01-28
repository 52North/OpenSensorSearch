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

import java.util.ArrayList;

import org.n52.sor.OwsExceptionReport;
import org.n52.sor.PhenomenonManager;
import org.n52.sor.datastructures.IDictionaryEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Class for common attributes (and respective accessor methods) among all client page beans.
 * 
 * @author Jan Schulte
 * 
 */
public abstract class AbstractBean {

    private static Logger log = LoggerFactory.getLogger(AbstractBean.class);

    protected String requestString = "";

    protected String responseString = "";

    /**
     * Build the request based on user input, then save it in {@link AbstractBean#requestString}.
     */
    public abstract void buildRequest();

    /**
     * 
     * @return
     */
    public String[] getAvailableURIs() {
        ArrayList<IDictionaryEntry> phenomList;
        try {
            phenomList = (ArrayList<IDictionaryEntry>) PhenomenonManager.getInstance().getPhenomenaList();
        }
        catch (OwsExceptionReport e) {
            log.error("Could not retrieve available phenomena.", e);
            return new String[] {"Could not retrieve available phenomena."};
        }

        String[] uris = new String[phenomList.size()];

        int i = 0;
        for (IDictionaryEntry p : phenomList) {
            uris[i++] = p.getIdentifier();
        }

        return uris;
    }

    /**
     * @return the request
     */
    public String getRequestString() {
        return this.requestString;
    }

    /**
     * @return the response
     */
    public String getResponseString() {
        return this.responseString;
    }

    /**
     * @param request
     *        the request to set
     */
    public void setRequestString(String request) {
        this.requestString = request;
    }

    /**
     * @param response
     *        the response to set
     */
    public void setResponseString(String response) {
        this.responseString = response;
    }

}