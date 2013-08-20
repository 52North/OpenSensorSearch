package org.n52.sir.IT;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;

public class harvestScheduleIT {
	public static final String SCRIPT_ID = "org.n52.sir.harvest.scriptId";
	public static final String randomString = "z7ecmioktu";
	@Before
	public void uploadAFileAndRetrieveScriptId() throws ClientProtocolException, IOException {
		File harvestScript = new File(ClassLoader.getSystemResource(
				"Requests/randomSensor.js").getFile());
		PostMethod method = new PostMethod(
				"http://localhost:8080/SIR/harvest/script/submit");
		Part[] parts = new Part[] { new StringPart("user", "User"),
				new FilePart("file", harvestScript) };
		method.setRequestEntity(new MultipartRequestEntity(parts, method
				.getParams()));
		MultipartEntity multipartEntity = new MultipartEntity();
		// upload the file
		multipartEntity.addPart("file", new FileBody(harvestScript));
		multipartEntity.addPart("user", new StringBody("User"));
		HttpPost post = new HttpPost(
				"http://localhost:8080/SIR/harvest/script/submit");
		post.setEntity(multipartEntity);
		org.apache.http.client.HttpClient client = new DefaultHttpClient();
		HttpResponse resp = client.execute(post);
		int responseCode = resp.getStatusLine().getStatusCode();
		
		assertEquals(responseCode, 200);
		StringBuilder response = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(resp
				.getEntity().getContent()));
		String s = null;
		while ((s = reader.readLine()) != null)
			response.append(s);

		int scriptId = Integer.parseInt(response.toString());
		
		System.setProperty(SCRIPT_ID, scriptId + "");
	}

	@Test
	public void doAScheduleAtTime() throws InterruptedException, ClientProtocolException, IOException {
		String scriptId = System.getProperty(SCRIPT_ID);
		StringBuilder scheduleRequest = new StringBuilder();
		scheduleRequest
				.append("http://localhost:8080/SIR/harvest/script/schedule");
		scheduleRequest.append("?id=");
		scheduleRequest.append(scriptId);
		Date d = new Date();
		scheduleRequest.append("&date=" + (d.getTime() + (10 * 1000)));

		HttpGet get = new HttpGet(scheduleRequest.toString());
		HttpResponse resp = new DefaultHttpClient().execute(get);

		assertEquals(resp.getStatusLine().getStatusCode(), 200);

		Thread.sleep(10 * 1000);

		SOLRSearchSensorDAO DAO = new SOLRSearchSensorDAO();
		Collection<SirSearchResultElement> results = DAO
				.searchByContact(randomString);

		assertTrue(results.size() > 0);

	}
}