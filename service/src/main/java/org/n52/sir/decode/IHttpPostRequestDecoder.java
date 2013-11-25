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

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.request.AbstractSirRequest;

/**
 * interface offers method to receive a request. Returns internal SIR representation of request
 * 
 * @author Jan Schulte
 * 
 */
public interface IHttpPostRequestDecoder {

    /**
     * method receives request and returns internal SIR representation of request
     * 
     * @param docString
     *        string, which contains the request document
     * @return Returns internal SIR representation of the request
     * @throws OwsExceptionReport
     *         if parsing the request fails
     */
    public AbstractSirRequest receiveRequest(String docString) throws OwsExceptionReport;

}