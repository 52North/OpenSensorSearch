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

package org.n52.sir;

/**
 * @author Yakoub 
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.detailed.SirDetailedSensorDescription;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoCompleteSearch extends HttpServlet {

    private static final long serialVersionUID = 5313315792207485425L;

    private static Logger log = LoggerFactory.getLogger(SOLRSearchSensorDAO.class);

    private static final String REQUEST_PARAM_AUTOCOMPLETE = "text";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String text = req.getParameter(REQUEST_PARAM_AUTOCOMPLETE);
        log.trace("new GET request for autocomplete: {}", text);

        // TODO return reasonable error message and status code
        if (text == null)
            throw new RuntimeException("Query parameter " + REQUEST_PARAM_AUTOCOMPLETE + " must be given.");

        try (PrintWriter pw = resp.getWriter();) {
            // I'm using a set to avoid duplications
            Collection<Object> results = new HashSet<>();
            SOLRSearchSensorDAO dao = new SOLRSearchSensorDAO();
            Collection<SirSearchResultElement> search_results = dao.searchByAll(text,
                                                                                null,
                                                                                null,
                                                                                null,
                                                                                null,
                                                                                null,
                                                                                null);
            Iterator<SirSearchResultElement> it = search_results.iterator();

            while (it.hasNext()) {
                SirSearchResultElement element = it.next();
                SirDetailedSensorDescription desc = (SirDetailedSensorDescription) element.getSensorDescription();
                results.addAll(desc.getKeywords());
                if (desc.getContacts() != null)
                    results.addAll(desc.getContacts());
                if (desc.getDescription() != null)
                    results.add(desc.getDescription().toString());
                if (desc.getInputs() != null)
                    results.addAll(desc.getInputs());
                if (desc.getOutputs() != null)
                    results.addAll(desc.getOutputs());
                if (desc.getIdentifiers() != null)
                    results.addAll(desc.getIdentifiers());
                if (desc.getClassifiers() != null)
                    results.addAll(desc.getClassifiers());
            }

            // returns the result as json array
            if (results.size() == 0)
                pw.println(""); // FIXME more helpful response message
            else {
                Iterator<Object> iterator = results.iterator();
                StringBuilder res = new StringBuilder();
                res.append(iterator.next());
                while (iterator.hasNext()) {
                    res.append(",");
                    res.append(iterator.next());
                }
                pw.println(res.toString());
            }
            pw.flush();
            resp.flushBuffer();
            log.debug("Done serving servlet");
        }
        catch (Exception e) {
            log.error("error on searching", e);
        }

    }

}
