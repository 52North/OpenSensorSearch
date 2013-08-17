package org.n52.sir.harvest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.UnavailableException;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.n52.sir.SirConfigurator;
import org.n52.sir.datastructure.SirSearchCriteria;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.detailed.SirDetailedSensorDescription;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.scheduler.QuartzConstants;
import org.n52.sir.scheduler.RemoteHarvestJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class remoteHarvestingTest {
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(3000); // No-args
																// constructor
																// defaults

	@Before
	public void initiateStubServer() throws UnavailableException,
			OwsExceptionReport, FileNotFoundException {
		if (SirConfigurator.getInstance() == null) {
			String basePath = (this.getClass().getResource("/prop").getFile());
			File sir_File = new File(basePath + "/sir.properties");
			File db_File = new File(basePath + "/db.properties");

			InputStream dbStream = new FileInputStream(db_File);
			InputStream sirStream = new FileInputStream(sir_File);
			// Read configurator if null
			SirConfigurator.getInstance(sirStream, dbStream, null, null);

		}

		stubFor(get(urlEqualTo("/sensors")).willReturn(
				aResponse().withHeader("Content-Type", "text/json").withBody(
						"[1,2]")));
		stubFor(get(urlEqualTo("/sensors/1")).willReturn(
				aResponse().withHeader("Content-Type", "text/json").withBody(
						"{id:1,keywords:['test1','testkeyword1']}")));
		stubFor(get(urlEqualTo("/sensors/2")).willReturn(
				aResponse().withHeader("Content-Type", "text/json").withBody(
						"{id:2,keywords:['test2','testkeyword2']}")));

	}

	@Test
	public void testRemoteServer() throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();

		HttpPost post = new HttpPost(
				"http://localhost:8080/SIR/harvest/script/remote/server");

		List<NameValuePair> list_params = new ArrayList<NameValuePair>();

		list_params
				.add(new BasicNameValuePair("url", "http://localhost:3000/"));

		post.setEntity(new UrlEncodedFormEntity(list_params));

		org.apache.http.HttpResponse resp = client.execute(post);
		BufferedReader reader = new BufferedReader(new InputStreamReader(resp
				.getEntity().getContent()));
		StringBuilder builder = new StringBuilder();
		String s = "";
		while ((s = reader.readLine()) != null)
			builder.append(s);
		// Get the list of ids
		String auth_token = (builder.toString());

		System.out.println("auth_token:" + auth_token);
		SchedulerFactory factory = new StdSchedulerFactory();
		JobDetail detail = JobBuilder
				.newJob(RemoteHarvestJob.class)
				.withIdentity("_J" + auth_token)
				.usingJobData(QuartzConstants.REMOTE_SENSOR_AUTH_TOKEN,
						auth_token).build();

		try {
			Trigger tr = TriggerBuilder
					.newTrigger()
					.withIdentity("_T" + auth_token)
					.withSchedule(
							CronScheduleBuilder.cronSchedule("0/10 * * * * ?"))
					.startAt(new Date()).build();
			Scheduler sch = factory.getScheduler();
			sch.scheduleJob(detail, tr);
			sch.start();
			Thread t = Thread.currentThread();

			// time for insertion
			t.sleep(10000);

			SOLRSearchSensorDAO dao = new SOLRSearchSensorDAO();
			SirSearchCriteria crit = new SirSearchCriteria();
			ArrayList<String> list = new ArrayList<String>();
			list.add("test1");
			crit.setSearchText(list);
			Collection<SirSearchResultElement> results = dao.searchSensor(crit,
					true);
			Iterator<SirSearchResultElement> it = results.iterator();
			assertTrue(results.size() > 0);
			while (it.hasNext()) {
				SirSearchResultElement elem = it.next();
				// assertThat("Keywords should match results",((SirDetailedSensorDescription)elem.getSensorDescription()).getKeywords().toArray(),containsInAnyOrder(list.toArray()));
			}

		} catch (Exception e) {

		}

	}
}
