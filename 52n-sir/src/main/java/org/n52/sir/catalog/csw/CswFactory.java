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
    private String[] classificationInitFiles;
    private String slotInitFile;
    private List<XmlObject> classificationInitDocs;
    private XmlObject slotInitDoc;
    private boolean doNotCheck;

    /**
     * Create a CSW with the given URL.
     * 
     * @param catalogUrl
     * @throws IOException 
     * @throws XmlException 
     */
    public CswFactory(URL catalogUrl, String[] classificationInitFiles, String slotInitFile, Boolean doNotCheckCatalog) throws XmlException, IOException {
        this.catalogUrl = catalogUrl;
        this.classificationInitFiles = classificationInitFiles;
        this.slotInitFile = slotInitFile;
        
        FileReader reader;
        XmlObject e;
        this.classificationInitDocs = new ArrayList<XmlObject>();
        for (String filename : classificationInitFiles) {
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

    @Override
    public URL getCatalogUrl() {
        return this.catalogUrl;
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
}
