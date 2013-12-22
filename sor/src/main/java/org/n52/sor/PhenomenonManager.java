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
package org.n52.sor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import net.opengis.gml.DefinitionType;
import net.opengis.gml.DictionaryDocument;
import net.opengis.gml.DictionaryEntryType;
import net.opengis.gml.DictionaryType;
import net.opengis.swe.x101.PhenomenonType;

import org.n52.sor.OwsExceptionReport.ExceptionCode;
import org.n52.sor.datastructures.DictionaryEntryImpl;
import org.n52.sor.datastructures.IDictionaryEntry;
import org.n52.sor.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles the phenomenon list and all work with it. (singleton)
 * 
 * It basically serves as the database. It's a singleton and the access methods are synchronized (though this
 * is not extensively tested) for concurrent access. After every insert or delete operation it saves a backup
 * file. The count of backup files can be set in the properties file.
 * 
 * @created 20-Okt-2008 16:44:48
 * @version 1.0
 * @author Jan Schulte, Daniel Nüst
 */
public class PhenomenonManager {

    private static Logger log = LoggerFactory.getLogger(PhenomenonManager.class);

    /**
     * SorManager instance to be a singleton
     */
    private static PhenomenonManager instance = null;

    /**
     * collection of the phenomenas
     * 
     * TODO make this a hashmap with identifier as key and IDictionaryEntry as value for quicker querying
     */
    private Collection<IDictionaryEntry> phenomenaList = new ArrayList<>();

    /**
     * The dictionary with all phenomenas
     */
    private DictionaryDocument dictionary;

    /**
     * 
     */
    private static final SimpleDateFormat backupSdf = new SimpleDateFormat("yyMMdd-HHmmss.SSSZ");

    /**
     * 
     */
    protected static final CharSequence BACKUP_INFIX = "_backup_";

    /**
     * This methode provides the only instance of SORManager.
     * 
     * @return instance of the SorManager
     * @throws OwsExceptionReport
     */
    public synchronized static PhenomenonManager getInstance() throws OwsExceptionReport {
        if (instance == null)
            instance = new PhenomenonManager();
        
        return instance;
    }

    /**
     * private constructor for singleton pattern
     */
    private PhenomenonManager() {
        try {
            loadDictionaryFile();
        }
        catch (OwsExceptionReport e) {
            log.error("Could not load dictionary file.", e);
        }
    }

    /**
     * 
     */
    private void backupDictionaryFile() {
        PropertiesManager pm = PropertiesManager.getInstance();
        if (pm.getPhenomenonXmlBackupCount() > 0) {
            String phenFilePath = pm.getPhenomenonXMLPath();

            // check if maximum count backup files exist already
            File dataDir = new File(phenFilePath);
            dataDir = new File(dataDir.getParent());

            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.contains(BACKUP_INFIX);
                }
            };
            String[] children = dataDir.list(filter);

            if (children.length >= pm.getPhenomenonXmlBackupCount()) {
                // delete oldest backup
                Arrays.sort(children);
                File oldest = new File(children[0]);
                log.info("Deleting backup file " + oldest.getName());
                if ( !oldest.delete()) {
                    log.error("Could not delete backup file, possibly creating a lot of files then!");
                }
            }

            // save backup
            try (FileInputStream in = new FileInputStream(phenFilePath);
                    FileOutputStream out = new FileOutputStream(pm.getPhenomenonXMLPath() + BACKUP_INFIX
                            + backupSdf.format(new Date()));) {

                byte[] buf = new byte[1024];
                int len;
                while ( (len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();
            }
            catch (FileNotFoundException e) {
                log.error("Could not save backup of phenomenon file!");
            }
            catch (IOException e) {
                log.error("Could not save backup of phenomenon file!");
            }

        }
    }

    /**
     * Checks the phenomenaList, if it contains a given definition URI
     * 
     * @param definitionURI
     *        the given definition URI
     * @return The dictionary Entry if the given definitionURI is in the List, else return null
     */
    private IDictionaryEntry checkInPhenomenonList(String definitionURI) {
        for (IDictionaryEntry dicEntry : this.phenomenaList) {
            if (dicEntry.getIdentifier().equals(definitionURI)) {
                return dicEntry;
            }
        }
        return null;
    }

