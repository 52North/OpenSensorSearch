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
package org.n52.sir.harvest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.n52.sir.ds.solr.SolrConnection;

public class ScheduleBinding {
	public static final String randomString = "z7ecmioktu";
	//@Test
	public void testBinding() throws HttpException, IOException, InterruptedException, SolrServerException {
		File harvestScript = new File(ClassLoader.getSystemResource(
				"Requests/randomSensor.js").getFile());
		PostMethod method = new PostMethod(
				"http://localhost:8080/SIR/harvest/script/submit");
		Part[] parts = new Part[] { new StringPart("user", "testUser"),
				new FilePart("file", harvestScript) };
		method.setRequestEntity(new MultipartRequestEntity(parts, method
				.getParams()));
		MultipartEntity multipartEntity = new MultipartEntity();
		//upload the file
		multipartEntity.addPart("file", new FileBody(harvestScript));
		multipartEntity.addPart("user", new StringBody("testUserTest"));
		HttpPost post = new HttpPost(
				"http://localhost:8080/SIR/harvest/script/submit");
		post.setEntity(multipartEntity);
		org.apache.http.client.HttpClient client = new DefaultHttpClient();
		HttpResponse resp = client.execute(post);
		int responseCode = resp.getStatusLine().getStatusCode();
		
		assertEquals(responseCode,200);
		
		StringBuilder response = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
		String s = null;
		while((s=reader.readLine())!=null)
			response.append(s);
		
		
		int scriptId = Integer.parseInt(response.toString());
		
		//now do a scheduling
		
		StringBuilder scheduleRequest = new StringBuilder();
		scheduleRequest.append("http://localhost:8080/SIR/harvest/script/schedule");
		scheduleRequest.append("?id=");
		scheduleRequest.append(scriptId);
		Date d = new Date();
		scheduleRequest.append("&date="+(d.getTime()+(10*1000)));
		
		HttpGet get = new HttpGet(scheduleRequest.toString());
		resp = new DefaultHttpClient().execute(get);
		
		assertEquals(resp.getStatusLine().getStatusCode(), 200);
		
		Thread.sleep(10*1000);
		
		SOLRSearchSensorDAO DAO  = new SOLRSearchSensorDAO();
		Collection<SirSearchResultElement> results = DAO.searchByContact(randomString);
		
		assertTrue(results.size() > 0 );
		
		new SolrConnection().deleteByQuery("contacts:"+randomString);
	}
}
