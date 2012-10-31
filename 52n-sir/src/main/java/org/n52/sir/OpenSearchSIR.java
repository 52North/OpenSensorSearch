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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.sir.datastructure.SirBoundingBox;
import org.n52.sir.datastructure.SirSearchCriteria;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.listener.SearchSensorListener;
import org.n52.sir.opensearch.AtomListener;
import org.n52.sir.opensearch.HtmlListener;
import org.n52.sir.opensearch.IOpenSearchListener;
import org.n52.sir.opensearch.JsonListener;
import org.n52.sir.opensearch.KmlListener;
import org.n52.sir.opensearch.OpenSearchConfigurator;
import org.n52.sir.opensearch.OpenSearchConstants;
import org.n52.sir.opensearch.RequestDismantler;
import org.n52.sir.opensearch.RssListener;
import org.n52.sir.opensearch.XmlListener;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.request.SirSearchSensorRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirSearchSensorResponse;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Jan Schulte (j.schulte@52north.org), Daniel Nüst (d.nuest@52north.org)
 */
public class OpenSearchSIR extends HttpServlet {

    /**
     * The init parameter of the configFile
     */
    private static final String INIT_PARAM_CONFIG_FILE = "configFile";

    /**
     * The init parameter of the database configFile
     */
    private static final String INIT_PARAM_DBCONFIG_FILE = "dbConfigFile";

    /**
     * The logger, used to log exceptions and additional information
     */
    private static Logger log = LoggerFactory.getLogger(OpenSearchSIR.class);

    private static final long serialVersionUID = 3051953359478226492L;

    private OpenSearchConfigurator configurator;

    private RequestDismantler dismantler;

    private HashMap<String, IOpenSearchListener> listeners;

    private SearchSensorListener sensorSearcher;

    public OpenSearchSIR() {
        super();

        log.info("NEW " + this);
    }

