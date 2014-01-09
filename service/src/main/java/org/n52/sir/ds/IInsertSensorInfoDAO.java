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
package org.n52.sir.ds;

import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.api.SirSensorIdentification;
import org.n52.oss.sir.api.SirServiceReference;
import org.n52.oss.sir.ows.OwsExceptionReport;

/**
 * interface for the specific DAOFactories, offers methods to create the matching DAOs for the
 * insertSensorInfo operation
 * 
 * @author Jan Schulte
 * 
 */
public interface IInsertSensorInfoDAO {

    /**
     * Adds a service reference to a given sensor identification in SIR
     * 
     * @param sensIdent
     *        the sensor identification
     * @param servRef
     *        the added service description
     * @throws OwsExceptionReport
     */
    public String addNewReference(SirSensorIdentification sensIdent, SirServiceReference servRef) throws OwsExceptionReport;

    /**
     * Removes a service reference by a given sensor ID in SIR an the service description
     * 
     * @param sensIdent
     *        the sensor Identification
     * @param servRef
     *        the removed service description
     * @throws OwsExceptionReport
     */
    public String deleteReference(SirSensorIdentification sensIdent, SirServiceReference servRef) throws OwsExceptionReport;

    /**
     * Deletes a sensor by SensorIdentification
     * 
     * @param sensIdent
     *        the sensorIdentification
     * @return Returns the former SensorID in SIR
     * @throws OwsExceptionReport
     */
    public String deleteSensor(SirSensorIdentification sensIdent) throws OwsExceptionReport;

    /**
     * Inserts a sensor with service description and sensor information
     * 
     * @param sensor
     *        the sensor
     * @return Returns the sensorID in the Database
     * @throws OwsExceptionReport
     */
    public String insertSensor(SirSensor sensor) throws OwsExceptionReport;

    /**
     * Updates the sensor document by SensorIdentification with given sensorML document
     * 
     * @param sensIdent
     *        the sensorIdentification
     * @param sensor
     *        the sensor
     * @return Returns the SensorID in SIR
     * @throws OwsExceptionReport
     */
    public String updateSensor(SirSensorIdentification sensIdent, SirSensor sensor) throws OwsExceptionReport;

}
