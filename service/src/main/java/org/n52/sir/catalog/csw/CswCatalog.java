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
package org.n52.sir.catalog.csw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.TransformerException;

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
import net.opengis.cat.wrs.x10.ExtrinsicObjectDocument;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SystemType;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlValueDisconnectedException;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.api.SirXmlSensorDescription;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.util.XmlTools;
import org.n52.sir.SirConfigurator;
import org.n52.sir.catalog.ICatalog;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.util.Pair;
import org.n52.sir.util.SoapTools;
import org.n52.sir.xml.IProfileValidator;
import org.n52.sir.xml.ITransformer;
import org.n52.sir.xml.ValidationResult;
import org.n52.sir.xml.impl.SMLtoEbRIMTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import x0.oasisNamesTcEbxmlRegrepXsdRim3.ClassificationNodeDocument;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.ClassificationNodeType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.ExtrinsicObjectType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.IdentifiableType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.OrganizationType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryObjectListDocument;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryObjectListType;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryPackageDocument;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.ServiceType;

/**
 * 
 * {@link ICatalog}-implementation based on OGCs Web Catalog Service (CSW - Catalgue Service for the Web,
 * sometimes CAT) - see http://www.opengeospatial.org/standards/cat. More specifically it requires the ebRIM
 * Application Profile of CAT - see http://www.opengeospatial.org/standards/cat2eoext4ebrim - that is accessed
 * via SOAP (using {@link SimpleSoapCswClient}).
 * 
 * It implements functionalities to:
 * 
 * - push all data from the SIR to the catalogue. - check the capabilities of a catalogue and if necessary to
 * insert required elements to the catalogue. - check if a document would be accepted by the catalog.
 * 
 * A push consists of the following steps: creating insert transactions for all existing SensorML descriptions
 * in the SIR, transmitting the inserts to the client, returning possible error reports of all transactions.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class CswCatalog implements ICatalog {

    private static final String INSERT_HANDLE_SENSOR_DESCRIPTIONS = "InsertSensorDescription";

    private static Logger log = LoggerFactory.getLogger(CswCatalog.class);

    private static final int MAXIMUM_COUNT_OF_SENSORS_IN_DOC = 10;

    private static Random rand = new Random();

    private CswCatalogChecker checker;

    private SimpleSoapCswClient client;

    private CswCatalogCache identifiableCache;

    private int[] lastPushSummary;

    private ITransformer transformer;

    private ISearchSensorDAO searchDao;

    private IProfileValidator validator;

    public CswCatalog(ISearchSensorDAO searchDao,
                      SimpleSoapCswClient c,
                      List<XmlObject> classificationInitDocs,
                      XmlObject slotInitDoc,
                      ITransformer transformer,
                      IProfileValidator validator) {
        this.client = c;
        this.identifiableCache = new CswCatalogCache();
        this.checker = new CswCatalogChecker(this.client, classificationInitDocs, slotInitDoc);
        this.transformer = transformer;
        this.searchDao = searchDao;
        this.validator = validator;

        log.info("NEW {}", this);
    }

    @Override
    public boolean acceptsDocument(XmlObject doc) throws OwsExceptionReport, IOException {
        ValidationResult result = this.validator.validate(doc);

        if (result.isValidated())
            log.debug("Given document is profile conform!");
        else
            log.debug("Given document is NOT profile conform! {}", result);

        this.validator = null;
        return result.isValidated();
    }

    /**
     * 
     * generate random integer between 10000 and 100000
     * 
     * @param oldId
     * @return
     */
    private String appendRandomSuffix(String oldId) {
        int postfix = 10000 + rand.nextInt(10000);
        String newId = oldId.concat(":random_").concat(Integer.toString(postfix));
        return newId;
    }

    /**
     * 
     * @param ids
     * @return
     */
    private GetRecordByIdDocument createGetRecordById(Collection<String> ids) {
        log.debug("* Creating GetRecordsByIdDocument with these ids: {}", Arrays.toString(ids.toArray()));

        GetRecordByIdDocument getRecDoc = GetRecordByIdDocument.Factory.newInstance();
        GetRecordByIdType getRecordById = getRecDoc.addNewGetRecordById();
        getRecordById.setService(CswCatalogConstants.CATALOG_SERVICE);
        getRecordById.setVersion(CswCatalogConstants.CATALOG_VERSION);

        for (String s : ids) {
            getRecordById.addId(s);
        }

        return getRecDoc;
    }

    /**
     * 
     * @param lastPushToCatalog
     *        only insert transactions for sensors updated after the given date are created.
     * @return
     * @throws OwsExceptionReport
     */
    private List<Pair<Document, Integer>> createInsertTransactions(Date lastPushToCatalog) throws OwsExceptionReport {
        ArrayList<Pair<Document, Integer>> documents = new ArrayList<>();

        // get all sensor descriptions
        Collection<SirSearchResultElement> sensors;
        try {
            sensors = this.searchDao.getAllSensors(false);
        }
        catch (OwsExceptionReport e) {
            log.error("Error searching for sensor descriptions.", e);
            throw e;
        }

        if (sensors.size() < 1) {
            log.warn("No sensors found to insert!");
            throw new OwsExceptionReport("No sensors found to insert!", null);
        }

        // handle only a limited number of sensors at a time to limit the size of the transaction
        Collection<SirSearchResultElement> currentSensors = new ArrayList<>();
        Hashtable<String, RegistryPackageDocument> transformedDocs;

        log.debug("Transforming description of {} sensors into transactions with {} each.",
                  sensors.size(),
                  MAXIMUM_COUNT_OF_SENSORS_IN_DOC);

        TransactionDocument transactionDoc;
        while (sensors.size() > 0) {
            Iterator<SirSearchResultElement> sensorsIterator = sensors.iterator();

            while (currentSensors.size() < MAXIMUM_COUNT_OF_SENSORS_IN_DOC) {
                if (sensorsIterator.hasNext()) {
                    // check if an update is neccessary, add the sensor if that is the case
                    SirSearchResultElement s = sensorsIterator.next();
                    if (s.getLastUpdate().after(lastPushToCatalog))
                        currentSensors.add(s);

                    else
                        log.debug("Not adding sensor because it was not updated since last push to catalog!");
                }
                else
                    break;
            }

            // transform all sensor descriptions
            transformedDocs = transformSensorDescriptions(currentSensors);

            // check if there was a problem with some documents
            if (transformedDocs.containsValue(ITransformer.TRANSFORMATION_ERROR_OBJECT)) {
                Set<Entry<String, RegistryPackageDocument>> transformedEntries = transformedDocs.entrySet();
                ArrayList<String> nulledKeys = new ArrayList<>();
                for (Entry<String, RegistryPackageDocument> entry : transformedEntries) {
                    if (entry.getValue() == ITransformer.TRANSFORMATION_ERROR_OBJECT) {
                        log.warn("Could not transform sensor description for sensor with ID " + entry.getKey()
                                + ". Please check the log for details!");
                        nulledKeys.add(entry.getKey());
                    }
                }
                for (String key : nulledKeys) {
                    transformedDocs.remove(key);
                }
            }

            // check if ids, classification nodes etc. already exist in the service
            processSensorDescriptions(transformedDocs);

            // do not continue if sensor descriptions could not be transformed
            if (transformedDocs.size() < 1) {
                log.warn("None of the sensor descriptions given for this insert transaction could be transformed/processed: "
                        + Arrays.toString(currentSensors.toArray()));
            }
            else {
                try {
                    // wrap transformed documents in insert transaction
                    transactionDoc = createTransaction(transformedDocs);

                    // add transaction to list as document and number of expected inserts
                    Pair<Document, Integer> p = new Pair<>((Document) transactionDoc.getDomNode(),
                                                           Integer.valueOf(transformedDocs.size()));
                    documents.add(p);
                    log.debug("Added insert transaction document: {}", p);
                }
                catch (OwsExceptionReport e) {
                    log.error("Could not create transaction document! None of these will be inserted: "
                                      + Arrays.toString(transformedDocs.keySet().toArray()),
                              e);
                }
            }

            // remove processed sensors and clear current sensor list
            sensors.removeAll(currentSensors);
            log.info("Finished transforming and processing of " + transformedDocs.size() + " out of current set of "
                    + currentSensors.size() + " sensors. See the debug log for details on missing transformations.");
            currentSensors.clear();
        }

        if (documents.size() < 1)
            throw new OwsExceptionReport("Found sensors but none (!) could be transformed. Check the log for details!",
                                         null);

        log.info("Finished creating insert transactions, count: " + documents.size());
        return documents;
    }

    /**
     * @param transformedDocs
     * @return
     * @throws OwsExceptionReport
     */
    private TransactionDocument createTransaction(Hashtable<String, RegistryPackageDocument> transformedDocs) throws OwsExceptionReport {
        log.debug("** Creating TransactionDocument for {} transformed docs.", transformedDocs.size());

        TransactionDocument transactionDoc = TransactionDocument.Factory.newInstance();
        TransactionType transaction = transactionDoc.addNewTransaction();
        transaction.setService(CswCatalogConstants.CATALOG_SERVICE);
        transaction.setVersion(CswCatalogConstants.CATALOG_VERSION);
        InsertType insert = transaction.addNewInsert();

        // string hack here, but xmlbeans-methods (tried a lot!) did not work at all.
        StringBuilder sb = new StringBuilder();
        sb.append(SMLtoEbRIMTransformer.TRANSFORMED_DOCS_LISTING_ELEMENT_BEFORE);

        // remove the schema location from all but the first registry package
        boolean isFirst = true;
        for (RegistryPackageDocument elem : transformedDocs.values()) {
            if (isFirst) {
                sb.append(elem.xmlText(XmlTools.PRETTY_PRINT_OPTIONS));
                isFirst = false;
            }
            else {
                XmlCursor c = elem.getRegistryPackage().newCursor();
                c.removeAttribute(XmlTools.SCHEMA_LOCATION_ATTRIBUTE_QNAME);
                c.dispose();
                c = null;

                sb.append(elem.xmlText(XmlTools.PRETTY_PRINT_OPTIONS));
            }

        }
        sb.append(SMLtoEbRIMTransformer.TRANSFORMED_DOCS_LISTING_ELEMENT_AFTER);

        RegistryObjectListDocument regObjListDoc;
        try {
            regObjListDoc = RegistryObjectListDocument.Factory.parse(sb.toString());

            if (SirConfigurator.getInstance().isValidateRequests()) {
                if ( !regObjListDoc.validate()) {
                    log.warn("Created invalid RegistryObjectListDocument, possibly rejected by the service! Set the logging level to DEBUG for more information on the document.");
                    log.debug("### Invalid document: ###\n{}", regObjListDoc.xmlText(XmlTools.PRETTY_PRINT_OPTIONS));
                }
            }
        }
        catch (XmlException e) {
            OwsExceptionReport report = new OwsExceptionReport("Could not create insert-transaction document for given transformed docs",
                                                               e);
            throw report;
        }

        // insert the RegistryObjectListDocument into the insert transaction
        insert.newCursor().getObject().set(regObjListDoc);
        insert.setHandle(INSERT_HANDLE_SENSOR_DESCRIPTIONS);

        return transactionDoc;
    }

    @Override
    public boolean ensureSufficientCapabilities() throws OwsExceptionReport {
        // check if catalog has all neccessary classification and slot elements and an accepted version etc.
        boolean b = this.checker.checkAndUpdateClient(this.client);
        if ( !b) {
            log.warn("Capabilities and catalog elements not sufficient due to catalog checker! See the log for details.");
            return false;
        }

        // did find all needed values
        return b;
    }

    /**
     * 
     * equality means "the same id"
     * 
     * @param identifableArray
     * @return
     * @throws OwsExceptionReport
     */
    private Map<IdentifiableType, Boolean> getExistingIdentifiablesById(IdentifiableType[] identifiableArray) throws OwsExceptionReport {
        log.debug("** Requesting {} identifiables from catalog.", identifiableArray.length);

        // use a set to remove duplicates
        SortedSet<String> ids = new TreeSet<>();
        for (int i = 0; i < identifiableArray.length; i++) {
            if ( !this.identifiableCache.contains(identifiableArray[i])) {
                boolean added = ids.add(identifiableArray[i].getId());
                if ( !added)
                    log.debug("Did not add duplicate to identifiables cache {}", identifiableArray[i].getId());
            }
            else
                log.debug("Not requesting element because it is cached: {}", identifiableArray[i].getId());
        }

        // create request
        GetRecordByIdDocument doc = createGetRecordById(ids);

        // send request
        SOAPMessage responseMessage;
        try {
            responseMessage = this.client.send((Document) doc.getDomNode());
        }
        catch (OwsExceptionReport e) {
            throw new OwsExceptionReport("Could not request records by id to process possibly existing elements.", e);
        }

        // parse content to GetRecordByIdResponseDocument
        GetRecordByIdResponseType getRecordByIdResponse;
        try {
            XmlObject content = this.client.extractContent(responseMessage);
            GetRecordByIdResponseDocument getRecRespDoc = GetRecordByIdResponseDocument.Factory.parse(content.getDomNode());

            getRecordByIdResponse = getRecRespDoc.getGetRecordByIdResponse();
            log.debug("* GetRecordsByIdResponseDocument: {}", XmlTools.inspect(getRecordByIdResponse));
        }
        catch (XmlException e) {
            log.error("Could not parse response to GetRecordByIdResponseDocument.");
            // check for fault
            SOAPFault f = this.client.extractFault(responseMessage);
            if (f != null)
                throw new OwsExceptionReport("FAULT RETURNED VIA SOAP:\n" + SoapTools.inspect(f), null);

            throw new OwsExceptionReport("Could not parse response to GetRecordByIdResponseDocument.", e);
        }

        // build list of updateable / removeable objects
        Map<IdentifiableType, Boolean> toUpdate = new HashMap<>();
        NodeList nodes = getRecordByIdResponse.getDomNode().getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);

            try {
                XmlObject xmlO = XmlObject.Factory.parse(n);
                IdentifiableType iT = null;

                if (xmlO instanceof ClassificationNodeDocument) {
                    ClassificationNodeDocument cnt = (ClassificationNodeDocument) xmlO;
                    iT = cnt.getClassificationNode();
                }
                else if (xmlO instanceof ExtrinsicObjectDocument) {
                    ExtrinsicObjectDocument eo = (ExtrinsicObjectDocument) xmlO;
                    iT = eo.getExtrinsicObject();
                }

                if (iT != null) {
                    this.identifiableCache.add(iT);
                    toUpdate.put(iT, Boolean.TRUE);
                    log.debug("Found element in response: {}", XmlTools.inspect(iT));
                }
                else
                    log.debug("Could not identify element in get records response, this element is NOT added to cache: "
                            + xmlO.xmlText());

            }
            catch (XmlException e) {
                log.debug("Node in response is not a Classification Node...");
            }
        }

        return toUpdate;

    }

    @Override
    public int[] getSummaryOfLastPush() {
        return this.lastPushSummary;
    }

    /**
     * 
     * @param responseString
     * @return
     * @throws OwsExceptionReport
     */
    private void handleTransactionResponse(SOAPMessage message, int insertedDocuments) throws OwsExceptionReport {
        // try parsing content to TransactionResponseDocument
        try {
            XmlObject content = this.client.extractContent(message);
            TransactionResponseDocument transactionRespDoc = TransactionResponseDocument.Factory.parse(content.getDomNode());

            // check if correct count was inserted
            TransactionResponseType transactionResp = transactionRespDoc.getTransactionResponse();
            TransactionSummaryType transactionSummary = transactionResp.getTransactionSummary();

            // save response summary, not so nice that this is done with a side effect, but now other way atm.
            this.lastPushSummary[0] += (transactionSummary.getTotalInserted() != null) ? transactionSummary.getTotalInserted().intValue()
                                                                                      : 0;
            this.lastPushSummary[1] += (transactionSummary.getTotalUpdated() != null) ? transactionSummary.getTotalUpdated().intValue()
                                                                                     : 0;
            this.lastPushSummary[2] += (transactionSummary.getTotalDeleted() != null) ? transactionSummary.getTotalDeleted().intValue()
                                                                                     : 0;

            if (insertedDocuments < (this.lastPushSummary[0] + this.lastPushSummary[1])) {
                log.error("Insert Transaction did not complete succesfully! Wanted to insert " + insertedDocuments
                        + " documents but only " + this.lastPushSummary[0] + "/" + this.lastPushSummary[1]
                        + " were inserted/updated!");
            }
            else
                log.info("Got transaction response with count of inserted high enough - set the logging level to DEBUG for details.");

            log.debug("RESPONSE FOR INSERT TRANSACTION: {}", XmlTools.inspect(transactionSummary));
        }
        catch (XmlException e) {
            log.warn("Could not parse response to TransactionResponseDocument. Maybe it's a Fault?");
            // check for fault
            SOAPFault f = this.client.extractFault(message);
            if (f != null)
                throw new OwsExceptionReport("FAULT RETURNED VIA SOAP:\n" + SoapTools.inspect(f), null);
        }
    }

    @Override
    public boolean hasSufficientCapabilities() throws OwsExceptionReport {
        return this.checker.checkClient(this.client);
    }

    /**
     * 
     * works directly on the given table
     * 
     * @param transformedDocs
     * @return
     * @throws OwsExceptionReport
     */
    private void processSensorDescriptions(Hashtable<String, RegistryPackageDocument> transformedDocs) throws OwsExceptionReport {
        for (Entry<String, RegistryPackageDocument> transformed : transformedDocs.entrySet()) {
            RegistryPackageDocument doc = transformed.getValue();

            // FIXME catalogue does not accept the same registry package id twice, even if it contains the
            // same extrinsic objects
            String oldId = doc.getIdentifiable().getId();
            String newId = appendRandomSuffix(oldId);
            doc.getIdentifiable().setId(newId);
            log.info("Changed RegistryPackage id from " + oldId + " to " + newId);

            RegistryObjectListType registryObjectList = doc.getRegistryPackage().getRegistryObjectList();
            IdentifiableType[] identifableArray = registryObjectList.getIdentifiableArray();

            if (identifableArray == null) {
                transformedDocs.put(transformed.getKey(), ITransformer.PROCESSING_ERROR_OBJECT);
                continue;
            }

            log.debug("Starting to process {} elements in RegistryPackageDocument {}",
                      identifableArray.length,
                      transformed.getValue().getIdentifiable().getId());

            // query service which Identifiables can be found
            Map<IdentifiableType, Boolean> existing = getExistingIdentifiablesById(identifableArray);
            ArrayList<String> idsToRemove = new ArrayList<>();

            // iterate through all identifiables
            for (Entry<IdentifiableType, Boolean> entry : existing.entrySet()) {
                IdentifiableType iT = entry.getKey();
                boolean needsUpdate = entry.getValue().booleanValue();

                if ( !needsUpdate)
                    continue;

                log.debug("Processing {}", iT.getId());

                // check for already existing ExtrinsicObjects
                if (iT instanceof ExtrinsicObjectType) {
                    ExtrinsicObjectType eo = (ExtrinsicObjectType) iT;
                    log.debug("ExtrinsicObject already found in catalog, but proceeding normally (not removed!): "
                            + XmlTools.inspect(eo));
                }

                // check for already existing ClassificationNodes
                else if (iT instanceof ClassificationNodeType) {
                    ClassificationNodeType cnt = (ClassificationNodeType) iT;
                    log.warn("REMOVING " + XmlTools.inspect(cnt));
                    idsToRemove.add(cnt.getId());
                }

                // check for already existing Service definitions
                else if (iT instanceof ServiceType) {
                    ServiceType st = (ServiceType) iT;
                    log.debug("REMOVING {}", XmlTools.inspect(st));

                    // TODO get the capabilites of the service and fill in rim:Name and rim:Description with
                    // ows:ServiceIdentification/ows:Title and /ows:Abstract respectively (see 07-144r4 12.2)

                    // if service and binding does exist, update the corresponding associations in the current
                    // document

                    // remove existing service and service binding
                    idsToRemove.add(st.getId());
                }

                // check for existing organizations
                else if (iT instanceof OrganizationType) {
                    OrganizationType ot = (OrganizationType) iT;
                    log.warn("REMOVING " + XmlTools.inspect(ot));
                    idsToRemove.add(ot.getId());
                }
            }

            removeAll(registryObjectList, idsToRemove);

            log.info("Processed RegistryPackage: " + doc.getIdentifiable().getId());
        }
    }

    @Override
    public List<OwsExceptionReport> pushAllDataToCatalog() throws OwsExceptionReport {
        return pushAllDataToCatalog(OLDEST_PUSH_DATE);
    }

    @Override
    public List<OwsExceptionReport> pushAllDataToCatalog(Date lastPush) throws OwsExceptionReport {
        log.info("Start pushing all data to catalog for all data after " + lastPush + " ...");
        this.lastPushSummary = new int[3];

        // create documents with all sensors, classification nodes and associations
        List<Pair<Document, Integer>> inserts = createInsertTransactions(lastPush);

        // send request and handle response seperately for each document, log errors if any occured
        List<OwsExceptionReport> reports = new ArrayList<>();
        for (Pair<Document, Integer> currentPair : inserts) {
            try {
                log.debug("Sending Document with {} sensor description(s) to client {}",
                          currentPair.getSecond(),
                          this.client);
                SOAPMessage responseMessage = this.client.send(currentPair.getFirst());
                handleTransactionResponse(responseMessage, currentPair.getSecond().intValue());
            }
            catch (OwsExceptionReport e) {
                reports.add(e);
            }
        }

        inserts = null;
        return reports;
    }

    /**
     * Removes all id listed in the second parameter from the first list.
     * 
     * @param registryObjectList
     *        the registry object list
     * @param idsToRemove
     *        the ids to remove
     */
    private void removeAll(RegistryObjectListType registryObjectList, ArrayList<String> idsToRemove) {
        IdentifiableType[] identifiables = registryObjectList.getIdentifiableArray();
        int beforeLength = identifiables.length;
        for (int i = 0; i < identifiables.length; i++) {
            IdentifiableType current = identifiables[i];

            if (idsToRemove.contains(current.getId())) {
                log.debug("Removing identifiable from registry object list: {} with index {}", current.getId(), i);
                registryObjectList.removeIdentifiable(i);
                identifiables = registryObjectList.getIdentifiableArray();
            }
        }
        if ( !idsToRemove.isEmpty())
            log.info("Removed " + (beforeLength - identifiables.length) + " of " + beforeLength
                    + " element(s) from registry object list with the ids " + Arrays.toString(idsToRemove.toArray()));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CswCatalog [client=");
        sb.append(this.client);
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * Method tries to transform the given collection of sensor description documents. if there is a problem
     * with one of the documents the returned list will contain the kvp <"sensorID", NULL>. This allows
     * alerting the user to check the log for details.
     * 
     * @param sensors
     * @return
     */
    private Hashtable<String, RegistryPackageDocument> transformSensorDescriptions(Collection<SirSearchResultElement> sensors) {
        Hashtable<String, RegistryPackageDocument> transformedDocs = new Hashtable<>();
        XmlObject description;

        for (SirSearchResultElement sensorResultElem : sensors) {
            log.debug("Transforming sensor description of sensor {}", sensorResultElem.getSensorId());

            // get SensorML
            SirXmlSensorDescription sensorDescr = (SirXmlSensorDescription) sensorResultElem.getSensorDescription();
            description = sensorDescr.getDescription();

            // having problems with XmlValueDisconnectedException, try transforming a deep copy
            XmlObject copy = description.copy();

            boolean isConform;
            try {
                isConform = acceptsDocument(copy);
            }
            catch (OwsExceptionReport | IOException e) {
                log.error("Could not check if catalog accepts the given document!", e);
                continue;
            }

            if (isConform) {
                // transform SensorML
                XmlObject ebrimDescription;
                try {
                    if (copy instanceof SystemType) {
                        SystemType st = (SystemType) copy;
                        ebrimDescription = this.transformer.transform(st);
                    }
                    else if (copy instanceof SensorMLDocument) {
                        SensorMLDocument smlDoc = (SensorMLDocument) copy;
                        ebrimDescription = this.transformer.transform(smlDoc);
                    }
                    else {
                        throw new UnsupportedOperationException("Sensor description is of unsupported type (must be either SystemType or SensorMLDocument): "
                                + copy.xmlText());
                    }
                }
                catch (XmlException | TransformerException | XmlValueDisconnectedException | IOException e) {
                    log.error("Exception: Could not transform sensor description: " + XmlTools.inspect(description)
                            + "\n  - TransformerException: " + e);
                    transformedDocs.put(sensorResultElem.getSensorId(), ITransformer.TRANSFORMATION_ERROR_OBJECT);
                    continue;
                }

                // convert transformed document to identifiable type
                RegistryPackageDocument registryPackage;
                try {
                    registryPackage = RegistryPackageDocument.Factory.parse(ebrimDescription.getDomNode());
                }
                catch (XmlException e) {
                    log.warn("Could not parse sensor description to IdentifiableType: "
                                     + XmlTools.inspect(ebrimDescription),
                             e);
                    transformedDocs.put(sensorResultElem.getSensorId(), ITransformer.TRANSFORMATION_ERROR_OBJECT);
                    continue;
                }

                if (SirConfigurator.getInstance().isValidateRequests()) {
                    // check if the transformed document for the outgoing request is valid and warn if not.
                    if (registryPackage.validate()) {
                        log.debug("Added new (valid!) transformed sensor description for sensor with id {}:\n{}",
                                  sensorResultElem.getSensorId(),
                                  XmlTools.inspect(ebrimDescription));
                    }
                    else {
                        String errors = XmlTools.validateAndIterateErrors(registryPackage);
                        log.warn("Transformed sensor description sensor with id " + sensorResultElem.getSensorId()
                                + " IS NOT VALID and might not be accepted by the service.");
                        log.debug("\nErrors:\t{}\nebRIM:\t{}", errors, ebrimDescription.xmlText());

                    }
                }

                // add transformed document and sensor id
                transformedDocs.put(sensorResultElem.getSensorId(), registryPackage);

                log.info("Transformed sensor with id " + sensorResultElem.getSensorId()
                        + " to RegistryPackage with id " + registryPackage.getIdentifiable().getId());
            }
            else {
                log.warn("Could not transform sensor description of sensor " + sensorResultElem.getSensorId()
                        + ". It is not conform with this catalog's required profile!");
                transformedDocs.put(sensorResultElem.getSensorId(), ITransformer.TRANSFORMATION_ERROR_OBJECT);
            }
        } // for loop
        return transformedDocs;
    }
}