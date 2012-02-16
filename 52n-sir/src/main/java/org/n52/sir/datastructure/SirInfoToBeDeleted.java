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
package org.n52.sir.datastructure;

/**
 * @author Daniel Nüst
 * 
 */
public class SirInfoToBeDeleted {

    private SirSensorIdentification sensorIdentification;

    private boolean deleteSensor;

    private SirServiceInfo serviceInfo;

    /**
     * @param sensorIdentification
     * @param deleteSensor
     * @param serviceInfo
     */
    public SirInfoToBeDeleted(SirSensorIdentification sensorIdentification,
                              boolean deleteSensor,
                              SirServiceInfo serviceInfo) {
        this.sensorIdentification = sensorIdentification;
        this.deleteSensor = deleteSensor;
        this.serviceInfo = serviceInfo;
    }

    /**
     * 
     */
    public SirInfoToBeDeleted() {
        //
    }

    /**
     * @return the sensorIdentification
     */
    public SirSensorIdentification getSensorIdentification() {
        return this.sensorIdentification;
    }

    /**
     * @return the deleteSensor
     */
    public boolean isDeleteSensor() {
        return this.deleteSensor;
    }

    /**
     * @return the serviceInfo
     */
    public SirServiceInfo getServiceInfo() {
        return this.serviceInfo;
    }

    /**
     * @param sensorIdentification
     *        the sensorIdentification to set
     */
    public void setSensorIdentification(SirSensorIdentification sensorIdentification) {
        this.sensorIdentification = sensorIdentification;
    }

    /**
     * @param deleteSensor
     *        the deleteSensor to set
     */
    public void setDeleteSensor(boolean deleteSensor) {
        this.deleteSensor = deleteSensor;
    }

    /**
     * @param serviceInfo
     *        the serviceInfo to set
     */
    public void setServiceInfo(SirServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

}
