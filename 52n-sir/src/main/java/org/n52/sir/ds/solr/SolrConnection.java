package org.n52.sir.ds.solr;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class SolrConnection {
	public static final String URL ="http://localhost";
	private SolrServer server;
	public SolrConnection(){
		server = new HttpSolrServer(URL);
		
	}
	
	public void addInputDocument(SolrInputDocument doc) throws SolrServerException, IOException{
		server.add(doc);
	}
	
	public void commitChanges() throws SolrServerException, IOException{
		server.commit();
	}
	
}
