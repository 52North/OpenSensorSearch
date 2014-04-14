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

-- Database structure for PostGIS 2.x only

-----------------------------------------------------------------------------------------------------------------------
-- Creating the extensions - DISABLE IF YOU CREATED THE DATABASE WITH A TEMPLATE
-----------------------------------------------------------------------------------------------------------------------
CREATE EXTENSION postgis;
CREATE EXTENSION postgis_topology;

-----------------------------------------------------------------------------------------------------------------------
-- Dropping Tables
-----------------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS catalog CASCADE;
DROP TABLE IF EXISTS phenomenon CASCADE;
DROP TABLE IF EXISTS sensor CASCADE;
DROP TABLE IF EXISTS sensor_phen CASCADE;
DROP TABLE IF EXISTS sensor_service CASCADE;
DROP TABLE IF EXISTS service CASCADE;
DROP TABLE IF EXISTS status CASCADE;
DROP TABLE IF EXISTS userAuthToken CASCADE;
DROP TABLE IF EXISTS harvestScript CASCADE;
DROP TABLE IF EXISTS remoteHarvestSensor CASCADE;
DROP TABLE IF EXISTS userAccount CASCADE;
DROP TABLE IF EXISTS badge CASCADE;
DROP TABLE IF EXISTS sensor_badge CASCADE;
DROP TABLE IF EXISTS service_badge CASCADE;
DROP TABLE IF EXISTS settings_info CASCADE;

-----------------------------------------------------------------------------------------------------------------------
-- Creating the tables
-----------------------------------------------------------------------------------------------------------------------

-- Table: settings_info
-- project settings and information
CREATE TABLE settings_info (
  setting_id SERIAL,
  setting_name varchar(255) NOT NULL,
  setting_value varchar(255) NOT NULL,
  PRIMARY KEY(setting_id)
);

INSERT INTO settings_info(setting_name, setting_value) VALUES ('db_version', '3');

-- Table: sensor_service
-- links every service to a sensor
CREATE TABLE sensor_service (
  service_id integer,
  sensor_id_sir integer,
  service_spec_id varchar(200) NOT NULL
);

-- Table: service
-- represents services 
CREATE TABLE service
(
  service_id SERIAL,
  service_url varchar(255) NOT NULL,
  service_type varchar(5) NOT NULL,
  PRIMARY KEY(service_id),
  CHECK (service_type IN ('SOS', 'sos', 'SPS', 'sps', 'ses', 'SES', 'sas', 'SAS', 'SIR', 'sir', 'OSS', 'oss')),
  CONSTRAINT uc_service UNIQUE (service_url,service_type)
);

-- Table: sensor
-- represents sensor 
CREATE TABLE sensor
(
  sensor_id_sir SERIAL,
  sensor_id varchar(32) NOT NULL,
  bbox geometry,
  time_start timestamptz NOT NULL,
  time_end timestamptz,
  sensorml text,
  text text[],
  last_update timestamptz,
  PRIMARY KEY (sensor_id_sir),
  CONSTRAINT uc_sensor_id UNIQUE (sensor_id)
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
   userId integer,
   PRIMARY KEY(scriptId)
) ;


--Table: remoteHarvestSensor
CREATE TABLE remoteHarvestSensor
(
   serverId bigserial NOT NULL, 
   serverURL text NOT NULL, 
   auth_token text, 
   lastStatus integer
);

--Table : userAccount
CREATE TABLE userAccount
(
   userId bigserial NOT NULL, 
   userName text NOT NULL, 
   passwordsha1 text,
   isAdmin boolean,
   isValid boolean,
   PRIMARY KEY(userId),
   CONSTRAINT uc_username UNIQUE (userName)
);


CREATE TABLE userAuthToken
(
	userId integer,
	user_auth_token text
);

-----------------------------------------------------------------------------------------------------------------------
-- add indices
-----------------------------------------------------------------------------------------------------------------------
CREATE INDEX phenIndex ON phenomenon(phenomenon_urn);
CREATE INDEX bbox_index ON sensor USING
        GIST (bbox gist_geometry_ops_2d);

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

--Table: badge
--information about badges available for sensors and services
CREATE TABLE badge
(
   badge_dbid bigserial NOT NULL, 
   badge_id text NOT NULL,
   short_name text NOT NULL,
   long_name text,
   description text,
   target_type text NOT NULL,
   PRIMARY KEY(badge_dbid),
   CONSTRAINT b_id UNIQUE (badge_id),
   CONSTRAINT b_sname UNIQUE (short_name)
);


-- Table: sensor_badge
-- links a sensor to earned badges
CREATE TABLE sensor_badge (
  sensor_id_sir integer,
  badge_dbid integer,
  last_update timestamptz NOT NULL
);

-- Table: servic_badge
-- links a service to earned badges
CREATE TABLE service_badge (
  service_id integer,
  badge_dbid integer,
  last_update timestamptz NOT NULL
);

--foreign keys for sensor_badge
ALTER TABLE sensor_badge ADD FOREIGN KEY (sensor_id_sir) REFERENCES sensor ON UPDATE CASCADE ON DELETE CASCADE INITIALLY DEFERRED;
ALTER TABLE sensor_badge ADD FOREIGN KEY (badge_dbid) REFERENCES badge ON UPDATE CASCADE ON DELETE CASCADE INITIALLY DEFERRED;

--foreign keys for service_badge
ALTER TABLE service_badge ADD FOREIGN KEY (service_id) REFERENCES service ON UPDATE CASCADE ON DELETE CASCADE INITIALLY DEFERRED;
ALTER TABLE service_badge ADD FOREIGN KEY (badge_dbid) REFERENCES badge ON UPDATE CASCADE ON DELETE CASCADE INITIALLY DEFERRED;
