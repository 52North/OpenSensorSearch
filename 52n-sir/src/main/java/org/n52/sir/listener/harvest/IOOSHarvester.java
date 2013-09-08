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
/**
 * 
 */

package org.n52.sir.listener.harvest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import net.opengis.gml.TimePeriodType;
import net.opengis.sensorML.x101.ClassificationDocument.Classification;
import net.opengis.sensorML.x101.ClassificationDocument.Classification.ClassifierList;
import net.opengis.sensorML.x101.ClassificationDocument.Classification.ClassifierList.Classifier;
import net.opengis.sensorML.x101.ContactDocument.Contact;
import net.opengis.sensorML.x101.ContactInfoDocument.ContactInfo;
import net.opengis.sensorML.x101.ContactInfoDocument.ContactInfo.Address;
import net.opengis.sensorML.x101.IdentificationDocument.Identification;
import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList;
import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList.Identifier;
import net.opengis.sensorML.x101.InputsDocument.Inputs.InputList;
import net.opengis.sensorML.x101.IoComponentPropertyType;
import net.opengis.sensorML.x101.KeywordsDocument.Keywords.KeywordList;
import net.opengis.sensorML.x101.OutputsDocument.Outputs.OutputList;
import net.opengis.sensorML.x101.PositionDocument.Position;
import net.opengis.sensorML.x101.ResponsiblePartyDocument.ResponsibleParty;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML;
import net.opengis.sensorML.x101.SystemType;
import net.opengis.sensorML.x101.TermDocument.Term;
import net.opengis.swe.x101.AbstractDataRecordType;
import net.opengis.swe.x101.BooleanDocument.Boolean;
import net.opengis.swe.x101.DataComponentPropertyType;
import net.opengis.swe.x101.DataRecordType;
import net.opengis.swe.x101.EnvelopeType;
import net.opengis.swe.x101.ObservablePropertyDocument.ObservableProperty;
import net.opengis.swe.x101.PositionType;
import net.opengis.swe.x101.QuantityDocument.Quantity;
import net.opengis.swe.x101.VectorPropertyType;
import net.opengis.swe.x101.VectorType;
import net.opengis.swe.x101.VectorType.Coordinate;

