/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.sir.sml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.CapabilitiesDocument.Capabilities;
import net.opengis.sensorML.x101.ClassificationDocument.Classification;
import net.opengis.sensorML.x101.ClassificationDocument.Classification.ClassifierList.Classifier;
import net.opengis.sensorML.x101.IdentificationDocument.Identification;
import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList.Identifier;
import net.opengis.sensorML.x101.IoComponentPropertyType;
import net.opengis.sensorML.x101.KeywordsDocument.Keywords;
import net.opengis.sensorML.x101.OutputsDocument.Outputs;
import net.opengis.sensorML.x101.OutputsDocument.Outputs.OutputList;
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
import net.opengis.swe.x101.VectorType.Coordinate;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sir.datastructure.SirBoundingBox;
import org.n52.sir.datastructure.SirPhenomenon;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.SirSensorIDInSir;
import org.n52.sir.datastructure.SirSensorIdentification;
import org.n52.sir.datastructure.SirTimePeriod;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.util.Tools;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class SensorMLDecoder {

    private static final Object BOUNDING_BOX_FIELD_DEFINITION = "urn:ogc:def:property:OGC:1.0:observedBBOX";

    private static Logger log = LoggerFactory.getLogger(SensorMLDecoder.class);

    // dateformater for ISO 8601 Date format
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private static final ArrayList<String> X_AXIS_IDENTIFIERS = new ArrayList<String>(Arrays.asList(new String[] {"x",
                                                                                                                  "easting"}));

    private static final ArrayList<String> Y_AXIS_IDENTIFIERS = new ArrayList<String>(Arrays.asList(new String[] {"y",
                                                                                                                  "northing"}));

    /**
     * @param classifications
     * @return
     */
    private static String createClassificationString(Classification[] classifications) {
        StringBuilder sbClass = new StringBuilder();
        sbClass.append("Classifications: ");
        for (Classification classification : classifications) {
            Classifier[] classifiers = classification.getClassifierList().getClassifierArray();
            for (Classifier classifier : classifiers) {
                // sbClass.append(classifier.getName());
                // sbClass.append(" - ");
                sbClass.append(Tools.simplifyString(classifier.getTerm().getValue()));
                sbClass.append(", ");
            }
        }
        sbClass.replace(sbClass.length() - 2, sbClass.length(), ""); // remove last space
        sbClass.append(";");
        return sbClass.toString();
    }

    /**
     * 
     * @param identifications
     * @return
     */
    private static String createIdentifierString(Identification[] identifications) {
        StringBuilder sbIdent = new StringBuilder();
        sbIdent.append("Identifications: ");
        for (Identification identification : identifications) {
            Identifier[] identifiers = identification.getIdentifierList().getIdentifierArray();
            for (Identifier identifier : identifiers) {
                Term term = identifier.getTerm();
                String[] s = term.getDefinition().split(":");
                sbIdent.append(s[s.length - 1]);
                sbIdent.append(": ");
                sbIdent.append(Tools.simplifyString(term.getValue()));
                sbIdent.append(", ");
            }
        }
        sbIdent.replace(sbIdent.length() - 2, sbIdent.length(), ""); // remove last space and comma
        sbIdent.append(";");
        return sbIdent.toString();
    }

    /**
     * 
     * @param keywords
     * @return
     */
    private static String createKeywordsString(Keywords[] keywords) {
        StringBuilder sbKeywords = new StringBuilder();
        sbKeywords.append("Keywords: ");
        for (Keywords keyword : keywords) {
            String[] kwArray = keyword.getKeywordList().getKeywordArray();
            for (String currentKeyword : kwArray) {
                sbKeywords.append(Tools.simplifyString(currentKeyword));
                sbKeywords.append(", ");
            }
        }
        sbKeywords.replace(sbKeywords.length() - 2, sbKeywords.length(), ""); // remove last space
        sbKeywords.append(";");
        return sbKeywords.toString();
    }

    /**
     * 
     * @param sensorML
     * @return
     * @throws OwsExceptionReport
     */
    public static SirSensor decode(SensorMLDocument sensorML) throws OwsExceptionReport {
        SirSensor sensor = new SirSensor();

        // set sensorML file
        sensor.setSensorMLDocument(sensorML);

        // set bounding box
        sensor.setbBox(getBoundingBox(sensorML));

        // set phenomenon
        sensor.setPhenomenon(getPhenomenona(sensorML));

        // set time period
        sensor.setTimePeriod(getTimePeriod(sensorML));

        // set text
        sensor.setText(getText(sensorML));

        return sensor;
    }

    /**
     * 
     * decodes the given sensor description and also adds the given identification to the returned SirSensor
     * instance if possible.
     * 
     * @param sensorIdent
     * @param sensorDescription
     * @return
     * @throws OwsExceptionReport
     */
    public static SirSensor decode(SirSensorIdentification sensorIdent, XmlObject sensorDescription) throws OwsExceptionReport {
        SirSensor sensor = decode(sensorDescription);

        if (sensorIdent instanceof SirSensorIDInSir) {
            SirSensorIDInSir sid = (SirSensorIDInSir) sensorIdent;
            sensor.setSensorIDInSIR(sid.getSensorIdInSir());
        }

        return sensor;
    }

    /**
     * 
     * @param system
     * @return
     * @throws OwsExceptionReport
     */
    public static SirSensor decode(SystemType system) throws OwsExceptionReport {
        SirSensor sensor = new SirSensor();

        if (log.isDebugEnabled()) {
            log.debug("Decoding SystemType:");
            String errors = XmlTools.validateAndIterateErrors(system);
            log.debug(errors);
        }

        // set sensorML file
        sensor.setSensorMLDocument(Tools.wrapSystemTypeInSensorMLDocument(system));

        // set bounding box
        sensor.setbBox(getBoundingBox(system));

        // set phenomenon
        sensor.setPhenomenon(getPhenomenon(system));

        // set time period
        sensor.setTimePeriod(getTimePeriod(system));

        // set text
        sensor.setText(getText(system));

        return sensor;
    }

    /**
     * 
     * @param sensorML
     * @return
     * @throws OwsExceptionReport
     */
    public static SirSensor decode(XmlObject sensorML) throws OwsExceptionReport {
        // check if sensoML is a standalone sensor description or a sir:sensorDescription
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

            // abstractProcess.changeType(SystemType.type) creates another xml-fragment around
            // sir:sensorDescription, whereas abstractProcess.substitute(new
            // QName(XmlTools.SML_1_0_1_NAMESPACE_URI, "System"), SystemType.type does return another
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

    /**
     * 
     * @param sensDoc
     * @return
     */
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

    /**
     * 
     * @param system
     * @return
     */
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
                        if (log.isDebugEnabled())
                            log.debug("A data record, check if bbox!");

                        String currentDefinition = dataRec.getDefinition();
                        if (currentDefinition.equals(BOUNDING_BOX_FIELD_DEFINITION)) {
                            if (dataRec instanceof EnvelopeType) {

                                if (log.isDebugEnabled())
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

    /**
     * 
     * @param system
     * @return
     */
    private static ArrayList<SirPhenomenon> getPhenomenon(SystemType system) {
        ArrayList<SirPhenomenon> phenomenona = new ArrayList<SirPhenomenon>();

        Outputs outputs = system.getOutputs();
        OutputList outputList = outputs.getOutputList();
        IoComponentPropertyType[] outputArray = outputList.getOutputArray();
        for (IoComponentPropertyType output : outputArray) {
            SirPhenomenon phenom = new SirPhenomenon();

            if (output.isSetQuantity()) {
                // urn
                phenom.setUrn(output.getQuantity().getDefinition());
                // uom
                phenom.setUom(output.getQuantity().getUom().getCode());
                phenomenona.add(phenom);
            }
            else if (output.isSetAbstractDataArray1()) {
                if (output.getAbstractDataArray1().isSetDefinition()) {
                    phenom.setUrn(output.getAbstractDataArray1().getDefinition());
                    phenomenona.add(phenom);
                }
                else {
                    log.warn("Could not handle output phenomenon data array, definition missing.");
                }
            }
            else {
                log.warn("Could not handle output phenomenon: " + output);
            }
        }

        return phenomenona;
    }

    /**
     * 
     * @param sensDoc
     * @return
     */
    private static ArrayList<SirPhenomenon> getPhenomenona(SensorMLDocument sensDoc) {
        ArrayList<SirPhenomenon> phenomenona = new ArrayList<SirPhenomenon>();

        Member[] members = sensDoc.getSensorML().getMemberArray();
        for (Member member : members) {
            SystemType systemType = (SystemType) member.getProcess();
            ArrayList<SirPhenomenon> currentPhenomenona = getPhenomenon(systemType);

            phenomenona.addAll(currentPhenomenona);
        }
        return phenomenona;
    }

    /**
     * discovery profile expects one {@link SystemType} as a {@link Member}.
     * 
     * @param sensorML
     * @return
     */
    private static Collection<String> getText(SensorML sensorML) {
        Collection<String> texts = new ArrayList<String>();

        Member[] members = sensorML.getMemberArray();

        Member member = members[0];
        if (member.getProcess() instanceof SystemType) {
            SystemType system = (SystemType) member.getProcess();
            return getText(system);
        }
        log.warn("Could not get text from given document. It is required to contain one member which is a SystemType.");

        return texts;
    }

    /**
     * 
     * @param sensDoc
     * @return
     */
    private static Collection<String> getText(SensorMLDocument sensDoc) {
        return getText(sensDoc.getSensorML());
    }

    /**
     * 
     * method handles the extraction of string descriptions
     * 
     * @param system
     * @return
     */
    private static Collection<String> getText(SystemType system) {
        Collection<String> texts = new ArrayList<String>();

        // add identification to text field
        Identification[] identifications = system.getIdentificationArray();
        String sIdent = createIdentifierString(identifications);
        if ( !sIdent.isEmpty())
            texts.add(sIdent);
        
        // add classification to text field
        Classification[] classifications = system.getClassificationArray();
        String sClass = createClassificationString(classifications);
        if ( !sClass.isEmpty())
            texts.add(sClass);

        // add keywords to text field
        Keywords[] keywords = system.getKeywordsArray();
        String sKeywords = createKeywordsString(keywords);
        if ( !sKeywords.isEmpty())
            texts.add(sKeywords);

        return texts;
    }

    /**
     * 
     * @param sensDoc
     * @return
     * @throws OwsExceptionReport
     */
    private static SirTimePeriod getTimePeriod(SensorMLDocument sensDoc) throws OwsExceptionReport {
        SirTimePeriod sirTimePeriod = null;
        Member[] members = sensDoc.getSensorML().getMemberArray();
        for (Member member : members) {
            SystemType systemType = (SystemType) member.getProcess();
            if (sirTimePeriod == null)
                sirTimePeriod = getTimePeriod(systemType);
            else {
                SirTimePeriod stp = getTimePeriod(systemType);
                sirTimePeriod.union(stp);
            }
        }
        return sirTimePeriod;
    }

    /**
     * 
     * @param system
     * @return
     * @throws OwsExceptionReport
     */
    private static SirTimePeriod getTimePeriod(SystemType system) throws OwsExceptionReport {
        SirTimePeriod sirTimePeriod = new SirTimePeriod();

        if (system.isSetValidTime()) {
            ValidTime validTime = system.getValidTime();

            String startString = null;
            if (validTime.getTimePeriod().getBeginPosition() != null) {
                startString = validTime.getTimePeriod().getBeginPosition().getStringValue();
            }
            else {
                startString = validTime.getTimePeriod().getBegin().getTimeInstant().getTimePosition().getStringValue();
            }

            String endString = null;
            if (validTime.getTimePeriod().getEndPosition() != null) {
                endString = validTime.getTimePeriod().getEndPosition().getStringValue();
            }
            else {
                endString = validTime.getTimePeriod().getEnd().getTimeInstant().getTimePosition().getStringValue();
            }

            try {
                sirTimePeriod.setStartTime(sdf.parse(startString));
            }
            catch (ParseException e) {
                log.error("Error parsing gml:beginPosition in valid time element.", e);
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.NoApplicableCode,
                                     "SensorMLDecoder.decode",
                                     "The start time is missing or cannot be parsed in the timePeriod element of valid time section!");
                throw se;
            }

            try {
                sirTimePeriod.setEndTime(sdf.parse(endString));
            }
            catch (ParseException e) {
                log.error("The start time is missing or cannot be parsed in the timePeriod element of valid time section!");
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.NoApplicableCode,
                                     "SensorMLDecoder.decode",
                                     "The end time is missing in the timePeriod element of valid time parameter!");
                throw se;
            }
        }

        return sirTimePeriod;
    }

    /**
     * 
     * @param coordinates
     * @return
     */
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

    /**
     * 
     */
    private SensorMLDecoder() {
        //
    }

}
