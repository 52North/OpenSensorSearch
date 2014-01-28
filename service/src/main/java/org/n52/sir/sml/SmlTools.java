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
package org.n52.sir.sml;

import javax.xml.namespace.QName;

import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML;
import net.opengis.sensorML.x101.SystemType;

import org.n52.oss.sir.SMLConstants;

public class SmlTools {

    public static SensorMLDocument wrapSystemTypeInSensorMLDocument(SystemType stToSet) {
        SensorMLDocument document;
        document = SensorMLDocument.Factory.newInstance();
        SensorML newSensorML = document.addNewSensorML();
        newSensorML.setVersion(SMLConstants.SML_VERSION);
        AbstractProcessType abstractProcess = newSensorML.addNewMember().addNewProcess();
        SystemType newSystemType = (SystemType) abstractProcess.substitute(new QName(SMLConstants.NAMESPACE, "System"),
                                                                           SystemType.type);
        newSystemType.set(stToSet);
        return document;
    }

}
