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

import java.util.Collection;
import java.util.Date;

import org.apache.xmlbeans.XmlObject;

/**
 * represents an SIR internal sensor
 * 
 * @author Jan Schulte
 * 
 */
public class SirSensor {

    /**
     * sensor ID in SIR
     */
    private String sensorIDInSIR;

    /**
     * service descriptions
     */
    private Collection<SirServiceReference> servDescs;

    /**
     * observed phenomenon
     */
    private Collection<SirPhenomenon> phenomenon;

    /**
     * bounding box of the observed area
     */
    private SirBoundingBox bBox;

    /**
     * searchwords for this sensor
     */
    private Collection<String> text;

    /**
     * sensorML document
     */
    private XmlObject sensorMLDocument;

    /**
     * time period
     */
    private SirTimePeriod timePeriod;

    /**
     * time period
     */
    private Date lastUpdate;

    /**
     * @return the sensorIDInSIR
     */
    public String getSensorIDInSIR() {
        return this.sensorIDInSIR;
    }

    /**
     * @param sensorIDInSIR
     *        the sensorIDInSIR to set
     */
    public void setSensorIDInSIR(String sensorIDInSIR) {
        this.sensorIDInSIR = sensorIDInSIR;
    }

    /**
     * @return the servDescs
     */
    public Collection<SirServiceReference> getServDescs() {
        return this.servDescs;
    }

    /**
     * @param servDescs
     *        the servDescs to set
     */
    public void setServDescs(Collection<SirServiceReference> servDescs) {
        this.servDescs = servDescs;
    }

    /**
     * @return the phenomenon
     */
    public Collection<SirPhenomenon> getPhenomenon() {
        return this.phenomenon;
    }

    /**
     * @param phenomenon
     *        the phenomenon to set
     */
    public void setPhenomenon(Collection<SirPhenomenon> phenomenon) {
        this.phenomenon = phenomenon;
    }

    /**
     * @return the bBox
     */
    public SirBoundingBox getbBox() {
        return this.bBox;
    }

    /**
     * @param bBox
     *        the bBox to set
     */
    public void setbBox(SirBoundingBox bBox) {
        this.bBox = bBox;
    }

    /**
     * @return the text
     */
    public Collection<String> getText() {
        return this.text;
    }

    /**
     * @param text
     *        the text to set
     */
    public void setText(Collection<String> text) {
        this.text = text;
    }

    /**
     * @return the sensorMLDocument
     */
    public XmlObject getSensorMLDocument() {
        return this.sensorMLDocument;
    }

    /**
     * @param sensorMLDocument
     *        the sensorMLDocument to set
     */
    public void setSensorMLDocument(XmlObject sensorMLDocument) {
        this.sensorMLDocument = sensorMLDocument;
    }

    /**
     * @return the timePeriod
     */
    public SirTimePeriod getTimePeriod() {
        return this.timePeriod;
    }

    /**
     * @param timePeriod
     *        the timePeriod to set
     */
    public void setTimePeriod(SirTimePeriod timePeriod) {
        this.timePeriod = timePeriod;
    }

    /**
     * @return the lastUpdate
     */
    public Date getLastUpdate() {
        return this.lastUpdate;
    }

    /**
     * @param lastUpdate
     *        the lastUpdate to set
     */
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SirSensor: ");
        sb.append("SensorID in SIR: " + this.sensorIDInSIR);
        sb.append(", Service description: " + this.servDescs);
        sb.append(", " + this.phenomenon);
        sb.append(", " + this.bBox);
        sb.append(", Timeperiod: " + this.timePeriod);
        sb.append(", Last update: " + this.lastUpdate);
        return sb.toString();
    }

}
