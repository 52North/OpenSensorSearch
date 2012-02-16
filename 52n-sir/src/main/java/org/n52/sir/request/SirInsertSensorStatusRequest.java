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
package org.n52.sir.request;

import java.util.Collection;

import org.n52.sir.datastructure.SirSensorIdentification;
import org.n52.sir.datastructure.SirStatus;

/**
 * @author Jan Schulte
 * 
 */
public class SirInsertSensorStatusRequest extends AbstractSirRequest {

    /**
     * the sensor identification
     */
    private SirSensorIdentification sensIdent;

    /**
     * status of the sensor
     */
    private Collection<SirStatus> status;

    /**
     * @return the sensIdent
     */
    public SirSensorIdentification getSensIdent() {
        return this.sensIdent;
    }

    /**
     * @param sensIdent
     *        the sensIdent to set
     */
    public void setSensIdent(SirSensorIdentification sensIdent) {
        this.sensIdent = sensIdent;
    }

    /**
     * @return the status
     */
    public Collection<SirStatus> getStatus() {
        return this.status;
    }

    /**
     * @param status
     *        the status to set
     */
    public void setStatus(Collection<SirStatus> status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Info to be inserted: ");
        sb.append("Identification: " + this.sensIdent);
        sb.append(" Status: " + this.status);
        return sb.toString();
    }
}
