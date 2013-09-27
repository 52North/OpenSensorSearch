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
package org.n52.oss.testdata.sml;

import java.util.Collection;
import java.util.Map;

import javax.xml.namespace.QName;

import net.opengis.gml.StringOrRefType;
import net.opengis.gml.TimePeriodType;
import net.opengis.gml.TimePositionType;
import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.CapabilitiesDocument.Capabilities;
import net.opengis.sensorML.x101.ClassificationDocument.Classification;
import net.opengis.sensorML.x101.ClassificationDocument.Classification.ClassifierList;
import net.opengis.sensorML.x101.ClassificationDocument.Classification.ClassifierList.Classifier;
import net.opengis.sensorML.x101.ContactDocument.Contact;
import net.opengis.sensorML.x101.IdentificationDocument.Identification;
import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList;
import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList.Identifier;
import net.opengis.sensorML.x101.InputsDocument.Inputs;
import net.opengis.sensorML.x101.InputsDocument.Inputs.InputList;
import net.opengis.sensorML.x101.InterfaceDefinitionDocument.InterfaceDefinition;
import net.opengis.sensorML.x101.InterfaceDocument.Interface;
import net.opengis.sensorML.x101.InterfacesDocument.Interfaces;
import net.opengis.sensorML.x101.InterfacesDocument.Interfaces.InterfaceList;
import net.opengis.sensorML.x101.IoComponentPropertyType;
import net.opengis.sensorML.x101.KeywordsDocument.Keywords;
import net.opengis.sensorML.x101.KeywordsDocument.Keywords.KeywordList;
import net.opengis.sensorML.x101.LayerPropertyType;
import net.opengis.sensorML.x101.OutputsDocument.Outputs;
import net.opengis.sensorML.x101.OutputsDocument.Outputs.OutputList;
import net.opengis.sensorML.x101.PositionDocument.Position;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML.Member;
import net.opengis.sensorML.x101.SystemType;
import net.opengis.sensorML.x101.TermDocument.Term;
import net.opengis.sensorML.x101.ValidTimeDocument.ValidTime;
import net.opengis.swe.x101.AbstractDataRecordType;
import net.opengis.swe.x101.DataComponentPropertyType;
import net.opengis.swe.x101.DataRecordType;
import net.opengis.swe.x101.EnvelopeType;
import net.opengis.swe.x101.ObservablePropertyDocument.ObservableProperty;
import net.opengis.swe.x101.PositionType;
import net.opengis.swe.x101.QuantityDocument.Quantity;
import net.opengis.swe.x101.TextDocument.Text;
import net.opengis.swe.x101.UomPropertyType;
import net.opengis.swe.x101.VectorPropertyType;
import net.opengis.swe.x101.VectorType;
import net.opengis.swe.x101.VectorType.Coordinate;

import org.apache.xmlbeans.XmlCursor;
import org.n52.sir.sml.SMLConstants;
import org.n52.sir.util.XmlTools;

