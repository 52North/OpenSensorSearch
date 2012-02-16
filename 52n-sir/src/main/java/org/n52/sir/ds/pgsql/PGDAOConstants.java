/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.sir.ds.pgsql;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PGDAOConstants {

    /**
     * logger, used for logging while initializing the constants from config file
     */
    private static Logger log = LoggerFactory.getLogger(PGDAOConstants.class);

    /**
     * instance attribute due to singleton pattern
     */
    private static PGDAOConstants instance = null;

    // propertyNames of table names
    private final String TNPHENOMENON = "TNPHENOMENON";

    private final String TNSENSOR = "TNSENSOR";

    private final String TNSENSOR_PHEN = "TNSENSOR_PHEN";

    private final String TNSENSOR_SERVICE = "TNSENSOR_SERVICE";

    private final String TNSERVICE = "TNSERVICE";

    private final String TNSTATUS = "TNSTATUS";

    private final String TNCATALOG = "TNCATALOG";

    // propertynames of column names
    private final String CATALOGURL = "CATALOGURL";

    private final String PUSHINTERVAL = "PUSHINTERVAL";

    private final String CATALOGIDSIR = "CATALOGIDSIR";

    private final String CATALOGSTATUS = "CATALOGSTATUS";

    private final String PHENOMENONID = "PHENOMENONID";

    private final String PHENOMENONURN = "PHENOMENONURN";

    private final String PHENOMENONUOM = "PHENOMENONUOM";

    private final String SENSORIDSIR = "SENSORIDSIR";

    private final String SENSSERVSERVICESPECID = "SENSSERVSERVICESPECID";

    private final String SENSORBBOX = "SENSORBBOX";

    private final String SENSORTIMESTART = "SENSORTIMESTART";

    private final String SENSORTIMEEND = "SENSORTIMEEND";

    private final String SENSORSENSORML = "SENSORSENSORML";

    private final String SENSORTEXT = "SENSORTEXT";

    private final String SENSORLASTUPDATE = "SENSORLASTUPDATE";

    private final String SENSPHENSENSORIDSIR = "SENSPHENSENSORIDSIR";

    private final String SENSPHENPHENOMENONID = "SENSPHENPHENOMENONID";

    private final String SENSSERVSERVICESID = "SENSSERVSERVICESID";

    private final String SENSSERVSENSORIDSIR = "SENSSERVSENSORIDSIR";

    private final String SERVICEID = "SERVICEID";

    private final String SERVICEURL = "SERVICEURL";

    private final String SERVICETYPE = "SERVICETYPE";

    private final String STATUSID = "STATUSID";

    private final String STATUSSENSORIDSIR = "STATUSSENSORIDSIR";

    private final String STATUSPROPERTYNAME = "STATUSPROPERTYNAME";

    private final String STATUSPROPERTYVALUE = "STATUSPROPERTYVALUE";

    private final String STATUSTIME = "STATUSTIME";

    private final String STATUSUOM = "STATUSUOM";

    // other propertynames
    private static final String DAOFACTORY = "DAOFACTORY";

    private static final String CONNECTIONSTRING = "CONNECTIONSTRING";

    private static final String USER = "USER";

    private static final String PASSWORD = "PASSWORD";

    private static final String DRIVER = "DRIVER";

    private static final String INITCON = "INITCON";

    private static final String MAXCON = "MAXCON";

    // table names
    public static String phenomenon;

    public static String sensor;

    public static String sensorPhen;

    public static String sensorService;

    public static String service;

    public static String status;

    public static String catalog;

    // column names of phenomenon
    public static String phenomenonId;

    public static String phenomenonUrn;

    public static String phenomenonUom;

    // column names of sensor
    public static String sensorIdSir;

    public static String bBox;

    public static String sensorTimeStart;

    public static String sensorTimeEnd;

    public static String sensorml;

    public static String sensorText;

    public static String lastUpdate;

    // column names of sensor_phenomenon
    public static String sensorIdSirOfSensPhen;

    public static String phenomeonIdOfSensPhen;

    // column names of sensor_service
    public static String serviceIdOfSensServ;

    public static String sensorIdSirSensServ;

    public static String serviceSpecId;

    // column names of service
    public static String serviceId;

    public static String serviceUrl;

    public static String serviceType;

    // column names of status
    public static String statusId;

    public static String sensorIdSirOfStatus;

    public static String propertyName;

    public static String propertyValue;

    public static String time;

    public static String uom;

    // column names of catalog
    public static String catalogUrl;

    public static String pushInterval;

    public static String catalogIdSir;

    public static String catalogStatus;

    // other constants
    public static String daoFactory;

    public static String connectionString;

    public static String user;

    public static String password;

    public static String driver;

    public static int initcon = 0;

    public static int maxcon = 0;

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
}