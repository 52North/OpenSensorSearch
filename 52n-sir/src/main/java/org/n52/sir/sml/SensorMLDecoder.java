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
package org.n52.sir.sml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.CapabilitiesDocument.Capabilities;
import net.opengis.sensorML.x101.ClassificationDocument.Classification;
import net.opengis.sensorML.x101.ClassificationDocument.Classification.ClassifierList.Classifier;
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
import net.opengis.sensorML.x101.TermDocument.Term;
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

	private static final ArrayList<String> X_AXIS_IDENTIFIERS = new ArrayList<String>(
			Arrays.asList(new String[] { "x", "easting" }));

	private static final ArrayList<String> Y_AXIS_IDENTIFIERS = new ArrayList<String>(
			Arrays.asList(new String[] { "y", "northing" }));

	/**
	 * @param classifications
	 * @return
	 */
	private static String createClassificationString(
			Classification[] classifications) {
		StringBuilder sbClass = new StringBuilder();
		sbClass.append("Classifications: ");
		for (Classification classification : classifications) {
			Classifier[] classifiers = classification.getClassifierList()
					.getClassifierArray();
			for (Classifier classifier : classifiers) {
				// sbClass.append(classifier.getName());
				// sbClass.append(" - ");
				sbClass.append(Tools.simplifyString(classifier.getTerm()
						.getValue()));
				sbClass.append(", ");
			}
		}
		sbClass.replace(sbClass.length() - 2, sbClass.length(), ""); // remove
																		// last
																		// space
		sbClass.append(";");
		return sbClass.toString();
	}

	/**
	 * 
	 * @param identifications
	 * @return
	 */
	private static String createIdentifierString(
			Identification[] identifications) {
		StringBuilder sbIdent = new StringBuilder();
		sbIdent.append("Identifications: ");
		for (Identification identification : identifications) {
			Identifier[] identifiers = identification.getIdentifierList()
					.getIdentifierArray();
			for (Identifier identifier : identifiers) {
				Term term = identifier.getTerm();
				String[] s = term.getDefinition().split(":");
				sbIdent.append(s[s.length - 1]);
				sbIdent.append(": ");
				sbIdent.append(Tools.simplifyString(term.getValue()));
				sbIdent.append(", ");
			}
		}
		sbIdent.replace(sbIdent.length() - 2, sbIdent.length(), "");
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
		sbKeywords.replace(sbKeywords.length() - 2, sbKeywords.length(), "");
		sbKeywords.append(";");
		return sbKeywords.toString();
	}

	/**
	 * 
	 * @param sensorML
	 * @return
	 * @throws OwsExceptionReport
	 */
	public static SirSensor decode(SensorMLDocument sensorML)
			throws OwsExceptionReport {
		SirSensor sensor = new SirSensor();

		// TODO check how this creates the identification part...

		sensor.setSensorMLDocument(sensorML);
		sensor.setbBox(getBoundingBox(sensorML));
		sensor.setPhenomenon(getPhenomenona(sensorML));
		sensor.setTimePeriod(getTimePeriod(sensorML));
		sensor.setText(getText(sensorML));
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

	private static Collection<String> getOutputs(SensorMLDocument sensorML) {
		List<String> output_results = new ArrayList<String>();
		if (sensorML.getSensorML().getMemberArray().length != 0) {
			SystemType type = (SystemType) sensorML.getSensorML()
					.getMemberArray()[0].getProcess();
			Outputs inputs = type.getOutputs();
			OutputList list = inputs.getOutputList();
			IoComponentPropertyType[] outputsarr = list.getOutputArray();
			for (int i = 0; i < outputsarr.length; i++) {
				StringBuilder builder = new StringBuilder();
				builder.append(outputsarr[i].getName());
				builder.append(" ");
				if (outputsarr[i].getQuantity() != null)
					builder.append(outputsarr[i].getQuantity().getUom()
							.getCode().toString());
				output_results.add(builder.toString());
			}
		}
		return output_results;

	}

	private static Collection<String> getInputs(SensorMLDocument sensorML) {
		List<String> inputs_results = new ArrayList<String>();
		if (sensorML.getSensorML().getMemberArray().length != 0) {
			SystemType type = (SystemType) sensorML.getSensorML()
					.getMemberArray()[0].getProcess();
			Inputs inputs = type.getInputs();
			InputList list = inputs.getInputList();
			IoComponentPropertyType[] inputsarr = list.getInputArray();
			for (int i = 0; i < inputsarr.length; i++) {
				StringBuilder builder = new StringBuilder();
				builder.append(inputsarr[i].getName());
				builder.append(" ");
				if (inputsarr[i].getQuantity() != null)
					builder.append(inputsarr[i].getQuantity().getUom()
							.getCode().toString());
				inputs_results.add(builder.toString());
			}
		}
		return inputs_results;

	}

	private static Collection<String> getInterfaces(SensorMLDocument sensorML) {
		List<String> interfaces_result = new ArrayList<String>();
		if (sensorML.getSensorML().getMemberArray().length != 0) {
			SystemType type = (SystemType) sensorML.getSensorML()
					.getMemberArray()[0].getProcess();
			Interfaces interfaces = type.getInterfaces();
			if (interfaces == null)
				return null;
			InterfaceList list = interfaces.getInterfaceList();
			if (list == null)
				return null;
			Interface[] interfacearr = list.getInterfaceArray();
			for (int i = 0; i < interfacearr.length; i++) {
				DataRecordType t = (DataRecordType) (interfacearr[i]
						.getInterfaceDefinition().getServiceLayer()
						.getAbstractDataRecord());
				DataComponentPropertyType fields[] = t.getFieldArray();
				for (int j = 0; j < fields.length; j++)
					interfaces_result.add(fields[j].getText().getValue()
							.toString());
			}
		}
		return interfaces_result;
	}

	private static Collection<String> getContacts(SensorMLDocument sensorML) {
		List<String> contacts = new ArrayList<String>();
		if (sensorML.getSensorML().getMemberArray().length != 0) {
			SystemType type = (SystemType) sensorML.getSensorML()
					.getMemberArray()[0].getProcess();
			contacts.addAll(getContacts(type));
		}

		return contacts;
	}

	private static Collection<Object> getIdentificationList(
			SensorMLDocument sensorML) {
		List<Object> identifications = new ArrayList<Object>();
		if (sensorML.getSensorML().getMemberArray().length != 0) {
			SystemType type = (SystemType) sensorML.getSensorML()
					.getMemberArray(0).getProcess();
			identifications.addAll(getIdentificationList(type));
		}

		return identifications;

	}

	private static Object getClassificationList(SensorMLDocument sensorML) {
		List<String> classifications = new ArrayList<String>();
		if (sensorML.getSensorML().getMemberArray().length != 0) {
			SystemType type = (SystemType) sensorML.getSensorML()
					.getMemberArray(0).getProcess();
			return getClassificationList(type);
		}
		return classifications;
	}

	private static Object getDescription(SensorMLDocument sensorML) {
		if (sensorML.getSensorML().getMemberArray().length == 0)
			return "";
		SystemType type = (SystemType) sensorML.getSensorML().getMemberArray(0)
				.getProcess();
		return getDescription(type);
	}

	private static String getLatitude(SensorMLDocument sensorML) {
		if (sensorML.getSensorML().getMemberArray().length == 0)
			return "";
		SystemType type = (SystemType) sensorML.getSensorML().getMemberArray(0)
				.getProcess();
		if (type.getPosition() == null)
			return "";
		return getLatitude(type);
	}

	private static String getLongitude(SensorMLDocument sensorML) {
		if (sensorML.getSensorML().getMemberArray().length == 0)
			return "";
		SystemType type = (SystemType) sensorML.getSensorML().getMemberArray(0)
				.getProcess();
		if (type.getPosition() == null)
			return "";
		return getLongitude(type);
	}

	private static Collection<String> getKeywords(SensorMLDocument sensorML) {
		return getKeywords(sensorML.getSensorML());
	}

	private static Collection<String> getKeywords(SensorML sensorML) {
		ArrayList<String> keywords = new ArrayList<String>();

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

	private static Collection<String> getKeywords(SystemType system) {
		ArrayList<String> keywordList = new ArrayList<String>();

		Keywords[] keywords = system.getKeywordsArray();
		for (Keywords k : keywords) {
			String[] kwArray = k.getKeywordList().getKeywordArray();

			for (String currentKeyword : kwArray) {
				keywordList.add(currentKeyword);
			}
		}

		return keywordList;
	}

	/**
	 * 
	 * decodes the given sensor description and also adds the given
	 * identification to the returned SirSensor instance if possible.
	 * 
	 * @param sensorIdent
	 * @param sensorDescription
	 * @return
	 * @throws OwsExceptionReport
	 */
	public static SirSensor decode(SirSensorIdentification sensorIdent,
			XmlObject sensorDescription) throws OwsExceptionReport {
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

		sensor.setSensorMLDocument(Tools
				.wrapSystemTypeInSensorMLDocument(system));
		sensor.setbBox(getBoundingBox(system));
		sensor.setPhenomenon(getPhenomenon(system));
		sensor.setTimePeriod(getTimePeriod(system));
		sensor.setText(getText(system));
		sensor.setLongitude(getLongitude(system));
		sensor.setDescription(getDescription(system));
		sensor.setClassificationList(getClassificationList(system));
		sensor.setIdentificationsList(getIdentificationList(system));
		sensor.setContacts(getContacts(system));
		sensor.setKeywords(getKeywords(system));

		//sensor.setInterfaces(getInterfaces(system));
		//sensor.setInputs(getInputs(system));
		//sensor.setOutputs(getOutputs(system));

		return sensor;
	}

	private static Collection<String> getContacts(SystemType system) {
		Collection<String> contacts = new ArrayList<String>();
		Contact[] contactsarr = system.getContactArray();
		for (Contact m : contactsarr) {
			if (m.getPerson() != null) {
				Person p = m.getPerson();
				System.out.println(p);
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

	private static Collection<Object> getIdentificationList(SystemType system) {
		Collection<Object> identifications = new ArrayList<Object>();
		Identification[] ids = system.getIdentificationArray();
		for (Identification id : ids) {
			Identifier[] iden = id.getIdentifierList().getIdentifierArray();
			for (int i = 0; i < iden.length; i++)
				identifications.add(iden[i].getTerm().getValue());
		}
		return identifications;

	}

	private static Object getClassificationList(SystemType system) {
		List<String> classifications = new ArrayList<String>();
		Classification cs[] = system.getClassificationArray();
		for (Classification c : cs)
			classifications.add(c.getClassifierList().getId());
		return classifications;

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

	private static Object getDescription(SystemType system) {
		// TODO Auto-generated method stub
		return system.getDescription().getStringValue();
	}

	/**
	 * 
	 * @param sensorML
	 * @return
	 * @throws OwsExceptionReport
	 */
	public static SirSensor decode(XmlObject sensorML)
			throws OwsExceptionReport {
		// check if sensoML is a standalone sensor description or a
		// sir:sensorDescription
		if (sensorML instanceof SystemType) {
			SystemType system = (SystemType) sensorML;
			return decode(system);
		} else if (sensorML instanceof SensorMLDocument) {
			SensorMLDocument smlDoc = (SensorMLDocument) sensorML;
			return decode(smlDoc);
		} else if (sensorML instanceof AbstractProcessType) {
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
			} catch (XmlException e) {
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
			sensorMLDocument = SensorMLDocument.Factory.parse(sensorML
					.toString());
		} catch (XmlException e) {
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
		SirBoundingBox bb = new SirBoundingBox(Double.MAX_VALUE,
				Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);

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
			AbstractDataRecordType abstractDataRecord = capabilities
					.getAbstractDataRecord();
			if (abstractDataRecord instanceof DataRecordType) {
				DataRecordType dataRecord = (DataRecordType) abstractDataRecord;
				DataComponentPropertyType[] fieldArray = dataRecord
						.getFieldArray();

				for (DataComponentPropertyType dataComponent : fieldArray) {
					AbstractDataRecordType dataRec = dataComponent
							.getAbstractDataRecord();
					if (dataRec != null) {
						if (log.isDebugEnabled())
							log.debug("A data record, check if bbox!");

						String currentDefinition = dataRec.getDefinition();
						if (currentDefinition
								.equals(BOUNDING_BOX_FIELD_DEFINITION)) {
							if (dataRec instanceof EnvelopeType) {

								if (log.isDebugEnabled())
									log.debug("Found bbox!");
								EnvelopeType envelope = (EnvelopeType) dataRec;
								Coordinate[] lowerCornerCoords = envelope
										.getLowerCorner().getVector()
										.getCoordinateArray();
								Coordinate[] upperCornerCoords = envelope
										.getUpperCorner().getVector()
										.getCoordinateArray();

								double[] lowerCoords = getXYCoords(lowerCornerCoords);
								double[] upperCoords = getXYCoords(upperCornerCoords);
								double east = lowerCoords[0];
								double south = lowerCoords[1];
								double north = upperCoords[1];
								double west = upperCoords[0];
								return new SirBoundingBox(east, south, west,
										north);
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
			} else if (output.isSetAbstractDataArray1()) {
				if (output.getAbstractDataArray1().isSetDefinition()) {
					phenom.setUrn(output.getAbstractDataArray1()
							.getDefinition());
					phenomenona.add(phenom);
				} else {
					log.warn("Could not handle output phenomenon data array, definition missing.");
				}
			} else {
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
	private static ArrayList<SirPhenomenon> getPhenomenona(
			SensorMLDocument sensDoc) {
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
		if (!sIdent.isEmpty())
			texts.add(sIdent);

		// add classification to text field
		Classification[] classifications = system.getClassificationArray();
		String sClass = createClassificationString(classifications);
		if (!sClass.isEmpty())
			texts.add(sClass);

		// add keywords to text field
		Keywords[] keywords = system.getKeywordsArray();
		String sKeywords = createKeywordsString(keywords);
		if (!sKeywords.isEmpty())
			texts.add(sKeywords);

		return texts;
	}

	/**
	 * 
	 * @param sensDoc
	 * @return
	 * @throws OwsExceptionReport
	 */
	private static SirTimePeriod getTimePeriod(SensorMLDocument sensDoc)
			throws OwsExceptionReport {
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
	private static SirTimePeriod getTimePeriod(SystemType system)
			throws OwsExceptionReport {
		SirTimePeriod sirTimePeriod = new SirTimePeriod();

		if (system.isSetValidTime()) {
			ValidTime validTime = system.getValidTime();

			String startString = null;
			if (validTime.getTimePeriod().getBeginPosition() != null) {
				startString = validTime.getTimePeriod().getBeginPosition()
						.getStringValue();
			} else {
				startString = validTime.getTimePeriod().getBegin()
						.getTimeInstant().getTimePosition().getStringValue();
			}

			String endString = null;
			if (validTime.getTimePeriod().getEndPosition() != null) {
				endString = validTime.getTimePeriod().getEndPosition()
						.getStringValue();
			} else {
				endString = validTime.getTimePeriod().getEnd().getTimeInstant()
						.getTimePosition().getStringValue();
			}

			try {
				sirTimePeriod.setStartTime(sdf.parse(startString));
			} catch (ParseException e) {
				log.error(
						"Error parsing gml:beginPosition in valid time element.",
						e);
				OwsExceptionReport se = new OwsExceptionReport();
				se.addCodedException(
						ExceptionCode.NoApplicableCode,
						"SensorMLDecoder.decode",
						"The start time is missing or cannot be parsed in the timePeriod element of valid time section!");
				throw se;
			}

			try {
				sirTimePeriod.setEndTime(sdf.parse(endString));
			} catch (ParseException e) {
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

			if (X_AXIS_IDENTIFIERS.contains(name)
					|| X_AXIS_IDENTIFIERS.contains(id)) {
				xy[0] = coordinate.getQuantity().getValue();
			} else if (Y_AXIS_IDENTIFIERS.contains(name)
					|| Y_AXIS_IDENTIFIERS.contains(id)) {
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
