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
package org.n52.sir.ds.solr;

/** 
 * @author Yakoub 
 */
import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;

public class SolrConnection {

    private SolrServer server;

    public SolrConnection() {
        this.server = new HttpSolrServer(SolrConstants.SOLR_URL);
    }

    public void addInputDocument(SolrInputDocument doc) throws SolrServerException, IOException {
        this.server.add(doc);
    }

    public void commitChanges() throws SolrServerException, IOException {
        this.server.commit();
    }

    public QueryResponse SolrQuery(SolrParams params) throws SolrServerException {
        return this.server.query(params);

    }
    
    public void deleteSensorWithID(String sensorID) throws SolrServerException, IOException {
    	this.server.deleteByQuery(SolrConstants.ID+":"+sensorID);
    	commitChanges();
    }
    
    public void deleteByQuery(String query) throws SolrServerException, IOException{
    	this.server.deleteByQuery("*:*");
    	commitChanges();
    }

}