/**
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class SensorMLEncoder {

    public static final String MAP_KEY_NAME = "name";
    public static final String MAP_KEY_DEFINITION = "definition";
    public static final String MAP_KEY_UOM = "uom";
    public static final String MAP_KEY_SERVICE_URL = "urn:ogc:def:interface:OGC:1.0:ServiceURL";
    public static final String MAP_KEY_SERVICE_TYPE = "urn:ogc:def:interface:OGC:1.0:ServiceType";
    public static final String MAP_KEY_SERVICE_SPECIFIC_SENSOR_ID = "urn:ogc:def:interface:OGC:1.0:ServiceSpecificSensorID";

    private static final String SENSOR_ML_VERSION = "1.0.1";
    private static final String IDENTIFIER_NAME_UNIQUE_ID = "uniqueID";
    private static final String IDENTIFIER_NAME_LONG_NAME = "longName";
    private static final String IDENTIFIER_NAME_SHORT_NAME = "shortName";
    private static final String TERM_DEF_UNIQUE_ID = "urn:ogc:def:identifier:OGC:1.0:uniqueID";
    private static final String TERM_DEF_LONG_NAME = "urn:ogc:def:identifier:OGC:1.0:longName";
    private static final String TERM_DEF_SHORT_NAME = "urn:ogc:def:identifier:OGC:1.0:shortName";
    private static final String FIELD_NAME_BBOX = "observedBBOX";
    private static final String DATARECORD_DEF_CAPS = "urn:ogc:def:property:capabilities";
    private static final String BBOX_ENVELOPE_SRS_NAME_LATLON = "urn:ogc:def:crs:EPSG:4326";
    private static final String BBOX_ENVELOPE_DEFINITION = "urn:ogc:def:property:OGC:1.0:observedBBOX";
    private static final String COORD_NAME_LAT = "latitude";
    private static final String QUANTITY_AXIS_ID_LAT = "y";
    private static final String UOM_CODE_LATLON = "degree";
    private static final String COORD_NAME_LON = "longitude";
    private static final String QUANTITY_AXIS_ID_LON = "x";
    private static final String CLASSIFIER_NAME_INTENDED_APPLICATION = "intendedApplication";
    private static final String CLASSIFIER_NAME_SENSOR_TYPE = "sensorType";
    private static final String POSITION_NAME = "stationPosition";
    private static final String COORD_NAME_ALTITUDE = "altitude";
    private static final String QUANTITY_AXIS_ID_ALTITUDE = "z";
    private static final String DATARECORD_DEF_SWE_SERVICE_INTERFACE = "urn:ogc:def:interface:OGC:1.0:SWEServiceInterface";
    private static final String CLASSIFIER_TERM_DEFINITION_SENSOR_TYPE = "urn:ogc:def:classifier:OGC:1.0:sensorType";
    private static final String CLASSIFIER_TERM_DEFINITION_INTENDED_APPLICATION = "urn:ogc:def:classifier:OGC:1.0:intendedApplication";
    private static final String POSTION_REFERENCE_FRAME = "urn:ogc:def:crs:EPSG:4326";
    private static final String SYSTEM_NAME = "System";
    private static final String UOM_CODE_ALTITUDE = "m";

    public SensorMLDocument encode(TestSensor sensor) {
        SensorMLDocument smlDocument = SensorMLDocument.Factory.newInstance(XmlTools.unconfiguredXmlOptionsForNamespaces());
        SensorML sml = smlDocument.addNewSensorML();
        sml.setVersion(SENSOR_ML_VERSION);
        Member smlMember = sml.addNewMember();
        AbstractProcessType abstractProcess = smlMember.addNewProcess();
        SystemType systemType = (SystemType) abstractProcess.substitute(new QName(SMLConstants.NAMESPACE, SYSTEM_NAME),
                                                                        SystemType.type);

        XmlCursor requestCursor = sml.newCursor();
        requestCursor.setAttributeText(XmlTools.SCHEMA_LOCATION_ATTRIBUTE_QNAME, SMLConstants.SCHEMA_LOCATION);

        /* DESCRIPTION */
        String descriptionString = sensor.getGmlDescription();
        StringOrRefType description = systemType.addNewDescription();
        description.setStringValue(descriptionString);

        /* KEYWORDS */
        Keywords keywords = systemType.addNewKeywords();
        KeywordList keywordList = keywords.addNewKeywordList();
        Collection<String> ks = sensor.getKeywords();
        for (String s : ks) {
            keywordList.addKeyword(s);
        }

        /* IDENTIFICATION */
        Identification identification = systemType.addNewIdentification();
        IdentifierList identifierList = identification.addNewIdentifierList();
        Identifier ident_uniqueID = identifierList.addNewIdentifier();
        ident_uniqueID.setName(IDENTIFIER_NAME_UNIQUE_ID);
        Term term_uniqueID = ident_uniqueID.addNewTerm();
        term_uniqueID.setDefinition(TERM_DEF_UNIQUE_ID);
        term_uniqueID.setValue(sensor.getUniqueId());

        Identifier ident_longName = identifierList.addNewIdentifier();
        ident_longName.setName(IDENTIFIER_NAME_LONG_NAME);
        Term term_longName = ident_longName.addNewTerm();
        term_longName.setDefinition(TERM_DEF_LONG_NAME);
        term_longName.setValue(sensor.getLongName());

        Identifier ident_shortName = identifierList.addNewIdentifier();
        ident_shortName.setName(IDENTIFIER_NAME_SHORT_NAME);
        Term term_shortName = ident_shortName.addNewTerm();
        term_shortName.setDefinition(TERM_DEF_SHORT_NAME);
        term_shortName.setValue(sensor.getShortName());

        /* CLASSIFICATION */
        Classification classification = systemType.addNewClassification();
        ClassifierList classifierList = classification.addNewClassifierList();
        Classifier classifierIA = classifierList.addNewClassifier();
        classifierIA.setName(CLASSIFIER_NAME_INTENDED_APPLICATION);
        Term termIA = classifierIA.addNewTerm();
        termIA.setDefinition(CLASSIFIER_TERM_DEFINITION_INTENDED_APPLICATION);
        termIA.setValue(sensor.getIntendedApplication());
        Classifier classifierST = classifierList.addNewClassifier();
        classifierST.setName(CLASSIFIER_NAME_SENSOR_TYPE);
        Term termST = classifierST.addNewTerm();
        termST.setDefinition(CLASSIFIER_TERM_DEFINITION_SENSOR_TYPE);
        termST.setValue(sensor.getSensorType());

        /* VALID TIME */
        ValidTime validTime = systemType.addNewValidTime();
        TimePeriodType validTimePeriod = validTime.addNewTimePeriod();
        TimePositionType beginPos = validTimePeriod.addNewBeginPosition();
        beginPos.setStringValue(sensor.getValidTimeBegin());
        TimePositionType endPos = validTimePeriod.addNewEndPosition();
        endPos.setStringValue(sensor.getValidTimeEnd());

        /* CAPABILITIES */
        Capabilities capabilities = systemType.addNewCapabilities();
        AbstractDataRecordType abstractDataRecord = capabilities.addNewAbstractDataRecord();
        DataRecordType dataRecord = (DataRecordType) abstractDataRecord.substitute(new QName(XmlTools.SWE_1_0_1_NAMESPACE_URI,
                                                                                             "DataRecord"),
                                                                                   DataRecordType.type);
        dataRecord.setDefinition(DATARECORD_DEF_CAPS);
        DataComponentPropertyType field_bbox = dataRecord.addNewField();
        field_bbox.setName(FIELD_NAME_BBOX);

        /* ENVELOPE */
        EnvelopeType envelope = EnvelopeType.Factory.newInstance();
        envelope.setDefinition(BBOX_ENVELOPE_DEFINITION);
        envelope.setReferenceFrame(BBOX_ENVELOPE_SRS_NAME_LATLON);
        double[] lowerCornerPoint = sensor.getBboxLowerCorner();
        VectorPropertyType lowerCorner = envelope.addNewLowerCorner();
        VectorType lCVector = lowerCorner.addNewVector();
        Coordinate lCLat = lCVector.addNewCoordinate();
        lCLat.setName(COORD_NAME_LAT);
        Quantity lCLatQuantity = lCLat.addNewQuantity();
        lCLatQuantity.setAxisID(QUANTITY_AXIS_ID_LAT);
        lCLatQuantity.addNewUom().setCode(UOM_CODE_LATLON);
        lCLatQuantity.setValue(lowerCornerPoint[0]);
        Coordinate lCLon = lCVector.addNewCoordinate();
        lCLon.setName(COORD_NAME_LON);
        Quantity lCLonQuantity = lCLon.addNewQuantity();
        lCLonQuantity.setAxisID(QUANTITY_AXIS_ID_LON);
        lCLonQuantity.addNewUom().setCode(UOM_CODE_LATLON);
        lCLonQuantity.setValue(lowerCornerPoint[1]);
        double[] upperCornerPoint = sensor.getBboxUpperCorner();
        VectorPropertyType upperCorner = envelope.addNewUpperCorner();
        VectorType uCVector = upperCorner.addNewVector();
        Coordinate uCLat = uCVector.addNewCoordinate();
        uCLat.setName(COORD_NAME_LAT);
        Quantity uCLatQuantity = uCLat.addNewQuantity();
        uCLatQuantity.setAxisID(QUANTITY_AXIS_ID_LAT);
        uCLatQuantity.addNewUom().setCode(UOM_CODE_LATLON);
        uCLatQuantity.setValue(upperCornerPoint[0]);
        Coordinate uCLon = uCVector.addNewCoordinate();
        uCLon.setName(COORD_NAME_LON);
        Quantity uCLonQuantity = uCLon.addNewQuantity();
        uCLonQuantity.setAxisID(QUANTITY_AXIS_ID_LON);
        uCLonQuantity.addNewUom().setCode(UOM_CODE_LATLON);
        uCLonQuantity.setValue(upperCornerPoint[1]);

        AbstractDataRecordType bboxAbstractDataRec = field_bbox.addNewAbstractDataRecord();
        EnvelopeType env = (EnvelopeType) bboxAbstractDataRec.substitute(new QName(XmlTools.SWE_1_0_1_NAMESPACE_URI,
                                                                                   "Envelope"), EnvelopeType.type);
        env.set(envelope);

        /* CONTACT */
        Contact contact = systemType.addNewContact();
        contact.set(sensor.getContact());

        /* POSITION */
        double[] positionCoords = sensor.getLatLonPosition();
        Position smlPosition = systemType.addNewPosition();
        smlPosition.setName(POSITION_NAME);
        AbstractProcessType abstractProcessPos = smlPosition.addNewProcess();
        PositionType swePosition = (PositionType) abstractProcessPos.substitute(new QName(XmlTools.SWE_1_0_1_NAMESPACE_URI,
                                                                                          "Position"),
                                                                                PositionType.type);
        swePosition.setReferenceFrame(POSTION_REFERENCE_FRAME);
        VectorType vector = swePosition.addNewLocation().addNewVector();
        vector.setId(sensor.getLocationId());
        Coordinate coordLatitude = vector.addNewCoordinate();
        coordLatitude.setName(COORD_NAME_LAT);
        Quantity quantityLatitude = coordLatitude.addNewQuantity();
        quantityLatitude.setAxisID(QUANTITY_AXIS_ID_LAT);
        quantityLatitude.addNewUom().setCode(UOM_CODE_LATLON);
        quantityLatitude.setValue(positionCoords[0]);
        Coordinate coordLongitude = vector.addNewCoordinate();
        coordLongitude.setName(COORD_NAME_LON);
        Quantity quantityLongitude = coordLongitude.addNewQuantity();
        quantityLongitude.setAxisID(QUANTITY_AXIS_ID_LON);
        quantityLongitude.setValue(positionCoords[1]);
        quantityLongitude.addNewUom().setCode(UOM_CODE_LATLON);
        Coordinate coordAltitude = vector.addNewCoordinate();
        coordAltitude.setName(COORD_NAME_ALTITUDE);
        Quantity quantityAltitude = coordAltitude.addNewQuantity();
        quantityAltitude.setAxisID(QUANTITY_AXIS_ID_ALTITUDE);
        quantityAltitude.setValue(sensor.getAltitude());
        quantityAltitude.addNewUom().setCode(UOM_CODE_ALTITUDE);

        /* INTERFACES */
        Interfaces interfaces = systemType.addNewInterfaces();
        InterfaceList interfacesList = interfaces.addNewInterfaceList();
        for (Map<String, String> currentInterface : sensor.getInterfaces()) {
            Interface newInterfaceBean = interfacesList.addNewInterface();
            newInterfaceBean.setName(currentInterface.get(MAP_KEY_SERVICE_SPECIFIC_SENSOR_ID).replace(":", "_"));

            InterfaceDefinition interfaysDef = newInterfaceBean.addNewInterfaceDefinition();
            LayerPropertyType serviceLayer = interfaysDef.addNewServiceLayer();
            AbstractDataRecordType abstractDataRec = serviceLayer.addNewAbstractDataRecord();

            DataRecordType dataRec = (DataRecordType) abstractDataRec.substitute(new QName(XmlTools.SWE_1_0_1_NAMESPACE_URI,
                                                                                           "DataRecord"),
                                                                                 DataRecordType.type);
            dataRec.setDefinition(DATARECORD_DEF_SWE_SERVICE_INTERFACE);
            DataComponentPropertyType serviceUrl = dataRec.addNewField();
            serviceUrl.setName(MAP_KEY_SERVICE_URL);
            Text serviceUrlText = serviceUrl.addNewText();
            serviceUrlText.setValue(currentInterface.get(MAP_KEY_SERVICE_URL));

            DataComponentPropertyType serviceType = dataRec.addNewField();
            serviceType.setName(MAP_KEY_SERVICE_TYPE);
            Text serviceTypeText = serviceType.addNewText();
            serviceTypeText.setValue(currentInterface.get(MAP_KEY_SERVICE_TYPE));

            DataComponentPropertyType sssId = dataRec.addNewField();
            sssId.setName(MAP_KEY_SERVICE_SPECIFIC_SENSOR_ID);
            Text sssIdText = sssId.addNewText();
            sssIdText.setValue(currentInterface.get(MAP_KEY_SERVICE_SPECIFIC_SENSOR_ID));
        }

        /* INPUTS */
        Inputs inputs = systemType.addNewInputs();
        InputList inputList = inputs.addNewInputList();
        for (Map<String, String> input : sensor.getInputs()) {
            IoComponentPropertyType ioComp = inputList.addNewInput();
            ioComp.setName(input.get(MAP_KEY_NAME));
            ObservableProperty obsProp = ioComp.addNewObservableProperty();
            obsProp.setDefinition(input.get(MAP_KEY_DEFINITION));
        }

        /* OUTPUTS */
        Outputs outputs = systemType.addNewOutputs();
        OutputList outputList = outputs.addNewOutputList();
        for (Map<String, String> output : sensor.getOutput()) {
            IoComponentPropertyType ioComp = outputList.addNewOutput();
            ioComp.setName(output.get(MAP_KEY_NAME));
            Quantity quantity = ioComp.addNewQuantity();
            quantity.setDefinition(output.get(MAP_KEY_DEFINITION));
            UomPropertyType uom = quantity.addNewUom();
            uom.setCode(output.get(MAP_KEY_UOM));
        }

        return smlDocument;
    }

}