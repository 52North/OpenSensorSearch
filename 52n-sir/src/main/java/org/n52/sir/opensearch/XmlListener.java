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

package org.n52.sir.opensearch;

import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.response.SirSearchSensorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlListener implements IOpenSearchListener {

    private static final Logger log = LoggerFactory.getLogger(XmlListener.class);

    private static final String MIME_TYPE = "application/xml";

    private static final String NAME = "XML";

    private OpenSearchConfigurator conf;

    public XmlListener(OpenSearchConfigurator configurator) {
        this.conf = configurator;
        this.conf.addResponseFormat(this);
    }

    @Override
    public void createResponse(HttpServletRequest req,
                               HttpServletResponse resp,
                               Collection<SirSearchResultElement> searchResult,
                               PrintWriter writer,
                               String searchText) throws OwsExceptionReport {
        log.debug("Creating XML response for {}", searchText);

        resp.setContentType(MIME_TYPE);
        SirSearchSensorResponse sssr = new SirSearchSensorResponse();
        sssr.setSearchResultElements(searchResult);
        sssr.writeTo(writer);

        log.debug("Done with XML response.");
    }

    @Override
    public String getMimeType() {
        return MIME_TYPE;
    }

    @Override
    public String getName() {
        return NAME;
    }

}
