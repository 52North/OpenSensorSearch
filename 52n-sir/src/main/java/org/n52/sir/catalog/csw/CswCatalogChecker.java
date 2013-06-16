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
package org.n52.sir.catalog.csw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

import net.opengis.cat.csw.x202.CapabilitiesType;
import net.opengis.cat.csw.x202.ElementSetNameType;
import net.opengis.cat.csw.x202.GetCapabilitiesDocument;
import net.opengis.cat.csw.x202.GetCapabilitiesType;
import net.opengis.cat.csw.x202.GetRecordByIdDocument;
import net.opengis.cat.csw.x202.GetRecordByIdResponseDocument;
import net.opengis.cat.csw.x202.GetRecordByIdResponseType;
import net.opengis.cat.csw.x202.GetRecordByIdType;
import net.opengis.cat.csw.x202.InsertType;
import net.opengis.cat.csw.x202.TransactionDocument;
import net.opengis.cat.csw.x202.TransactionResponseDocument;
import net.opengis.cat.csw.x202.TransactionResponseType;
import net.opengis.cat.csw.x202.TransactionSummaryType;
import net.opengis.cat.csw.x202.TransactionType;
import net.opengis.cat.wrs.x10.CapabilitiesDocument;
import net.opengis.ows.AcceptVersionsType;
import net.opengis.ows.DCPDocument.DCP;
import net.opengis.ows.DomainType;
import net.opengis.ows.OperationDocument.Operation;
import net.opengis.ows.RequestMethodType;
import net.opengis.ows.ServiceIdentificationDocument.ServiceIdentification;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.util.Pair;
import org.n52.sir.util.SoapTools;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import x0.oasisNamesTcEbxmlRegrepXsdRim3.ClassificationNodeType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.ClassificationSchemeDocument;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.ClassificationSchemeType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.ExtrinsicObjectType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.IdentifiableType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryObjectListDocument;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.SlotDocument;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.SlotType1;

