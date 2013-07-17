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

import java.util.Collection;

/**
 * @author Jan Schulte
 * 
 */
public class SirServiceInfo {

    private Collection<SirServiceReference> serviceReferences;

    /**
     * 
     * @param b
     * @param servRef
     */
    public SirServiceInfo(Collection<SirServiceReference> servRef) {
        this.serviceReferences = servRef;
    }

    /**
     * @return the servDesc
     */
    public Collection<SirServiceReference> getServiceReferences() {
        return this.serviceReferences;
    }

    /**
     * @param servRefs
     *        the servDesc to set
     */
    public void setServiceReference(Collection<SirServiceReference> servRefs) {
        this.serviceReferences = servRefs;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SirServiceInfo: ");
        sb.append(this.serviceReferences);
        return sb.toString();
    }
}
