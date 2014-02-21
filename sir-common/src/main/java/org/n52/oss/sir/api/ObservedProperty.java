/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
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
public class ObservedProperty {

    String id;

    String uom;

    String urn;

    public ObservedProperty() {
        //
    }

    public ObservedProperty(String urn, String uom, String phenomenonId) {
        this.urn = urn;
        this.uom = uom;
        this.id = phenomenonId;
    }

    public String getId() {
        return this.id;
    }

    public String getUom() {
        return this.uom;
    }

    public String getUrn() {
        return this.urn;
    }

    public void setId(String phenomenonId) {
        this.id = phenomenonId;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SirPhenomenon [");
        if (id != null) {
            builder.append("phenomenonId=");
            builder.append(id);
            builder.append(", ");
        }
        if (uom != null) {
            builder.append("uom=");
            builder.append(uom);
            builder.append(", ");
        }
        if (urn != null) {
            builder.append("urn=");
            builder.append(urn);
        }
        builder.append("]");
        return builder.toString();
    }

}
