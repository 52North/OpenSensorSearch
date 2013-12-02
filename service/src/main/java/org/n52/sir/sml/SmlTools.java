
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
