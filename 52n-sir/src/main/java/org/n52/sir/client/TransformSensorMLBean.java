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
package org.n52.sir.client;

import javax.xml.transform.TransformerException;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sir.SirConfigurator;
import org.n52.sir.xml.ITransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class TransformSensorMLBean extends AbstractBean {

    private static Logger log = LoggerFactory.getLogger(TransformSensorMLBean.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.client.beans.AbstractBean#buildRequest()
     */
    @Override
    public void buildRequest() {
        // do nothing...
    }

    /**
     * 
     */
    public void requestTransformation() {
        this.responseString = "";
        
        ITransformer transformer = SirConfigurator.getInstance().getTransformerFactory().getSensorMLtoCatalogXMLTransformer();

        SensorMLDocument sml = null;
        try {
            sml = SensorMLDocument.Factory.parse(getRequestString());
        }
        catch (XmlException e) {
            log.error("Error parsing input.", e);
            setResponseString("Exception:\n" + e.getMessage());
        }

        try {
            XmlObject ebrim = transformer.transform(sml);
            setResponseString(ebrim.toString());
        }
        catch (XmlException e) {
            log.error("Error during transformation.", e);
            setResponseString("Exception:\n" + e.getMessage());
        }
        catch (TransformerException e) {
            log.error("Error during transformation.", e);
            setResponseString("Exception:\n" + e.getMessage());
        }
    }

}
