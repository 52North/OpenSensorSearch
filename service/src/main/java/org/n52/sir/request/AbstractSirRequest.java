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
package org.n52.sir.request;

import java.net.URI;

/**
 * abstract super class for all sir request classes
 * 
 * @author Jan Schulte
 * 
 */
public abstract class AbstractSirRequest {

    private String service;

    private String version;

    private URI requestUri;

    public String getService() {
        return this.service;
    }

    public String getVersion() {
        return this.version;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setRequestUri(URI requestUri) {
        this.requestUri = requestUri;
    }

    public URI getRequestUri() {
        return this.requestUri;
    }

}