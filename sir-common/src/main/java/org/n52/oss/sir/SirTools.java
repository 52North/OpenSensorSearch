package org.n52.oss.sir;

import javax.xml.namespace.QName;

import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML;
import net.opengis.sensorML.x101.SystemType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SirTools {

    private static Logger log = LoggerFactory.getLogger(SirTools.class);

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
