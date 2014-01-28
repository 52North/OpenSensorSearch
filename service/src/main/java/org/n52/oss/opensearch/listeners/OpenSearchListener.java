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
package org.n52.oss.opensearch.listeners;

import java.net.URI;
import java.util.Collection;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.ows.OwsExceptionReport;

public interface OpenSearchListener {

    public Response createResponse(Collection<SirSearchResultElement> searchResult,
                                   MultivaluedMap<String, String> params) throws OwsExceptionReport;

    public String getMimeType();

    public String getName();

    public void setOpenSearchEndpoint(URI uri);

    public void setHomeURI(URI uri);

}