    /**
     * Creates a dictonary entry of the item.
     * 
     * @param definitionType
     *        The item of the dictonary entry
     * @return The DictonaryEntry
     * @throws OwsExceptionReport
     */
    private IDictionaryEntry createDictEntry(DefinitionType definitionType) throws OwsExceptionReport {
        // only mandatory element in schema is gml:identifier, check for that
        if (definitionType != null && definitionType.isSetId()) {

            if (definitionType instanceof PhenomenonType) {
                PhenomenonType pT = (PhenomenonType) definitionType;
                IDictionaryEntry entry = new DictionaryEntryImpl(pT);

                if (log.isDebugEnabled()) {
                    log.debug("Created entry from definition: " + entry);
                }

                return entry;
            }
            log.error("Cannot create DictionaryEntry because definition is not a swe:Phenomenon!");
            throw new OwsExceptionReport(ExceptionCode.MissingParameterValue,
                                         "Definition",
                                         "Cannot create DictionaryEntry because definition is not a swe:Phenomenon!");
        }

        log.error("Cannot create DictionaryEntry because gml:identifer is missing: " + definitionType);
        throw new OwsExceptionReport(ExceptionCode.MissingParameterValue,
                                     "gml:identifier",
                                     "Cannot create DictionaryEntry because gml:identifer is missing!");
    }

    /**
     * Deletes the given definition of the Phenomenon List
     * 
     * @param definitionURI
     *        The definition URI
     * @return true - if the given definition is deleted, false - if the given definition is not deleted
     */
    public synchronized boolean deleteOfPhenomenonList(String definitionURI) {
        backupDictionaryFile();

        IDictionaryEntry dictionaryEntry = checkInPhenomenonList(definitionURI);
        if (dictionaryEntry == null) {
            log.warn("No entry found for given definition URI '" + definitionURI + "' - not deleting anything.");
            return false;
        }

        // delete of the List
        this.phenomenaList.remove(dictionaryEntry);

        // delete of the File
        DictionaryType dictionaryType = this.dictionary.getDictionary();
        for (int i = 0; i < dictionaryType.getDictionaryEntryArray().length; i++) {
            String currentURI = dictionaryType.getDictionaryEntryArray(i).getDefinition().getId();
            if (currentURI.equals(definitionURI)) {
                dictionaryType.removeDictionaryEntry(i);
                break;
            }
        }

        saveDictionaryFile();
        return true;
    }

