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
