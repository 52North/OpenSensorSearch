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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.catalog.ICatalog;
import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.catalog.ICatalogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class CswFactory implements ICatalogFactory {

    private static Logger log = LoggerFactory.getLogger(CswFactory.class);

    private URL catalogUrl;
    private List<XmlObject> classificationInitDocs;
    private String[] classificationInitFiles;
    private boolean doNotCheck;
    private XmlObject slotInitDoc;
    private String slotInitFile;

    public CswFactory(URL catalogUrl, String[] classificationInitFiles, String slotInitFile, boolean doNotCheckCatalog) throws XmlException,
            IOException {
        this.catalogUrl = catalogUrl;
        this.classificationInitFiles = classificationInitFiles;
        this.slotInitFile = slotInitFile;

        XmlObject e;
        this.classificationInitDocs = new ArrayList<>();
        for (String filename : classificationInitFiles) {
            try (FileReader reader = new FileReader(filename);) {
            e = XmlObject.Factory.parse(reader);
            this.classificationInitDocs.add(e);
            }
        }

        try (FileReader reader = new FileReader(this.slotInitFile);) {
        this.slotInitDoc = XmlObject.Factory.parse(reader);
        }

        this.doNotCheck = doNotCheckCatalog;

        log.info("NEW {}", this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ds.ICatalogFactory#getClient()
     */
    @Override
    public ICatalog getCatalog() throws OwsExceptionReport {
        SimpleSoapCswClient client = new SimpleSoapCswClient(this.catalogUrl, this.doNotCheck);
        CswCatalog catalog = new CswCatalog(client, this.classificationInitDocs, this.slotInitDoc);
        return catalog;
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
    public URL getCatalogUrl() {
        return this.catalogUrl;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CswFactory [");
        if (this.catalogUrl != null) {
            builder.append("catalogUrl=");
            builder.append(this.catalogUrl);
            builder.append(", ");
        }
        if (this.classificationInitDocs != null) {
            builder.append("classificationInitDocs=");
            builder.append(this.classificationInitDocs);
            builder.append(", ");
        }
        if (this.classificationInitFiles != null) {
            builder.append("classificationInitFiles=");
            builder.append(Arrays.toString(this.classificationInitFiles));
            builder.append(", ");
        }
        builder.append("doNotCheck=");
        builder.append(this.doNotCheck);
        builder.append(", ");
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
