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
package org.n52.sir;
/**
 * @author Yakoub 
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.sir.datastructure.SirSearchCriteria;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.solr.SirSolrSensorDescription;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.n52.sir.json.SearchResultElement;
import org.n52.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class AutoCompleteSearch extends HttpServlet {

	private static Logger log = LoggerFactory
			.getLogger(SOLRSearchSensorDAO.class);
	private static final String AUTOCOMPLETE = "text";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String text = req.getParameter(AUTOCOMPLETE);
		PrintWriter pw = resp.getWriter();
		// I'm using a set to avoid duplications
		Collection<Object> results = new HashSet<Object>();
		SOLRSearchSensorDAO dao = new SOLRSearchSensorDAO();
		SirSearchCriteria criteria = new SirSearchCriteria();
		List<String> crit = new ArrayList<String>();
		crit.add(text);
		criteria.setSearchText(crit);
		try {
			Collection<SirSearchResultElement> search_results = dao
					.searchSensor(criteria, true);
			Iterator<SirSearchResultElement> it = search_results.iterator();

			while (it.hasNext()) {
				SirSearchResultElement element = it.next();
				SirSolrSensorDescription desc = (SirSolrSensorDescription) element
						.getSensorDescription();
				results.addAll(desc.getKeywords());
			}

			// returns the result as json array
			if (results.size() == 0)
				pw.println("");
			else {
				Iterator<Object> iterator = results.iterator();
				StringBuilder res = new StringBuilder();
				res.append(iterator.next());
				while(iterator.hasNext()){
					res.append(",");
					res.append(iterator.next());
				}
				pw.println(res.toString());
			}
			pw.flush();
			resp.flushBuffer();
			log.debug("Done serving servlet");
		} catch (OwsExceptionReport e) {
			// TODO Auto-generated catch block
			log.error("error on searching", e);
		}

	}

}
