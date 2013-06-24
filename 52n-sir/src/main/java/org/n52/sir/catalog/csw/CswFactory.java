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
import org.n52.sir.catalog.ICatalog;
import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.catalog.ICatalogFactory;
import org.n52.sir.ows.OwsExceptionReport;
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

    /**
     * Create a CSW with the given URL.
     * 
     * @param catalogUrl
     * @throws IOException
     * @throws XmlException
     */
    public CswFactory(URL catalogUrl, String[] classificationInitFiles, String slotInitFile, Boolean doNotCheckCatalog) throws XmlException,
            IOException {
        this.catalogUrl = catalogUrl;
        this.classificationInitFiles = classificationInitFiles;
        this.slotInitFile = slotInitFile;

        FileReader reader;
        XmlObject e;
        this.classificationInitDocs = new ArrayList<XmlObject>();
        for (String filename : classificationInitFiles) {
            // FIXME for using resources instead of file names
            reader = new FileReader(filename);
            e = XmlObject.Factory.parse(reader);
            this.classificationInitDocs.add(e);
        }

        reader = new FileReader(this.slotInitFile);
        this.slotInitDoc = XmlObject.Factory.parse(reader);

        this.doNotCheck = doNotCheckCatalog.booleanValue();

        reader.close();
        reader = null;
        e = null;

        log.info("NEW " + this);
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
        StringBuilder sb = new StringBuilder();
        sb.append("SensorMLCatalogFactory [cswUrl=");
        sb.append(this.catalogUrl);
        sb.append(", doNotCheck=");
        sb.append(this.doNotCheck);
        sb.append(", classificationInitFiles=");
        sb.append(Arrays.toString(this.classificationInitFiles));
        sb.append(", slotInitFile=");
        sb.append(this.slotInitFile);
        sb.append("]");
        return sb.toString();
    }
}
