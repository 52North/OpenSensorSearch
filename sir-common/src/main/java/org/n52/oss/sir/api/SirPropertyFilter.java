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
 * @author Jan Schulte
 * 
 */
public class SirPropertyFilter {

    /**
     * property constraint
     */
    private SirPropertyConstraint propConst;

    /**
     * property name
     */
    private String propertyName;

    /**
     * @return the propConst
     */
    public SirPropertyConstraint getPropConst() {
        return this.propConst;
    }

    /**
     * @return the propertyName
     */
    public String getPropertyName() {
        return this.propertyName;
    }

    /**
     * @param propConst
     *        the propConst to set
     */
    public void setPropConst(SirPropertyConstraint propConst) {
        this.propConst = propConst;
    }

    /**
     * @param propertyName
     *        the propertyName to set
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("PropertyFilter: ");
        sb.append("PropertyName: " + this.propertyName);
        if (this.propConst != null) {
            sb.append(", PropertyConstraint: " + this.propConst);
        }
        return sb.toString();
    }
}
