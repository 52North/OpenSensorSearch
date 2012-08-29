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

package org.n52.ar.wikitude;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.ar.SirCallbackServlet;
import org.n52.ar.SirPOI;
import org.n52.sir.json.SearchResultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 */
public class WikitudeServlet extends SirCallbackServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 7433725421346384867L;
    
    private static Logger log = LoggerFactory.getLogger(WikitudeServlet.class);

    public WikitudeServlet() {
        log.debug("NEW {}", this);
    }

    @Override
    protected SirPOI createPOI(SearchResultElement sre) {
        // TODO implement
        return null;
    }

    /**
     * http://www.wikitude.com/developer/publish-details
     * 
     * Just need a ARML transformer??
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userAgent = request.getHeader("User-agent");
        log.debug("User-agent={}", userAgent);
        
        // TODO implement callback URL
        
        // get parameters

        // query SIR
        
        // write response

        // clean up
        
        response.flushBuffer();
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        log.info("Initialized " + this);
    }
}