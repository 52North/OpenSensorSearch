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
package org.n52.sir.decode;

import org.n52.sir.decode.impl.HttpGetRequestDecoder;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.request.AbstractSirRequest;

import com.google.inject.ImplementedBy;

/**
 * interface offers parsing method to parse the String representing a Get request and create a Sir request
 * 
 * @author Jan Schulte
 * 
 */
@ImplementedBy(HttpGetRequestDecoder.class)
public interface IHttpGetRequestDecoder {

    /**
     * parses the String representing the Get-request and returns an internal SIR representation of the
     * request
     * 
     * @param capString
     *        String with the getCapabilities parameters
     * @return Returns SirGetCapabilitiesRequest representing the request
     * @throws OwsExceptionReport
     *         If parsing the string failed
     */
    public AbstractSirRequest receiveRequest(String capString) throws OwsExceptionReport;

}