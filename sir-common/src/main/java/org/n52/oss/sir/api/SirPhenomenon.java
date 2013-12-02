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
package org.n52.oss.sir.api;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class SirPhenomenon {

    String phenomenonId;

    String uom;

    String urn;

    public SirPhenomenon() {
        //
    }

    public SirPhenomenon(String urn, String uom, String phenomenonId) {
        this.urn = urn;
        this.uom = uom;
        this.phenomenonId = phenomenonId;
    }

    public String getPhenomenonId() {
        return this.phenomenonId;
    }

    public String getUom() {
        return this.uom;
    }

    public String getUrn() {
        return this.urn;
    }

    public void setPhenomenonId(String phenomenonId) {
        this.phenomenonId = phenomenonId;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

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
        sb.append(this.phenomenonId);
        return sb.toString();
    }

}
