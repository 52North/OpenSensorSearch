/**
 * ﻿Copyright (C) 2013 52°North Initiative for Geospatial Open Source Software GmbH
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
package org.n52.oss.ui.beans;

import org.n52.oss.sir.Client;
import org.n52.oss.ui.WebsiteConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public abstract class TestClientBean {

    private static Logger log = LoggerFactory.getLogger(TestClientBean.class);

    protected String requestString = "";

    protected String responseString = "";

    private Client client;

    public TestClientBean() {
        WebsiteConfig c = new WebsiteConfig();
        String sirEndpoint = c.getSirEndpoint();
        this.client = new Client(sirEndpoint);
        log.info("NEW {}", this);
    }

    /**
     * Build the request based on the user input, then save it in {@link TestClientBean#requestString}.
     */
    public abstract void buildRequest();

    public String getRequestString() {
        return this.requestString;
    }

    public String getResponseString() {
        return this.responseString;
    }

    public void setRequestString(String request) {
        this.requestString = request;
    }

    public void setResponseString(String response) {
        this.responseString = response;
    }

    public String sendRequest(String request) {
        log.debug("Sending request: {}", request);

        String response;
        response = this.client.sendPostRequest(request);

        log.debug("Got response: {}", response);
        setResponseString(response);
        return response;
    }

}
