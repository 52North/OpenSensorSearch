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
package org.n52.sor.reasoner;

import java.util.List;

import org.n52.sor.OwsExceptionReport;

/**
 * 
 * @author Jan Schulte
 * 
 */
public interface IReasoner {

    public enum MatchingCode {
        SUPER_CLASS, EQUIVALENT_CLASS, SUB_CLASS
    }

    /**
     * Looks in the ontology for the matching phenomenons regarding a given URL.
     * 
     * @param ontologyURL
     *        Phenomenon URL
     * @param code
     *        Matching relationship between phenomenon: MatchingCode.Super_Class,
     *        MatchingCode.Equivalent_Class or MatchingCode.Sub_Class.
     * @param searchDepth
     *        Depth of the matching phenomenon search in the ontology. From 1 onwards.
     * @return List with the related URL phenomenons
     * @throws OwsExceptionReport
     */
    public List<String> getMatchingURLs(String ontologyURL, MatchingCode code, int searchDepth) throws OwsExceptionReport;

}