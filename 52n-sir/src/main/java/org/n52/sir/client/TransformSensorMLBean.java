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
public class TransformSensorMLBean extends TestClientBean {

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
