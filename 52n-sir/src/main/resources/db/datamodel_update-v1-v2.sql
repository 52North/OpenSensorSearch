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

-- last change: 2013-09-08
-- last change by: Daniel Nüst

DROP TABLE IF EXISTS userAuthToken CASCADE;
DROP TABLE IF EXISTS harvestScript CASCADE;
DROP TABLE IF EXISTS remoteHarvestSensor CASCADE;
DROP TABLE IF EXISTS userAccount CASCADE;

-----------------------------------------------------------------------------------------------------------------------
-- Creating the tables
-----------------------------------------------------------------------------------------------------------------------

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
) 
;

--Table : userAccount
CREATE TABLE userAccount
(
   userId bigserial NOT NULL, 
   userName text NOT NULL, 
   passwordSHA1 text,
   PRIMARY KEY(userId),
   CONSTRAINT uc_username UNIQUE (userName)
);


CREATE TABLE userAuthToken
(
	userId integer,
	user_auth_token text
);
