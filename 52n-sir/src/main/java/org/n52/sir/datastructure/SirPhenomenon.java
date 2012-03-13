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
 * @author Jan Schulte
 * 
 */
public class SirPhenomenon {

    /**
     * identifier of phenomenon in SIR
     */
    String phenomenonIdInSIR;

    /**
     * unit of measure
     */
    String uom;

    /**
     * urn to the phenomenon
     */
    String urn;

    public SirPhenomenon() {
        //
    }

    public SirPhenomenon(String urn, String uom, String phenomenonIdInSIR) {
        this.urn = urn;
        this.uom = uom;
        this.phenomenonIdInSIR = phenomenonIdInSIR;
    }

    /**
     * @return the phenomenonIdInSIR
     */
    public String getPhenomenonIdInSIR() {
        return this.phenomenonIdInSIR;
    }

    /**
     * @return the uom
     */
    public String getUom() {
        return this.uom;
    }

    /**
     * @return the urn
     */
    public String getUrn() {
        return this.urn;
    }

    /**
     * @param phenomenonIdInSIR
     *        the phenomenonIdInSIR to set
     */
    public void setPhenomenonIdInSIR(String phenomenonIdInSIR) {
        this.phenomenonIdInSIR = phenomenonIdInSIR;
    }

    /**
     * @param uom
     *        the uom to set
     */
    public void setUom(String uom) {
        this.uom = uom;
    }

    /**
     * @param urn
     *        the urn to set
     */
    public void setUrn(String urn) {
        this.urn = urn;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Phenomenon: ");
        sb.append("Urn: ");
        sb.append(this.urn);
        sb.append(", UOM: ");
        sb.append(this.uom);
        sb.append(", id: ");
        sb.append(this.phenomenonIdInSIR);
        return sb.toString();
    }

}
