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

import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.catalog.CatalogConnectionImpl;
import org.n52.sir.catalog.ICatalog;
import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.catalog.ICatalogFactory;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.xml.IProfileValidator;
import org.n52.sir.xml.ITransformer;
import org.n52.sir.xml.ITransformer.TransformableFormat;
import org.n52.sir.xml.TransformerModule;
import org.n52.sir.xml.ValidatorModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class CswFactory implements ICatalogFactory {

    private static final String CONFIG_FILE_LIST_SEPARATOR = ",";

    private static Logger log = LoggerFactory.getLogger(CswFactory.class);

    private List<XmlObject> classificationInitDocs;

    private String[] classificationInitFilesArray;

    private XmlObject slotInitDoc;

    private String slotInitFile;

    private ITransformer transformer;

    private ArrayList<URL> doNotCheckCatalogsList;

    private ISearchSensorDAO searchDao;

    private IProfileValidator validator;

    @Inject
    public CswFactory(@Named("oss.catalogconnection.csw-ebrim.classificationInitFilenames")
    String classificationInitFilenames, @Named("oss.catalogconnection.csw-ebrim.slotInitFilename")
    String slotInitFile, @Named("oss.catalogconnection.doNotCheckCatalogs")
    String doNotCheckCatalogs, Set<ITransformer> transformers, @Named(ISearchSensorDAO.FULL)
    ISearchSensorDAO searchDao, Set<IProfileValidator> validators) throws XmlException, IOException {
        this.slotInitFile = getAbsolutePath(slotInitFile);
        this.searchDao = searchDao;

        try (FileReader reader = new FileReader(this.slotInitFile);) {
            this.slotInitDoc = XmlObject.Factory.parse(reader);
            log.debug("Loaded slot init doc: {}", this.slotInitDoc);
        }

        this.transformer = TransformerModule.getFirstMatchFor(transformers,
                                                              TransformableFormat.SML,
                                                              TransformableFormat.EBRIM);

        XmlObject initDoc;
        this.classificationInitDocs = new ArrayList<>();
        this.classificationInitFilesArray = classificationInitFilenames.split(CONFIG_FILE_LIST_SEPARATOR);
        for (String filename : this.classificationInitFilesArray) {
            String filePath = getAbsolutePath(filename.trim());
            try (FileReader reader = new FileReader(filePath);) {
                initDoc = XmlObject.Factory.parse(reader);
                this.classificationInitDocs.add(initDoc);
            }
        }
        log.debug("Loaded classification files: {}\nDocs: {}",
                  Arrays.toString(this.classificationInitFilesArray),
                  Arrays.toString(this.classificationInitDocs.toArray()));

        this.doNotCheckCatalogsList = new ArrayList<>();
        String[] splitted = doNotCheckCatalogs.split(CONFIG_FILE_LIST_SEPARATOR);
        if (splitted.length > 0) {
            for (String s : splitted) {
                try {
                    if ( !s.isEmpty())
                        this.doNotCheckCatalogsList.add(new URL(s.trim()));
                }
                catch (MalformedURLException e) {
                    log.error("Could not parse catalog url to 'do not check' list. Catalog will be checked during runtime!",
                              e);
                }
            }
        }
        log.debug("Loaded do-not-check-catalogs: {}", Arrays.toString(this.doNotCheckCatalogsList.toArray()));

        this.validator = ValidatorModule.getFirstMatchFor(validators,
                                                          IProfileValidator.ValidatableFormatAndProfile.SML_DISCOVERY);

        log.info("NEW {}", this);
    }

    private String getAbsolutePath(String file) {
        try {
            URL r = getClass().getResource(file);
            Path p = Paths.get(r.toURI());
            String s = p.toAbsolutePath().toString();
            return s;
        }
        catch (URISyntaxException e) {
            log.error("Could not load resource " + file, e);
            return file;
        }
    }

    @Override
    public ICatalog getCatalog(URL catalogUrl) throws OwsExceptionReport {
        SimpleSoapCswClient client = new SimpleSoapCswClient(catalogUrl, catalogIsOnDoNotCheckList(catalogUrl));
        CswCatalog catalog = new CswCatalog(this.searchDao,
                                            client,
                                            this.classificationInitDocs,
                                            this.slotInitDoc,
                                            this.transformer,
                                            this.validator);
        return catalog;
    }

    private boolean catalogIsOnDoNotCheckList(URL catalogUrl) {
        return this.doNotCheckCatalogsList.contains(catalogUrl);
    }

    /**
     * 
     * @param connectionID
     * @param url
     * @param pushInterval
     * @param newConnectionStatus
     * @return an instance of the {@link ICatalogConnection} implementation used within the clients of this
     *         factory.
     */
    @Override
    public ICatalogConnection getCatalogConnection(String connectionID,
                                                   URL url,
                                                   int pushInterval,
                                                   String newConnectionStatus) {
        return new CatalogConnectionImpl(connectionID, url, pushInterval, ICatalogConnection.NEW_CONNECTION_STATUS);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CswFactory [");
        if (this.classificationInitDocs != null) {
            builder.append("classificationInitDocs=");
            builder.append(this.classificationInitDocs);
            builder.append(", ");
        }
        if (this.classificationInitFilesArray != null) {
            builder.append("classificationInitFiles=");
            builder.append(Arrays.toString(this.classificationInitFilesArray));
            builder.append(", ");
        }
        if (this.slotInitDoc != null) {
            builder.append("slotInitDoc=");
            builder.append(this.slotInitDoc);
            builder.append(", ");
        }
        if (this.slotInitFile != null) {
            builder.append("slotInitFile=");
            builder.append(this.slotInitFile);
        }
        builder.append("]");
        return builder.toString();
    }

}
