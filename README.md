#OpenSensorSearch#

Open Sensor Search (OSS) is a platform for discovery of in-situ sensor data. It spans across large scale networks based on heavyweight OGC Sensor Web standards and protocols (SensorObservationService, SensorML, ...) and grassroots platforms and the internet of things (IoT) with a modular and open architecture. Open Sensor Search is based on the SensorInstanceRegistry and can be seen as its next version

###1- Test set Generation
The current implementation supports test performance using both [Apache JMeter](http://jmeter.apache.org) and a dummy sensor generation as well as harvest sensor for harvesting remote sensors.
<p>There are two classes in the test package that can fill your backend with the dummy sensors , namely
<p>1. <code>org.org.n52.sir.ds.solr.data.TempelateSensorTest</code>
<p>2. <code>org.org.n52.sir.ds.solr.data.JSONSensorParserTest</code>

###Wiki

For technical information about the OpenSensorSearch you can visit the [Wiki page](https://wiki.52north.org/bin/view/SensorWeb/OpenSensorSearch)

