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

package org.n52.sir.client;

import com.google.inject.Inject;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public abstract class AbstractBean {

    protected String requestString = "";

    protected String responseString = "";

    @Inject
    private Client client;
//    private Client client = new Client(SirConfigurator.getInstance().getServiceUrl());

    /**
     * Build the request based on the user input, then save it in {@link AbstractBean#requestString}.
     */
    public abstract void buildRequest();

    public Client getClient() {
        return this.client;
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