    /**
     * Searches for phenomena URIs related with a list of ontology URLs passed as a parameter
     * 
     * @param ontologyURIlist
     *        List of Strings containing one or more ontology URLs
     * @return List of Strings containing phenomena URIs
     */
    public Collection<String> getEquivalentURI(Collection<String> ontologyURIlist) {
        Collection<String> result = new ArrayList<>();
        for (IDictionaryEntry dicEntry : this.phenomenaList) {
            for (String url : ontologyURIlist) {
                if (dicEntry.getUrl() != null && dicEntry.getUrl().equals(url)) {
                    result.add(dicEntry.getIdentifier());
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Searches for the URL of the ontology related with the URI of a phenomena passed as a parameter.
     * 
     * @param phenomenaURI
     *        String URI of a phenomena
     * @return String URL of an ontology
     */
    public String getOntology(String phenomenaURI) {
        for (IDictionaryEntry dicEntry : this.phenomenaList) {
            if (dicEntry.getIdentifier().equals(phenomenaURI)) {
                return dicEntry.getUrl();
            }
        }
        return null;
    }

    /**
     * @return A collection of all phenomenons
     */
    public Collection<IDictionaryEntry> getPhenomenaList() {
        return this.phenomenaList;
    }

    /**
     * Inserts the given definition into the phenomenon List
     * 
     * @param definitionURI
     *        The definition URI
     * @param phenomenon
     *        The phenomenon dictionary
     * @return true - if the given definition is inserted, false - if the given definition is not inserted
     * @throws OwsExceptionReport
     * 
     */
    public synchronized boolean insertInPhenomenonList(String definitionURI, PhenomenonType phenomenon) throws OwsExceptionReport {
        if (checkInPhenomenonList(definitionURI) == null) {
            backupDictionaryFile();

            // create entry and insert in the list
            IDictionaryEntry newPhen = createDictEntry(phenomenon);
            this.phenomenaList.add(newPhen);

            // insert in the file
            DictionaryEntryType dictionaryEntry = this.dictionary.getDictionary().addNewDictionaryEntry();
            DefinitionType newDefinition = phenomenon;
            dictionaryEntry.setDefinition(newDefinition);
            saveDictionaryFile();

            return true;
        }

        log.warn("Phenomenon NOT inserted, as already contained in the existing dictionary!");

        throw new OwsExceptionReport(ExceptionCode.InvalidParameterValue,
                                     "gml:identifier",
                                     "This SOR already contains an observable with the given identifier '"
                                             + definitionURI
                                             + "'. If you want to update this definition, please remove it first (using DeleteDefinition).");
    }

    /**
     * This methode creates the phenomenon list of the phenomenon xml file.
     * 
     * @throws OwsExceptionReport
     */
    private synchronized void loadDictionaryFile() throws OwsExceptionReport {
        String path = PropertiesManager.getInstance().getPhenomenonXMLPath();
        log.info("Create phenomena list of file: " + path);

        try {
            this.dictionary = DictionaryDocument.Factory.parse(new File(path));
        }
        catch (Exception e) {
            log.error("Error on parsing phenomena list!");
            e.printStackTrace();
        }

        DictionaryEntryType[] dictionaryEntryArray = this.dictionary.getDictionary().getDictionaryEntryArray();

        for (DictionaryEntryType dictionaryEntryType : dictionaryEntryArray) {
            DefinitionType definition = dictionaryEntryType.getDefinition();
            if (definition != null) {
                IDictionaryEntry e = createDictEntry(definition);
                this.phenomenaList.add(e);
            }
            else
                log.warn("DefinitionType is null for " + dictionaryEntryType.xmlText());
        }

        log.info("Dictionary loaded!" + path);
    }

    /**
     * 
     */
    private void saveDictionaryFile() {
        try {
            if ( !this.dictionary.validate())
                log.warn("Saving invalid dictionary!\n" + XmlTools.validateAndIterateErrors(this.dictionary));

            // save new dictionary
            this.dictionary.save(new File(PropertiesManager.getInstance().getPhenomenonXMLPath()));
        }
        catch (IOException e) {
            log.error("Error on saving the phenomenon list!");
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param definitionURI
     * @param phenomenon
     * @return
     * @throws OwsExceptionReport
     */
    public synchronized boolean updateInPhenomenonList(String definitionURI, PhenomenonType phenomenon) throws OwsExceptionReport {
        backupDictionaryFile();

        IDictionaryEntry dictionaryEntry = checkInPhenomenonList(definitionURI);
        if (dictionaryEntry == null) {
            return false;
        }
        // update in the list
        this.phenomenaList.remove(dictionaryEntry);
        this.phenomenaList.add(createDictEntry(phenomenon));

        // update in the file
        DictionaryType dictionaryType = this.dictionary.getDictionary();
        for (int i = 0; i < dictionaryType.getDictionaryEntryArray().length; i++) {
            String currentId = dictionaryType.getDictionaryEntryArray(i).getDefinition().getId();
            if (currentId.equals(definitionURI)) {
                dictionaryType.removeDictionaryEntry(i);
                break;
            }
        }

        DictionaryEntryType newEntry = this.dictionary.getDictionary().addNewDictionaryEntry();
        DefinitionType newDefinition = DefinitionType.Factory.newInstance();
        newDefinition = phenomenon;
        newEntry.setDefinition(newDefinition);

        saveDictionaryFile();
        return true;
    }
}