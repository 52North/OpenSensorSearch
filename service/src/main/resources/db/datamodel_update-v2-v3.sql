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

-- Database structure update for PostGIS 2

-- last change: 2014-01-16
-- last change by: Daniel Nüst

DROP TABLE IF EXISTS badge CASCADE;
DROP TABLE IF EXISTS sensor_badge CASCADE;
DROP TABLE IF EXISTS service_badge CASCADE;

-----------------------------------------------------------------------------------------------------------------------
-- Creating the tables
-----------------------------------------------------------------------------------------------------------------------

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
