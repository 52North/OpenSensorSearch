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

package org.n52.sir.IT;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.xmlbeans.XmlObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.Client;
import org.n52.oss.ui.beans.HarvestServiceBean;
import org.x52North.sir.x032.HarvestServiceRequestDocument;
import org.x52North.sir.x032.HarvestServiceResponseDocument;

/**
 * 
 * @author Yakoub, Daniel Nüst
 */
public class HarvestServiceIT {

    // FIXME use a mocked up webservice to test harvesting
    private String harvestedServiceURL = "http://geoviqua.dev.52north.org/SOS-Q/sos/pox";

    private static String sirServiceURL = "http://localhost:8080/oss-service/sir";

    private String harvestedServiceType = "SOS";

    private static Client client;

    @BeforeClass
    public static void setUp() {
        client = new Client(sirServiceURL);
    }

    @Test
    public void harvestWeatherServiceBean() throws Exception {
        // HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // // increase timeout for slow servers
        // RequestConfig config =
        // RequestConfig.custom().setConnectionRequestTimeout(6000).setSocketTimeout(6000).build();
        // httpClientBuilder.setDefaultRequestConfig(config);
        // try (CloseableHttpClient c = httpClientBuilder.build();) {
        // HttpGet get = new HttpGet("http://www.google.de");
        // CloseableHttpResponse response = c.execute(get);
        //
        // int respCode = response.getStatusLine().getStatusCode();
        //
        // IOUtils.copy(response.getEntity().getContent(), System.out);
        // response.close();
        // }

        // buildRequest
        HarvestServiceBean hsb = new HarvestServiceBean(this.harvestedServiceURL, this.harvestedServiceType);
        hsb.buildRequest();

        // send request
        String response = client.sendPostRequest(hsb.getRequestString());

        // parse and validate response
        HarvestServiceResponseDocument cd = HarvestServiceResponseDocument.Factory.parse(response);
        assertTrue(cd.validate());

        // FIXME test must check whether the correct number of sensors was added, and more
    }

    // @Test
    public void harvestWeatherServiceDoc() throws Exception {
        File f = new File(ClassLoader.getSystemResource("Requests/HarvestService.xml").getFile());
        HarvestServiceRequestDocument hsrd = HarvestServiceRequestDocument.Factory.parse(f);

        XmlObject response = client.xSendPostRequest(hsrd);

        // parse and validate response
        HarvestServiceResponseDocument cd = HarvestServiceResponseDocument.Factory.parse(response.getDomNode());
        assertTrue(cd.validate());

        // FIXME test must check whether the correct number of sensors was added, and more
    }

}