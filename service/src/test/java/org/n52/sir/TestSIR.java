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

package org.n52.sir;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * TODO make unit tests out of these hacked tests
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class TestSIR {

    private static Logger log = LoggerFactory.getLogger(TestSIR.class);

    /**
     * Sends an HTTP GET request to a url
     * 
     * @param endpoint
     *        The URL of the server.
     * @param requestParameters
     *        all the request parameters
     * @return - The response from the end point
     */
    public static String sendGetRequest(String endpoint, String requestParameters) {
        String result = null;
        if (endpoint.startsWith("http://")) {
            // Send a GET request to the servlet
            try {
                // Send data
                String urlStr = endpoint;
                if (requestParameters != null && requestParameters.length() > 0) {
                    urlStr += "?" + requestParameters;
                }
                URL url = new URL(urlStr);
                URLConnection conn = url.openConnection();

                // Get the response
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer();
                String line;
                while ( (line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();

                log.debug("{}", XmlObject.Factory.parse(sb.toString()));

                result = sb.toString();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Reads data from the data reader and posts it to a server via POST request. data - The data you want to
     * send endpoint - The server's address output - writes the server's response to output
     * 
     * @param data
     *        Request data
     * @param endpoint
     *        URL of the Server
     * @param output
     *        the Server response
     * @throws Exception
     */
    public static void postData(Reader data, URL endpoint, Writer output) throws Exception {
        HttpURLConnection urlc = null;
        try {
            urlc = (HttpURLConnection) endpoint.openConnection();
            try {
                urlc.setRequestMethod("POST");
            }
            catch (ProtocolException e) {
                throw new Exception("Shouldn't happen: HttpURLConnection doesn't support POST??", e);
            }
            urlc.setDoOutput(true);
            urlc.setDoInput(true);
            urlc.setUseCaches(false);
            urlc.setAllowUserInteraction(false);
            urlc.setRequestProperty("Content-type", "text/xml; charset=" + "UTF-8");

            OutputStream out = urlc.getOutputStream();

            try {
                Writer writer = new OutputStreamWriter(out, "UTF-8");
                pipe(data, writer);
                writer.close();
            }
            catch (IOException e) {
                throw new Exception("IOException while posting data", e);
            }
            finally {
                if (out != null)
                    out.close();
            }

            InputStream in = urlc.getInputStream();
            try {
                Reader reader = new InputStreamReader(in);
                pipe(reader, output);
                reader.close();
            }
            catch (IOException e) {
                throw new Exception("IOException while reading response", e);
            }
            finally {
                if (in != null)
                    in.close();
            }

        }
        catch (IOException e) {
            throw new Exception("Connection error (is server running at " + endpoint + " ?): " + e);
        }
        finally {
            if (urlc != null)
                urlc.disconnect();
        }
    }

    /**
     * Pipes everything from the reader to the writer via a buffer
     */
    private static void pipe(Reader reader, Writer writer) throws IOException {
        char[] buf = new char[1024];
        int read = 0;
        while ( (read = reader.read(buf)) >= 0) {
            writer.write(buf, 0, read);
        }
        writer.flush();
    }

    public static void main(String[] args) {
        String url = "http://localhost:8080/SIR/sir";
        String path = "/media/DATEN/workspace/SIR/WebContent/TestRequests";
        Writer out = new BufferedWriter(new OutputStreamWriter(System.out));
        try {
            // ######################## getCapabilities ##################
            testGetCapabilities(url, path, out);

            // ######################## getAllServices ##################
            testGetAllServices(url, path, out);

            // // ######################## harvestService ##################
            testHarvestService(url, path, out);

            // // ######################## describeSensor ##################
            testDescribeSensor(url, path, out);

            // // ######################## SearchSensor ##################
            testSearchSensor(url, path, out);

            // ######################## InsertSensorInfo ##################
            testInsertSensorInfo(url, path, out);

            // ######################## InsertSensorStatus ##################
            testInsertSensorStatus(url, path, out);

            // ######################## GetSensorStatus ##################
            testGetSensorStatus(url, path, out);

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param url
     * @param path
     * @param out
     * @throws FileNotFoundException
     * @throws Exception
     * @throws MalformedURLException
     */
    private static void testInsertSensorStatus(String url, String path, Writer out) throws FileNotFoundException,
            Exception,
            MalformedURLException {
        Reader in;
        log.info("----------- POST InsertSensorStatus by sensorIDInSIR -----------");
        in = new FileReader(new File(path + "/InsertSensorStatus/bySensorIDInSIR.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST InsertSensorStatus by serviceDescription -----------");
        in = new FileReader(new File(path + "/InsertSensorStatus/byServiceDescription.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST InsertSensorStatus wrong sensorIDInSIR -----------");
        in = new FileReader(new File(path + "/InsertSensorStatus/wrongSensorIDInSIR.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST InsertSensorStatus wrong serviceDescription -----------");
        in = new FileReader(new File(path + "/InsertSensorStatus/wrongServiceDescription.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST InsertSensorStatus statusDescription missing -----------");
        in = new FileReader(new File(path + "/InsertSensorStatus/statusDescriptionMissing.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST InsertSensorStatus sensorIdentification missing -----------");
        in = new FileReader(new File(path + "/InsertSensorStatus/sensorIdentificationMissing.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST InsertSensorStatus status missing -----------");
        in = new FileReader(new File(path + "/InsertSensorStatus/statusMissing.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST InsertSensorStatus propertyName missing -----------");
        in = new FileReader(new File(path + "/InsertSensorStatus/propertyNameMissing.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST InsertSensorStatus propertyValue missing -----------");
        in = new FileReader(new File(path + "/InsertSensorStatus/propertyValueMissing.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST InsertSensorStatus uom missing -----------");
        in = new FileReader(new File(path + "/InsertSensorStatus/uomMissing.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST InsertSensorStatus timestamp missing -----------");
        in = new FileReader(new File(path + "/InsertSensorStatus/timestampMissing.xml"));
        postData(in, new URL(url), out);
    }

    /**
     * @param url
     * @param path
     * @param out
     * @throws FileNotFoundException
     * @throws Exception
     * @throws MalformedURLException
     */
    private static void testInsertSensorInfo(String url, String path, Writer out) throws FileNotFoundException,
            Exception,
            MalformedURLException {
        Reader in;
        log.info("----------- POST InsertSensorInfo new sensor -----------");
        in = new FileReader(new File(path + "/InsertSensorInfo/newSensor.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST InsertSensorInfo delete sensor -----------");
        in = new FileReader(new File(path + "/InsertSensorInfo/deleteSensor.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST InsertSensorInfo add reference -----------");
        in = new FileReader(new File(path + "/InsertSensorInfo/addReference.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST InsertSensorInfo delete reference -----------");
        in = new FileReader(new File(path + "/InsertSensorInfo/deleteReference.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST InsertSensorInfo update sensor -----------");
        in = new FileReader(new File(path + "/InsertSensorInfo/updateSensor.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST InsertSensorInfo infoToBeInserted missing -----------");
        in = new FileReader(new File(path + "/InsertSensorInfo/infoToBeInsertedMissing.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST InsertSensorInfo delete in sensorInfo missing -----------");
        in = new FileReader(new File(path + "/InsertSensorInfo/deleteInSensorInfoMissing.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST InsertSensorInfo delete in serviceInfo missing -----------");
        in = new FileReader(new File(path + "/InsertSensorInfo/deleteInServiceInfoMissing.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST InsertSensorInfo serviceDescription in serviceInfo missing -----------");
        in = new FileReader(new File(path + "/InsertSensorInfo/serviceDescriptionInServiceInfoMissing.xml"));
        postData(in, new URL(url), out);
    }

    /**
     * @param url
     * @param path
     * @param out
     * @throws FileNotFoundException
     * @throws Exception
     * @throws MalformedURLException
     */
    private static void testSearchSensor(String url, String path, Writer out) throws FileNotFoundException,
            Exception,
            MalformedURLException {
        Reader in;
        log.info("----------- POST searchSensor by search criteria -----------");
        in = new FileReader(new File(path + "/SearchSensor/bySearchCriteria.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST searchSensor by sensorIDInSIR -----------");
        in = new FileReader(new File(path + "/SearchSensor/bySensorIDInSIR.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST searchSensor by service description -----------");
        in = new FileReader(new File(path + "/SearchSensor/byServiceDescription.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST searchSensor no identification or search criteria -----------");
        in = new FileReader(new File(path + "/SearchSensor/noIdentOrCriteria.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST searchSensor no sensorID or service description -----------");
        in = new FileReader(new File(path + "/SearchSensor/noSensorIDOrServDesc.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST searchSensor no serviceURL and service type and serviceSpecificSensorID -----------");
        in = new FileReader(new File(path + "/SearchSensor/noServiceAndSpecificID.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST searchSensor no starttime and endtime -----------");
        in = new FileReader(new File(path + "/SearchSensor/noStarttimeAndEndtime.xml"));
        postData(in, new URL(url), out);
    }

    /**
     * @param url
     * @param path
     * @param out
     * @throws FileNotFoundException
     * @throws Exception
     * @throws MalformedURLException
     */
    private static void testDescribeSensor(String url, String path, Writer out) throws FileNotFoundException,
            Exception,
            MalformedURLException {
        Reader in;
        log.info("----------- GET describe sensor right -----------");
        sendGetRequest(url, "request=describeSensor&sensorID=1");
        log.info("----------- GET describe sensor REQUEST missing -----------");
        sendGetRequest(url, "sensorID=1");
        log.info("----------- GET describe sensor REQUEST wrong -----------");
        sendGetRequest(url, "request=deeSensor&sensorID=1");
        log.info("----------- GET describe sensor SENSORID missing -----------");
        sendGetRequest(url, "request=describeSensor");
        log.info("----------- GET describe sensor SENSORID not found -----------");
        sendGetRequest(url, "request=describeSensor&sensorID=500");
        log.info("----------- POST describe sensor right -----------");
        in = new FileReader(new File(path + "/DescribeSensor/right.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST describe sensor sensorIDInSir missing -----------");
        in = new FileReader(new File(path + "/DescribeSensor/sensorIDMissing.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST describe sensor sensorIDInSir not found -----------");
        in = new FileReader(new File(path + "/DescribeSensor/sensorIDNotFound.xml"));
        postData(in, new URL(url), out);
    }

    /**
     * @param url
     * @param path
     * @param out
     * @throws FileNotFoundException
     * @throws Exception
     * @throws MalformedURLException
     */
    private static void testHarvestService(String url, String path, Writer out) throws FileNotFoundException,
            Exception,
            MalformedURLException {
        Reader in;
        log.info("----------- POST harvestService OWS5SOS -----------");
        in = new FileReader(new File(path + "/HarvestService/OWS5SOS.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST harvestService WeatherSOS -----------");
        in = new FileReader(new File(path + "/HarvestService/WeatherSOS.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST harvestService type missing -----------");
        in = new FileReader(new File(path + "/HarvestService/typeMissing.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST harvestService url missing -----------");
        in = new FileReader(new File(path + "/HarvestService/urlMissing.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST harvestService wrong -----------");
        in = new FileReader(new File(path + "/HarvestService/wrong.xml"));
        postData(in, new URL(url), out);
    }

    /**
     * @param url
     * @param path
     * @param out
     * @throws FileNotFoundException
     * @throws Exception
     * @throws MalformedURLException
     */
    private static void testGetAllServices(String url, String path, Writer out) throws FileNotFoundException,
            Exception,
            MalformedURLException {
        Reader in;
        log.info("----------- POST getAllServices right -----------");
        in = new FileReader(new File(path + "/GetAllServices/right.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST getAllServices wrong -----------");
        in = new FileReader(new File(path + "/GetAllServices/wrong.xml"));
        postData(in, new URL(url), out);
    }

    /**
     * @param url
     * @param path
     * @param out
     * @throws FileNotFoundException
     * @throws Exception
     * @throws MalformedURLException
     */
    private static void testGetCapabilities(String url, String path, Writer out) throws FileNotFoundException,
            Exception,
            MalformedURLException {
        Reader in;
        log.info("----------- GET get capabilities right -----------");
        sendGetRequest(url, "request=getCapabilities&service=sir");
        log.info("----------- GET get capabilities wrong REQUEST -----------");
        sendGetRequest(url, "requst=getCapabilities&service=sir");
        log.info("----------- GET get capabilities wrong REQUEST entry -----------");
        sendGetRequest(url, "request=getCapabilitie&service=sir");
        log.info("----------- GET get capabilities wrong SERVICE -----------");
        sendGetRequest(url, "request=getCapabilities&servce=sir");
        log.info("----------- GET get capabilities wrong SERVICE entry -----------");
        sendGetRequest(url, "request=getCapabilities&service=sr");
        log.info("----------- GET get capabilities REQUEST missing -----------");
        sendGetRequest(url, "service=sir");
        log.info("----------- GET get capabilities SERVICE missing -----------");
        sendGetRequest(url, "request=getCapabilities");
        log.info("----------- POST get capabilities right -----------");
        in = new FileReader(new File(path + "/GetCapabilities/right.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST get capabilities wrong REQUEST -----------");
        in = new FileReader(new File(path + "/GetCapabilities/wrongRequest.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST get capabilities wrong SERVICE -----------");
        in = new FileReader(new File(path + "/GetCapabilities/wrongService.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST get capabilities wrong SERVICE entry -----------");
        in = new FileReader(new File(path + "/GetCapabilities/wrongServiceEntry.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST get capabilities SERVICE missing -----------");
        in = new FileReader(new File(path + "/GetCapabilities/serviceMissing.xml"));
        postData(in, new URL(url), out);
    }

    /**
     * @param url
     * @param path
     * @param out
     * @throws FileNotFoundException
     * @throws Exception
     * @throws MalformedURLException
     */
    private static void testGetSensorStatus(String url, String path, Writer out) throws FileNotFoundException,
            Exception,
            MalformedURLException {
        Reader in;
        log.info("----------- POST GetSensorStatus by sensorIDInSIR -----------");
        in = new FileReader(new File(path + "/GetSensorStatus/bySensorIDInSIR.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST GetSensorStatus by serviceDescription -----------");
        in = new FileReader(new File(path + "/GetSensorStatus/byServiceDescription.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST GetSensorStatus by searchCriteria -----------");
        in = new FileReader(new File(path + "/GetSensorStatus/bySearchCriteria.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST GetSensorStatus propertyName missing -----------");
        in = new FileReader(new File(path + "/GetSensorStatus/propertyNameMissing.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST GetSensorStatus constraint missing -----------");
        in = new FileReader(new File(path + "/GetSensorStatus/constraintMissing.xml"));
        postData(in, new URL(url), out);
        log.info("----------- POST GetSensorStatus isEqual missing -----------");
        in = new FileReader(new File(path + "/GetSensorStatus/isEqualMissing.xml"));
        postData(in, new URL(url), out);
    }

}