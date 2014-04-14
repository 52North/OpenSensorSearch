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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.CapabilitiesDocument.Capabilities;
import net.opengis.sensorML.x101.ClassificationDocument.Classification;
import net.opengis.sensorML.x101.ContactDocument.Contact;
import net.opengis.sensorML.x101.IdentificationDocument.Identification;
import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList.Identifier;
import net.opengis.sensorML.x101.InputsDocument.Inputs;
import net.opengis.sensorML.x101.InputsDocument.Inputs.InputList;
import net.opengis.sensorML.x101.InterfaceDocument.Interface;
import net.opengis.sensorML.x101.InterfacesDocument.Interfaces;
import net.opengis.sensorML.x101.InterfacesDocument.Interfaces.InterfaceList;
import net.opengis.sensorML.x101.IoComponentPropertyType;
import net.opengis.sensorML.x101.KeywordsDocument.Keywords;
import net.opengis.sensorML.x101.OutputsDocument.Outputs;
import net.opengis.sensorML.x101.OutputsDocument.Outputs.OutputList;
import net.opengis.sensorML.x101.PersonDocument.Person;
import net.opengis.sensorML.x101.ResponsiblePartyDocument.ResponsibleParty;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML.Member;
import net.opengis.sensorML.x101.SystemType;
import net.opengis.sensorML.x101.ValidTimeDocument.ValidTime;
import net.opengis.swe.x101.AbstractDataRecordType;
import net.opengis.swe.x101.DataComponentPropertyType;
import net.opengis.swe.x101.DataRecordType;
import net.opengis.swe.x101.EnvelopeType;
import net.opengis.swe.x101.PositionType;
import net.opengis.swe.x101.VectorType;
import net.opengis.swe.x101.VectorType.Coordinate;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oss.sir.api.InternalSensorID;
import org.n52.oss.sir.api.ObservedProperty;
import org.n52.oss.sir.api.SirBoundingBox;
import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.api.SirSensorIdentification;
import org.n52.oss.sir.api.TimePeriod;
import org.n52.oss.sir.api.TimePeriod.IndeterminateTime;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.oss.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class SensorMLDecoder {

    private static final Object BOUNDING_BOX_FIELD_DEFINITION = "urn:ogc:def:property:OGC:1.0:observedBBOX";

    private static Logger log = LoggerFactory.getLogger(SensorMLDecoder.class);

    private static final ArrayList<String> X_AXIS_IDENTIFIERS = new ArrayList<>(Arrays.asList(new String[] {"x",
                                                                                                            "easting"}));

    private static final ArrayList<String> Y_AXIS_IDENTIFIERS = new ArrayList<>(Arrays.asList(new String[] {"y",
                                                                                                            "northing"}));


    private static SensorMLStringConverter stringConverter = new SensorMLStringConverter();

    public SirSensor decode(SensorMLDocument sensorML) {
        SirSensor sensor = new SirSensor();

        sensor.setSensorMLDocument(sensorML);
        sensor.setbBox(getBoundingBox(sensorML));
        sensor.setObservedProperties(getObservedProperties(sensorML));
        sensor.setTimePeriod(getTimePeriod(sensorML));
        sensor.setText(stringConverter.getText(sensorML));
        sensor.setKeywords(getKeywords(sensorML));
        sensor.setLatitude(getLatitude(sensorML));
        sensor.setLongitude(getLongitude(sensorML));
        sensor.setDescription(getDescription(sensorML));
        sensor.setClassificationList(getClassificationList(sensorML));
        sensor.setIdentificationsList(getIdentificationList(sensorML));
        sensor.setContacts(getContacts(sensorML));
        sensor.setInterfaces(getInterfaces(sensorML));
        sensor.setInputs(getInputs(sensorML));
        sensor.setOutputs(getOutputs(sensorML));

        return sensor;
    }

    /**
     * 
     * decodes the given sensor description and also adds the given identification to the returned SirSensor
     * instance if possible.
     */
    public SirSensor decode(SirSensorIdentification sensorIdent, XmlObject sensorDescription) throws OwsExceptionReport {
        SirSensor sensor = decode(sensorDescription);

        if (sensorIdent instanceof InternalSensorID) {
            InternalSensorID sid = (InternalSensorID) sensorIdent;
            sensor.setInternalSensorId(sid.getId());
        }

        return sensor;
    }

    public static SirSensor decode(SystemType system) {
        SirSensor sensor = new SirSensor();

        log.debug("Decoding SystemType: \n{}", XmlTools.validateAndIterateErrors(system));

        sensor.setSensorMLDocument(SmlTools.wrapSystemTypeInSensorMLDocument(system));
        sensor.setbBox(getBoundingBox(system));
        sensor.setObservedProperties(getObservedProperties(system));
        sensor.setTimePeriod(getTimePeriod(system));
        sensor.setText(stringConverter.getText(system));
        sensor.setLongitude(getLongitude(system));
        sensor.setDescription(getDescription(system));
        sensor.setClassificationList(getClassificationList(system));
        sensor.setIdentificationsList(getIdentificationList(system));
        sensor.setContacts(getContacts(system));
        sensor.setKeywords(getKeywords(system));

        // sensor.setInterfaces(getInterfaces(system));
        // sensor.setInputs(getInputs(system));
        // sensor.setOutputs(getOutputs(system));

        return sensor;
    }

    public SirSensor decode(XmlObject sensorML) throws OwsExceptionReport {
        // check if sensoML is a standalone sensor description or a
        // sir:sensorDescription
        if (sensorML instanceof SystemType) {
            SystemType system = (SystemType) sensorML;
            return decode(system);
        }
        else if (sensorML instanceof SensorMLDocument) {
            SensorMLDocument smlDoc = (SensorMLDocument) sensorML;
            return decode(smlDoc);
        }
        else if (sensorML instanceof AbstractProcessType) {
            AbstractProcessType abstractProcess = (AbstractProcessType) sensorML;

            // abstractProcess.changeType(SystemType.type) creates another
            // xml-fragment around
            // sir:sensorDescription, whereas abstractProcess.substitute(new
            // QName(XmlTools.SML_1_0_1_NAMESPACE_URI, "System"),
            // SystemType.type does return another
            // abstractProcess that cannot be cast - strange!

            SystemType system;
            try {
                system = SystemType.Factory.parse(abstractProcess.toString());
            }
            catch (XmlException e) {
                log.error("Error on parsing abstractProcess!");
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.NoApplicableCode,
                                     "SensorMLDecoder.decode",
                                     "Error on parsing sensorML document! " + e.getMessage());
                throw se;
            }

            return decode(system);
        }

        // last try: parse sensorML document
        SensorMLDocument sensorMLDocument;
        try {
            sensorMLDocument = SensorMLDocument.Factory.parse(sensorML.toString());
        }
        catch (XmlException e) {
            log.error("Error on parsing sensorML Document!");
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 "SensorMLDecoder.decode",
                                 "Error on parsing sensorML document! " + e.getMessage());
            throw se;
        }
        return decode(sensorMLDocument);
    }

    private static SirBoundingBox getBoundingBox(SensorMLDocument sensDoc) {
        SirBoundingBox bb = new SirBoundingBox(Double.MAX_VALUE, Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);

        Member[] members = sensDoc.getSensorML().getMemberArray();
        for (Member member : members) {
            SystemType systemType = (SystemType) member.getProcess();

            SirBoundingBox currentBB = getBoundingBox(systemType);
            bb.union(currentBB);
        }
        return bb;
    }

    private static SirBoundingBox getBoundingBox(SystemType system) {
        Capabilities[] capabilitiesArray = system.getCapabilitiesArray();
        for (Capabilities capabilities : capabilitiesArray) {
            AbstractDataRecordType abstractDataRecord = capabilities.getAbstractDataRecord();
            if (abstractDataRecord instanceof DataRecordType) {
                DataRecordType dataRecord = (DataRecordType) abstractDataRecord;
                DataComponentPropertyType[] fieldArray = dataRecord.getFieldArray();

                for (DataComponentPropertyType dataComponent : fieldArray) {
                    AbstractDataRecordType dataRec = dataComponent.getAbstractDataRecord();
                    if (dataRec != null) {
                        log.debug("A data record, check if bbox!");

                        String currentDefinition = dataRec.getDefinition();
                        if (currentDefinition.equals(BOUNDING_BOX_FIELD_DEFINITION)) {
                            if (dataRec instanceof EnvelopeType) {

                                log.debug("Found bbox!");
                                EnvelopeType envelope = (EnvelopeType) dataRec;
                                Coordinate[] lowerCornerCoords = envelope.getLowerCorner().getVector().getCoordinateArray();
                                Coordinate[] upperCornerCoords = envelope.getUpperCorner().getVector().getCoordinateArray();

                                double[] lowerCoords = getXYCoords(lowerCornerCoords);
                                double[] upperCoords = getXYCoords(upperCornerCoords);
                                double east = lowerCoords[0];
                                double south = lowerCoords[1];
                                double north = upperCoords[1];
                                double west = upperCoords[0];
                                return new SirBoundingBox(east, south, west, north);
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private static Object getClassificationList(SensorMLDocument sensorML) {
        List<String> classifications = new ArrayList<>();
        if (sensorML.getSensorML().getMemberArray().length != 0) {
            SystemType type = (SystemType) sensorML.getSensorML().getMemberArray(0).getProcess();
            return getClassificationList(type);
        }
        return classifications;
    }

    private static Object getClassificationList(SystemType system) {
        List<String> classifications = new ArrayList<>();
        Classification cs[] = system.getClassificationArray();
        for (Classification c : cs)
            classifications.add(c.getClassifierList().getId());
        return classifications;

    }

    private static Collection<String> getContacts(SensorMLDocument sensorML) {
        List<String> contacts = new ArrayList<>();
        if (sensorML.getSensorML().getMemberArray().length != 0) {
            SystemType type = (SystemType) sensorML.getSensorML().getMemberArray()[0].getProcess();
            contacts.addAll(getContacts(type));
        }

        return contacts;
    }

    private static Collection<String> getContacts(SystemType system) {
        Collection<String> contacts = new ArrayList<>();
        Contact[] contactsarr = system.getContactArray();
        for (Contact m : contactsarr) {
            if (m.getPerson() != null) {
                Person p = m.getPerson();
                contacts.add(p.getName());
                contacts.add(p.getEmail());
                contacts.add(p.getPhoneNumber());
            }
            if (m.getResponsibleParty() != null) {
                ResponsibleParty party = m.getResponsibleParty();
                contacts.add(party.getIndividualName());
                contacts.add(party.getOrganizationName());
            }
        }
        return contacts;

    }

    private static Object getDescription(SensorMLDocument sensorML) {
        if (sensorML.getSensorML().getMemberArray().length == 0)
            return "";
        SystemType type = (SystemType) sensorML.getSensorML().getMemberArray(0).getProcess();
        return getDescription(type);
    }

    private static Object getDescription(SystemType system) {
        return system.getDescription().getStringValue();
    }

    private static Collection<Object> getIdentificationList(SensorMLDocument sensorML) {
        List<Object> identifications = new ArrayList<>();
        if (sensorML.getSensorML().getMemberArray().length != 0) {
            SystemType type = (SystemType) sensorML.getSensorML().getMemberArray(0).getProcess();
            identifications.addAll(getIdentificationList(type));
        }

        return identifications;

    }

    private static Collection<Object> getIdentificationList(SystemType system) {
        Collection<Object> identifications = new ArrayList<>();
        Identification[] ids = system.getIdentificationArray();
        for (Identification id : ids) {
            Identifier[] iden = id.getIdentifierList().getIdentifierArray();
            for (int i = 0; i < iden.length; i++)
                identifications.add(iden[i].getTerm().getValue());
        }
        return identifications;

    }

    private static Collection<String> getInputs(SensorMLDocument sensorML) {
        List<String> inputs_results = new ArrayList<>();
        if (sensorML.getSensorML().getMemberArray().length != 0) {
            SystemType type = (SystemType) sensorML.getSensorML().getMemberArray()[0].getProcess();
            Inputs inputs = type.getInputs();
            InputList list = inputs.getInputList();
            IoComponentPropertyType[] inputsarr = list.getInputArray();
            for (int i = 0; i < inputsarr.length; i++) {
                StringBuilder builder = new StringBuilder();
                builder.append(inputsarr[i].getName());
                builder.append(" ");
                if (inputsarr[i].getQuantity() != null)
                    builder.append(inputsarr[i].getQuantity().getUom().getCode().toString());
                inputs_results.add(builder.toString());
            }
        }
        return inputs_results;

    }

    private static Collection<String> getInterfaces(SensorMLDocument sensorML) {
        List<String> interfaces_result = new ArrayList<>();
        if (sensorML.getSensorML().getMemberArray().length != 0) {
            SystemType type = (SystemType) sensorML.getSensorML().getMemberArray()[0].getProcess();
            Interfaces interfaces = type.getInterfaces();
            if (interfaces == null)
                return null;
            InterfaceList list = interfaces.getInterfaceList();
            if (list == null)
                return null;
            Interface[] interfacearr = list.getInterfaceArray();
            for (int i = 0; i < interfacearr.length; i++) {
                DataRecordType t = (DataRecordType) (interfacearr[i].getInterfaceDefinition().getServiceLayer().getAbstractDataRecord());
                DataComponentPropertyType fields[] = t.getFieldArray();
                for (int j = 0; j < fields.length; j++)
                    interfaces_result.add(fields[j].getText().getValue().toString());
            }
        }
        return interfaces_result;
    }

    private static Collection<String> getKeywords(SensorML sensorML) {
        ArrayList<String> keywords = new ArrayList<>();

        // sensorML.getSensorML().getMemberArray()[0].getProcess().getKeywordsArray()
        Member[] members = sensorML.getMemberArray();

        Member member = members[0];
        if (member.getProcess() instanceof SystemType) {
            SystemType system = (SystemType) member.getProcess();
            Collection<String> systemsKeywords = getKeywords(system);

            keywords.addAll(systemsKeywords);
        }

        return keywords;
    }

    private static Collection<String> getKeywords(SensorMLDocument sensorML) {
        return getKeywords(sensorML.getSensorML());
    }

    private static Collection<String> getKeywords(SystemType system) {
        ArrayList<String> keywordList = new ArrayList<>();

        Keywords[] keywords = system.getKeywordsArray();
        for (Keywords k : keywords) {
            String[] kwArray = k.getKeywordList().getKeywordArray();

            for (String currentKeyword : kwArray) {
                keywordList.add(currentKeyword);
            }
        }

        return keywordList;
    }

    private static String getLatitude(SensorMLDocument sensorML) {
        if (sensorML.getSensorML().getMemberArray().length == 0)
            return "";
        SystemType type = (SystemType) sensorML.getSensorML().getMemberArray(0).getProcess();
        if (type.getPosition() == null)
            return "";
        return getLatitude(type);
    }

    private static String getLatitude(SystemType system) {
        PositionType p = system.getPosition().getPosition();
        if (p == null)
            return "";
        VectorType vector = (p.getLocation().getVector());
        if (vector == null)
            return "";
        Coordinate[] coordinates = vector.getCoordinateArray();
        if (coordinates.length == 0)
            return "";
        StringBuilder latitude = new StringBuilder();
        for (Coordinate cord : coordinates) {
            if (cord.getName().equals("latitude")) {
                latitude.append(cord.getQuantity().getValue());
                break;
            }
        }
        return latitude.toString();

    }

    private static String getLongitude(SensorMLDocument sensorML) {
        if (sensorML.getSensorML().getMemberArray().length == 0)
            return "";
        SystemType type = (SystemType) sensorML.getSensorML().getMemberArray(0).getProcess();
        if (type.getPosition() == null)
            return "";
        return getLongitude(type);
    }

    private static String getLongitude(SystemType system) {
        PositionType p = system.getPosition().getPosition();
        if (p == null)
            return "";
        VectorType vector = (p.getLocation().getVector());
        if (vector == null)
            return "";
        Coordinate[] coordinates = vector.getCoordinateArray();
        if (coordinates.length == 0)
            return "";
        StringBuilder latitude = new StringBuilder();
        for (Coordinate cord : coordinates) {
            if (cord.getName().equals("longitude")) {
                latitude.append(cord.getQuantity().getValue());
                break;
            }
        }
        return latitude.toString();
    }

    private static Collection<String> getOutputs(SensorMLDocument sensorML) {
        List<String> output_results = new ArrayList<>();
        if (sensorML.getSensorML().getMemberArray().length != 0) {
            SystemType type = (SystemType) sensorML.getSensorML().getMemberArray()[0].getProcess();
            Outputs inputs = type.getOutputs();
            OutputList list = inputs.getOutputList();
            IoComponentPropertyType[] outputsarr = list.getOutputArray();
            for (int i = 0; i < outputsarr.length; i++) {
                StringBuilder builder = new StringBuilder();
                builder.append(outputsarr[i].getName());
                builder.append(" ");
                if (outputsarr[i].getQuantity() != null)
                    builder.append(outputsarr[i].getQuantity().getUom().getCode().toString());
                output_results.add(builder.toString());
            }
        }
        return output_results;

    }

    private static ArrayList<ObservedProperty> getObservedProperties(SystemType system) {
        ArrayList<ObservedProperty> obsProps = new ArrayList<>();

        Outputs outputs = system.getOutputs();
        OutputList outputList = outputs.getOutputList();
        IoComponentPropertyType[] outputArray = outputList.getOutputArray();
        for (IoComponentPropertyType output : outputArray) {
            ObservedProperty obsProp = new ObservedProperty();

            if (output.isSetQuantity()) {
                String urn = output.getQuantity().getDefinition();
                obsProp.setUrn(urn);
                String uom = output.getQuantity().getUom().getCode();
                obsProp.setUom(uom);
                obsProps.add(obsProp);
            }
            else if (output.isSetAbstractDataArray1()) {
                if (output.getAbstractDataArray1().isSetDefinition()) {
                    obsProp.setUrn(output.getAbstractDataArray1().getDefinition());
                    obsProps.add(obsProp);
                }
                else {
                    log.warn("Could not handle output observedProperty data array, definition missing.");
                }
            }
            else {
                log.warn("Could not handle output observedProperty: " + output);
            }
        }

        return obsProps;
    }

    private static ArrayList<ObservedProperty> getObservedProperties(SensorMLDocument sensDoc) {
        ArrayList<ObservedProperty> obsProps = new ArrayList<>();

        Member[] members = sensDoc.getSensorML().getMemberArray();
        for (Member member : members) {
            SystemType systemType = (SystemType) member.getProcess();
            ArrayList<ObservedProperty> current = getObservedProperties(systemType);

            obsProps.addAll(current);
        }
        return obsProps;
    }

    private static TimePeriod getTimePeriod(SensorMLDocument sensDoc) {
        TimePeriod sirTimePeriod = null;
        Member[] members = sensDoc.getSensorML().getMemberArray();
        for (Member member : members) {
            SystemType systemType = (SystemType) member.getProcess();
            if (sirTimePeriod == null)
                sirTimePeriod = getTimePeriod(systemType);
            else {
                TimePeriod stp = getTimePeriod(systemType);
                sirTimePeriod.union(stp);
            }
        }
        return sirTimePeriod;
    }

    private static TimePeriod getTimePeriod(SystemType system) {
        TimePeriod sirTimePeriod = new TimePeriod();

        if (system.isSetValidTime()) {
            ValidTime validTime = system.getValidTime();

            String startString = null;
            if (validTime.getTimePeriod().getBeginPosition() != null) {
                startString = validTime.getTimePeriod().getBeginPosition().getStringValue();
                if (startString.isEmpty())
                    startString = validTime.getTimePeriod().getBeginPosition().getIndeterminatePosition().toString();
            }
            else
                startString = validTime.getTimePeriod().getBegin().getTimeInstant().getTimePosition().getStringValue();

            String endString = null;
            if (validTime.getTimePeriod().getEndPosition() != null) {
                endString = validTime.getTimePeriod().getEndPosition().getStringValue();
                if (endString.isEmpty()) // could be indeterminate
                    endString = validTime.getTimePeriod().getEndPosition().getIndeterminatePosition().toString();
            }
            else
                endString = validTime.getTimePeriod().getEnd().getTimeInstant().getTimePosition().getStringValue();

            IndeterminateTime start = new IndeterminateTime(startString);
            sirTimePeriod.setStartTime(start);
            IndeterminateTime end = new IndeterminateTime(endString);
            sirTimePeriod.setEndTime(end);

            checkTime(sirTimePeriod);
        }

        return sirTimePeriod;
    }

    private static void checkTime(TimePeriod sirTimePeriod) {
        checkTime(sirTimePeriod.getStartTime());
        checkTime(sirTimePeriod.getEndTime());
    }

    private static void checkTime(IndeterminateTime it) {
        if (it.isIndeterminate() && it.isDeterminate()) {
            // no time created
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 "SensorMLDecoder.decode",
                                 "The start time is missing or cannot be parsed in the timePeriod element of valid time section! Time: "
                                         + it);
        }
    }

    private static double[] getXYCoords(Coordinate[] coordinates) {
        double[] xy = new double[2];

        for (Coordinate coordinate : coordinates) {
            String name = coordinate.getName();
            String id = coordinate.getQuantity().getAxisID();

            if (X_AXIS_IDENTIFIERS.contains(name) || X_AXIS_IDENTIFIERS.contains(id)) {
                xy[0] = coordinate.getQuantity().getValue();
            }
            else if (Y_AXIS_IDENTIFIERS.contains(name) || Y_AXIS_IDENTIFIERS.contains(id)) {
                xy[1] = coordinate.getQuantity().getValue();
            }
        }

        return xy;
    }

    public SensorMLDecoder() {
        log.info("NEW {}", this);
    }

}