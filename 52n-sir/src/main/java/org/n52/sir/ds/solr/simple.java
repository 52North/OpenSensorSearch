package org.n52.sir.ds.solr;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;

public class simple {
	public static void main(String args[]) throws SolrServerException, IOException{
		SolrConnection conn  =new SolrConnection();
		conn.deleteByQuery("");
	}

}
