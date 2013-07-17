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

    private static final String CONNECTIONSTRING = "CONNECTIONSTRING";

    // other constants
    public static String daoFactory;

    // other propertynames
    private static final String DAOFACTORY = "DAOFACTORY";

    public static String driver;

    private static final String DRIVER = "DRIVER";

    public static int initcon = 0;

    private static final String INITCON = "INITCON";

    /**
     * instance attribute due to singleton pattern
     */
    private static PGDAOConstants instance = null;

    public static String lastUpdate;

    /**
     * logger, used for logging while initializing the constants from config file
     */
    private static Logger log = LoggerFactory.getLogger(PGDAOConstants.class);

    public static int maxcon = 0;

    private static final String MAXCON = "MAXCON";

    public static String password;

    private static final String PASSWORD = "PASSWORD";

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

    private static final String USER = "USER";

    /**
     * getInstance method due to singleton pattern
     * 
     * @param daoProps
     *        the Properties for the DAO
     * @return The only PGDAOConstants instance
     */
    public static synchronized PGDAOConstants getInstance(Properties daoProps) {
        if (instance == null) {
            instance = new PGDAOConstants(daoProps);
            return instance;
        }
        return instance;
    }

    private final String CATALOGIDSIR = "CATALOGIDSIR";

    private final String CATALOGSTATUS = "CATALOGSTATUS";

    // propertynames of column names
    private final String CATALOGURL = "CATALOGURL";

    private final String PHENOMENONID = "PHENOMENONID";

    private final String PHENOMENONUOM = "PHENOMENONUOM";

    private final String PHENOMENONURN = "PHENOMENONURN";

    private final String PUSHINTERVAL = "PUSHINTERVAL";

    private final String SENSORBBOX = "SENSORBBOX";

    private final String SENSORIDSIR = "SENSORIDSIR";

    private final String SENSORLASTUPDATE = "SENSORLASTUPDATE";

    private final String SENSORSENSORML = "SENSORSENSORML";

    private final String SENSORTEXT = "SENSORTEXT";

    private final String SENSORTIMEEND = "SENSORTIMEEND";

    private final String SENSORTIMESTART = "SENSORTIMESTART";

    private final String SENSPHENPHENOMENONID = "SENSPHENPHENOMENONID";

    private final String SENSPHENSENSORIDSIR = "SENSPHENSENSORIDSIR";

    private final String SENSSERVSENSORIDSIR = "SENSSERVSENSORIDSIR";

    private final String SENSSERVSERVICESID = "SENSSERVSERVICESID";

    private final String SENSSERVSERVICESPECID = "SENSSERVSERVICESPECID";

    private final String SERVICEID = "SERVICEID";

    private final String SERVICETYPE = "SERVICETYPE";

    private final String SERVICEURL = "SERVICEURL";

    private final String STATUSID = "STATUSID";

    private final String STATUSPROPERTYNAME = "STATUSPROPERTYNAME";

    private final String STATUSPROPERTYVALUE = "STATUSPROPERTYVALUE";

    private final String STATUSSENSORIDSIR = "STATUSSENSORIDSIR";

    private final String STATUSTIME = "STATUSTIME";

    private final String STATUSUOM = "STATUSUOM";

    private final String TNCATALOG = "TNCATALOG";

    // propertyNames of table names
    private final String TNPHENOMENON = "TNPHENOMENON";

    private final String TNSENSOR = "TNSENSOR";

    private final String TNSENSOR_PHEN = "TNSENSOR_PHEN";

    private final String TNSENSOR_SERVICE = "TNSENSOR_SERVICE";

    private final String TNSERVICE = "TNSERVICE";

    private final String TNSTATUS = "TNSTATUS";

    /**
     * constructor
     * 
     * @param props
     *        the Properties for the DAO
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

        log.info("PGDAOConstants initialized successfully!");
    }
}