import org.apache.xmlbeans.XmlException;
import org.n52.sir.datastructure.SirBoundingBox;
import org.n52.sir.ds.IHarvestServiceDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.request.SirHarvestServiceRequest;
import org.n52.sir.sml.SMLConstants;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class IOOSHarvester extends FileHarvester {

    /**
     * 
     * @author Daniel Nüst (d.nuest@52north.org)
     * 
     */
    private class IOOSCatalogHandler implements ContentHandler {

        /**
         * 
         * @author Daniel Nüst (d.nuest@52north.org)
         * 
         */
        private class IOOSObservation {

            public String dataType;
            public String label;
            public long lastTime;
            public String value;

            public IOOSObservation(String label, String value, String dataType, long lastTime) {
                this.label = label;
                this.value = value;
                this.dataType = dataType;
                this.lastTime = lastTime;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.lang.Object#toString()
             */
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("IOOSObservation [");
                sb.append(this.label);
                sb.append(", ");
                sb.append(this.value);
                sb.append(", ");
                sb.append(this.dataType);
                sb.append(", ");
                sb.append(this.lastTime);
                sb.append("]");
                return sb.toString();
            }

        }

        /**
         * 
         * @author Daniel Nüst (d.nuest@52north.org)
         * 
         */
        private class IOOSPlatform {

            public SirBoundingBox bbox;
            public String dataProvider;
            public String[] datatypes;
            public String desc;
            public URL descriptionUrl;
            public long endTime;
            public boolean hasRecentValue;
            public double lat;
            public long latestTime; // TODO put in status for the sensor
            public double lon;
            public String name;
            public List<IOOSObservation> observations = new ArrayList<>();
            public String organisationName;
            @SuppressWarnings("unused")
            public URL organisationUrl;
            public String program;
            public String serviceType;
            public long startTime;
            public String title;
            public String wmoId;

            /**
             * 
             * @param name
             * @param program
             * @param serviceType
             * @param wmoId
             * @param lat
             * @param lon
             * @param sirBoundingBox
             * @param title
             * @param description
             * @param organisationName
             * @param organisationUrl
             * @param dataProvider
             * @param startTime
             * @param endTime
             * @param latestTime
             * @param descriptionUrl
             * @param datatypes
             * @param hasRecentValues
             */
            public IOOSPlatform(String name,
                                String program,
                                String serviceType,
                                String wmoId,
                                double lat,
                                double lon,
                                SirBoundingBox sirBoundingBox,
                                String title,
                                String description,
                                String organisationName,
                                URL organisationUrl,
                                String dataProvider,
                                long startTime,
                                long endTime,
                                long latestTime,
                                URL descriptionUrl,
                                String[] datatypes,
                                boolean hasRecentValues) {
                this.name = name;
                this.serviceType = serviceType;
                this.lat = lat;
                this.lon = lon;
                this.bbox = sirBoundingBox;
                this.title = title;
                this.desc = description;
                this.organisationName = organisationName;
                this.organisationUrl = organisationUrl;
                this.dataProvider = dataProvider;
                this.startTime = startTime;
                this.endTime = endTime;
                this.latestTime = latestTime;
                this.descriptionUrl = descriptionUrl;
                this.datatypes = datatypes;
                this.hasRecentValue = hasRecentValues;
                this.program = program;
                this.wmoId = wmoId;
            }

        }

        private static final String APPlICATION_TERM_DEFINITION = "urn:ogc:def:classifier:OGC:1.0:application";

        private static final String BBOX_DEFINITION = "urn:ogc:def:property:OGC:1.0:observedBBOX";

        private static final String CLASSIFIER_NAME_INTENDED_APPLICATION = "intendedApplication";

        private static final String CLASSIFIER_NAME_SENSOR_TYPE = "sensorType";

        @SuppressWarnings("unused")
        private static final String COORD_NAME_ALTITUDE = "altitude";

        private static final String COORD_NAME_LAT = "latitude";
        private static final String COORD_NAME_LON = "longitude";
        private static final String DATA_RECORD_CAPABILITIES_DEFINITION = "urn:ogc:def:property:capabilities";
        private static final String EPSG_4326_REFERENCE_SYSTEM_DEFINITION = "urn:ogc:def:crs:EPSG::4326";
        private static final String FIELD_NAME_BBOX = "observedBBOX";
        private static final String FIELD_NAME_STATUS = "status";
        private static final String LONG_NAME_DEFINITION = "urn:ogc:def:identifier:OGC:1.0:longName";

        private static final String OBS_ELEMENTNAME = "obs";
        private static final String PLATFORM_ELEMENTNAME = "platform";

        @SuppressWarnings("unused")
        private static final String QUANTITY_AXIS_ID_ALTITUDE = "z";

        private static final String QUANTITY_AXIS_ID_LAT = "y";

        private static final String QUANTITY_AXIS_ID_LON = "x";

        private static final String SENSORTYPE_TERM_DEFINITION = "urn:ogc:def:classifier:OGC:1.0:sensorType";

        private static final String SHORT_NAME_DEFINITION = "urn:ogc:def:identifier:OGC:1.0:shortName";

        protected static final String SOS_PLATFORMS_ELEMENTNAME = "sos_platforms";

        private static final String UNIQUE_ID_DEFINITION = "urn:ogc:def:identifier:OGC:1.0:uniqueID";

        @SuppressWarnings("unused")
        private static final String UOM_CODE_ALTITUDE = "meter";

        private static final String UOM_CODE_LATLON = "degree";

        private String[] additionalKeywords = new String[] {"IOOS"};

        private IOOSPlatform currentPlatform;

        private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        private int platformCounter;

        /**
         * 
         */
        protected IOOSCatalogHandler() {
        }

        private void buildCapabilities(IOOSPlatform platform, SystemType systemType) {
            net.opengis.sensorML.x101.CapabilitiesDocument.Capabilities capabilities = systemType.addNewCapabilities();
            AbstractDataRecordType abstractDataRecord = capabilities.addNewAbstractDataRecord();
            DataRecordType dataRecord = (DataRecordType) abstractDataRecord.substitute(new QName(XmlTools.SWE_1_0_1_NAMESPACE_URI,
                                                                                                 "DataRecord"),
                                                                                       DataRecordType.type);
            dataRecord.setDefinition(DATA_RECORD_CAPABILITIES_DEFINITION);
            DataComponentPropertyType field_bbox = dataRecord.addNewField();
            field_bbox.setName(FIELD_NAME_BBOX);

            // status field
            DataComponentPropertyType statusField = dataRecord.addNewField();
            statusField.setName(FIELD_NAME_STATUS);
            Boolean statusBoolean = statusField.addNewBoolean();
            statusBoolean.setDefinition("hasRecentValue");
            statusBoolean.setValue(platform.hasRecentValue);

            // envelope
            EnvelopeType envelope = EnvelopeType.Factory.newInstance();
            envelope.setDefinition(BBOX_DEFINITION);
            envelope.setReferenceFrame(EPSG_4326_REFERENCE_SYSTEM_DEFINITION);
            VectorPropertyType lowerCorner = envelope.addNewLowerCorner();
            VectorType lCVector = lowerCorner.addNewVector();
            Coordinate lCLat = lCVector.addNewCoordinate();
            lCLat.setName(COORD_NAME_LAT);
            Quantity lCLatQuantity = lCLat.addNewQuantity();
            lCLatQuantity.setAxisID(QUANTITY_AXIS_ID_LAT);
            lCLatQuantity.addNewUom().setCode(UOM_CODE_LATLON);
            lCLatQuantity.setValue(platform.bbox.getLowerCornerPoint()[0]);
            Coordinate lCLon = lCVector.addNewCoordinate();
            lCLon.setName(COORD_NAME_LON);
            Quantity lCLonQuantity = lCLon.addNewQuantity();
            lCLonQuantity.setAxisID(QUANTITY_AXIS_ID_LON);
            lCLonQuantity.addNewUom().setCode(UOM_CODE_LATLON);
            lCLonQuantity.setValue(platform.bbox.getLowerCornerPoint()[1]);
            VectorPropertyType upperCorner = envelope.addNewUpperCorner();
            VectorType uCVector = upperCorner.addNewVector();
            Coordinate uCLat = uCVector.addNewCoordinate();
            uCLat.setName(COORD_NAME_LAT);
            Quantity uCLatQuantity = uCLat.addNewQuantity();
            uCLatQuantity.setAxisID(QUANTITY_AXIS_ID_LAT);
            uCLatQuantity.addNewUom().setCode(UOM_CODE_LATLON);
            uCLatQuantity.setValue(platform.bbox.getUpperCornerPoint()[0]);
            Coordinate uCLon = uCVector.addNewCoordinate();
            uCLon.setName(COORD_NAME_LON);
            Quantity uCLonQuantity = uCLon.addNewQuantity();
            uCLonQuantity.setAxisID(QUANTITY_AXIS_ID_LON);
            uCLonQuantity.addNewUom().setCode(UOM_CODE_LATLON);
            uCLonQuantity.setValue(platform.bbox.getUpperCornerPoint()[1]);

            AbstractDataRecordType bboxAbstractDataRec = field_bbox.addNewAbstractDataRecord();
            EnvelopeType env = (EnvelopeType) bboxAbstractDataRec.substitute(new QName(XmlTools.SWE_1_0_1_NAMESPACE_URI,
                                                                                       "Envelope"),
                                                                             EnvelopeType.type);
            env.set(envelope);
        }

        /**
         * @param station
         * @param systemType
         */
        private void buildClassification(IOOSPlatform platform, SystemType systemType) {
            Classification classification = systemType.addNewClassification();
            ClassifierList classifierList = classification.addNewClassifierList();

            // application
            Classifier classifierIA = classifierList.addNewClassifier();
            classifierIA.setName(CLASSIFIER_NAME_INTENDED_APPLICATION);
            Term termIA = classifierIA.addNewTerm();
            termIA.setDefinition(APPlICATION_TERM_DEFINITION);
            termIA.setValue("ocean observation");

            // wmo id
            for (String s : platform.datatypes) {
                Classifier classifierST = classifierList.addNewClassifier();
                classifierST.setName(CLASSIFIER_NAME_SENSOR_TYPE);
                Term termST = classifierST.addNewTerm();
                termST.setDefinition(SENSORTYPE_TERM_DEFINITION);
                termST.setValue(s);
            }
        }

        /**
         * @param station
         * @param systemType
         */
        private void buildContact(IOOSPlatform platform, SystemType systemType) {
            Contact contact = systemType.addNewContact();

            ResponsibleParty rp = contact.addNewResponsibleParty();
            rp.setId(platform.program);
            // rp.setIndividualName("N/A");
            rp.setOrganizationName(platform.organisationName);
            ContactInfo ci = rp.addNewContactInfo();
            Address a = ci.addNewAddress();
            a.setCountry("USA");
            a.setElectronicMailAddress("noaa.ioos.webmaster@noaa.gov");
        }

        private void buildDescription(IOOSPlatform platform, SystemType systemType) {
            systemType.addNewDescription().setStringValue("IOOS Sensor at " + platform.desc + " by " + platform.program);
        }

        private void buildIdentification(IOOSPlatform platform, SystemType system, SystemType inputSystem) {
            IdentifierList idenList = system.addNewIdentification().addNewIdentifierList();

            Identifier ident = idenList.addNewIdentifier();
            Term term = ident.addNewTerm();
            term.setDefinition(UNIQUE_ID_DEFINITION);
            Identification identification = inputSystem.getIdentificationArray(0);
            Identifier identifier = identification.getIdentifierList().getIdentifierArray(0);
            term.setValue(identifier.getTerm().getValue());

            ident = idenList.addNewIdentifier();
            term = ident.addNewTerm();
            term.setDefinition(LONG_NAME_DEFINITION);
            term.setValue(platform.title);

            ident = idenList.addNewIdentifier();
            term = ident.addNewTerm();
            term.setDefinition(SHORT_NAME_DEFINITION);
            term.setValue(platform.name);

            ident = idenList.addNewIdentifier();
            term = ident.addNewTerm();
            term.setDefinition("WMO_ID");
            term.setValue(platform.wmoId);
        }

        private void buildInputOutputLists(IOOSPlatform platform, SystemType system) {
            InputList inputList = system.addNewInputs().addNewInputList();
            OutputList outputList = system.addNewOutputs().addNewOutputList();

            for (IOOSObservation obs : platform.observations) {

                String name = obs.label; // getOfferingName(configuration.getComponentCode());//
                                         // configuration.getComponentName().replace(' ',
                                         // '_').toUpperCase();

                /* inputs */
                IoComponentPropertyType ioComp = inputList.addNewInput();
                ioComp.setName(name);
                ObservableProperty obsProp = ioComp.addNewObservableProperty();
                obsProp.setDefinition(obs.dataType);

                /* outputs */
                IoComponentPropertyType output = outputList.addNewOutput();
                output.setName(name);
                Quantity quantity = output.addNewQuantity();
                quantity.setDefinition(obs.dataType);

                /* offering */
                // TODO don't know the offering without querying the SOS
                // MetaDataPropertyType metaDataProperty = quantity.addNewMetaDataProperty();
                // XmlCursor cursor = metaDataProperty.newCursor();
                // cursor.toNextToken();
                // cursor.beginElement(ELEMENT_NAME_OFFERING);
                // cursor.insertElementWithText(ELEMENT_NAME_ID, name);
                // cursor.insertElementWithText(ELEMENT_NAME_NAME, name);

                quantity.addNewDescription().setStringValue(obs.dataType);
                quantity.addNewUom().setCode("N/A");
            }
        }

        private void buildKeywords(IOOSPlatform platform, SystemType systemType) {
            KeywordList keywordList = systemType.addNewKeywords().addNewKeywordList();

            keywordList.addKeyword(platform.dataProvider.trim());
            keywordList.addKeyword(platform.program.trim());
            keywordList.addKeyword(platform.desc.trim());

            for (IOOSObservation o : platform.observations) {
                keywordList.addKeyword(o.label.trim());
            }

            for (String s : this.additionalKeywords) {
                keywordList.addKeyword(escapeCharacters(s));
            }
        }

        private void buildLatLonPosition(IOOSPlatform platform, SystemType systemType) {
            Position position = systemType.addNewPosition();
            position.setName(platform.name);

            PositionType positionType = (PositionType) position.addNewProcess().substitute(new QName(XmlTools.SWE_1_0_1_NAMESPACE_URI,
                                                                                                     "Position"),
                                                                                           PositionType.type);
            positionType.setReferenceFrame(EPSG_4326_REFERENCE_SYSTEM_DEFINITION);
            positionType.setFixed(true);
            VectorType vector = positionType.addNewLocation().addNewVector();

            /* Latitude */
            Coordinate coordLatitude = vector.addNewCoordinate();
            coordLatitude.setName(COORD_NAME_LAT);
            Quantity quantityLatitude = coordLatitude.addNewQuantity();
            quantityLatitude.setAxisID(QUANTITY_AXIS_ID_LAT);
            quantityLatitude.addNewUom().setCode(UOM_CODE_LATLON);
            quantityLatitude.setValue(platform.lat);

            /* Longitude */
            Coordinate coordLongitude = vector.addNewCoordinate();
            coordLongitude.setName(COORD_NAME_LON);
            Quantity quantityLongitude = coordLongitude.addNewQuantity();
            quantityLongitude.setAxisID(QUANTITY_AXIS_ID_LON);
            quantityLongitude.addNewUom().setCode(UOM_CODE_LATLON);
            quantityLongitude.setValue(platform.lon);

            /* Altitude */
            // Coordinate coordAltitude = vector.addNewCoordinate();
            // coordAltitude.setName(COORD_NAME_ALTITUDE);
            // Quantity quantityAltitude = coordAltitude.addNewQuantity();
            // quantityAltitude.setAxisID(QUANTITY_AXIS_ID_ALTITUDE);
            // quantityAltitude.addNewUom().setCode(UOM_CODE_ALTITUDE);
            // quantityAltitude.setValue();
        }

        // private void buildObservationTemplate(ObservationTemplate template) {
        // MeasurementType measurementType = (MeasurementType)
        // template.addNewObservation().substitute(SOSNamespaceUtils.om("Measurement"),
        // MeasurementType.type);
        // measurementType.addNewSamplingTime();
        // measurementType.addNewProcedure();
        // measurementType.addNewObservedProperty();
        // measurementType.addNewFeatureOfInterest();
        // XmlObject result = measurementType.addNewResult();
        // XmlCursor resultCursor = result.newCursor();
        // resultCursor.toNextToken();
        // resultCursor.insertAttributeWithValue(ATTRIBUTE_NAME_UOM, "");
        // resultCursor.insertChars("0.0");
        // resultCursor.dispose();
        // }

        /**
         * 
         * @param invalidDescription
         * @param currentPlatform2
         */
        private SensorMLDocument buildValidSensorDescription(IOOSPlatform platform, SensorMLDocument invalidDescription) {
            SensorMLDocument smlDocument = SensorMLDocument.Factory.newInstance();
            XmlTools.sensorMLNameSpaceOptions(smlDocument);
            SensorML sml = smlDocument.addNewSensorML();
            sml.setVersion(SMLConstants.SML_VERSION);

            SystemType systemType = (SystemType) sml.addNewMember().addNewProcess().substitute(new QName(SMLConstants.NAMESPACE,
                                                                                                         "System"),
                                                                                               SystemType.type);
            SystemType inputSystem = (SystemType) invalidDescription.getSensorML().getMemberArray(0).getProcess();
            systemType.setId(inputSystem.getId());

            log.debug("Creating a valid profile-conform description for " + systemType.getId());

            buildDescription(platform, systemType);
            buildKeywords(platform, systemType);
            buildIdentification(platform, systemType, inputSystem);
            buildClassification(platform, systemType);
            buildValidTime(platform, systemType);
            buildCapabilities(platform, systemType);
            buildContact(platform, systemType);
            buildLatLonPosition(platform, systemType);
            buildInputOutputLists(platform, systemType);
            invalidDescription.set(smlDocument);

            // System.out.println(smlDocument.xmlText(XmlTools.PRETTY_PRINT_OPTIONS));

            return smlDocument;
        }

        /**
         * @param station
         * @param systemType
         */
        private void buildValidTime(IOOSPlatform platform, SystemType systemType) {
            Date startDate = new Date(platform.startTime);
            String start = this.dateFormat.format(startDate);
            Date endDate;
            if (platform.endTime != 0)
                endDate = new Date(platform.endTime);
            else
                endDate = new Date(platform.latestTime);
            String end = this.dateFormat.format(endDate);

            // if (end.isEmpty()) {
            // // set the end data to today
            // end = Utils.REVERSE_DATE_FORMAT.print(new DateTime());
            // }

            TimePeriodType validTime = systemType.addNewValidTime().addNewTimePeriod();
            validTime.addNewBeginPosition().setStringValue(start);
            validTime.addNewEndPosition().setStringValue(end);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.xml.sax.ContentHandler#characters(char[], int, int)
         */
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            //
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.xml.sax.ContentHandler#endDocument()
         */
        @Override
        public void endDocument() throws SAXException {
            log.info("Done parsing document, contained " + this.platformCounter + " platforms.");
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
         */
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (localName.equals(OBS_ELEMENTNAME)) {
                // handle observation
                // log.debug("Unhandled obs: " + this.currentObs);
                // this.currentPlatform.observations.add(this.currentObs);
            }
            else if (localName.equals(PLATFORM_ELEMENTNAME)) {
                // log.debug("end element " + localName);

                // FIXME check if it actually is a new element!

                this.platformCounter++;
                log.debug("Platform description ended, processing platform with name '" + this.currentPlatform.name
                        + "'");

                // get sensor description/SensorML
                SensorMLDocument sensorDescription = null;
                try {
                    log.debug("Parsing SensorMLDocument from URL " + this.currentPlatform.descriptionUrl);
                    sensorDescription = SensorMLDocument.Factory.parse(this.currentPlatform.descriptionUrl);
                }
                catch (XmlException e) {
                    log.error("Could not parse sensor description from URL " + this.currentPlatform.descriptionUrl, e);
                    IOOSHarvester.this.failedSensors.put(this.currentPlatform.name, e.getMessage());
                    return;
                }
                catch (IOException e) {
                    log.error("Could not parse sensor description from URL " + this.currentPlatform.descriptionUrl, e);
                    IOOSHarvester.this.failedSensors.put(this.currentPlatform.name, e.getMessage());
                    return;
                }
                catch (NullPointerException e) {
                    log.error("NPE", e); // occurs when required elements are not found during decode()
                    IOOSHarvester.this.failedSensors.put(this.currentPlatform.name, e.getMessage());
                }

                // probably not valid, create extended sensorml document based on additional metadata in
                // currentPlatform
                if (sensorDescription == null) {
                    log.warn("Could not get sensorDescription for " + this.currentPlatform.descriptionUrl);
                }
                else {
                    String serviceURL = this.currentPlatform.descriptionUrl.toExternalForm().split("\\?")[0];
                    String serviceSpecificSensorId = this.currentPlatform.descriptionUrl.toExternalForm().split("procedure=")[1];
                    try {
                        serviceSpecificSensorId = URLDecoder.decode(serviceSpecificSensorId, "UTF-8");
                    }
                    catch (UnsupportedEncodingException e) {
                        log.warn("Could not decode sensor ID.", e);
                    }

                    // extend sensorDescription to be valid according to discovery profile
                    SensorMLDocument validDescription = buildValidSensorDescription(this.currentPlatform,
                                                                                    sensorDescription);

                    // add/save service reference
                    String serviceID;
                    try {
                        serviceID = IOOSHarvester.this.harvServDao.addService(serviceURL,
                                                                              this.currentPlatform.serviceType);

                        log.info("Added service to database with SERVICEID: " + serviceID);
                    }
                    catch (OwsExceptionReport e) {
                        log.error("Could not add service to the database.", e);
                        IOOSHarvester.this.failedSensors.put(serviceSpecificSensorId, e.getMessage());
                    }

                    try {
                        // adding to database happens here...
                        processSensorMLDocument(serviceURL,
                                                this.currentPlatform.serviceType,
                                                IOOSHarvester.this.insertedSensors,
                                                IOOSHarvester.this.updatedSensors,
                                                validDescription,
                                                serviceSpecificSensorId);
                    }
                    catch (OwsExceptionReport | IOException e) {
                        log.error("Could not process created sensor description.", e);
                        IOOSHarvester.this.failedSensors.put(serviceSpecificSensorId, e.getMessage());
                    }
                }
            }
            else
                log.debug("Unhandled element end: " + localName);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
         */
        @Override
        public void endPrefixMapping(String prefix) throws SAXException {
            //
        }

        /**
         * @param s
         * @return
         */
        private String escapeCharacters(String s) {
            String encoded = s.replace("<", "&lt;");
            encoded = encoded.replace(">", "&gt;");
            encoded = encoded.replace('\'', '_');
            return encoded;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
         */
        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            //
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
         */
        @Override
        public void processingInstruction(String target, String data) throws SAXException {
            //
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
         */
        @Override
        public void setDocumentLocator(Locator locator) {
            //
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
         */
        @Override
        public void skippedEntity(String name) throws SAXException {
            //
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.xml.sax.ContentHandler#startDocument()
         */
        @Override
        public void startDocument() throws SAXException {
            log.debug("start document");
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String,
         * org.xml.sax.Attributes)
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            // log.debug("start element " + localName);
            if (localName.equals(SOS_PLATFORMS_ELEMENTNAME)) {
                log.debug("Starting SOS plaforms...");
                this.platformCounter = 0;
            }
            else if (localName.equals(OBS_ELEMENTNAME)) {
                log.debug("Starting obs...");

                // <obs label="WaterTemperature" value="11.30 C at 2012-02-09 14:13"
                // data_type="sea_water_temperature" msse_last_time="1328796780000"/>
                String label = atts.getValue("label");
                String value = atts.getValue("value");
                String dataType = atts.getValue("data_type");
                String msseLastTime = atts.getValue("msse_last_time");
                long lastTime = Long.parseLong(msseLastTime) / 1000;

                IOOSObservation currentObs = new IOOSObservation(label, value, dataType, lastTime);
                this.currentPlatform.observations.add(currentObs);
                log.debug("Added obs " + currentObs + " to " + this.currentPlatform);
            }
            else if (localName.equals(PLATFORM_ELEMENTNAME)) {
                this.currentPlatform = null;

                // name="029" program="CENCOOS" svc_type="OGC:SOS" wmo_id="029  " lat="37.95" lng="-123.47"
                String name = atts.getValue("name");

                log.debug("Starting plaform no. {} with name {}", Integer.valueOf(this.platformCounter), name);

                String program = atts.getValue("program");
                String serviceType = atts.getValue("svc_type");
                if (serviceType.equals("OGC:SOS"))
                    serviceType = "SOS";

                String wmoId = atts.getValue("wmo_id");
                double lat = Double.parseDouble(atts.getValue("lat"));
                double lon = Double.parseDouble(atts.getValue("lng"));

                // llat="37.945599" llng="-123.46978" ulat="37.945599" ulng="-123.46978"
                double llat = Double.parseDouble(atts.getValue("llat"));
                double llng = Double.parseDouble(atts.getValue("llng"));
                double ulat = Double.parseDouble(atts.getValue("ulat"));
                double ulng = Double.parseDouble(atts.getValue("ulng"));

                // title="Central and Northern California Ocean Observing System SOS" desc="POINT REYES, CA"
                String title = atts.getValue("title");
                String description = atts.getValue("desc");

                // orgname="Central and Northern California Ocean Observing System"
                String organisationName = atts.getValue("orgname");

                // orgurl="http://www.cencoos.org/"
                URL organisationUrl = null;
                try {
                    organisationUrl = new URL(atts.getValue("orgurl"));
                }
                catch (MalformedURLException e) {
                    log.warn("Could not create organisation URL form " + atts.getValue("orgurl"), e);
                }

                // data_provider="Central and Northern California Ocean Observing System"
                String dataProvider = atts.getValue("data_provider");

                // start_time="2009-12-02 22:08:00" end_time="" latest_time="2012-02-09 14:13"
                // long startTime = Date.parse(atts.getValue("start_time"));
                // long endTime = Date.parse(atts.getValue("end_time"));
                // long latestTime = Date.parse(atts.getValue("latest_time"));

                // msse_start_time="1259791680000" msse_end_time="0" msse_latest_time="1328796780000"
                String msseStartTime = atts.getValue("msse_start_time");
                long startTime = Long.parseLong(msseStartTime);
                String msseEndTime = atts.getValue("msse_end_time");
                long endTime = Long.parseLong(msseEndTime);
                String msseLatestTime = atts.getValue("msse_latest_time");
                long latestTime = Long.parseLong(msseLatestTime);

                // descurl="http://204.115.180.244/sos/server.php?request=DescribeSensor&service=SOS&version=1.0.0&outputFormat=text%2Fxml%3Bsubtype%3D%22sensorML%2F1.0.0%22&procedure=urn%3Aioos%3Astation%3Awmo%3A029"
                URL descriptionUrl = null;
                try {
                    descriptionUrl = new URL(atts.getValue("descurl"));
                }
                catch (MalformedURLException e) {
                    log.warn("Could not create description URL form " + atts.getValue("descurl"), e);
                }

                // datatypes="sea_water_temperature,waves" recent_obs="1"
                String[] datatypes = atts.getValue("datatypes").split(",");
                String recentObs = atts.getValue("recent_obs");
                boolean hasRecentValues = false;
                if (recentObs.equals("1"))
                    hasRecentValues = true;
                // else if (recentObs.equals("0"))
                // hasRecentValues = false;

                this.currentPlatform = new IOOSPlatform(name,
                                                        program,
                                                        serviceType,
                                                        wmoId,
                                                        lat,
                                                        lon,
                                                        new SirBoundingBox(llng, llat, ulng, ulat),
                                                        title,
                                                        description,
                                                        organisationName,
                                                        organisationUrl,
                                                        dataProvider,
                                                        startTime,
                                                        endTime,
                                                        latestTime,
                                                        descriptionUrl,
                                                        datatypes,
                                                        hasRecentValues);

                log.debug("Created new IOOSPlatform: {}", this.currentPlatform);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
         */
        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            //
        }
    }

    static final Logger log = LoggerFactory.getLogger(IOOSHarvester.class);

    /**
     * 
     * @param request
     * @param harvServDao
     * @throws OwsExceptionReport
     */
    public IOOSHarvester(SirHarvestServiceRequest request, IHarvestServiceDAO harvServDao) throws OwsExceptionReport {
        super(request, harvServDao);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.listener.harvest.FileHarvester#getHandler()
     */
    @Override
    protected ContentHandler getHandler() {
        return new IOOSCatalogHandler();
    }

}
