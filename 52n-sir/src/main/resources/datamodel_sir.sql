--
-- ﻿Copyright (C) 2012
-- by 52 North Initiative for Geospatial Open Source Software GmbH
--
-- Contact: Andreas Wytzisk
-- 52 North Initiative for Geospatial Open Source Software GmbH
-- Martin-Luther-King-Weg 24
-- 48155 Muenster, Germany
-- info@52north.org
--
-- This program is free software; you can redistribute and/or modify it under
-- the terms of the GNU General Public License version 2 as published by the
-- Free Software Foundation.
--
-- This program is distributed WITHOUT ANY WARRANTY; even without the implied
-- WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- General Public License for more details.
--
-- You should have received a copy of the GNU General Public License along with
-- this program (see gnu-gpl v2.txt). If not, write to the Free Software
-- Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
-- visit the Free Software Foundation web page, http://www.fsf.org.
--

--FILE CREATES TABLE STRUCTURE OF DATABASE

-- last change: 2009-05-11
-- last change by: Jan Schulte

-- Dropping Tables
----------------------
DROP TABLE IF EXISTS catalog CASCADE;
DROP TABLE IF EXISTS phenomenon CASCADE;
DROP TABLE IF EXISTS sensor CASCADE;
DROP TABLE IF EXISTS sensor_phen CASCADE;
DROP TABLE IF EXISTS sensor_service CASCADE;
DROP TABLE IF EXISTS service CASCADE;
DROP TABLE IF EXISTS status CASCADE;

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

-----------------------------------------------------------------------------------------------------------------------
--add indices
-----------------------------------------------------------------------------------------------------------------------
CREATE INDEX phenIndex ON phenomenon(phenomenon_urn);
CREATE INDEX bbox_index ON sensor USING
        GIST (bbox GIST_GEOMETRY_OPS); 

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
