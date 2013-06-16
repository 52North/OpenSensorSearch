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
package org.n52.sir.ds;

import org.apache.xmlbeans.XmlObject;
import org.n52.sir.ows.OwsExceptionReport;

/**
 * interface for the specific DAOFactories, offers methods to create the matching DAOs to the describeSensor
 * operation
 * 
 * @author Jan Schulte
 * 
 */
public interface IDescribeSensorDAO {

    /**
     * Returns a sensor description by a given sensorID in SIR
     * 
     * @param sensorIdInSir
     *        the given sensorID
     * @return Returns the requested sensorMLDocument
     * @throws OwsExceptionReport
     */
    public XmlObject getSensorDescription(String sensorIdInSir) throws OwsExceptionReport;

}
