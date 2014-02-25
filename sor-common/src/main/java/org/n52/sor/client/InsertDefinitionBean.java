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

package org.n52.sor.client;

import net.opengis.swe.x101.PhenomenonDocument;

import org.apache.xmlbeans.XmlException;
import org.n52.sor.PropertiesManager;
import org.n52.sor.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sor.x031.InsertDefinitionRequestDocument;
import org.x52North.sor.x031.InsertDefinitionRequestDocument.InsertDefinitionRequest;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class InsertDefinitionBean extends AbstractBean {

    private static Logger log = LoggerFactory.getLogger(InsertDefinitionBean.class);

    private String phenomenon = "";

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.client.AbstractBean#buildRequest()
     */
    @Override
    public void buildRequest() {
        InsertDefinitionRequestDocument requestDoc = InsertDefinitionRequestDocument.Factory.newInstance();
        InsertDefinitionRequest request = requestDoc.addNewInsertDefinitionRequest();
        request.setService(PropertiesManager.getInstance().getService());
        request.setVersion(PropertiesManager.getInstance().getServiceVersion());

        // phenomenon dictinary
        if ( !this.phenomenon.isEmpty()) {
            try {
                PhenomenonDocument phenDoc = PhenomenonDocument.Factory.parse(this.phenomenon);

                if ( !phenDoc.validate()) {
                    this.requestString = "The phenomenon is not a valid PhenomenonType!\n\n"
                            + XmlTools.validateAndIterateErrors(phenDoc);
                    return;
                }

                request.setPhenomenon(phenDoc.getPhenomenon());
            }
            catch (XmlException e) {
                this.requestString = "The phenomenon could not be parsed to swe:PhenomenonDocument!";
                return;
            }
        }
        else {
            this.requestString = "Phenomenon is mandatory.";
            return;
        }

        if ( !requestDoc.validate()) {
            log.warn("Request is NOT valid, service may return error!\n"
                    + XmlTools.validateAndIterateErrors(requestDoc));
        }

        this.requestString = requestDoc.toString();
    }

    /**
     * @return the phenomenon
     */
    public String getPhenomenon() {
        return this.phenomenon;
    }

    /**
     * @param phenomenon
     *        the phenomenon to set
     */
    public void setPhenomenon(String phenomenon) {
        this.phenomenon = phenomenon;
    }
}