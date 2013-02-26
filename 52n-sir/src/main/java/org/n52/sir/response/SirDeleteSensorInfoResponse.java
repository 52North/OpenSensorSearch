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

package org.n52.sir.response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.n52.sir.SirConfigurator;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.DeleteSensorInfoResponseDocument;
import org.x52North.sir.x032.DeleteSensorInfoResponseDocument.DeleteSensorInfoResponse;
import org.x52North.sir.x032.DeleteSensorInfoResponseDocument.DeleteSensorInfoResponse.DeletedSensors;

/**
 * @author Daniel Nüst
 * 
 */
public class SirDeleteSensorInfoResponse extends AbstractXmlResponse {

    private static Logger log = LoggerFactory.getLogger(SirDeleteSensorInfoResponse.class);

    private Collection<String> deletedSensors = new ArrayList<String>();

    private int numberOfDeletedSensors = 0;

    private int numberOfDeletedServiceReferences = 0;

    @Override
    public DeleteSensorInfoResponseDocument createXml() {
        DeleteSensorInfoResponseDocument document = DeleteSensorInfoResponseDocument.Factory.newInstance();
        DeleteSensorInfoResponse delteSensInfoResp = document.addNewDeleteSensorInfoResponse();

        delteSensInfoResp.setNumberOfDeletedSensors(this.numberOfDeletedSensors);
        delteSensInfoResp.setNumberOfDeletedServiceReferences(this.numberOfDeletedServiceReferences);
        DeletedSensors deletedSensor = delteSensInfoResp.addNewDeletedSensors();
        for (String inSens : this.deletedSensors) {
            deletedSensor.addSensorIDInSIR(inSens);
        }

        XmlTools.addSirAndSensorMLSchemaLocation(delteSensInfoResp);

        if (SirConfigurator.getInstance().isValidateResponses()) {
            if ( !document.validate())
                log.warn("Service created invalid document!\n" + XmlTools.validateAndIterateErrors(document));
        }

        return document;
    }

    /**
     * @return the deletedSensors
     */
    public Collection<String> getDeletedSensors() {
        return this.deletedSensors;
    }

    /**
     * @return the numberOfNewSensors
     */
    public int getNumberOfDeletedSensors() {
        return this.numberOfDeletedSensors;
    }

    /**
     * @return the numberOfNewServiceReferences
     */
    public int getNumberOfDeletedServiceReferences() {
        return this.numberOfDeletedServiceReferences;
    }

    /**
     * @param deletedSensors
     *        the deletedSensors to set
     */
    public void setDeletedSensors(Collection<String> insertedSensors) {
        this.deletedSensors = insertedSensors;
    }

    /**
     * @param numberOfDeletedSensors
     *        the numberOfDeletedSensors to set
     */
    public void setNumberOfDeletedSensors(int numberOfDeletedSensors) {
        this.numberOfDeletedSensors = numberOfDeletedSensors;
    }

    /**
     * @param numberOfDeletedServiceReferences
     *        the numberOfDeletedServiceReferences to set
     */
    public void setNumberOfDeletedServiceReferences(int numberOfDeletedServiceReferences) {
        this.numberOfDeletedServiceReferences = numberOfDeletedServiceReferences;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SirDeleteSensorInfoResponse: ");
        sb.append("\n# of deleted sensors: " + this.numberOfDeletedSensors);
        sb.append("\nDeleted service references: " + this.numberOfDeletedServiceReferences);
        sb.append("\nDeleted Sensors: " + Arrays.toString(this.deletedSensors.toArray()));
        return sb.toString();
    }

}
