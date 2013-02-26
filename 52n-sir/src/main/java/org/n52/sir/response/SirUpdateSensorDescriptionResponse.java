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

import java.util.Arrays;
import java.util.Collection;

import org.n52.sir.SirConfigurator;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.UpdateSensorDescriptionResponseDocument;
import org.x52North.sir.x032.UpdateSensorDescriptionResponseDocument.UpdateSensorDescriptionResponse;
import org.x52North.sir.x032.UpdateSensorDescriptionResponseDocument.UpdateSensorDescriptionResponse.UpdatedSensors;

/**
 * @author Daniel Nüst
 * 
 */
public class SirUpdateSensorDescriptionResponse extends AbstractXmlResponse {

    private static Logger log = LoggerFactory.getLogger(SirUpdateSensorDescriptionResponse.class);

    private int numberOfUpdatedSensorDescriptions;

    private Collection<String> updatedSensors;

    @Override
    public UpdateSensorDescriptionResponseDocument createXml() {
        UpdateSensorDescriptionResponseDocument document = UpdateSensorDescriptionResponseDocument.Factory.newInstance();
        UpdateSensorDescriptionResponse updSensDescrResp = document.addNewUpdateSensorDescriptionResponse();

        updSensDescrResp.setNumberOfUpdatedSensors(this.numberOfUpdatedSensorDescriptions);
        UpdatedSensors updatedSensorsXB = updSensDescrResp.addNewUpdatedSensors();
        for (String inSens : this.updatedSensors) {
            updatedSensorsXB.addSensorIDInSIR(inSens);
        }

        XmlTools.addSirAndSensorMLSchemaLocation(updSensDescrResp);

        if (SirConfigurator.getInstance().isValidateResponses()) {
            if ( !document.validate())
                log.warn("Service created invalid document!\n" + XmlTools.validateAndIterateErrors(document));
        }

        return document;
    }

    /**
     * @return the numberOfNewSensors
     */
    public int getNumberOfUpdatedSensorDescriptions() {
        return this.numberOfUpdatedSensorDescriptions;
    }

    /**
     * @return the insertedSensors
     */
    public Collection<String> getUpdatedSensors() {
        return this.updatedSensors;
    }

    /**
     * @param numberOfNewSensors
     *        the numberOfNewSensors to set
     */
    public void setNumberOfUpdatedSensorDescriptions(int numberOfNewSensors) {
        this.numberOfUpdatedSensorDescriptions = numberOfNewSensors;
    }

    /**
     * @param insertedSensors
     *        the insertedSensors to set
     */
    public void setUpdatedSensors(Collection<String> updatedSensors) {
        this.updatedSensors = updatedSensors;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SirUpdateSensorDescriptionResponse: ");
        sb.append("\n#Sensors: " + this.numberOfUpdatedSensorDescriptions);
        sb.append("\nUpdated Sensors: " + Arrays.toString(this.updatedSensors.toArray()));
        return sb.toString();
    }

}
