# OpenSensorSearch

Open Sensor Search (OSS) is a platform for discovery of in-situ sensor data. across all sensor web supporting major specifications (OGC SWE) and popular IoT websites (Xively, Thingspeak, ...). It spans across large scale networks based on powerful OGC Sensor Web standards and protocols (Sensor Observation Service, SensorML) and specific individual platforms by providing a modular and open architecture and a flexible mechanism to include data from many different sources.


## Structure

OSS is written in Java and organized as a Maven multi-module project. The modules and their functionaliy are briefly desribed here, but they also contain their own README.md files with more detailed information.

* **arsearch**: service endpoints that can as callback URLs for popular augmented reality applications
* **it**: integration tests
* **service**: service implementation of the OSS API
* **sir-common**: common files for the SIR modules 
* **sir-it**: integration tests for SIR API
* **sir-json**: json datamodel files
* **website**: browser user interface for the OSS API


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
* Download the solr schema file from https://raw.github.com/52North/OpenSensorSearch/master/service/src/main/resources/db/solrschema.xml and save it, e.g. as /tmp/solrschema.xml
* Create a **node** in solr
  * See http://wiki.apache.org/solr/CoreAdmin for a detailed description, and http://wiki.apache.org/solr/ConfiguringSolr for configuration options.
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

* **server module**
  * ``org.n52.oss.service.properties`` are added to (and potentially overwrite) the base file ``src/main/resources/app.properties``
  * ``org.n52.oss.service.db.properties`` are added to (and potentially overwrite) the base file ``src/main/resources/prop/db.properties``
  * ``org.n52.oss.service.sir.properties`` are added to (and potentially overwrite) the base file ``src/main/resources/prop/sir.properties``
* **website module**
  * ``org.n52.oss.website.properties`` overwrites the values in website module's file ``src/main/resources/org.n52.oss.website.properties``

There are sample files in the same directories of the base file to get you started.

### Wiki

For organisational information about Open Sensor Search please visit the [Wiki page](https://wiki.52north.org/bin/view/SensorWeb/OpenSensorSearch).


## Development

[![Build Status](https://travis-ci.org/52North/OpenSensorSearch.png?branch=master)](https://travis-ci.org/52North/OpenSensorSearch)

If you want to join the Open Sensor Search developer team, take a look at the CONTRIBUTE.md file.

### Integration tests

The integration tests can be activated during build with a Maven profile: ``mvn clean install -Pintegration-test``


## License

OSS is published under Apache Software License, Version 2.0.

### Java Libraries

See NOTICE file.

### Javascript Libraries

The website module uses a collection of Javascript libraries:

* AngularJS, https://github.com/angular/angular.js/blob/master/LICENSE - MIT License
* Angular-UI Bootstrap, https://github.com/angular-ui/bootstrap/blob/master/LICENSE - MIT License
* CodeMirror, http://codemirror.net/LICENSE - MIT License
* jQuery, https://jquery.org/license/ - MIT License
* Bootstrap, https://github.com/twbs/bootstrap/blob/master/LICENSE - MIT License
* angucomplete-alt, http://ngmodules.org/modules/angucomplete-alt - MIT License
* Swagger API Documentation
  * swagger-js, https://github.com/wordnik/swagger-js - Apache Software License, Version 2.0
  * jQuery BBQ, http://benalman.com/code/projects/jquery-bbq/docs/files/jquery-ba-bbq-js.html#License - MIT License
  * underscore.js, https://github.com/jashkenas/underscore/blob/master/LICENSE - MIT License
  * Backbone.js, http://github.com/jashkenas/backbone/blob/master/LICENSE - MIT License
  * SocialSharePrivacy, https://github.com/patrickheck/socialshareprivacy - MIT License
  * Handlebars.js, https://github.com/wycats/handlebars.js/blob/master/LICENSE - MIT License
  * highlight.js, https://github.com/isagalaev/highlight.js/blob/master/LICENSE - BSD 3 clause license
