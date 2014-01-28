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
package org.n52.sir.listener;

import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.response.ISirResponse;

/**
 * interface for the request listeners
 * 
 * @author Jan Schulte
 * 
 */
public interface ISirRequestListener {

    /**
     * @return Returns the name of the supported operation
     */
    public String getOperationName();

    /**
     * method handles the incoming operation request an returns a matching response or an
     * ServiceExceptionReport if the SIR was not able to build a response
     * 
     * @param request
     *        the operation request
     * @return Returns the responose of the request or an operation failed
     */
    public ISirResponse receiveRequest(AbstractSirRequest request);

}