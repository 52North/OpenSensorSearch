package org.n52.sir.ds.solr.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Iterator;

import org.joda.time.DateTime;
import org.junit.Test;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.SirTimePeriod;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.ds.solr.data.ds.JSONSensor;
import org.n52.sir.ds.solr.data.ds.JSONSensorsCollection;
import org.n52.sir.ows.OwsExceptionReport;

import com.google.gson.Gson;

public class JSONSensorParserTest {
	@Test
	public void parseJsonSensorsAndInsert() throws IOException, OwsExceptionReport {
		File sensor_file = new File(ClassLoader.getSystemResource(
				"data/randomSensors.json").getFile());
		Gson gson = new Gson();
		StringBuilder builder = new StringBuilder();
		String s;
		BufferedReader reader = new BufferedReader(
				(new FileReader(sensor_file)));
		while ((s = reader.readLine()) != null)
			builder.append(s);
		JSONSensorsCollection collection = gson
				.fromJson(builder.toString(), JSONSensorsCollection.class);
		Iterator<JSONSensor> sensors = collection.sensors.iterator();
		while(sensors.hasNext()){
			SirSensor sensor = new SirSensor();
			JSONSensor jsensor = sensors.next();
			sensor.setKeywords(jsensor.keywords);
			SirTimePeriod period = new SirTimePeriod();
			DateTime begin = DateTime.parse(jsensor.beginPosition);
			DateTime end = DateTime.parse(jsensor.endPosition);
			//fix because we need start < end , and the data is randomly generated
			if(begin.getMillis() > end.getMillis()){
				DateTime temp = begin;
				end = begin;
				begin=temp;
			}
			period.setStartTime(begin.toDate());
			period.setEndTime(end.toDate());
			SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO();
			dao.insertSensor(sensor);
		}
	}
}
