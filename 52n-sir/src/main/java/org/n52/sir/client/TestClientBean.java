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

import java.io.IOException;

import org.apache.http.HttpException;
import org.n52.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public abstract class TestClientBean {

    private static Logger log = LoggerFactory.getLogger(TestClientBean.class);

    protected String requestString = "";

    protected String responseString = "";

    @Inject
    private Client client;

    /**
     * Build the request based on the user input, then save it in {@link TestClientBean#requestString}.
     */
    public abstract void buildRequest();

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

    public String sendRequest(String request) {
        String response;
        try {
            response = this.client.sendPostRequest(request);
        }
        catch (IOException | OwsExceptionReport | HttpException e) {
            log.warn("Could not send request in bean.", e);
            response = "ERROR: " + e.getMessage();
        }
        return response;
    }

}