    @Override
    public void destroy() {
        log.info("destroy() called...");

        super.destroy();
        SirConfigurator.getInstance().getExecutor().shutdown();

        log.info("done destroy()");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (log.isDebugEnabled())
            log.debug(" ****** (GET) Connected from: " + req.getRemoteAddr() + " " + req.getRemoteHost());

        // String acceptHeader = req.getHeader("accept");
        // log.debug("Accept header: " + acceptHeader);
        String httpAccept = req.getParameter(OpenSearchConstants.ACCEPT_PARAMETER);
        // log.debug("Accept header 2: " + httpAccept);

        String searchText = req.getParameter(OpenSearchConstants.QUERY_PARAMETER);

        // redirect if httpAccept is missing
        if (httpAccept == null || httpAccept.isEmpty()) {
            redirectMissingHttpAccept(req, resp);
            return;
        }
        if (httpAccept.contains(" "))
            httpAccept = httpAccept.replace(" ", "+");

        // must be set before getWriter() is called.
        resp.setCharacterEncoding(SirConfigurator.getInstance().getCharacterEncoding());
        PrintWriter writer = resp.getWriter();

        Collection<SirSearchResultElement> searchResult = null;

        // handle missing query parameter, can be the case if just using geo extension...
        if (searchText == null || searchText.isEmpty()) {
            searchResult = new ArrayList<SirSearchResultElement>();
            searchText = "";
            log.debug("No search text given.");
        }

        // see if Geo Extension is used:
        // http://www.opensearch.org/Specifications/OpenSearch/Extensions/Geo/1.0/Draft_2
        SirBoundingBox boundingBox = null;
        if (this.dismantler.requestContainsGeoParameters(req)) {
            boundingBox = this.dismantler.getBoundingBox(req);
            log.info("Geo extension used: bounding box {} from query {} (source: {})",
                     new Object[] {boundingBox, req.getQueryString(), req.getRemoteAddr()});
        }
        else
            log.info("Searching with query {} (source: {})", new Object[] {req.getQueryString(), req.getRemoteAddr()});

        // TODO see if time extension is used:
        // http://www.opensearch.org/Specifications/OpenSearch/Extensions/Time/1.0/Draft_1
        Calendar start = null;
        Calendar end = null;
        if (this.dismantler.requestContainsTime(req)) {
            Calendar[] startEnd = this.dismantler.getStartEnd(req);
            start = startEnd[0];
            end = startEnd[1];
            log.debug("Time extension used: {} - {}", start, end);
        }

        // create search criteria
        SirSearchCriteria searchCriteria = new SirSearchCriteria();
        if ( !searchText.isEmpty()) {
            ArrayList<String> searchTexts = new ArrayList<String>();
            searchTexts.add(searchText);
            searchCriteria.setSearchText(searchTexts);
        }

        if (boundingBox != null)
            searchCriteria.setBoundingBox(boundingBox);

        if (start != null && end != null) {
            searchCriteria.setEnd(end);
            searchCriteria.setStart(start);
        }

        // create search request
        SirSearchSensorRequest searchRequest = new SirSearchSensorRequest();
        searchRequest.setSimpleResponse(true);
        searchRequest.setVersion(SirConstants.SERVICE_VERSION_0_3_1);
        searchRequest.setSearchCriteria(searchCriteria);

        ISirResponse response = this.sensorSearcher.receiveRequest(searchRequest);

        if (response instanceof SirSearchSensorResponse) {
            SirSearchSensorResponse sssr = (SirSearchSensorResponse) response;
            searchResult = sssr.getSearchResultElements();
        }
        else if (response instanceof ExceptionResponse) {
            ExceptionResponse er = (ExceptionResponse) response;
            String s = new String(er.getByteArray());
            writer.print(s);
        }
        else {
            log.error("Unhandled response: {}", response);
            writer.print(response.toString());
        }

        try {
            if (this.listeners.containsKey(httpAccept)) {
                IOpenSearchListener l = this.listeners.get(httpAccept);

                l.createResponse(req, resp, searchResult, writer, searchText);
            }
            else {
                throw new OwsExceptionReport(ExceptionCode.InvalidParameterValue,
                                             OpenSearchConstants.ACCEPT_PARAMETER,
                                             "Unsupported output format.");
            }
        }
        catch (OwsExceptionReport e) {
            log.error("Could not create response as {} : {}", httpAccept, e);

            // if (httpAccept.equals(this.jsonListener.getMimeType())) {
            // this.mapper.writeValue(writer, e);
            // }

            e.getDocument().save(writer, XmlTools.xmlOptionsForNamespaces());
        }

        writer.close();
        resp.flushBuffer(); // commits the response

        log.debug(" *** (GET) Done.");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init() throws ServletException {
        super.init();

        this.configurator = new OpenSearchConfigurator();

        try {
            this.sensorSearcher = new SearchSensorListener();
            this.sensorSearcher.setEncodeURLs(false);
        }
        catch (OwsExceptionReport e) {
            log.error("Could not create SearchSensorListener.", e);
            throw new ServletException(e);
        }

        this.dismantler = new RequestDismantler();

        this.listeners = new HashMap<String, IOpenSearchListener>();

        // TODO move listener configuration to config file
        IOpenSearchListener jsonListener = new JsonListener(this.configurator);
        this.listeners.put(jsonListener.getMimeType(), jsonListener);
        IOpenSearchListener htmlListener = new HtmlListener(this.configurator);
        this.listeners.put(htmlListener.getMimeType(), htmlListener);
        IOpenSearchListener xmlListener = new XmlListener(this.configurator);
        this.listeners.put(xmlListener.getMimeType(), xmlListener);
        IOpenSearchListener rssListener = new RssListener(this.configurator);
        this.listeners.put(rssListener.getMimeType(), rssListener);
        IOpenSearchListener atomListener = new AtomListener(this.configurator);
        this.listeners.put(atomListener.getMimeType(), atomListener);
        IOpenSearchListener kmlListener = new KmlListener(this.configurator);
        this.listeners.put(kmlListener.getMimeType(), kmlListener);

        // get ServletContext
        // ServletContext context = getServletContext();
        // String basepath = context.getRealPath("/");

        // get configFile as Inputstream
        // InputStream configStream = context.getResourceAsStream(getInitParameter(INIT_PARAM_CONFIG_FILE));
        // if (configStream == null) {
        // throw new UnavailableException("could not open the config file");
        // }
        //
        // // get dbconfigFile as Inputstream
        // InputStream dbConfigStream =
        // context.getResourceAsStream(getInitParameter(INIT_PARAM_DBCONFIG_FILE));
        // if (dbConfigStream == null) {
        // throw new UnavailableException("could not open the database config file");
        // }
    }

    private void redirectMissingHttpAccept(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append(this.configurator.getFullOpenSearchPath());
        sb.append("?");

        Enumeration< ? > params = req.getParameterNames();
        while (params.hasMoreElements()) {
            String s = (String) params.nextElement();
            sb.append(s);
            sb.append("=");
            String[] parameterValues = req.getParameterValues(s);
            for (String sVal : parameterValues) {
                sb.append(sVal);
                sb.append(",");
            }

            sb.replace(sb.length() - 1, sb.length(), "&");
        }

        sb.append(OpenSearchConstants.ACCEPT_PARAMETER);
        sb.append("=");
        sb.append(OpenSearchConstants.X_DEFAULT_MIME_TYPE);
        log.debug("Redirecting to {}", sb.toString());
        resp.sendRedirect(sb.toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OpenSearchSIR [");
        if (this.configurator != null)
            sb.append(this.configurator.getOpenSearchPath());
        sb.append("]");
        return sb.toString();
    }
}