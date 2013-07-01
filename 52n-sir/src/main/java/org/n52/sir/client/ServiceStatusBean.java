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
/**
 * 
 */

package org.n52.sir.client;

import java.util.Date;

import org.n52.oss.sir.SirConfig;
import org.n52.sir.SirConfigurator;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.IGetCapabilitiesDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class ServiceStatusBean {

    private static Logger log = LoggerFactory.getLogger(ServiceStatusBean.class);

    private IGetCapabilitiesDAO dao;

    private Date nextUpdate;

    private long numberOfPhenomena = 0;

    private long numberOfSensors = 0;

    private long numberOfServices = 0;

    private int updateIntervalMillis = 1000 * 60 * 10;

    /**
     * 
     */
    public ServiceStatusBean() {
        SirConfig instance = SirConfigurator.getInstance();

        if (instance != null) {
            IDAOFactory factory = instance.getFactory();
            try {
                this.dao = factory.getCapabilitiesDAO();
            }
            catch (OwsExceptionReport e) {
                log.error("Error getting DAO.", e);
            }

            this.nextUpdate = new Date(System.currentTimeMillis() - this.updateIntervalMillis);
            update();
        }
        else
            log.error("Could not get SirConfigurator, instance is null!");
    }

    /**
     * @return the numberOfPhenomena
     */
    public long getNumberOfPhenomena() {
        return this.numberOfPhenomena;
    }

    /**
     * @return the numberOfSensors
     */
    public long getNumberOfSensors() {
        update();

        return this.numberOfSensors;
    }

    /**
     * @return the numberOfServices
     */
    public long getNumberOfServices() {
        update();

        return this.numberOfServices;
    }

    /**
     * @param numberOfPhenomena
     *        the numberOfPhenomena to set
     */
    public void setNumberOfPhenomena(long numberOfPhenomena) {
        this.numberOfPhenomena = numberOfPhenomena;
    }

    /**
     * @param numberOfSensors
     *        the numberOfSensors to set
     */
    public void setNumberOfSensors(long numberOfSensors) {
        this.numberOfSensors = numberOfSensors;
    }

    /**
     * @param numberOfServices
     *        the numberOfServices to set
     */
    public void setNumberOfServices(long numberOfServices) {
        this.numberOfServices = numberOfServices;
    }

    /**
     * 
     */
    private synchronized void update() {
        Date now = new Date();
        if (now.after(this.nextUpdate)) {
            try {
                this.numberOfServices = this.dao.getServiceCount();
                this.numberOfSensors = this.dao.getSensorCount();
                this.numberOfPhenomena = this.dao.getPhenomenonCount();

                this.nextUpdate = new Date(now.getTime() + this.updateIntervalMillis);
                log.debug("Next update of sensor and service counts not before " + this.nextUpdate
                                  + ". Current counts: {} sensors, {} phenomena, {} services.",
                          new Object[] {Long.valueOf(this.numberOfSensors),
                                        Long.valueOf(this.numberOfPhenomena),
                                        Long.valueOf(this.numberOfServices)});
            }
            catch (OwsExceptionReport e) {
                log.error("Error getting sensor or service count.", e);
            }
        }
    }

}
