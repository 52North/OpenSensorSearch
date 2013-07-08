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

import org.n52.sir.SirConstants.Operations;

/**
 * abstract super class for all sir request classes
 * 
 * @author Jan Schulte
 * 
 */
public abstract class AbstractSirRequest {

    /**
     * service parameter
     */
    private String service;

    /**
     * version parameter
     */
    private String version;

    /**
     * @return the service
     */
    public String getService() {
        return this.service;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * @param service
     *        the service to set
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * @param version
     *        the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }
    
    public abstract Operations getOperation();

}