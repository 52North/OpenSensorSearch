<!DOCTYPE html>
<html>

  <head>
    <meta charset='utf-8' />
    <meta http-equiv="X-UA-Compatible" content="chrome=1" />
    <meta name="description" content="Opensensorsearch : Open Sensor Search is a platform for sensor discovery across all sensor web supporting major specifications (OGC SWE) and popular IoT websites (Xively, Thingspeak, ...)." />

    <link rel="stylesheet" type="text/css" media="screen" href="../${context}/styles/stylesheet.css">

    <title>Opensensorsearch</title>
  </head>

  <body>

    <!-- HEADER -->
    <div id="header_wrap" class="outer">
        <header class="inner">
          <a id="forkme_banner" href="https://github.com/Moh-Yakoub/OpenSensorSearch">View on GitHub</a>

          <h1 id="project_title">Opensensorsearch</h1>
          <h2 id="project_tagline">Open Sensor Search is a platform for sensor discovery across all sensor web supporting major specifications (OGC SWE) and popular IoT websites (Xively, Thingspeak, ...).</h2>

            <section id="downloads">
              <a class="zip_download_link" href="https://github.com/Moh-Yakoub/OpenSensorSearch/zipball/master">Download this project as a .zip file</a>
              <a class="tar_download_link" href="https://github.com/Moh-Yakoub/OpenSensorSearch/tarball/master">Download this project as a tar.gz file</a>
            </section>
        </header>
    </div>

    <!-- MAIN CONTENT -->
    <div id="main_content_wrap" class="outer">
      <section id="main_content" class="inner">
        <h3>
<a name="welcome-to-opensensorsearch" class="anchor" href="#welcome-to-opensensorsearch"><span class="octicon octicon-link"></span></a>Welcome to OpenSensorSearch.</h3>

<p>Open Sensor Search is a platform for sensor discovery across all sensor web supporting major specifications (OGC SWE) and popular IoT websites (Xively, Thingspeak, ...) , The UI interface allows you to harvest and schedule harvest tasks for a remote server ( harvest callback ) or a javascript file you develop.</p>

<h3>
<a name="server-developer" class="anchor" href="#server-developer"><span class="octicon octicon-link"></span></a>Server developer</h3>

<ul>
<li>Choose the 'Harvest a remote server' tab , enter the url and click add server. , your <code>auth_token</code> by which you can refer to the server later</li>
<li>Your server needs to include the following routes</li>
</ul><pre><code>. GET /sensors
. GET /sensors/:id
</code></pre>

<p><code>GET /sensors/</code> implement the method that retrieve all of the sensors in a json list
<code>GET /sensors/:id</code> return the method that retrieves a sensor identified with an identifier.</p>

<ul>
<li>If you click on schedule harvest you can use the <code>auth_token</code> and the data to harvest a remote server.</li>
</ul><h3>
<a name="javascript-developers" class="anchor" href="#javascript-developers"><span class="octicon octicon-link"></span></a>Javascript developers</h3>

<ul>
<li>Clicking on the tab of the "Harvest a javascript script" allows you to upload a file containing a javascript , be careful that you need to supply your license with the code.</li>
<li>After uploading the process returns a script id which you can use later for schedule.</li>
<li>We provide here a few harvesting Javascript scripts.</li>
</ul><ol>
<li>
<p>A simple harvest script for basic utility.</p>

<pre><code>function insert() {
dao = new org.n52.sir.ds.solr.SOLRSearchSensorDAO();
sensor = new org.n52.sir.datastructure.SirSensor();
keywords = new java.util.ArrayList();
keywords.add("javascript");
keywords.add("harvest");
sensor.setKeywords(keywords);
// set contacts
contacts = new java.util.ArrayList();
contacts.add("rhino");
contacts.add("52north");
sensor.setContacts(contacts);
// add location
sensor.setLongitude("1.5");
sensor.setLatitude("3");

insert = new org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO();
return insert.insertSensor(sensor);
}
insert();
</code></pre>
</li>
<li>
<p>A harvest script for a smart citizen feed.</p>

