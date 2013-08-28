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

package org.n52.sir.ds.pgsql;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PGDAOConstants {

	public static String bBox;

	public static String catalog;

	public static String catalogIdSir;

	public static String catalogStatus;

	// column names of catalog
	public static String catalogUrl;

	public static String connectionString;

	private static final String CONNECTIONSTRING = "oss.db.CONNECTIONSTRING";

	// other constants
	public static String daoFactory;

	// other propertynames
	private static final String DAOFACTORY = "oss.db.DAOFACTORY";

	public static String driver;

	private static final String DRIVER = "oss.db.DRIVER";

	public static int initcon = 0;

	private static final String INITCON = "oss.db.INITCON";

	/**
	 * instance attribute due to singleton pattern
	 */
	private static PGDAOConstants instance = null;

	public static String lastUpdate;

	/**
	 * logger, used for logging while initializing the constants from config
	 * file
	 */
	private static Logger log = LoggerFactory.getLogger(PGDAOConstants.class);

	public static int maxcon = 0;

	private static final String MAXCON = "oss.db.MAXCON";

	public static String password;

	private static final String PASSWORD = "oss.db.PASSWORD";

	// table names
	public static String phenomenon;

	// column names of phenomenon
	public static String phenomenonId;

	public static String phenomenonUom;

	public static String phenomenonUrn;

	public static String phenomeonIdOfSensPhen;

	public static String propertyName;

	public static String propertyValue;

	public static String pushInterval;

	public static String sensor;

	// column names of sensor
	public static String sensorIdSir;

	// column names of sensor_phenomenon
	public static String sensorIdSirOfSensPhen;

	public static String sensorIdSirOfStatus;

	public static String sensorIdSirSensServ;

	public static String sensorml;

	public static String sensorPhen;

	public static String sensorService;

	public static String sensorText;

	public static String sensorTimeEnd;

	public static String sensorTimeStart;

	public static String service;

	// column names of service
	public static String serviceId;

	// column names of sensor_service
	public static String serviceIdOfSensServ;

	public static String serviceSpecId;

	public static String serviceType;

	public static String serviceUrl;

	public static String status;

	// column names of status
	public static String statusId;

	public static String time;

	public static String uom;

	public static String user;

	private static final String USER = "oss.db.USER";

	// harvestScript tables;
	public static String harvestScript;

	public static String SCRIPTID;
	public static String SCRIPT_VERSION;
	public static String PATH_URL;
	public static String SCRIPT_LAST_RUN_DATE;
	public static String SCRIPT_LAST_RUN_RESULT;
	public static String SCRIPT_UPLOAD_TIME;
	public static String SCRIPT_OWNER_USERNAME;

	// User token constants
	public static String USER_ACCOUNT_TABLE;
	public static String PASSWORD_HASH;
	public static String USER_AUTH_TOKEN;
	public static String USER_NAME;
	public static String USER_ID;
	public static String AUTH_TOKEN_TABLE;

	/**
	 * getInstance method due to singleton pattern
	 * 
	 * @param daoProps
	 *            the Properties for the DAO
	 * @return The only PGDAOConstants instance
	 */
	public static synchronized PGDAOConstants getInstance(Properties daoProps) {
		if (instance == null) {
			instance = new PGDAOConstants(daoProps);
			return instance;
		}
		return instance;
	}

	private final String CATALOGIDSIR = "oss.db.CATALOGIDSIR";

	private final String CATALOGSTATUS = "oss.db.CATALOGSTATUS";

	// propertynames of column names
	private final String CATALOGURL = "oss.db.CATALOGURL";

	private final String PHENOMENONID = "oss.db.PHENOMENONID";

	private final String PHENOMENONUOM = "oss.db.PHENOMENONUOM";

	private final String PHENOMENONURN = "oss.db.PHENOMENONURN";

	private final String PUSHINTERVAL = "oss.db.PUSHINTERVAL";

	private final String SENSORBBOX = "oss.db.SENSORBBOX";

	private final String SENSORIDSIR = "oss.db.SENSORIDSIR";

	private final String SENSORLASTUPDATE = "oss.db.SENSORLASTUPDATE";

	private final String SENSORSENSORML = "oss.db.SENSORSENSORML";

	private final String SENSORTEXT = "oss.db.SENSORTEXT";

	private final String SENSORTIMEEND = "oss.db.SENSORTIMEEND";

	private final String SENSORTIMESTART = "oss.db.SENSORTIMESTART";

	private final String SENSPHENPHENOMENONID = "oss.db.SENSPHENPHENOMENONID";

	private final String SENSPHENSENSORIDSIR = "oss.db.SENSPHENSENSORIDSIR";

	private final String SENSSERVSENSORIDSIR = "oss.db.SENSSERVSENSORIDSIR";

	private final String SENSSERVSERVICESID = "oss.db.SENSSERVSERVICESID";

	private final String SENSSERVSERVICESPECID = "oss.db.SENSSERVSERVICESPECID";

	private final String SERVICEID = "oss.db.SERVICEID";

	private final String SERVICETYPE = "oss.db.SERVICETYPE";

	private final String SERVICEURL = "oss.db.SERVICEURL";

	private final String STATUSID = "oss.db.STATUSID";

	private final String STATUSPROPERTYNAME = "oss.db.STATUSPROPERTYNAME";

	private final String STATUSPROPERTYVALUE = "oss.db.STATUSPROPERTYVALUE";

	private final String STATUSSENSORIDSIR = "oss.db.STATUSSENSORIDSIR";

	private final String STATUSTIME = "oss.db.STATUSTIME";

	private final String STATUSUOM = "oss.db.STATUSUOM";

	private final String TNCATALOG = "oss.db.TNCATALOG";

	private final String TNPHENOMENON = "oss.db.TNPHENOMENON";

	private final String TNSENSOR = "oss.db.TNSENSOR";

	private final String TNSENSOR_PHEN = "oss.db.TNSENSOR_PHEN";

	private final String TNSENSOR_SERVICE = "oss.db.TNSENSOR_SERVICE";

	private final String TNSERVICE = "oss.db.TNSERVICE";

	private final String TNSTATUS = "oss.db.TNSTATUS";

	// remoteHarvestSensor table
	public static String remoteHarvestSensor;
	public static String SERVER_ID;
	public static String SERVER_URL;
	public static String AUTH_TOKEN;
	public static String REMOTE_LAST_UPDATE;

	/**
	 * constructor
	 * 
	 * @param props
	 *            the Properties for the DAO
	 */
	public PGDAOConstants(Properties props) {

		phenomenon = props.getProperty(this.TNPHENOMENON);
		sensor = props.getProperty(this.TNSENSOR);
		sensorPhen = props.getProperty(this.TNSENSOR_PHEN);
		sensorService = props.getProperty(this.TNSENSOR_SERVICE);
		service = props.getProperty(this.TNSERVICE);
		status = props.getProperty(this.TNSTATUS);
		catalog = props.getProperty(this.TNCATALOG);
		phenomenonId = props.getProperty(this.PHENOMENONID);
		phenomenonUrn = props.getProperty(this.PHENOMENONURN);
		phenomenonUom = props.getProperty(this.PHENOMENONUOM);
		sensorIdSir = props.getProperty(this.SENSORIDSIR);
		serviceSpecId = props.getProperty(this.SENSSERVSERVICESPECID);
		bBox = props.getProperty(this.SENSORBBOX);
		sensorTimeStart = props.getProperty(this.SENSORTIMESTART);
		sensorTimeEnd = props.getProperty(this.SENSORTIMEEND);
		sensorml = props.getProperty(this.SENSORSENSORML);
		sensorText = props.getProperty(this.SENSORTEXT);
		lastUpdate = props.getProperty(this.SENSORLASTUPDATE);
		sensorIdSirOfSensPhen = props.getProperty(this.SENSPHENSENSORIDSIR);
		phenomeonIdOfSensPhen = props.getProperty(this.SENSPHENPHENOMENONID);
		serviceIdOfSensServ = props.getProperty(this.SENSSERVSERVICESID);
		sensorIdSirSensServ = props.getProperty(this.SENSSERVSENSORIDSIR);
		serviceId = props.getProperty(this.SERVICEID);
		serviceUrl = props.getProperty(this.SERVICEURL);
		serviceType = props.getProperty(this.SERVICETYPE);
		statusId = props.getProperty(this.STATUSID);
		sensorIdSirOfStatus = props.getProperty(this.STATUSSENSORIDSIR);
		propertyName = props.getProperty(this.STATUSPROPERTYNAME);
		propertyValue = props.getProperty(this.STATUSPROPERTYVALUE);
		time = props.getProperty(this.STATUSTIME);
		uom = props.getProperty(this.STATUSUOM);
		catalogUrl = props.getProperty(this.CATALOGURL);
		pushInterval = props.getProperty(this.PUSHINTERVAL);
		catalogIdSir = props.getProperty(this.CATALOGIDSIR);
		catalogStatus = props.getProperty(this.CATALOGSTATUS);
		daoFactory = props.getProperty(DAOFACTORY);
		connectionString = props.getProperty(CONNECTIONSTRING);
		user = props.getProperty(USER);
		password = props.getProperty(PASSWORD);
		driver = props.getProperty(DRIVER);
		initcon = new Integer(props.getProperty(INITCON)).intValue();
		maxcon = new Integer(props.getProperty(MAXCON)).intValue();
		harvestScript = props.getProperty("TNSCRIPT");
		SCRIPTID = props.getProperty("SCRIPTID");
		SCRIPT_VERSION = props.getProperty("SCRIPTVERSION");
		PATH_URL = props.getProperty("PATHURL");
		SCRIPT_LAST_RUN_DATE = props.getProperty("LASTRUN");
		SCRIPT_UPLOAD_TIME = props.getProperty("UPLOADTIME");
		SCRIPT_LAST_RUN_RESULT = props.getProperty("lastRunResult");
		SCRIPT_OWNER_USERNAME = props.getProperty("USERNAME");
		SERVER_ID = props.getProperty("SERVER_ID");
		SERVER_URL = props.getProperty("SERVER_URL");
		AUTH_TOKEN = props.getProperty("AUTH_TOKEN");
		REMOTE_LAST_UPDATE = props.getProperty("REMOTE_LAST_UPDATE");
		remoteHarvestSensor = props.getProperty("remoteHarvestSensor");
		USER_ACCOUNT_TABLE=props.getProperty("oss.db.USER_ACCOUNT_TABLE");
		PASSWORD_HASH=props.getProperty("oss.db.PASSWORD_HASH");
		USER_NAME=props.getProperty("oss.db.USERNAME");
		USER_ID=props.getProperty("oss.db.USER_ID");
		AUTH_TOKEN_TABLE=props.getProperty("oss.db.AUTH_TOKEN_TABLE");
		USER_AUTH_TOKEN=props.getProperty("oss.db.USER_AUTH_TOKEN");
		
		
		log.info("PGDAOConstants initialized successfully!");
		log.info("PGDAOConstants initialized successfully: {}", this);
	}
}