/**
 * 
 * Class encapsulates the checking of a catalogue's capabilities and the presence of required elements like
 * slots, classification schemes and classification nodes. Apart from checking it can also try to update the
 * catalogue service, i.e. insert the slots and classification elements via insert transactions. The latter is
 * implemented for Buddata ebXML Registry/Repository (see http://code.google.com/p/buddata-ebxml-registry/).
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class CswCatalogChecker {

    private static Collection<String> acceptedServiceParameters;

    private static Collection<String> acceptedServiceTypes;

    private static Collection<String> acceptedServiceTypeVersions;

    private static Collection<String> acceptedVersionParameters;

    private static List<XmlObject> classificationInitDocs;

    private static final Object EBRIM_NAMESPACE_URI = "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0";

    private static final String GET_RECORDS_REQUEST_OUTPUTFORMAT = "application/xml";

    private static final String GET_RECORDS_REQUEST_SERVICE = "CSW";

    private static final String GET_RECORDS_REQUEST_VERSION = "2.0.2";

    private static final String INSERT_HANDLE_ADD_CLASSIFICATIONS = "InsertClassificationScheme";

    private static Logger log = LoggerFactory.getLogger(CswCatalogChecker.class);

    private static final String POST_ENCODING_CONSTRAINT_NAME = "PostEncoding";

    private static Map<String, String> requiredSlots = new HashMap<String, String>();

    private static String requiredSlotsDocumentId;

    private static boolean saveClassificationsCheckOnRuntime = true;

    private static final String SERVICE_PARAMETER_NAME = "service";

    private static final String SLOT_DESCRIPTION_INSERTION_EO_ID = "urn:sir:id:SlotDefinition";

    private static final Object SLOT_DESRCRIPTION_INSERTION_EO_TYPE = "urn:kzen:ObjectType:SlotDescription";

    private static XmlObject slotInitDoc;

    private static final String SOAP_POST_ENCODING_VALUE = "SOAP";

    private static final String TRANSACTION_OPERATION_NAME = "Transaction";

    private static final String VERSION_PARAMETER_NAME = "version";

    static {
        acceptedServiceTypes = new HashSet<String>();
        acceptedServiceTypes.add("urn:ogc:serviceType:CatalogueService:2.0.2:HTPP:ebRIM");
        acceptedServiceTypes.add("urn:ogc:service:catalogue:csw-ebrim");

        acceptedServiceParameters = new HashSet<String>();
        acceptedServiceParameters.add("urn:x-ogc:specification:csw-ebrim:Service:OGC-CSW:ebRIM");

        acceptedServiceTypeVersions = new HashSet<String>();
        acceptedServiceTypeVersions.add("1.0.0");

        acceptedVersionParameters = new HashSet<String>();
        acceptedVersionParameters.add("2.0.2");
    }

    private SimpleSoapCswClient client;

    /**
     * 
     * @param clientP
     * @param classificationInitDocs
     * @param slotInitDocP
     */
    public CswCatalogChecker(SimpleSoapCswClient clientP,
                             List<XmlObject> classificationInitDocsP,
                             XmlObject slotInitDocP) {
        this.client = clientP;
        classificationInitDocs = classificationInitDocsP;
        slotInitDoc = slotInitDocP;
    }

    /**
     * 
     * Recursive method that goes through all given {@link ClassificationNodeType}s based ONLY on the ids.
     * 
     * @param foundIds
     * @param cNodes
     * @param currentRequiredClassificationNodes
     */
    private void addFoundNodes(Collection<String> foundIds,
                               ClassificationNodeType[] cNodes,
                               Map<String, Pair<String, String>> currentRequiredClassificationNodes) {
        for (ClassificationNodeType cNode : cNodes) {
            foundIds.add(cNode.getId());

            if (log.isDebugEnabled())
                log.debug("Added found id: " + cNode.getId());

            ClassificationNodeType[] ccNodes = cNode.getClassificationNodeArray();
            addFoundNodes(foundIds, ccNodes, currentRequiredClassificationNodes);
        }
    }

    /**
     * 
     * Recursive method that goes through all given {@link ClassificationNodeType}s also inspecting if the
     * correct parents are set.
     * 
     * @param foundIds
     * @param cNodes
     * @param currentParent
     * @param requiredClassificationNodes
     */
    @SuppressWarnings("unused")
    private void addFoundNodes(Collection<String> foundIds,
                               ClassificationNodeType[] cNodes,
                               String currentParent,
                               Map<String, Pair<String, String>> currentRequiredClassificationNodes) {
        for (ClassificationNodeType cNode : cNodes) {
            Pair<String, String> parentAndCode = currentRequiredClassificationNodes.get(cNode.getId());
            if (parentAndCode != null && parentAndCode.getFirst().equals(currentParent)) {
                if (parentAndCode.getSecond() != null && parentAndCode.getSecond().equals(cNode.getCode())) {
                    foundIds.add(cNode.getId());

                    if (log.isDebugEnabled())
                        log.debug("Found node: " + XmlTools.inspect(cNode));
                }
                // check for child nodes
                ClassificationNodeType[] ccNodes = cNode.getClassificationNodeArray();
                addFoundNodes(foundIds, ccNodes, cNode.getId(), currentRequiredClassificationNodes);
            }
            else {
                if (log.isDebugEnabled())
                    log.debug("Found node that is NOT required (due to current scheme document!): "
                            + XmlTools.inspect(cNode));
            }
        }
    }

    /**
     * 
     * Check for all required slots, classifications etc. and inserts them if neccessary. After the insertion,
     * another check is run and the result of that check is returned. After a successful check the catalog is
     * updated so that it is not checked again during runtime.
     * 
     * @param c
     * @throws OwsExceptionReport
     */
    public boolean checkAndUpdateClient(SimpleSoapCswClient c) throws OwsExceptionReport {
        if (this.client.isDoNotCheck()) {
            log.info("This client was already successfully checked during runtime or is not suppossed to be checked - not checking (again)!");
            return true;
        }

        log.info("Checking if capabilities are sufficient, and if classification schemes, classification nodes and needed slots are present in the catalog @ "
                + c + " AND trying to update the catalog if neccessary.");

        boolean hasCaps = isCapabilitiesSufficient();
        boolean hasClassification = checkForClassificationSchemes(c);
        boolean hasSlots = checkForSlots(c);

        if (log.isDebugEnabled()) {
            if ( !hasCaps)
                log.debug("Capabilities not sufficient due to CapabilitiesDocument!");
            if ( !hasClassification)
                log.debug("ClassificationSchemes not sufficient!");
            if ( !hasSlots)
                log.debug("Slots not sufficient!");
        }

        if ( !hasSlots) {
            sendInsertTransaction(c, slotInitDoc, requiredSlots.size());
            hasSlots = checkForSlots(c);

            if ( !hasSlots)
                log.warn("Insterted required Slots, but still not sufficient!");
        }
        if ( !hasClassification) {
            for (XmlObject xmlObj : classificationInitDocs) {
                int nClassificationSchemes = getClassificationSchemeCount(xmlObj);
                sendInsertTransaction(c, xmlObj, nClassificationSchemes);
            }
            hasClassification = checkForClassificationSchemes(c);

            if ( !hasClassification)
                log.warn("Insterted classification initialization docs, but still not sufficient!");
        }

        if (hasCaps && hasClassification && hasSlots) {
            // all good.
            log.info("Catalog has sufficient capabilities: " + c);
            if (saveClassificationsCheckOnRuntime) {
                this.client.setDoNotCheck(true);
                if (log.isDebugEnabled()) {
                    log.debug("Changed checking status for client, will not check this catalog anymore: " + c);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 
     * Method loads the XML document for classification initialization and checks whether the service provides
     * all required classification schemes and nodes that are needed by default.
     * 
     * First only the number of schemes is checked (and false returned if it is too small), then they are
     * examined in detail (checking all contained nodes etc.).
     * 
     * As a side effect the lists {@link CswCatalogChecker#requiredClassificationNodes} and
     * {@link CswCatalogChecker#currentRequiredClassificationSchemes} are filled with values!
     * 
     * @param c
     * @return true if the classification schemes are completely present in the given client
     * @throws OwsExceptionReport
     */
    private boolean checkClassificationScheme(SimpleSoapCswClient c, XmlObject classificationDoc) throws OwsExceptionReport {
        if (log.isDebugEnabled())
            log.debug("Parsing document with required classification schemes.");

        Map<String, String> currentRequiredClassificationSchemes = new HashMap<String, String>();
        Map<String, Pair<String, String>> currentRequiredClassificationNodes = new HashMap<String, Pair<String, String>>();

        GetRecordByIdDocument getRecDoc = GetRecordByIdDocument.Factory.newInstance();
        GetRecordByIdType getRec = getRecDoc.addNewGetRecordById();

        // load required classification schemes from init doc
        // several classification schemes in RegistryObjectList
        if (classificationDoc instanceof RegistryObjectListDocument) {
            RegistryObjectListDocument rold = (RegistryObjectListDocument) classificationDoc;
            IdentifiableType[] list = rold.getRegistryObjectList().getIdentifiableArray();

            parseClassificationSchemesAndNodes(list,
                                               currentRequiredClassificationSchemes,
                                               currentRequiredClassificationNodes);
        }
        // just a classification scheme
        else if (classificationDoc instanceof ClassificationSchemeDocument) {
            ClassificationSchemeDocument csd = (ClassificationSchemeDocument) classificationDoc;
            ClassificationSchemeType cs = csd.getClassificationScheme();

            currentRequiredClassificationSchemes.put(cs.getId(), cs.getNodeType());
            parseClassificationNodes(cs.getClassificationNodeArray(), currentRequiredClassificationNodes);
        }
        else {
            throw new OwsExceptionReport("Could not parse alle required elements from file for client check.", null);
        }

        log.info("Checking classficiation scheme(s): "
                + Arrays.toString(currentRequiredClassificationSchemes.keySet().toArray()));

        for (String id : currentRequiredClassificationSchemes.keySet()) {
            getRec.addId(id);
        }

        ElementSetNameType esnt = getRec.addNewElementSetName();
        esnt.set(ElementSetNameType.FULL);
        getRec.setService(GET_RECORDS_REQUEST_SERVICE);
        getRec.setVersion(GET_RECORDS_REQUEST_VERSION);
        getRec.setOutputFormat(GET_RECORDS_REQUEST_OUTPUTFORMAT);

        XmlObject response;
        try {
            SOAPMessage message = this.client.send((Document) getRecDoc.getDomNode());
            response = this.client.extractContent(message);
        }
        catch (OwsExceptionReport e) {
            log.warn("Could not send document to check for classification schemes!");
            throw e;
        }

        GetRecordByIdResponseDocument getRecRespDoc = (GetRecordByIdResponseDocument) response;
        GetRecordByIdResponseType getRecResp = getRecRespDoc.getGetRecordByIdResponse();
        NodeList records = getRecResp.getDomNode().getChildNodes();

        // check if really four are found
        int recordCount = records.getLength();
        if (recordCount < currentRequiredClassificationSchemes.size()) {
            log.warn("Less than required schemes found!");
            return false;
        }

        // check complete correctness of scheme(s)
        Collection<String> testedSchemes = new ArrayList<String>();
        Collection<String> foundIds = new ArrayList<String>();
        for (int i = 0; i < recordCount; i++) {
            ClassificationSchemeType currentCS;
            try {
                ClassificationSchemeDocument classificationScheme = ClassificationSchemeDocument.Factory.parse(records.item(i));
                currentCS = classificationScheme.getClassificationScheme();
            }
            catch (XmlException e) {
                if (log.isDebugEnabled())
                    log.debug("Could not parse response element to classification scheme.");
                continue;
            }

            if (log.isDebugEnabled())
                log.debug("Checking scheme: " + currentCS.getId());

            // check if the cs is internal and if the id and node type are as needed.
            if (currentCS.getIsInternal() && currentRequiredClassificationSchemes.containsKey(currentCS.getId())
                    && currentRequiredClassificationSchemes.get(currentCS.getId()).equals(currentCS.getNodeType())) {

                // check if the required children classification nodes are present (via id only)
                ClassificationNodeType[] cNodes = currentCS.getClassificationNodeArray();
                // String currentParent = currentCS.getId();
                // addFoundNodes(foundIds, cNodes, currentParent, currentRequiredClassificationNodes);
                addFoundNodes(foundIds, cNodes, currentRequiredClassificationNodes);

                foundIds.add(currentCS.getId());
            }

            testedSchemes.add(currentCS.getId());
        } // loop over all records in GetRecordsByIdResponse

        boolean returnValue;

        if ( !foundIds.containsAll(currentRequiredClassificationNodes.keySet())
                || !foundIds.containsAll(currentRequiredClassificationSchemes.keySet())) {
            ArrayList<String> missing = new ArrayList<String>(currentRequiredClassificationSchemes.keySet());
            missing.addAll(currentRequiredClassificationNodes.keySet());
            missing.removeAll(foundIds);
            log.warn("Not all required ids of classification scheme(s) found! Missing are: "
                    + Arrays.toString(missing.toArray()));

            returnValue = false;
        }
        else {
            log.info("*** All required classification schemes and nodes found for scheme(s) "
                    + Arrays.toString(testedSchemes.toArray()) + ". ***");
            returnValue = true;
        }

        // clean up!
        currentRequiredClassificationSchemes.clear();
        currentRequiredClassificationSchemes = null;
        currentRequiredClassificationNodes.clear();
        currentRequiredClassificationNodes = null;

        return returnValue;
    }

    /**
     * Checks the given catalog for sufficient capabilities, classification schemes, classification nodes and
     * slots.
     * 
     * @param c
     * @return
     * @throws OwsExceptionReport
     */
    public boolean checkClient(SimpleSoapCswClient c) throws OwsExceptionReport {
        if (this.client.isDoNotCheck()) {
            log.info("This soap client was already successfully checked during runtime or is not suppossed to be checked - not checking (again)!");
            return true;
        }

        log.info("Checking if capabilities are sufficient, and if classification schemes, classification nodes and needed slots are present in the catalog @ "
                + c);

        boolean hasCaps = isCapabilitiesSufficient();
        boolean hasClassification = checkForClassificationSchemes(c);
        boolean hasSlots = checkForSlots(c);

        if (log.isDebugEnabled()) {
            if ( !hasCaps)
                log.debug("Capabilities not sufficient due to CapabilitiesDocument!");
            if ( !hasClassification)
                log.debug("ClassificationSchemes not sufficient!");
            if ( !hasSlots)
                log.debug("Slots not sufficient!");
        }

        if (hasCaps && hasClassification && hasSlots) {
            return true;
        }
        return false;
    }

    /**
     * Method calls {@link CswCatalogChecker#checkClassificationScheme(SimpleSoapCswClient, XmlObject)} with
     * every object from the internal list and returnes the combined result.
     * 
     * @param c
     * @return
     * @throws OwsExceptionReport
     */
    private boolean checkForClassificationSchemes(SimpleSoapCswClient c) throws OwsExceptionReport {
        boolean all = true;

        for (XmlObject xmlObj : classificationInitDocs) {
            boolean passed = checkClassificationScheme(c, xmlObj);
            if ( !passed) {
                all = false;
                log.warn("Classification scheme did NOT pass test: " + XmlTools.inspect(xmlObj));
            }
            else {
                log.info("Classification scheme passed test: " + XmlTools.inspect(xmlObj));
            }
        }

        return all;
    }

    /**
     * 
     * Checks if the client has all required rim:Slot elements by comparing the available ones to the ones in
     * the XML Document for slot initialization (which is loaded at the beginning of the method).
     * 
     * First only the ids of the slots are checked, afterwards the name and type is inspected as well.
     * 
     * @param c
     * @return true if the needed slots are completely present in the given client
     * @throws OwsExceptionReport
     */
    private boolean checkForSlots(SimpleSoapCswClient c) throws OwsExceptionReport {
        log.info("Checking if all the required slots are present in the catalog.");

        GetRecordByIdDocument getRecDoc = GetRecordByIdDocument.Factory.newInstance();
        GetRecordByIdType getRec = getRecDoc.addNewGetRecordById();

        if (requiredSlots.size() < 1) {
            // load required slots from init doc
            try {
                RegistryObjectListDocument rold = RegistryObjectListDocument.Factory.parse(slotInitDoc.getDomNode());
                IdentifiableType[] list = rold.getRegistryObjectList().getIdentifiableArray();

                for (IdentifiableType identifiableType : list) {
                    if (identifiableType instanceof ExtrinsicObjectType) {
                        ExtrinsicObjectType eo = (ExtrinsicObjectType) identifiableType;
                        if (eo.getObjectType().equals(SLOT_DESRCRIPTION_INSERTION_EO_TYPE)) {
                            requiredSlotsDocumentId = eo.getId();

                            SlotType1[] slots = eo.getSlotArray();

                            for (SlotType1 s : slots) {
                                requiredSlots.put(s.getName(), s.getSlotType());
                            }
                        }
                    }
                }
            }
            catch (XmlException xe) {
                log.error("Could not parse required slots from file for client check.", xe);
                throw new OwsExceptionReport("Could not parse required slots from file for client check.", xe);
            }
        }

        // I cannot query for Slots directly, but only for "main object" like ExtrinsicObjects,
        getRec.addId(requiredSlotsDocumentId);

        ElementSetNameType esnt = getRec.addNewElementSetName();
        esnt.set(ElementSetNameType.FULL);
        getRec.setService(GET_RECORDS_REQUEST_SERVICE);
        getRec.setVersion(GET_RECORDS_REQUEST_VERSION);
        getRec.setOutputFormat(GET_RECORDS_REQUEST_OUTPUTFORMAT);

        XmlObject response;
        try {
            SOAPMessage message = this.client.send((Document) getRecDoc.getDomNode());
            response = this.client.extractContent(message);
        }
        catch (OwsExceptionReport e) {
            log.warn("Could not send document to check for classification schemes!");
            throw e;
        }

        GetRecordByIdResponseDocument getRecRespDoc = (GetRecordByIdResponseDocument) response;
        GetRecordByIdResponseType getRecResp = getRecRespDoc.getGetRecordByIdResponse();

        NodeList respChildNodes = getRecResp.getDomNode().getChildNodes();
        ArrayList<SlotType1> foundSlots = new ArrayList<SlotType1>();
        for (int i = 0; i < respChildNodes.getLength(); i++) {
            Node n = respChildNodes.item(i);
            // id="urn:sir:id:SlotDefinition"
            Node oT = n.getAttributes().getNamedItem("objectType");
            Node id = n.getAttributes().getNamedItem("id");
            if ( (oT != null && oT.getNodeValue().equals(SLOT_DESRCRIPTION_INSERTION_EO_TYPE))
                    && (id != null && id.getNodeValue().equals(SLOT_DESCRIPTION_INSERTION_EO_ID))) {
                try {
                    NodeList maybeSlots = n.getChildNodes();
                    // add slots to list
                    for (int j = 0; j < maybeSlots.getLength(); j++) {
                        Node s = maybeSlots.item(j);
                        if (s.getNamespaceURI().equals(EBRIM_NAMESPACE_URI) && s.getLocalName().equals("Slot")) {
                            SlotDocument sD = SlotDocument.Factory.parse(s);
                            foundSlots.add(sD.getSlot());
                            if (log.isDebugEnabled())
                                log.debug("Added slot from GetRecordsResponse: " + XmlTools.inspect(sD));
                        }
                    }
                    break;
                }
                catch (XmlException e) {
                    log.error("Could not extract slots from GetRecordsResponse");
                    throw new OwsExceptionReport("Could not extract slots from GetRecordsResponse", null);
                }

            }
        }

        if (foundSlots.isEmpty()) {
            log.error("Could not extract any slot(s) from GetRecordsResponse");
            return false;
        }

        if (foundSlots.size() < requiredSlots.size()) {
            log.warn("Less than required slots found!");
            // identify missing ones
            Collection<String> missing = new ArrayList<String>(requiredSlots.keySet());
            for (SlotType1 slot : foundSlots) {
                String n = slot.getName();
                if (missing.contains(n))
                    missing.remove(slot.getName());
            }

            if (log.isDebugEnabled())
                log.debug("Missing slot names: " + Arrays.toString(missing.toArray()));
            return false;
        }

        // just to be safe, check if the slots have the correct name and type
        boolean allCorrect = true;
        for (SlotType1 s : foundSlots) {
            if (log.isDebugEnabled())
                log.debug("Checking record " + XmlTools.inspect(s));

            String name = s.getName();
            if (requiredSlots.containsKey(name)) {
                String type = s.getSlotType();
                if ( !type.equals(requiredSlots.get(name))) {
                    log.error("Slot has the wrong type: " + name + " is " + type + "but is required to be "
                            + requiredSlots.get(name));
                    allCorrect = false;
                }
            }
        }

        if ( !allCorrect) {
            log.warn("Not all required slots with the correct type found - check the log for details.");
            return false;
        }

        log.info("*** All required slots found. ***");
        return true;
    }

    /**
     * Method checks if at least one element of the second collection is present in the first collection.
     * "true" is returned as soon as the first occurence of such an element is found.
     * 
     * @param first
     * @param second
     * @return
     */
    private boolean firstContainsAtLeastOneOfSecond(Collection<String> first, Collection<String> second) {
        for (String stringOfSecond : second) {
            if (first.contains(stringOfSecond))
                return true;
        }
        return false;
    }

    /**
     * 
     * @param xmlObj
     * @return
     */
    private int getClassificationSchemeCount(XmlObject xmlObj) {
        int returnValue = Integer.MAX_VALUE;

        // several classification schemes in RegistryObjectList
        if (xmlObj instanceof RegistryObjectListDocument) {
            RegistryObjectListDocument rold = (RegistryObjectListDocument) xmlObj;
            IdentifiableType[] list = rold.getRegistryObjectList().getIdentifiableArray();

            Map<String, String> currentRequiredClassificationSchemes = new HashMap<String, String>();
            Map<String, Pair<String, String>> currentRequiredClassificationNodes = new HashMap<String, Pair<String, String>>();
            parseClassificationSchemesAndNodes(list,
                                               currentRequiredClassificationSchemes,
                                               currentRequiredClassificationNodes);

            int n = Integer.valueOf(currentRequiredClassificationSchemes.size()).intValue();
            currentRequiredClassificationSchemes = null;

            returnValue = n;
        }

        // just a classification scheme
        if (xmlObj instanceof ClassificationSchemeDocument) {
            returnValue = 1;
        }

        if (returnValue == Integer.MAX_VALUE)
            log.warn("Could not extract number of classification schemes from given document, returning " + returnValue
                    + ". Document is: " + XmlTools.inspect(xmlObj));

        return returnValue;
    }

    /**
     * 
     * Method tries to parse given message's content to a {@link TransactionResponseDocument} and extract the
     * number of totally inserted elements. If it is smaller than the expected inserted documents an error is
     * logged. If the message is a {@link SOAPFault} an exception is thrown.
     * 
     * @param responseString
     * @throws OwsExceptionReport
     */
    private void handleResponse(SOAPMessage message, int expectedInsertions) throws OwsExceptionReport {
        // try parsing content to TransactionResponseDocument
        try {
            XmlObject content = this.client.extractContent(message);
            TransactionResponseDocument transactionRespDoc = TransactionResponseDocument.Factory.parse(content.getDomNode());

            // check if correct count was inserted
            TransactionResponseType transactionResp = transactionRespDoc.getTransactionResponse();
            TransactionSummaryType transactionSummary = transactionResp.getTransactionSummary();

            if (transactionSummary.getTotalInserted().intValue() < expectedInsertions) {
                log.error("Insert Transaction did not complete succesfully! Wanted to insert " + expectedInsertions
                        + " documents but only " + transactionSummary.getTotalInserted() + " were inserted!");
            }
            log.info("### RESPONSE FOR INSERT TRANSACTION ###\n" + XmlTools.inspect(transactionResp));

            return;
        }
        catch (XmlException e) {
            log.warn("Could not parse response to TransactionResponseDocument. Maybe it's a Fault?", e);
        }

        // check for fault
        SOAPFault f = this.client.extractFault(message);
        throw new OwsExceptionReport("FAULT RETURNED VIA SOAP:\n" + SoapTools.inspect(f), null);
    }

    /**
     * 
     * Method requests the {@link CapabilitiesDocument} from the service and checks if
     * 
     * - at least one of the accepted service types coincide
     * 
     * - at least one of the accepted version parameters coincide
     * 
     * - at least one of the accepted service parameter names coincide
     * 
     * - at least one of the accepted service type versions coincide.
     * 
     * - an transaction service operation that supports SOAP is given.
     * 
     * @param capabilitiesDoc
     * @throws OwsExceptionReport
     */
    public boolean isCapabilitiesSufficient() throws OwsExceptionReport {
        // request the document:
        CapabilitiesDocument capabilitiesDoc = requestCapabilities();

        CapabilitiesType caps = capabilitiesDoc.getCapabilities();
        ServiceIdentification serviceIdent = caps.getServiceIdentification();

        // check if service type is acceptable
        if ( !acceptedServiceTypes.contains(serviceIdent.getServiceType().getStringValue())) {
            log.warn("Error checking sufficiency of capabilities: No accepted service type ("
                    + Arrays.toString(acceptedServiceTypes.toArray()) + ") found!");
            return false;
        }

        // check if version parameter is acceptable
        DomainType[] parameters = caps.getOperationsMetadata().getParameterArray();
        for (DomainType domainType : parameters) {
            if (domainType.getName().equalsIgnoreCase(VERSION_PARAMETER_NAME)) {
                if ( !firstContainsAtLeastOneOfSecond(acceptedVersionParameters,
                                                      Arrays.asList(domainType.getValueArray()))) {
                    log.warn("Error checking sufficiency of capabilities: No accepted version parameter ("
                            + Arrays.toString(acceptedVersionParameters.toArray()) + ") found!");
                    return false;
                }
                break;
            }
        }

        // check if service parameter is acceptable
        for (DomainType domainType : parameters) {
            if (domainType.getName().equalsIgnoreCase(SERVICE_PARAMETER_NAME)) {
                if ( !firstContainsAtLeastOneOfSecond(acceptedServiceParameters,
                                                      Arrays.asList(domainType.getValueArray()))) {
                    log.warn("Error checking sufficiency of capabilities: No accepted service parameter ("
                            + Arrays.toString(acceptedServiceParameters.toArray()) + ") found!");
                    return false;
                }
                break;
            }
        }

        // check if service type version is acceptable
        if ( !firstContainsAtLeastOneOfSecond(acceptedServiceTypeVersions,
                                              Arrays.asList(serviceIdent.getServiceTypeVersionArray()))) {
            log.warn("Error checking sufficiency of capabilities: No accepted service type version ("
                    + Arrays.toString(acceptedServiceTypeVersions.toArray()) + ") found!");
            return false;
        }

        // check if transaction operation via HTTP-Post for a SOAP PostEncoding is present
        Operation[] operations = caps.getOperationsMetadata().getOperationArray();
        boolean foundIt = false;
        for (Operation operation : operations) {
            if (operation.getName().equalsIgnoreCase(TRANSACTION_OPERATION_NAME)) {
                DCP[] dcps = operation.getDCPArray();
                for (DCP dcp : dcps) {
                    RequestMethodType[] posts = dcp.getHTTP().getPostArray();
                    for (RequestMethodType requestMethodType : posts) {
                        DomainType[] constraints = requestMethodType.getConstraintArray();
                        for (DomainType domainType : constraints) {
                            if (domainType.getName().equals(POST_ENCODING_CONSTRAINT_NAME)) {
                                List<String> values = Arrays.asList(domainType.getValueArray());
                                if (values.contains(SOAP_POST_ENCODING_VALUE)) {
                                    foundIt = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        if ( !foundIt) {
            log.warn("Error checking sufficiency of capabilities: Did not find the needed value "
                    + SOAP_POST_ENCODING_VALUE + " of a constraint " + POST_ENCODING_CONSTRAINT_NAME
                    + " for an HTTP DCP of an operation named " + TRANSACTION_OPERATION_NAME);
            return false;
        }

        return true;
    }

    /**
     * 
     * @param classificationNodeArray
     * @param requiredClassificationNodes
     */
    private void parseClassificationNodes(ClassificationNodeType[] classificationNodeArray,
                                          Map<String, Pair<String, String>> currentRequiredClassificationNodes) {
        for (ClassificationNodeType cNode : classificationNodeArray) {
            currentRequiredClassificationNodes.put(cNode.getId(),
                                                   new Pair<String, String>(cNode.getParent(), cNode.getCode()));

            if (log.isDebugEnabled())
                log.debug("Added Node to required list: " + XmlTools.inspect(cNode));

            // process child nodes
            parseClassificationNodes(cNode.getClassificationNodeArray(), currentRequiredClassificationNodes);
        }
    }

    /**
     * @param list
     * @param requiredClassificationNodes
     */
    private void parseClassificationSchemesAndNodes(IdentifiableType[] list,
                                                    Map<String, String> currentRequiredClassificationSchemes,
                                                    Map<String, Pair<String, String>> currentRequiredClassificationNodes) {
        for (IdentifiableType identifiableType : list) {
            if (identifiableType instanceof ClassificationSchemeType) {
                ClassificationSchemeType cs = (ClassificationSchemeType) identifiableType;

                currentRequiredClassificationSchemes.put(cs.getId(), cs.getNodeType());
                parseClassificationNodes(cs.getClassificationNodeArray(), currentRequiredClassificationNodes);

                if (log.isDebugEnabled())
                    log.debug("Added Scheme to required list: " + XmlTools.inspect(cs));

                // process internal ClassificationNodes happens below, do not need to get deeper in hierarchy
                // as list contains all elements...
                // parseClassificationNodes(cs.getClassificationNodeArray(),
                // currentRequiredClassificationNodes);

                continue;
            }
            if (identifiableType instanceof ClassificationNodeType) {
                ClassificationNodeType cn = (ClassificationNodeType) identifiableType;
                currentRequiredClassificationNodes.put(cn.getId(),
                                                       new Pair<String, String>(cn.getParent(), cn.getCode()));
                if (log.isDebugEnabled()) {
                    log.debug("Added Node to required list: " + XmlTools.inspect(cn));
                }
            }
        }
    }

    /**
     * 
     * @return
     * @throws OwsExceptionReport
     */
    private CapabilitiesDocument requestCapabilities() throws OwsExceptionReport {
        if (log.isDebugEnabled())
            log.debug("Requesting Capabilities...");

        // build request
        GetCapabilitiesDocument getCapDoc = GetCapabilitiesDocument.Factory.newInstance();
        GetCapabilitiesType getCap = getCapDoc.addNewGetCapabilities();
        getCap.setService(CswCatalogConstants.CATALOG_SERVICE_NS);
        AcceptVersionsType acceptVersions = getCap.addNewAcceptVersions();
        acceptVersions.addVersion(CswCatalogConstants.CATALOG_VERSION);

        // send document
        Document doc = (Document) getCapDoc.getDomNode();
        SOAPMessage message = this.client.send(doc);
        XmlObject response = this.client.extractContent(message);

        if (log.isDebugEnabled()) {
            log.debug("Response from catalogue:\n" + response.xmlText(XmlTools.PRETTY_PRINT_OPTIONS));
        }

        // parse and return response document
        try {
            CapabilitiesDocument capResponse = CapabilitiesDocument.Factory.parse(response.getDomNode());
            return capResponse;
        }
        catch (XmlException e) {
            log.error("Error parsing CapabilitiesDocument!", e);
            OwsExceptionReport er = new OwsExceptionReport("Error parsing CapabilitiesDocument.", e);
            er.addCodedException(ExceptionCode.NoApplicableCode,
                                 "CswCatalogChecker.requestCapabilities()",
                                 "\nRECEIVED MESSAGE:\n" + response.xmlText(XmlTools.PRETTY_PRINT_OPTIONS));
            throw er;
        }
    }

    /**
     * 
     * Send the given content wrapped in a {@link TransactionDocument} to the given client. The number of
     * features that are expected to be inserted is given as well. The response handling is passed on to the
     * method {@link CswCatalogChecker#handleResponse(SOAPMessage, int)}.
     * 
     * @param c
     * @throws OwsExceptionReport
     */
    private void sendInsertTransaction(SimpleSoapCswClient c, XmlObject content, int inserted) throws OwsExceptionReport {
        log.info("Sending InsertTransaction to " + c);
        if (log.isDebugEnabled()) {
            log.debug("Content: " + content.toString());
            log.debug("Expected insertion: " + inserted);
        }
        // wrap into transaction
        TransactionDocument transactionDoc = TransactionDocument.Factory.newInstance();
        TransactionType transaction = transactionDoc.addNewTransaction();
        transaction.setVersion(CswCatalogConstants.CATALOG_VERSION);
        transaction.setService(CswCatalogConstants.CATALOG_SERVICE);
        InsertType insert = transaction.addNewInsert();
        insert.newCursor().getObject().set(content);
        insert.setHandle(INSERT_HANDLE_ADD_CLASSIFICATIONS);

        // send requests and handle responses
        try {
            SOAPMessage message = c.send((Document) transactionDoc.getDomNode());
            handleResponse(message, inserted);
        }
        catch (OwsExceptionReport e) {
            log.error("Could not send transaction document!", e);
            throw e;
        }
    }

    /**
     * 
     * Use of this method is discouraged. Please use
     * {@link CswCatalogChecker#checkAndUpdateClient(SimpleSoapCswClient)}. This method simply tries to send
     * the classification schemes and required slots.
     * 
     * @param c
     * @throws OwsExceptionReport
     */
    public void sendRequiredElements(SimpleSoapCswClient c) throws OwsExceptionReport {
        log.info("Sending required classification schemes, classification nodes and needed slots to the catalog @ " + c);

        sendInsertTransaction(c, slotInitDoc, requiredSlots.size());

        for (XmlObject xmlObj : classificationInitDocs) {
            int nClassificationSchemes = getClassificationSchemeCount(xmlObj);
            sendInsertTransaction(c, xmlObj, nClassificationSchemes);
        }
    }

}
