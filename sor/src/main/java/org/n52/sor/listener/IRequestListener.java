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
package org.n52.sor.listener;

import org.n52.sor.ISorRequest;
import org.n52.sor.ISorResponse;
import org.n52.sor.OwsExceptionReport;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public interface IRequestListener {

    /**
     * This Methode conducts the processing of the request and creats a response for it.
     * 
     * @param request
     *        The given ISorRequest
     * @return The appropriate ISorResponse
     * @throws OwsExceptionReport
     */
    public ISorResponse receiveRequest(ISorRequest request) throws OwsExceptionReport;
}