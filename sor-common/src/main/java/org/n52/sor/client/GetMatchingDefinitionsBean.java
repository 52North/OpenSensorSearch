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

import org.n52.sor.ISorRequest.SorMatchingType;
import org.n52.sor.OwsExceptionReport;
import org.n52.sor.PropertiesManager;
import org.n52.sor.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sor.x031.GetMatchingDefinitionsRequestDocument;
import org.x52North.sor.x031.GetMatchingDefinitionsRequestDocument.GetMatchingDefinitionsRequest;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class GetMatchingDefinitionsBean extends AbstractBean {

    private static Logger log = LoggerFactory.getLogger(GetMatchingDefinitionsBean.class);

    private String inputURI = "";

    private String matchingTypeString;

    private int searchDepth;

    @Override
    public void buildRequest() {
        GetMatchingDefinitionsRequestDocument requestDoc = GetMatchingDefinitionsRequestDocument.Factory.newInstance();
        GetMatchingDefinitionsRequest request = requestDoc.addNewGetMatchingDefinitionsRequest();
        request.setService(PropertiesManager.getInstance().getService());
        request.setVersion(PropertiesManager.getInstance().getServiceVersion());

        // inputURI
        if ( !this.inputURI.isEmpty()) {
            request.setInputURI(this.inputURI);
        }
        else {
            this.requestString = "Please choose an input URI!";
            return;
        }

        // matchingType
        try {
            request.setMatchingType(SorMatchingType.getSorMatchingType(this.matchingTypeString).getSchemaMatchingType());
        }
        catch (OwsExceptionReport e) {
            log.warn("Matching type NOT supported!");
            this.requestString = "The matching type is not supported!\n\n" + e.getDocument();
        }

        // searchDepth
        request.setSearchDepth(this.searchDepth);

        if ( !requestDoc.validate()) {
            log.warn("Request is NOT valid, service may return error!\n"
                    + XmlTools.validateAndIterateErrors(requestDoc));
        }

        this.requestString = requestDoc.toString();
    }

    public String getInputURI() {
        return this.inputURI;
    }

    public String getMatchingTypeString() {
        return this.matchingTypeString;
    }

    public int getSearchDepth() {
        return this.searchDepth;
    }

    public void setInputURI(String inputURI) {
        this.inputURI = inputURI;
    }

    public void setMatchingTypeString(String matchingTypeString) {
        this.matchingTypeString = matchingTypeString;
    }

    public void setSearchDepth(int searchDepth) {
        this.searchDepth = searchDepth;
    }

}