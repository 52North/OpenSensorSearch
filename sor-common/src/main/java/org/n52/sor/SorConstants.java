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
package org.n52.sor;

/**
 * @author Daniel Nuest
 * 
 */
public class SorConstants {
    
    /**
     * enum with parameter names for GetCapabilities HTTP GET request
     */
    public enum GetCapGetParams {
        REQUEST, SERVICE, ACCEPTVERSIONS, SECTIONS, UPDATESEQUENCE, ACCEPTFORMATS;
    }

    /**
     * enum with parameter names for GetDefinition HTTP GET request
     */
    public enum GetDefParams {
        SERVICE, VERSION, INPUTURI;
    }
    
    /**
     * enum with parameter names for GetDefinitionURIs HTTP GET request
     */
    public enum GetDefURIsParams {
        SERVICE, VERSION, MAXNUMBEROFORESULTS, STARTRESULTELEMENT, SEARCHSTRING;
    }
    
    /**
     * enum with parameter names for GetMatchingDefinitions HTTP GET request
     */
    public enum GetMatchDefParams {
        SERVICE, VERSION, INPUTURI, MATCHINGTYPE, SEARCHDEPTH;
    }
    
    /**
     * enum for all supported request operations by the SOR
     */
    public enum Operations {
        GetCapabilities, GetDefinitionURIs, GetDefinition, GetMatchingDefinitions, InsertDefinition, DeleteDefinition
    }
    
    /**
     * Constant for the http get request REQUEST parameter
     */
    public static final String GETREQUESTPARAM = "REQUEST";

}
