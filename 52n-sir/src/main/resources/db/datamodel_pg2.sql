--
-- ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

-- Database structure for PostGIS 2

-- last change: 2013-08
-- last change by: Moh-Yakoub

-- Dropping Tables
----------------------
DROP TABLE IF EXISTS catalog CASCADE;
DROP TABLE IF EXISTS phenomenon CASCADE;
DROP TABLE IF EXISTS sensor CASCADE;
DROP TABLE IF EXISTS sensor_phen CASCADE;
DROP TABLE IF EXISTS sensor_service CASCADE;
DROP TABLE IF EXISTS service CASCADE;
DROP TABLE IF EXISTS status CASCADE;
DROP TABLE IF EXISTS harvestScript CASCADE;

-----------------------------------------------------------------------------------------------------------------------
-- Creating the tables
-----------------------------------------------------------------------------------------------------------------------

-- Table: sensor_service
-- links every service to a sensor
CREATE TABLE sensor_service (
  service_id integer,
  sensor_id_sir integer,
  service_spec_id varchar(200) NOT NULL --,
  --PRIMARY KEY (sens_serv_id)
  --PRIMARY KEY(service_id, sensor_id_sir)
);

-- Table: service
-- represents services 
CREATE TABLE service
(
  service_id SERIAL,
  service_url varchar(255) NOT NULL,
  service_type varchar(5) NOT NULL,
  PRIMARY KEY(service_id),
  CHECK (service_type IN ('SOS', 'sos', 'SPS', 'sps', 'ses', 'SES', 'sas', 'SAS')),
  CONSTRAINT uc_service UNIQUE (service_url,service_type)
);

-- Table: sensor
-- represents sensor 
CREATE TABLE sensor
(
  sensor_id_sir SERIAL,
  bbox geometry,
  time_start timestamptz NOT NULL,
  time_end timestamptz,
  sensorml text,
  text text[],
  last_update timestamptz,
  PRIMARY KEY (sensor_id_sir)
);

-- Table: status
-- represents status of a sensor
CREATE TABLE status
(
  status_id SERIAL,
  sensor_id_sir integer,
  property_name varchar(200) NOT NULL,
  property_value text NOT NULL,
  time timestamptz,
  uom varchar(100),
  PRIMARY KEY (status_id,sensor_id_sir)
);

-- Table: sensor_phen
-- links a sensor with a phenomenon
CREATE TABLE sensor_phen
(
  sensor_id_sir integer,
  phenomenon_id integer
);

-- Table: phenomenon
-- represents phenomena
CREATE TABLE phenomenon
(
  phenomenon_id SERIAL,
  phenomenon_urn varchar(200) NOT NULL,
  uom varchar(30) NOT NULL,
  PRIMARY KEY (phenomenon_id)
);

-- Table: catalog
-- respresents the connections to catalogs
CREATE TABLE catalog
(
  catalog_id_sir SERIAL,
  catalog_url varchar(255) NOT NULL,
  push_interval integer,
  catalog_status varchar(255),
  -- last_complete_push timestamptz,
  PRIMARY KEY (catalog_url)
);

--Table: harvestSctipt
--represents information about scripts uploaded to server
CREATE TABLE harvestScript
(
   scriptId bigserial NOT NULL, 
   scriptVersion integer, 
   pathURL text, 
   lastRunDate time with time zone, 
   uploadDate time with time zone, 
   lastRunResult text,
   username text,
   PRIMARY KEY(scriptId)
) ;


-----------------------------------------------------------------------------------------------------------------------
--add indices
-----------------------------------------------------------------------------------------------------------------------
CREATE INDEX phenIndex ON phenomenon(phenomenon_urn);
CREATE INDEX bbox_index ON sensor USING
        GIST (bbox);

-----------------------------------------------------------------------------------------------------------------------
-- add references and foreign keys
-----------------------------------------------------------------------------------------------------------------------
--foreign keys for sensor_service
ALTER TABLE sensor_service ADD FOREIGN KEY (sensor_id_sir) REFERENCES sensor (sensor_id_sir) ON UPDATE CASCADE ON DELETE CASCADE INITIALLY DEFERRED;
ALTER TABLE sensor_service ADD FOREIGN KEY (service_id) REFERENCES service (service_id) ON UPDATE CASCADE ON DELETE CASCADE INITIALLY DEFERRED;

--foreign keys for service
--ALTER TABLE service ADD FOREIGN KEY (service_id) REFERENCES sensor_service ON UPDATE CASCADE ON DELETE CASCADE INITIALLY DEFERRED;

--foreign keys for status
--ALTER TABLE status ADD FOREIGN KEY (sensor_id_sir) REFERENCES sensor ON UPDATE CASCADE ON DELETE CASCADE INITIALLY DEFERRED;

--foreign keys for sensor_phen
ALTER TABLE sensor_phen ADD FOREIGN KEY (sensor_id_sir) REFERENCES sensor ON UPDATE CASCADE ON DELETE CASCADE INITIALLY DEFERRED;
ALTER TABLE sensor_phen ADD FOREIGN KEY (phenomenon_id) REFERENCES phenomenon ON UPDATE CASCADE ON DELETE CASCADE INITIALLY DEFERRED;

--foreign key for phenomenon
--ALTER TABLE phenomenon ADD FOREIGN KEY (phenomenon_id) REFERENCES sensor_phen ON UPDATE CASCADE ON DELETE CASCADE INITIALLY DEFERRED;
