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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xmlbeans.XmlException;
import org.n52.sir.catalog.ICatalog;
import org.n52.sir.catalog.ICatalogFactory;
import org.n52.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class CatalogTest {

    private static Logger log = LoggerFactory.getLogger(CatalogTest.class);

    /**
     * @param args
     * @throws MalformedURLException
     */
    public static void main(String[] args) throws MalformedURLException {

        getCap();

        System.exit(0);
    }

    /**
     * 
     */
    private static void getCap() {
        URL url;
        try {
            url = new URL("http://localhost:8080/ergorr/webservice");
        }
        catch (MalformedURLException e1) {
            e1.printStackTrace();
            return;
        }

        String[] classInit = new String[] {"/home/daniel/workspace/SIR/WebContent/WEB-INF/conf/sirClassificationInit.xml",
                                           "/home/daniel/workspace/SIR/WebContent/WEB-INF/conf/ISO19119-Services-Scheme.xml"};
        String slotInit = "/home/daniel/workspace/SIR/WebContent/WEB-INF/conf/sirSlotInit.xml";

        ICatalogFactory factory;
        try {
            factory = new CswFactory(url, classInit, slotInit, Boolean.FALSE);
        }
        catch (XmlException e) {
            log.error("Could not parse classification scheme file!", e);
            return;
        }
        catch (IOException e) {
            log.error("Could not read classification scheme file!", e);
            return;
        }

        ICatalog client;
        try {
            client = factory.getCatalog();
        }
        catch (OwsExceptionReport e1) {
            e1.printStackTrace();
            return;
        }

        log.debug(client.toString());
        boolean b;
        try {
            b = client.ensureSufficientCapabilities();
            log.debug("Are capabilities sufficient? " + b);
        }
        catch (OwsExceptionReport e) {
            e.printStackTrace();
        }
    }
}
