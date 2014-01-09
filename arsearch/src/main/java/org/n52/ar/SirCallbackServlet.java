/**
 * Copyright 2012 52°North Initiative for Geospatial Open Source Software GmbH
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
package org.n52.ar;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.n52.sir.json.MapperFactory;
import org.n52.sir.json.SearchResult;
import org.n52.sir.json.SearchResultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class SirCallbackServlet extends HttpServlet {

	protected static final String CONTENT_TYPE = "application/json";
	protected static final String CONTENT_TYPE_XML = "application/xml";
	
    private static final String INIT_PARAM_SIR_URL = "sirURL";

    private static final String INIT_PARAM_URL = "url";

    private static Logger log = LoggerFactory.getLogger(SirCallbackServlet.class);

    /**
     * 
     */
    private static final long serialVersionUID = 2395985983391677158L;

    private ObjectMapper mapper;

    private String sirURL;

    private String url;

    /**
     * 
     * @param sre
     * @return
     */
    protected abstract SirPOI createPOI(SearchResultElement sre);

    public String getUrl() {
        return this.url;
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        String sirURLParam = getServletConfig().getInitParameter(INIT_PARAM_SIR_URL);
        if (sirURLParam != null) {
            this.sirURL = sirURLParam;
        }
        String urlParam = getServletContext().getInitParameter(INIT_PARAM_URL);
        if (urlParam != null) {
            this.url = urlParam;
        }

        this.mapper = MapperFactory.getMapper();
    }
    
    

    /**
     * 
     * @param center
     * @param radius
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    protected Collection<SirPOI> querySIR(double[] center, float radius, String requestFormat) throws JsonParseException,
            JsonMappingException,
            IOException {
        List<SirPOI> pois = new ArrayList<SirPOI>();
        StringBuilder sb = new StringBuilder();

        sb.append(this.sirURL);
        sb.append("?q=");

        // TODO allow using keyword entered by user
        // sb.append("muenster");

        sb.append("&httpAccept=");
        sb.append(requestFormat);

        // append location
        sb.append("&lat=");
        sb.append(center[0]);
        sb.append("&lon=");
        sb.append(center[1]);
        sb.append("&radius=");
        sb.append(radius);

        String request = sb.toString();
        log.info("Requesting {}", request);

        DefaultHttpClient client = new DefaultHttpClient();

        HttpGet get = new HttpGet(request);

        HttpResponse response = client.execute(get);
        InputStream responseStream = response.getEntity().getContent();
        int responseCode = response.getStatusLine().getStatusCode();
        log.debug("Response Code: " + responseCode);

        SearchResult result = this.mapper.readValue(responseStream, SearchResult.class);
        log.debug("Got {} search results.", Integer.valueOf(result.getResults().size()));

        // convert search result to a list of AugmentedPOIs
        Collection<SearchResultElement> results = result.getResults();
        for (SearchResultElement sre : results) {
            SirPOI p = createPOI(sre);
            pois.add(p);
        }

        responseStream.close();
        client = null;

        return pois;
    }

}