<pre><code>function harvestSmartCitizenChannel(id){
var req = new org.n52.sir.script.HTTPRequest();
var query = "http://api.smartcitizen.me/v0.0.1/"+id+"/lastpost.json";
var respStr = req.doGet(query);
if(respStr){
    var obj = JSON.parse(respStr);
    if(obj){
        if(obj['devices']){
            var devices = obj['devices'];
            var count = 0;
            var ids = [];
            for(var i=0;i&lt;devices.length;i++){
                var channel = devices[i]
                var description = channel['description'];
                var id = channel['id'];
                var latitude = channel['geo_lat'];
                var longitude = channel['geo_long'];

                // TODO add an interface that hides the DAO
                // oss = new org.n52.sir.script.Sensors();
                // var keywords = ["keyword", "keyword1"];
                // oss.insertSensor(id, description, latitude, longitude, keywords, ...);

                // alternative:
                // Sensor newSensor = new org.n52.sir.script.Sensor();
                // newSensor.set(...);

                dao = new org.n52.sir.ds.solr.SOLRSearchSensorDAO();
                sensor = new org.n52.sir.datastructure.SirSensor();
                sensor.setDescription(description);
                sensor.setLongitude(longitude);
                sensor.setLatitude(latitude);
                insert = new org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO();
                insert.insertSensor(sensor);
                count++ ;
            }
            return count;

        }else return -1;
    }else return -1;
}else return -1;
}
harvestSmartCitizenChannel("feed_id_goes_here");
</code></pre>
</li>
</ol><h3>
<a name="license-of-agreement" class="anchor" href="#license-of-agreement"><span class="octicon octicon-link"></span></a>License of agreement</h3>

<pre><code>/**
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
 */
</code></pre>

<p>There are many routes useful to developer</p>

<pre><code>GET OpenSensorSearch/scripts/:id
POST OpenSensorSearch/scripts/:id/update
POST OpenSensorSearch/scripts/submit
GET OpenSensorSearch/scripts/schedule
POST OpenSensorSearch/scripts/remote/server
</code></pre>

<p>For the SIR binding , there are many supported operations for example
the operation GetCapabilities has the following request form</p>

<div class="highlight highlight-xml"><pre><span class="nt">&lt;sir:GetCapabilities</span> <span class="na">service=</span><span class="s">"SIR"</span> <span class="na">xmlns:sir=</span><span class="s">"http://swsl.uni-muenster.de/sir"</span> <span class="na">xmlns:xsi=</span><span class="s">"http://www.w3.org/2001/XMLSchema-instance"</span> <span class="na">xmlns=</span><span class="s">"http://www.opengis.net/ows/1.1"</span> <span class="na">xsi:schemaLocation=</span><span class="s">"http://swsl.uni-muenster.de/sir http://giv-genesis.uni-muenster.de/schemas/sir/sirAll.xsd http://www.opengis.net/ows/1.1 http://schemas.opengis.net/ows/1.1.0/owsAll.xsd"</span><span class="nt">&gt;</span>
  <span class="nt">&lt;AcceptVersions&gt;</span>
    <span class="nt">&lt;Version&gt;</span>0.3.0<span class="nt">&lt;/Version&gt;</span>
  <span class="nt">&lt;/AcceptVersions&gt;</span>
  <span class="nt">&lt;Sections&gt;</span>
    <span class="nt">&lt;Section&gt;</span>Operationsmetadata<span class="nt">&lt;/Section&gt;</span>
    <span class="nt">&lt;Section&gt;</span>Contents<span class="nt">&lt;/Section&gt;</span>
  <span class="nt">&lt;/Sections&gt;</span>
<span class="nt">&lt;/sir:GetCapabilities&gt;</span>
</pre></div>

<h3>
<a name="for-more-operations-please-check-the-sir-documentation" class="anchor" href="#for-more-operations-please-check-the-sir-documentation"><span class="octicon octicon-link"></span></a>For more operations please check the <a href="https://52north.org/twiki/bin/view/SensorWeb/SensorInstanceRegistry">SIR Documentation</a>.</h3>

<p>Finally the OpenSensorSearch supports the following specifications from OpenSearch specifications</p>

<ul>
<li><a href="http://www.opensearch.org/Specifications/OpenSearch/Extensions/Suggestions/1.1">Content assist</a></li>
<li><a href="http://www.opensearch.org/Specifications/OpenSearch/Extensions/Geo/1.0/Draft_2">GeoLocation extension</a></li>
</ul><p>For more information please check the <a href="https://wiki.52north.org/bin/view/Projects/GSoC2013OpenSensorSearch">wiki</a>.</p>

<h3>
<a name="contact" class="anchor" href="#contact"><span class="octicon octicon-link"></span></a>Contact</h3>

<p><a href="https://github.com/Moh-Yakoub" class="user-mention">@Moh-Yakoub</a></p>
      </section>
    </div>

    <!-- FOOTER  -->
    <div id="footer_wrap" class="outer">
      <footer class="inner">
        <p class="copyright">Opensensorsearch maintained by <a href="https://github.com/Moh-Yakoub">Moh-Yakoub</a></p>
        <p>Published with <a href="http://pages.github.com">GitHub Pages</a></p>
      </footer>
    </div>

    

  </body>
</html>