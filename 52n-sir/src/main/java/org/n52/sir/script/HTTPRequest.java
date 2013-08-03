package org.n52.sir.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class HTTPRequest {
	
	public String doGet(String s) throws ClientProtocolException, IOException{
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(s);
		
		HttpResponse resp = client.execute(get);
		StringBuilder builder = new StringBuilder();
		String r = "";
		BufferedReader reader = new  BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
		while((r=reader.readLine())!=null)
			builder.append(r);
		
		return builder.toString();	
	}
}
