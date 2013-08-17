package org.n52.sir.harvest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class remoteHarvestingTest {
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(3000); // No-args
																// constructor
																// defaults

	@Before
	public void initiateStubServer(){
		stubFor(get(urlEqualTo("/sensors")).willReturn(
				aResponse().withHeader("Content-Type", "text/json").withBody(
						"[1,2]")));
	
	}
	@Test
	public void testRemoteServer() throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://localhost:3000/sensors");
		org.apache.http.HttpResponse resp = client.execute(get);
		BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
		StringBuilder builder = new StringBuilder();
		String s = "";
		while((s=reader.readLine())!=null)
			builder.append(s);
		
		System.out.println(builder.toString());
	}
	
	

}
