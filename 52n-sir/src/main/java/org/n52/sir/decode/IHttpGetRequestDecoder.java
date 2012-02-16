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
package org.n52.sir.decode;

import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.request.AbstractSirRequest;

/**
 * interface offers parsing method to parse the String representing a Get request and create a Sir request
 * 
 * @author Jan Schulte
 * 
 */
public interface IHttpGetRequestDecoder {

    /**
     * parses the String representing the Get-request and returns an internal SIR representation of the
     * request
     * 
     * @param capString
     *        String with the getCapabilities parameters
     * @return Returns SirGetCapabilitiesRequest representing the request
     * @throws OwsExceptionReport
     *         If parsing the string failed
     */
    public AbstractSirRequest receiveRequest(String capString) throws OwsExceptionReport;

}