/**
 * ?Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.harvest.exec.IHarvestJobExec;
import org.n52.sir.harvest.exec.impl.RemoteCallbackHarvestJobExec;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.scheduler.QuartzConstants;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(MockitoJUnitRunner.class)
public class remoteHarvestJobTest {

	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(3000); // No-args
	
	public static final String FIRST="first";
	public static final String SECOND= "second";
	BaseMatcher<SirSensor> firstSensorMatcher = new BaseMatcher<SirSensor>() {

		@Override
		public void describeTo(Description arg0) {
		}

		@Override
		public boolean matches(Object arg0) {
			if(arg0==null)return false;
			return ((SirSensor) arg0).getKeywords().contains("test1");
		}
	};

	BaseMatcher<SirSensor> secondSensorMatcher = new BaseMatcher<SirSensor>() {

		@Override
		public void describeTo(Description arg0) {

		}

		@Override
		public boolean matches(Object arg0) {
			if(arg0==null)return false;
				return ((SirSensor) arg0).getKeywords().contains("test2");
		}
	};

	
	public String insertFirst(){
		System.setProperty(FIRST,"true");
		return "1";
	}
	
	public String insertSecond(){
		System.setProperty(SECOND,"true");
		return "2";
	}
	@Mock
	IInsertSensorInfoDAO insertDAO;
	
	

	@Before
	public void setStubServerAndDataStorage() throws OwsExceptionReport{

		stubFor(get(urlEqualTo("/sensors")).willReturn(
				aResponse().withHeader("Content-Type", "text/json").withBody(
						"[1,2]")));
		stubFor(get(urlEqualTo("/sensors/1")).willReturn(
				aResponse().withHeader("Content-Type", "text/json").withBody(
						"{id:1,keywords:['test1','testkeyword1']}")));
		stubFor(get(urlEqualTo("/sensors/2")).willReturn(
				aResponse().withHeader("Content-Type", "text/json").withBody(
						"{id:2,keywords:['test2','testkeyword2']}")));
		when(insertDAO.insertSensor(argThat(firstSensorMatcher))).thenReturn(insertFirst());
		when(insertDAO.insertSensor(argThat(secondSensorMatcher))).thenReturn(insertSecond());
	}
	@Test
	public void callRemoteHarvestJobAndMacth() throws SchedulerException, InterruptedException{

		JobDataMap map = new JobDataMap();
		map.put(QuartzConstants.REMOTE_SENSOR_URL, "http://localhost:3000");
		map.put(QuartzConstants.INSERTION_INTERFACE, insertDAO);
		
		IHarvestJobExec job = new RemoteCallbackHarvestJobExec();
		job.execute(map, "_J59", "_T59", new Date());
		Thread.sleep(10000);
		assertEquals(System.getProperty(FIRST),"true");
		assertEquals(System.getProperty(SECOND),"true");
		
		

	}
}