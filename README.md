# OpenSensorSearch

Open Sensor Search (OSS) is a platform for discovery of in-situ sensor data. across all sensor web supporting major specifications (OGC SWE) and popular IoT websites (Xively, Thingspeak, ...). It spans across large scale networks based on powerful OGC Sensor Web standards and protocols (Sensor Observation Service, SensorML) and specific individual platforms by providing a modular and open architecture and a flexible mechanism to include data from many different sources.


## Installation

The basic installation steps are:

* Download code from GitHub: ``git clone https://github.com/52North/OpenSensorSearch.git``
* Build the project using Maven: ``mvn install``
* Deploy the created web archives in a servlet container
* Add configuration files in home folder to override default settings.

### PostgreSQL/PostGIS Database

* Create an new database for a user with the required rights to create new tables.
  * If you use PostGIS < 9.1, make sure to use a postgis_template.
  * If you use PostGIS > 9.1, the commands to install the extension are included in the database creation script. If you added the exentions manually please remove the respetive lines from the script.
  * Make sure that the database user has sufficient rights on the database. If you create the db with the superuser ``postgres`` this might require an extra configuration step.
* Run the database creation script from the directory ``service/src/main/resource/db``
* Edit your settings based on the file ``service/src/main/resource/prop/org.n52.oss.service.db-sample.properties`` and save that file in the home folder of the machine running OSS.
* Restart the service application

For details please check the PostgreSQL (http://www.postgresql.org/docs/) and PostGIS (http://postgis.net/docs/) documentation.

### Solr

* **Download and install solr 4.4.0**: There are good tutorials for several platforms around, such as these for Debian/Linux: http://www.arunchinnachamy.com/apache-solr-installation-and-configuration/ and http://andreas-lehr.com/blog/archives/536-installing-apache-solr-on-debian-squeeze-6-0-and-tomcat7.html
* Download the solr configuration file from https://raw.github.com/52North/OpenSensorSearch/master/service/src/main/resources/db/solrconfig.xml and save it, e.g. as /tmp/solrconfig.xml
* Create a **node** in solr
  * See http://wiki.apache.org/solr/CoreAdmin for a detailed description.
  * Create a new core with the following call to the admin API: ``.../admin/cores?action=CREATE&name=opensensorsearch&instanceDir=opensensorsearch&config=/tmp/solrconfig.xml&schema=/tmp/solrschema.xml&persist=true&loadOnStartup=true``

### Test Data Generation
The current implementation supports performance tests using both [Apache JMeter](http://jmeter.apache.org) and a dummy sensor generation which can be used to insert test data as well.

There are two classes in the test package that can fill your backend with the dummy sensors , namely
* ``org.org.n52.sir.ds.solr.data.TempelateSensorTest``
* ``org.org.n52.sir.ds.solr.data.JSONSensorParserTest``

Alternatively you can harvest sensors from remote platforms to fill you database.


## Configuration

The service is mostly configured with Guice-loaded named bindings based on a couple of configuration files. No Maven profiles are needed for building a configured service, instead you can overwrite all settings in the base files by adding your own configuration files to your user home directory (or the user home of the servlet container user on a server).

### User Home Files

Several files have to be used so that Guice does not try to bind the same property twice, which results in creation errors. So if you configure a property from one base file in a different overwriting file you will have errors.

* server module
  * ``org.n52.oss.service.properties`` are added to (and potentially overwrite) the base file ``src/main/resources/app.properties``
  * ``org.n52.oss.service.db.properties`` are added to (and potentially overwrite) the base file ``src/main/resources/prop/db.properties``
  * ``org.n52.oss.service.sir.properties`` are added to (and potentially overwrite) the base file ``src/main/resources/prop/sir.properties``
* website module
  * ``org.n52.oss.website.properties`` overwrites the values in website module src/main/resources/org.n52.oss.website.properties

There are sample files in the same directories of the base file to get you started.

### Wiki

For technical information about Open Sensor Search please visit the [Wiki page](https://wiki.52north.org/bin/view/SensorWeb/OpenSensorSearch).
