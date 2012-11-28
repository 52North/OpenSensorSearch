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

/**
 * class represents a GetCapabilities request and encapsulates the parameters
 * 
 * @author Jan Schulte
 * 
 */
public class SirGetCapabilitiesRequest extends AbstractSirRequest {

    private String[] acceptFormats;

    private String[] acceptVersions;

    private String[] sections;

    private String updateSequence;

    /**
     * @return the acceptFormats
     */
    public String[] getAcceptFormats() {
        return this.acceptFormats;
    }

    /**
     * @return the acceptVersions
     */
    public String[] getAcceptVersions() {
        return this.acceptVersions;
    }

    /**
     * @return the sections
     */
    public String[] getSections() {
        return this.sections;
    }

    /**
     * @return the updateSequence
     */
    public String getUpdateSequence() {
        return this.updateSequence;
    }

    /**
     * @param acceptFormats
     *        the acceptFormats to set
     */
    public void setAcceptFormats(String[] acceptFormats) {
        this.acceptFormats = acceptFormats;
    }

    /**
     * @param acceptVersions
     *        the acceptVersions to set
     */
    public void setAcceptVersions(String[] acceptVersions) {
        this.acceptVersions = acceptVersions;
    }

    /**
     * @param sections
     *        the sections to set
     */
    public void setSections(String[] sections) {
        this.sections = sections;
    }

    /**
     * @param updateSequence
     *        the updateSequence to set
     */
    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }

}