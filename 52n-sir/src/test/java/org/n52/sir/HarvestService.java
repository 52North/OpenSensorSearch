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
package org.n52.sir;

import java.io.File;

import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.sir.client.Client;
import org.n52.sir.client.HarvestServiceBean;

import de.uniMuenster.swsl.sir.HarvestServiceRequestDocument;
import de.uniMuenster.swsl.sir.HarvestServiceResponseDocument;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class HarvestService extends SirTestCase {

    private String serviceURL = "http://v-swe.uni-muenster.de:8080/WeatherSOS/sos";

    private String serviceType = "SOS";

    @Test
    public void testPost() throws Exception {
        // buildRequest
        HarvestServiceBean hsb = new HarvestServiceBean(this.serviceURL, this.serviceType);

        hsb.buildRequest();

        // send request
        String response = Client.sendPostRequest(hsb.getRequestString());

        // parse and validate response
        HarvestServiceResponseDocument cd = HarvestServiceResponseDocument.Factory.parse(response);
        assertTrue(cd.validate());
    }

    @Test
    public void testPostExample() throws Exception {
        File f = getPostExampleFile("HarvestService_WeatherSOS.xml");
        HarvestServiceRequestDocument hsrd = HarvestServiceRequestDocument.Factory.parse(f);

        XmlObject response = Client.xSendPostRequest(hsrd);

        // parse and validate response
        HarvestServiceResponseDocument cd = HarvestServiceResponseDocument.Factory.parse(response.getDomNode());
        assertTrue(cd.validate());
    }

}