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
package org.n52.oss.testdata.sml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.opengis.sensorML.x101.ComponentsDocument.Components.ComponentList.Component;
import net.opengis.sensorML.x101.ContactDocument.Contact;
import net.opengis.sensorML.x101.ContactInfoDocument.ContactInfo;
import net.opengis.sensorML.x101.ContactInfoDocument.ContactInfo.Address;
import net.opengis.sensorML.x101.ResponsiblePartyDocument.ResponsibleParty;

import org.joda.time.DateMidnight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class TestSensorFactory {

    private static Logger log = LoggerFactory.getLogger(TestSensorFactory.class);

    public static final String OGC_PROPERTY_PREFIX = "urn:ogc:def:property:OGC:1.0:";

    public static final String OGC_INTERFACE_PREFIX = "urn:ogc:def:interface:OGC:1.0:";

    public static final String UNIQUE_ID_PREFIX = "urn:ogc:object:feature:Sensor:IFGI:";

    private static final String DEFAULT_DESCRIPTION = "Test sensor (dummy, fake!) in the vicinity of Muenster, Germany";

    private static final DateMidnight DEFAULT_VALID_TIME_BEGIN = new DateMidnight("2010-04-28");

    private static final DateMidnight DEFAULT_VALID_TIME_END = new DateMidnight("2010-07-15");

    private static final String SENSOR_ID_PREFIX = "station ";

    private static final double[] DEFAULT_BBOX_LOWER_CORNER = {51.77d, 7.3d};

    private static final double[] DEFAULT_BBOX_UPPER_CORNER = {52.076d, 7.833d};

    private Random rand;

    private ArrayList<String> intendedApplications;

    private HashMap<String, List<String>> availableKeywords;

    private HashMap<String, List<String>> availableSensorTypes;

    private HashMap<String, List<String[]>> availableInterfaces;

    private HashMap<String, List<String>> availableInputs;

    private HashMap<String, String[]> outputsForInputs;

    private HashMap<String, String> uomsForOutputs;

    /**
     * 
     */
    public TestSensorFactory() {
        this.rand = new Random();

        this.intendedApplications = new ArrayList<String>();
        this.intendedApplications.add("weather");
        this.intendedApplications.add("air pollutant");
        this.intendedApplications.add("water gauge");

        this.availableKeywords = new HashMap<String, List<String>>();
        this.availableKeywords.put("weather",
                                   Arrays.asList(new String[] {"weather station",
                                                               "precipitation",
                                                               "wind",
                                                               "wind speed",
                                                               "wind direction",
                                                               "temperature",
                                                               "surface pressure",
                                                               "precipitation",
                                                               "air pressure",
                                                               "rain",
                                                               "surface air temperature",
                                                               "climatology",
                                                               "humidity",
                                                               "sunshine"}));
        this.availableKeywords.put("water gauge",
                                   Arrays.asList(new String[] {"water level",
                                                               "water gage",
                                                               "flood",
                                                               "warning system",
                                                               "weather",
                                                               "water",
                                                               "surface runoff",
                                                               "precipitation"}));
        this.availableKeywords.put("air pollutant",
                                   Arrays.asList(new String[] {"air quality",
                                                               "air pollution",
                                                               "public health",
                                                               "clean air",
                                                               "outdoor air pollution",
                                                               "primary pollutants",
                                                               "secondary pollutants",
                                                               "smog"}));

        this.availableSensorTypes = new HashMap<String, List<String>>();
        this.availableSensorTypes.put("weather",
                                      Arrays.asList(new String[] {"weather station",
                                                                  "private weather station",
                                                                  "weather station",
                                                                  "human observer",
                                                                  "old guy with thermometer",
                                                                  "weather station (fixed position)"}));
        this.availableSensorTypes.put("water gauge",
                                      Arrays.asList(new String[] {"gauge station",
                                                                  "bridge pillar station",
                                                                  "reservoir (intake)",
                                                                  "reservoir (drain)",
                                                                  "river",
                                                                  "river gauge"}));
        this.availableSensorTypes.put("air pollutant",
                                      Arrays.asList(new String[] {"mobile roadside station",
                                                                  "mechanical collector (active)",
                                                                  "diffusion tube",
                                                                  "optical analyser"}));

        this.availableInterfaces = new HashMap<String, List<String[]>>();
        // format: name, ServiceURL, ServiceType
        List<String[]> weatherInterfaces = new ArrayList<String[]>();
        weatherInterfaces.add(new String[] {"SOS1", "http://v-swe.uni-muenster.de:8080/WeatherSOS/sos", "SOS"});
        weatherInterfaces.add(new String[] {"SOS2", "http://giv-sos.uni-muenster.de:8080/ClimateSOS/sos", "SOS"});
        weatherInterfaces.add(new String[] {"SOS3", "http://giv-sos.uni-muenster.de:8080/52nSOSv3", "SOS"});
        weatherInterfaces.add(new String[] {"SOS4", "http://mars.uni-muenster.de:8080/OWS5SOS", "SOS"});
        weatherInterfaces.add(new String[] {"SPS1", "http://v-swe.uni-muenster.de:8080/SPS/sps", "SPS"});
        weatherInterfaces.add(new String[] {"SPS2", "http://v-swe.uni-muenster.de:8080/SAS/sas", "SPS"});
        weatherInterfaces.add(new String[] {"SAS1", "http://giv-genesis.uni-muenster.de:8080/SAS1", "SAS"});
        weatherInterfaces.add(new String[] {"SAS2", "http://giv-genesis.uni-muenster.de:8080/SAS2", "SAS"});

        List<String[]> waterGaugeInterfaces = new ArrayList<String[]>();
        waterGaugeInterfaces.add(new String[] {"PegelSOS1",
                                               "http://giv-sos.uni-muenster.de:8080/SosRheinpegelNord/sos",
                                               "SOS"});
        waterGaugeInterfaces.add(new String[] {"PegelSOS2",
                                               "http://giv-sos.uni-muenster.de:8080/SosRheinpegelSued/sos",
                                               "SOS"});
        waterGaugeInterfaces.add(new String[] {"SOS5", "http://giv-sos.uni-muenster.de:8080/Waterflow-SOS-1/sos", "SOS"});
        waterGaugeInterfaces.add(new String[] {"SOS6", "http://giv-sos.uni-muenster.de:8080/Waterflow-SOS-2/sos", "SOS"});
        waterGaugeInterfaces.add(new String[] {"WaterSAS", "http://giv-genesis.uni-muenster.de:8080/water/SAS", "SAS"});

        List<String[]> airPollutantInterfaces = new ArrayList<String[]>();
        airPollutantInterfaces.add(new String[] {"SOS1", "http://pollution.uni-muenster.de/SOS1/sos", "SOS"});
        airPollutantInterfaces.add(new String[] {"SOS2", "http://pollution.uni-muenster.de/SOS2/sos", "SOS"});
        airPollutantInterfaces.add(new String[] {"SPS1", "http://pollution.uni-muenster.de/SPS1/sps", "SPS"});
        airPollutantInterfaces.add(new String[] {"SPS2", "http://v-swe.uni-muenster.de:8080/PollutionSPS/sps", "SPS"});
        airPollutantInterfaces.add(new String[] {"PollSAS",
                                                 "http://giv-genesis.uni-muenster.de:8080/pollution/SAS",
                                                 "SAS"});

        this.availableInterfaces.put("weather", weatherInterfaces);
        this.availableInterfaces.put("water gauge", waterGaugeInterfaces);
        this.availableInterfaces.put("air pollutant", airPollutantInterfaces);

        this.availableInputs = new HashMap<String, List<String>>();
        this.availableInputs.put("weather",
                                 Arrays.asList(new String[] {"wind",
                                                             "wind",
                                                             "precipitation",
                                                             "sunshine",
                                                             "temperature",
                                                             "temperature",
                                                             "temperature",
                                                             "cloud_webcam"}));
        this.availableInputs.put("water gauge",
                                 Arrays.asList(new String[] {"water_level",
                                                             "water_level",
                                                             "water_level",
                                                             "river_level",
                                                             "webcam",
                                                             "current"}));
        this.availableInputs.put("air pollutant",
                                 Arrays.asList(new String[] {"ozone",
                                                             "carbonMonoxide",
                                                             "carbonDioxide",
                                                             "particulates",
                                                             "nitrogenOxides"}));

        this.outputsForInputs = new HashMap<String, String[]>();
        this.outputsForInputs.put(OGC_PROPERTY_PREFIX + "wind", new String[] {"windSpeed", "windDirection"});
        this.outputsForInputs.put(OGC_PROPERTY_PREFIX + "sunshine", new String[] {"sunshineDuration", "brightness"});
        this.outputsForInputs.put(OGC_PROPERTY_PREFIX + "temperature", new String[] {"temperature"});
        this.outputsForInputs.put(OGC_PROPERTY_PREFIX + "cloud_webcam", new String[] {"cloudCoverage"});
        this.outputsForInputs.put(OGC_PROPERTY_PREFIX + "precipitation", new String[] {"precipitation"});

        this.outputsForInputs.put(OGC_PROPERTY_PREFIX + "water_level", new String[] {"gauge"});
        this.outputsForInputs.put(OGC_PROPERTY_PREFIX + "river_level", new String[] {"gauge"});
        this.outputsForInputs.put(OGC_PROPERTY_PREFIX + "webcam", new String[] {"gauge"});
        this.outputsForInputs.put(OGC_PROPERTY_PREFIX + "current", new String[] {"gauge"});

        this.outputsForInputs.put(OGC_PROPERTY_PREFIX + "ozone", new String[] {"ozoneConcentration"});
        this.outputsForInputs.put(OGC_PROPERTY_PREFIX + "carbonMonoxide", new String[] {"carbonMonoxideConcentration"});
        this.outputsForInputs.put(OGC_PROPERTY_PREFIX + "particulates", new String[] {"particulates"});
        this.outputsForInputs.put(OGC_PROPERTY_PREFIX + "nitrogenOxides", new String[] {"NOxConcentration"});
        this.outputsForInputs.put(OGC_PROPERTY_PREFIX + "carbonDioxide", new String[] {"CO2Concentration"});

        this.uomsForOutputs = new HashMap<String, String>();
        this.uomsForOutputs.put("windSpeed", "m/s");
        this.uomsForOutputs.put("windDirection", "deg");
        this.uomsForOutputs.put("sunshineDuration", "hrs");
        this.uomsForOutputs.put("temperature", "Cel");
        this.uomsForOutputs.put("cloudCoverage", "%");
        this.uomsForOutputs.put("precipitation", "mm");
        this.uomsForOutputs.put("brightness", "cd/m2");

        this.uomsForOutputs.put("gauge", "m");

        this.uomsForOutputs.put("ozoneConcentration", "mol/m3");
        this.uomsForOutputs.put("CO2Concentration", "mol/m3");
        this.uomsForOutputs.put("carbonMonoxideConcentration", "mol/m3");
        this.uomsForOutputs.put("NOxConcentration", "mol/m3");
        this.uomsForOutputs.put("particulates", "kg/m3");
        this.uomsForOutputs.put("NOx", "mol/m3");
        this.uomsForOutputs.put("CO2", "mol/m3");
    }

    public TestSensor createRandomTestSensor() {
        if (log.isDebugEnabled())
            log.debug("Creating random sensor... ");

        String sensorId = randomSensorId();
        String uniqueId = UNIQUE_ID_PREFIX + sensorId.replace(" ", "_");
        String longName = sensorId + ", a dummy test station near Münster";
        String shortName = sensorId;

        String gmlDescription = DEFAULT_DESCRIPTION;
        String validTimeBegin = generateTimeAround(DEFAULT_VALID_TIME_BEGIN, 3);
        String validTimeEnd = generateTimeAround(DEFAULT_VALID_TIME_END, 20);

        String intendedApplication = randomIntendedApplication();

        Collection<String> keywords = generateKeywords(intendedApplication);
        String sensorType = generateSensorType(intendedApplication);

        double[] bboxLowerCorner = DEFAULT_BBOX_LOWER_CORNER;
        double[] bboxUpperCorner = DEFAULT_BBOX_UPPER_CORNER;

        Contact contact = Contact.Factory.newInstance();
        ResponsibleParty rp = contact.addNewResponsibleParty();
        rp.setId("WWU_ifgi_test_sensor_data_contact_" + this.rand.nextLong());
        rp.setIndividualName("Daniel Nuest");
        rp.setOrganizationName("Institute for Geoinformatics - Westfaelische Wilhelms Universitaet Muenster - Sensor Web and Simulation Lab");
        ContactInfo ci = rp.addNewContactInfo();
        Address a = ci.addNewAddress();
        a.setCity("Münster");
        a.setPostalCode("48151");
        a.setCountry("Germany");
        a.setElectronicMailAddress("daniel.nuest@uni-muenster.de");
        a.setDeliveryPointArray(new String[] {"Weseler Straße 253"});

        String locationId = "SYSTEM_LOCATION_" + this.rand.nextLong();
        double[] latLonPosition = generatePosition(bboxLowerCorner, bboxUpperCorner);
        double altitude = randomAltitude();

        Collection<Map<String, String>> interfaces = randomInterfaces(intendedApplication);
        Collection<Map<String, String>> inputs = generateInputs(intendedApplication);
        Collection<Map<String, String>> output = generateOutputs(inputs);

        List<Component> components = null;

        TestSensor s = new TestSensor(gmlDescription,
                                      keywords,
                                      uniqueId,
                                      longName,
                                      shortName,
                                      intendedApplication,
                                      sensorType,
                                      validTimeBegin,
                                      validTimeEnd,
                                      bboxLowerCorner,
                                      bboxUpperCorner,
                                      locationId,
                                      contact,
                                      latLonPosition,
                                      altitude,
                                      interfaces,
                                      inputs,
                                      output,
                                      components);

        return s;
    }

    /**
     * 
     * @param inputs
     * @return
     */
    private Collection<Map<String, String>> generateOutputs(Collection<Map<String, String>> inputs) {
        Collection<Map<String, String>> outputs = new ArrayList<Map<String, String>>();

        for (Map<String, String> in : inputs) {
            String key = in.get(SensorMLEncoder.MAP_KEY_DEFINITION);
            String[] outs = this.outputsForInputs.get(key);
            for (String s : outs) {
                HashMap<String, String> currentOutput = new HashMap<String, String>();
                currentOutput.put(SensorMLEncoder.MAP_KEY_NAME, in.get(SensorMLEncoder.MAP_KEY_NAME));
                currentOutput.put(SensorMLEncoder.MAP_KEY_DEFINITION, OGC_PROPERTY_PREFIX + s);
                currentOutput.put(SensorMLEncoder.MAP_KEY_UOM, this.uomsForOutputs.get(s));
                outputs.add(currentOutput);
            }
        }

        return outputs;
    }

    /**
     * 
     * @param intendedApplication
     * @return
     */
    private Collection<Map<String, String>> generateInputs(String intendedApplication) {
        int howMany = this.rand.nextInt(3) + 1;
        if (log.isDebugEnabled())
            log.debug("Generating " + howMany + " inputs for " + intendedApplication);

        Collection<Map<String, String>> inputs = new ArrayList<Map<String, String>>();
        List<String> added = new ArrayList<String>();
        List<String> possibleInterfaces = this.availableInputs.get(intendedApplication);

        for (int i = 0; i <= howMany; i++) {
            String s = possibleInterfaces.get(this.rand.nextInt(possibleInterfaces.size()));
            if ( !added.contains(s)) {
                added.add(s);

                HashMap<String, String> currentInput = new HashMap<String, String>();
                currentInput.put(SensorMLEncoder.MAP_KEY_NAME, s);
                currentInput.put(SensorMLEncoder.MAP_KEY_DEFINITION, OGC_PROPERTY_PREFIX + s.replace(" ", "_"));
                inputs.add(currentInput);
            }
        }

        return inputs;
    }

    /**
     * 
     * @param intendedApplication
     * @return
     */
    private Collection<Map<String, String>> randomInterfaces(String intendedApplication) {
        int howMany = this.rand.nextInt(2) + 1;
        if (log.isDebugEnabled())
            log.debug("Generating " + howMany + " interfaces for " + intendedApplication);

        Collection<Map<String, String>> interfaces = new ArrayList<Map<String, String>>();
        List<String> added = new ArrayList<String>();
        List<String[]> possibleInterfaces = this.availableInterfaces.get(intendedApplication);

        for (int i = 0; i <= howMany; i++) {
            String[] current = possibleInterfaces.get(this.rand.nextInt(possibleInterfaces.size()));
            if ( !added.contains(current[1])) {
                added.add(current[1]);

                HashMap<String, String> currentInterface = new HashMap<String, String>();
                currentInterface.put(OGC_INTERFACE_PREFIX + "ServiceURL", current[1]);
                currentInterface.put(OGC_INTERFACE_PREFIX + "ServiceType", current[2]);
                currentInterface.put(OGC_INTERFACE_PREFIX + "ServiceSpecificSensorID",
                                     current[0] + ":" + this.rand.nextInt(1000));
                interfaces.add(currentInterface);
            }
        }

        return interfaces;
    }

    /**
     * 
     * randome value for altitudes, between roughly (!) 5 and 100
     * 
     * @return
     */
    private double randomAltitude() {
        double result = (this.rand.nextInt(950) + 50) / 9.9d * 100;
        result = Math.round(result);
        result = result / 100;
        return result;
    }

    /**
     * 
     * create random values between the lower and upper bounds.
     * 
     * @param bboxLowerCorner
     * @param bboxUpperCorner
     * @return
     */
    private double[] generatePosition(double[] bboxLowerCorner, double[] bboxUpperCorner) {
        double latDiff = bboxUpperCorner[0] - bboxLowerCorner[0];
        double lat = (this.rand.nextDouble() * latDiff) + bboxLowerCorner[0];
        double lonDiff = bboxUpperCorner[1] - bboxLowerCorner[1];
        double lon = (this.rand.nextDouble() * lonDiff) + bboxLowerCorner[1];
        return new double[] {lat, lon};
    }

    /**
     * 
     * @param intendedApplication
     * @return
     */
    private String generateSensorType(String intendedApplication) {
        List<String> types = this.availableSensorTypes.get(intendedApplication);
        return types.get(this.rand.nextInt(types.size()));
    }

    /**
     * 
     * @param intendedApplication
     * @return
     */
    private List<String> generateKeywords(String intendedApplication) {
        int howMany = this.rand.nextInt(3) + 1;
        if (log.isDebugEnabled())
            log.debug("Generating " + howMany + " keywords for " + intendedApplication);

        List<String> keywords = new ArrayList<String>();
        List<String> possibleKeywords = this.availableKeywords.get(intendedApplication);

        for (int i = 0; i <= howMany; i++) {
            String k = possibleKeywords.get(this.rand.nextInt(possibleKeywords.size()));
            if ( !keywords.contains(k))
                keywords.add(k);
        }

        return keywords;
    }

    /**
     * 
     * @return
     */
    private String randomIntendedApplication() {
        return this.intendedApplications.get(this.rand.nextInt(this.intendedApplications.size()));
    }

    /**
     * 
     * @return
     */
    private String randomSensorId() {
        return SENSOR_ID_PREFIX + this.rand.nextInt(10000);
    }

    /**
     * 
     * @param base
     * @param days
     * @return
     */
    private String generateTimeAround(DateMidnight base, int days) {
        if (this.rand.nextBoolean()) {
            return base.plusDays(days).toString();
        }
        return base.minusDays(days).toString();
    }

}
