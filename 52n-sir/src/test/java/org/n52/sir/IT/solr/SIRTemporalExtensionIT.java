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

package org.n52.sir.IT.solr;

import java.net.URI;

import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sir.IT.GuiceUtil;
import org.n52.sir.client.Client;
import org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria;
import org.x52North.sir.x032.SearchSensorRequestDocument;
import org.x52North.sir.x032.SearchSensorRequestDocument.SearchSensorRequest;

public class SIRTemporalExtensionIT {

    private static Client client;

    @BeforeClass
    public static void setUp() {
        client = GuiceUtil.configureSirClient();
    }

    @Test
    public void sendSearchRequest() throws Exception {
        SearchSensorRequestDocument doc = SearchSensorRequestDocument.Factory.newInstance();
        SearchSensorRequest request = doc.addNewSearchSensorRequest();
        SearchCriteria crit = SearchCriteria.Factory.newInstance();
        crit.setSearchTextArray(new String[] {"pre"});
        request.setSearchCriteria(crit);
        System.out.println(client.xSendPostRequest(doc, new URI("http://localhost:8080/OpenSensorSearch")));
    }

}
