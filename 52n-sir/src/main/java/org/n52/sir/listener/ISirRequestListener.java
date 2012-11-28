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

package org.n52.sir.listener;

import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.response.ISirResponse;

/**
 * interface for the request listeners
 * 
 * @author Jan Schulte
 * 
 */
public interface ISirRequestListener {

    /**
     * @return Returns the name of the supported operation
     */
    public String getOperationName();

    /**
     * method handles the incoming operation request an returns a matching response or an
     * ServiceExceptionReport if the SIR was not able to build a response
     * 
     * @param request
     *        the operation request
     * @return Returns the responose of the request or an operation failed
     */
    public ISirResponse receiveRequest(AbstractSirRequest request);

}