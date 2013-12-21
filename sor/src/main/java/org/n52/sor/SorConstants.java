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
