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
/**
 * 
 */

package org.n52.sir.client;

import java.util.Date;

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
        SirConfigurator instance = SirConfigurator.getInstance();

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
