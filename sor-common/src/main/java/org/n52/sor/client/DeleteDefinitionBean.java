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

import org.n52.sor.PropertiesManager;
import org.n52.sor.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sor.x031.DeleteDefinitionRequestDocument;
import org.x52North.sor.x031.DeleteDefinitionRequestDocument.DeleteDefinitionRequest;

/**
 * @author Jan Schulte
 * 
 */
public class DeleteDefinitionBean extends AbstractBean {

    private static Logger log = LoggerFactory.getLogger(DeleteDefinitionBean.class);

    private String definitionURI = "";

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.client.AbstractBean#buildRequest()
     */
    @Override
    public void buildRequest() {
        DeleteDefinitionRequestDocument requestDoc = DeleteDefinitionRequestDocument.Factory.newInstance();
        DeleteDefinitionRequest request = requestDoc.addNewDeleteDefinitionRequest();
        request.setService(PropertiesManager.getInstance().getService());
        request.setVersion(PropertiesManager.getInstance().getServiceVersion());

        // definition URI
        if ( !this.definitionURI.isEmpty()) {
            request.setDefinitionURI(this.definitionURI);
        }
        else {
            this.requestString = "Please enter a Definition URI!";
        }

        if ( !requestDoc.validate()) {
            log.warn("Request is NOT valid, service may return error!\n"
                    + XmlTools.validateAndIterateErrors(requestDoc));
        }

        this.requestString = requestDoc.toString();
    }

    /**
     * @return the definitionURI
     */
    public String getDefinitionURI() {
        return this.definitionURI;
    }

    /**
     * @param definitionURI
     *        the definitionURI to set
     */
    public void setDefinitionURI(String definitionURI) {
        this.definitionURI = definitionURI;
    }
}