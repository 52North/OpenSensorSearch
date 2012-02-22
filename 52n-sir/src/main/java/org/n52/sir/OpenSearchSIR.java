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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.opengis.gml.ReferenceType;
import net.opengis.kml.x22.AbstractFeatureType;
import net.opengis.kml.x22.KmlDocument;
import net.opengis.kml.x22.KmlType;
import net.opengis.ows.ExceptionReportDocument;
import net.opengis.sos.x10.CapabilitiesDocument;
import net.opengis.sos.x10.CapabilitiesDocument.Capabilities;
import net.opengis.sos.x10.ContentsDocument.Contents;
import net.opengis.sos.x10.ContentsDocument.Contents.ObservationOfferingList;
import net.opengis.sos.x10.ObservationOfferingType;
import net.opengis.swe.x101.PhenomenonPropertyType;

import org.apache.xmlbeans.XmlObject;
import org.n52.api.access.AccessGenerator;
import org.n52.api.access.client.TimeRange;
import org.n52.api.access.client.TimeSeriesParameters;
import org.n52.api.access.client.TimeSeriesPermalinkBuilder;
import org.n52.sir.datastructure.SirSearchCriteria;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.datastructure.SirSimpleSensorDescription;
import org.n52.sir.listener.SearchSensorListener;
import org.n52.sir.listener.harvest.Harvester;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.request.SirSearchSensorRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirSearchSensorResponse;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndImage;
import com.sun.syndication.feed.synd.SyndImageImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * 
 * @author Jan Schulte (j.schulte@52north.org), Daniel Nüst (d.nuest@52north.org)
 */
public class OpenSearchSIR extends HttpServlet {

    /**
     * 
     * @author Daniel Nüst (d.nuest@52north.org)
     * 
     */
    private class PermalinkParameters {

        public ArrayList<String> foi = new ArrayList<String>();
        public ArrayList<String> offering = new ArrayList<String>();
        public ArrayList<String> phen = new ArrayList<String>();
        public ArrayList<String> proc = new ArrayList<String>();

        public PermalinkParameters() {
            //
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "[" + Arrays.deepToString(this.offering.toArray()) + " | " + Arrays.deepToString(this.foi.toArray())
                    + " | " + Arrays.deepToString(this.proc.toArray()) + " | "
                    + Arrays.deepToString(this.phen.toArray()) + "]";
        }

    }

    private static final String ACCEPT_PARAMETER = "httpAccept";

    private static final String CDATA_END_TAG = "]";

    private static final String CDATA_START_TAG = "![CDATA[";

    private static final String HOME_URL = "/SIR";

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

    private static final int MAX_GET_URL_CHARACTER_COUNT = 2000; // http://stackoverflow.com/questions/417142/what-is-the-maximum-length-of-a-url

    private static final String MIME_TYPE_ATOM = "application/atom+xml";

    private static final String MIME_TYPE_HTML = "text/html";

    private static final String MIME_TYPE_JSON = "application/json";

    private static final String MIME_TYPE_KML = "application/vnd.google-earth.kml+xml";

    private static final String MIME_TYPE_PLAIN = "text/plain";

    private static final String MIME_TYPE_RSS = "application/rss+xml";

    private static final String MIME_TYPE_XML = "application/xml";

    /**
     * 
     */
    private static final String QUERY_PARAMETER = "q";

    /**
	 * 
	 */
    private static final long serialVersionUID = 3051953359478226492L;

    private static Collection<String> TIME_SERIES_SERVICE_TYPES = new ArrayList<String>();

    private static final String X_DEFAULT_MIME_TYPE = MIME_TYPE_HTML;

    static {
        TIME_SERIES_SERVICE_TYPES.add("SOS");
        TIME_SERIES_SERVICE_TYPES.add("OGC:SOS");
    }

    /**
     * store the service capabilities
     * 
     * TODO delete the cache occasionally so that the capabilities are requested new from time to time
     */
    private HashMap<URL, XmlObject> capabilitiesCache;

    /**
     * store the urls where there were problems getting service capabilities
     */
    private HashMap<URL, XmlObject> capabilitiesErrorCache;

    private SirConfigurator configurator = SirConfigurator.getInstance();

    private String cssFile = "sir.css";

    private String feed_author = "Open Sensor Search by 52°North";

    private String foundResults_post = "hits.";

    private String foundResults_pre = "";

    private boolean highlightSearchText = true;

    private boolean linksInSearchText = true;

    private SearchSensorListener listener;

    private String mapImage = "/SIR/images/map.png";

    private String openMap = "Open hits on map.";

    private String openTimeSeries = "Open Sensor in Viewer";

    private String permalinkBaseURL = "http://sensorweb.demo.52north.org/ThinSweClient2.0/Client.html";

    private SimpleDateFormat permalinkDateFormat;

    private String searchButtonText = "Search";

    private String searchResultHeadline = "Open Sensor Search";

    private String searchResultTitle = "Open Sensor Search | Hits for";

    private String sensorInfo_BoundingBox = "BBOX:";

    private String sensorInfo_CatalogID = "Sensor Catalog ID:";

    private String sensorInfo_LastUpdate = "Last update:";

    private String sensorInfo_Title = "Sensor: ";

    private String timeseriesImage = "/SIR/images/timeseries.png";

    private boolean enableUrlCompression = false;

    /**
     * 
     */
    public OpenSearchSIR() {
        super();

        this.permalinkDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        this.capabilitiesCache = new HashMap<URL, XmlObject>();
        this.capabilitiesErrorCache = new HashMap<URL, XmlObject>();

        log.info("NEW " + this);
    }

    /**
     * 
     * @param sb
     * @param list
     * @param maxElements
     */
    private void concatenate(StringBuilder sb, ArrayList<String> list, int maxElements) {
        ArrayList<String> myList = new ArrayList<String>();
        while (myList.size() < maxElements) {
            // duplicate!
            myList.addAll(list);
        }

        int i = 0;
        while (i < maxElements) {
            String s = myList.get(i);
            sb.append(s);

            i++;
            if (i == maxElements)
                break;

            sb.append(",");
        }
    }

    /**
     * 
     * @param req
     * @param resp
     * @param searchResult
     * @param writer
     * @param searchText
     * @throws OwsExceptionReport
     */
    private void createAtomResponse(HttpServletRequest req,
                                    HttpServletResponse resp,
                                    Collection<SirSearchResultElement> searchResult,
                                    PrintWriter writer,
                                    String searchText) throws OwsExceptionReport {
        resp.setContentType(MIME_TYPE_ATOM);

        SyndFeed feed = createFeed(searchResult, searchText);
        feed.setFeedType("atom_0.3");

        outputFeed(writer, feed);
    }

    /**
     * 
     * @param searchResult
     * @param searchText
     * @return
     */
    private SyndFeed createFeed(Collection<SirSearchResultElement> searchResult, String searchText) {
        SyndFeed feed = new SyndFeedImpl();

        feed.setTitle("Sensor Search for " + searchText);
        String channelURL = getFullOpenSearchPath() + "?" + QUERY_PARAMETER + "=" + searchText + "&" + ACCEPT_PARAMETER
                + "=" + MIME_TYPE_RSS;
        feed.setLink(channelURL);
        feed.setPublishedDate(new Date());
        feed.setAuthor(this.feed_author);
        // feed.setContributors(contributors) // TODO add all service contacts
        // feed.setCategories(categories) // TODO user user tags for categories
        feed.setEncoding(this.configurator.getCharacterEncoding());
        SyndImage image = new SyndImageImpl();
        image.setUrl("http://52north.org/templates/52n/images/52n-logo.gif");
        image.setLink(this.configurator.getFullServicePath().toString());
        image.setTitle("52°North Logo");
        image.setDescription("Logo of the provider of Open Sensor Search: 52°North");
        feed.setImage(image);
        feed.setDescription("These are the sensors for the keywords '" + searchText + "' from Open Sensor Search ("
                + this.configurator.getFullServicePath().toString() + ").");

        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        for (SirSearchResultElement ssre : searchResult) {
            SyndEntry e = createFeedEntry(ssre);
            entries.add(e);
        }
        feed.setEntries(entries);

        return feed;
    }

    /**
     * 
     * @return
     */
    private String getFullOpenSearchPath() {
        return this.configurator.getFullServicePath().toString() + this.configurator.getOpenSearchPath();
    }

    /**
     * 
     * @param ssre
     * @return
     */
    private SyndEntry createFeedEntry(SirSearchResultElement ssre) {
        SirSimpleSensorDescription sensorDescription = (SirSimpleSensorDescription) ssre.getSensorDescription();

        SyndEntry entry = new SyndEntryImpl();
        // SyndContent title = new SyndContentImpl();
        // title.setType(MIME_TYPE_HTML)
        entry.setTitle(ssre.getSensorIdInSir());
        try {
            // String link = URLDecoder.decode(sensorDescription.getSensorDescriptionURL(),
            // this.configurator.getCharacterEncoding());
            String link = decode(URLDecoder.decode(sensorDescription.getSensorDescriptionURL(),
                                                   this.configurator.getCharacterEncoding()));

            entry.setLink(link);
        }
        catch (UnsupportedEncodingException e) {
            log.warn("Could not create URL for sensor {}", ssre.getSensorIdInSir());
        }

        // TODO include service references in text using text/html as description type
        // List<SyndLink> links = new ArrayList<SyndLink>();
        // for (SirServiceReference reference : ssre.getServiceReferences()) {
        // String getCapRequest = createGetCapabilitiesRequestURL(reference);
        // getCapRequest = encode(getCapRequest);
        // SyndLinkImpl link = new SyndLinkImpl();
        // link.setTitle(reference.getServiceSpecificSensorId() + " at " + reference.getService().getType());
        // link.setHref(getCapRequest);
        // links.add(link);
        // }
        // entry.setLinks(links);

        entry.setPublishedDate(ssre.getLastUpdate());
        SyndContent descr = new SyndContentImpl();
        descr.setType(MIME_TYPE_PLAIN); // alternative e.g. text/html
        descr.setValue(extractDescriptionText(sensorDescription));
        entry.setDescription(descr);

        return entry;
    }

    /**
     * 
     * @param reference
     * @return
     */
    private String createGetCapabilitiesRequestURL(SirServiceReference reference) {
        return reference.getService().getUrl() + "?REQUEST=GetCapabilities&SERVICE=SOS";
    }

    /**
     * Writes a HTML response for the SearchResultElements in the given writer
     * 
     * @param searchResultElements
     * @param writer
     * @param searchText
     * @throws UnsupportedEncodingException
     */
    private void createHTMLContent(Collection<SirSearchResultElement> searchResultElements,
                                   PrintWriter writer,
                                   String searchText) throws UnsupportedEncodingException {
        writer.print("<form name=\"requestform\" method=\"get\" action=\"");
        writer.print(getFullOpenSearchPath());
        writer.println("\">");

        writer.print("<div class=\"search-result-header\">");
        writer.print("<a href=\"");
        writer.print(HOME_URL);
        writer.print("\" title=\"Home\">");
        writer.print(this.searchResultHeadline);
        writer.print("</a>");
        writer.println("</div>");

        writer.print("<input name=\"");
        writer.print(QUERY_PARAMETER);
        writer.print("\" type=\"text\" value=\"");
        writer.print(searchText);
        writer.print("\" class=\"search-input\" />");

        // hidden input for default accept parameter
        writer.print("<input type=\"hidden\" name=\"");
        writer.print(ACCEPT_PARAMETER);
        writer.print("\" value=\"");
        writer.print(X_DEFAULT_MIME_TYPE);
        writer.print("\" />");

        writer.print("<input value=\"");
        writer.print(this.searchButtonText);
        writer.println("\" type=\"submit\" />");
        writer.println("</form>");

        writer.print("<span class=\"infotext\">");
        writer.print(this.foundResults_pre);
        writer.print(" ");
        writer.print(searchResultElements.size());
        writer.print(" ");
        writer.print(this.foundResults_post);
        writer.println("</span>");

        writer.print("<div style=\"float: right; margin: 0; width: 200px;\">");
        // dropdown for response format
        writer.print("<form action=\"");
        writer.print(this.configurator.getFullServicePath() + this.configurator.getOpenSearchPath());
        writer.print("\" method=\"get\">");
        writer.print("<input type=\"hidden\" name=\"");
        writer.print(QUERY_PARAMETER);
        writer.print("\" value=\"");
        writer.print(searchText);
        writer.print("\" />");

        writer.print("<span style=\"float:left;\" class=\"infotext\">");
        writer.print("Response format: ");

        writer.print("<select name=\"");
        writer.print(ACCEPT_PARAMETER);
        writer.print("\" onchange=\"this.form.submit();\">");
        writer.print("<option value=\"");
        writer.print(MIME_TYPE_HTML);
        writer.print("\" selected=\"selected\">HTML</option>");
        writer.print("<option value=\"");
        writer.print(MIME_TYPE_KML);
        writer.print("\">KML</option>");
        writer.print("<option value=\"");
        writer.print(MIME_TYPE_JSON);
        writer.print("\">JSON</option>");
        writer.print("<option value=\"");
        writer.print(MIME_TYPE_XML);
        writer.print("\">XML</option>");
        writer.print("<option value=\"");
        writer.print(MIME_TYPE_ATOM);
        writer.print("\">ATOM</option>");
        writer.print("<option value=\"");
        writer.print(MIME_TYPE_RSS);
        writer.print("\">RSS</option>");
        writer.print("</select>");
        writer.print("</span>");
        writer.print("</form>");

        // map button
        writer.print("<span style=\"float: right; padding: 1px 0 0 0;\"><a href=\"\" onclick=\"alert('This function is not implemented yet. "
                + "Follow the roadmap for new features at https://wiki.52north.org/bin/view/Sensornet/SensorInstanceRegistry.');\">");
        writer.print("<img src=\"");
        writer.print(this.mapImage);
        writer.print("\" alt=\"");
        writer.print(this.openMap);
        writer.print("\" />");
        writer.print("</a></span>");

        writer.println("</div>");

        for (SirSearchResultElement sirSearchResultElement : searchResultElements) {
            String s = createHTMLEntry(sirSearchResultElement, searchText);
            writer.print(s);
        }
    }

    /**
     * @param writer
     * @param sirSearchResultElement
     * @return
     * @throws UnsupportedEncodingException
     */
    private String createHTMLEntry(SirSearchResultElement sirSearchResultElement, String searchText) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        SirSimpleSensorDescription sensorDescription = (SirSimpleSensorDescription) sirSearchResultElement.getSensorDescription();
        sb.append("<div class=\"result-header\">");

        sb.append(this.sensorInfo_Title);
        sb.append("<a href=\"");
        String url = sensorDescription.getSensorDescriptionURL();
        sb.append(encode(url));
        sb.append("\">");
        sb.append(" ");
        // sb.append(sirSearchResultElement.getSensorIdInSir());
        sb.append(extractEntryTitle(sirSearchResultElement));
        sb.append("</a>");
        sb.append("</div>");

        for (SirServiceReference reference : sirSearchResultElement.getServiceReferences()) {
            sb.append("<div class=\"result-service\">");
            sb.append("");
            sb.append("Service");
            sb.append(": <a href=\"");
            String getCapRequest = createGetCapabilitiesRequestURL(reference);
            getCapRequest = encode(getCapRequest);
            sb.append(getCapRequest);
            sb.append("\">");
            sb.append(reference.getService().getUrl());
            sb.append("</a>");

            // timeseries link
            URL permalinkUrl = null;

            // permalink = getTimeseriesViewerPermalink(sirSearchResultElement, reference);
            try {
                permalinkUrl = getTimeSeriesPermalink(sirSearchResultElement, reference);
            }
            catch (MalformedURLException e) {
                log.warn("Could not create permalink for " + reference, e);
            }

            if (permalinkUrl != null) {
                sb.append("<span style=\"float: right;\"><a href=\"");
                sb.append(encode(permalinkUrl.toExternalForm()));
                sb.append("\" title=\"");
                sb.append(this.openTimeSeries);
                sb.append("\">");
                sb.append("<img src=\"");
                sb.append(this.timeseriesImage);
                sb.append("\" alt=\"");
                sb.append(this.openTimeSeries);
                sb.append("\" />");
                sb.append("</a></span>");
            }
            else
                log.debug("Could not create permalink for {}", reference);

            sb.append("</div>");
        }

        sb.append("<div class=\"result-properties\">");
        sb.append(this.sensorInfo_LastUpdate);
        sb.append(" ");
        sb.append(sirSearchResultElement.getLastUpdate());
        sb.append(" | ");
        sb.append(this.sensorInfo_CatalogID);
        sb.append(" ");
        sb.append(sirSearchResultElement.getSensorIdInSir());
        if (sensorDescription.getBoundingBox() != null) {
            sb.append(" | ");
            sb.append(this.sensorInfo_BoundingBox);
            sb.append(" ");
            sb.append(sensorDescription.getBoundingBox());
        }
        sb.append("</div>");

        sb.append("<div class=\"result-description\">");
        String text = extractDescriptionText(sensorDescription);
        text = highlightHTML(text, searchText);
        sb.append(text);
        sb.append("</div>");

        return sb.toString();
    }

    /**
     * 
     * @param req
     * @param resp
     * @param searchResultElements
     * @param writer
     * @param searchText
     * @throws IOException
     * @throws ServletException
     */
    private void createHTMLResponse(HttpServletRequest req,
                                    HttpServletResponse resp,
                                    Collection<SirSearchResultElement> searchResultElements,
                                    PrintWriter writer,
                                    String searchText) throws ServletException, IOException {
        writer.print("<?xml version=\"1.0\" encoding=\"");
        writer.print(SirConfigurator.getInstance().getCharacterEncoding().toLowerCase());
        writer.println("\"?>");

        writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        writer.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");

        writer.println("<head>");
        writer.print("<link href=\"");
        writer.print(this.cssFile);
        writer.println("\" rel=\"stylesheet\" type=\"text/css\" />");

        writer.print("<title>");
        writer.print(this.searchResultTitle);
        writer.print(" '");
        writer.print(searchText);
        writer.println("'</title>");

        writer.println("<link rel=\"shortcut icon\" href=\"https://52north.org/templates/52n/favicon.ico\" />");
        writer.println("</head>");

        writer.println("<body>");
        writer.println("<div id=\"content\">");

        createHTMLContent(searchResultElements, writer, searchText);

        writer.println("<div id=\"footer\">");
        writer.println("<p class=\"infotext\">Open Sensor Search is powered by the 52&deg;North Sensor Instance Registry. <a href=\"http://52north.org/communities/sensorweb/incubation/discovery/\" title=\"Sensor Discovery by 52N\">Find out more</a>.");
        writer.println("</p>");
        writer.println("<p class=\"infotext\"><a href=\"./\">Home</a> | <a href=\"client.jsp\">Extended Client</a> | <a href=\"testClient.html\">Form Client</a>");
        writer.println("</p>");
        writer.println("<p class=\"infotext\">&copy; 2012 <a href=\"http://52north.org\">52&deg;North Initiative for Geospatial Software GmbH</a>");
        writer.println("</p>");
        writer.println("</div>");

        writer.println("</div>"); // content
        writer.println("</body>");
        writer.println("</html>");
        writer.flush();
    }

    /**
     * 
     * @param req
     * @param resp
     * @param searchResult
     * @param writer
     * @param searchText
     * @throws OwsExceptionReport
     */
    private void createJSONResponse(HttpServletRequest req,
                                    HttpServletResponse resp,
                                    Collection<SirSearchResultElement> searchResult,
                                    PrintWriter writer,
                                    String searchText) throws OwsExceptionReport {
        resp.setContentType(MIME_TYPE_JSON);

        // TODO move this somewhere where all listeners can use it
        String responseDescription = "These are the search hits for the keyword(s) '" + searchText
                + "' from Open Sensor Search (" + this.configurator.getFullServicePath().toString() + ").";
        String responseURL = getFullOpenSearchPath() + "?" + QUERY_PARAMETER + "=" + searchText + "&"
                + ACCEPT_PARAMETER + "=" + MIME_TYPE_JSON;

        // http://sites.google.com/site/gson/gson-user-guide
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping();
        Gson gson = gsonBuilder.create();

        // creating json object
        JsonObject response = new JsonObject();
        response.add("source", new JsonPrimitive(this.configurator.getFullServicePath().toString()));
        response.add("searchText", new JsonPrimitive(searchText));
        response.add("searchURL", new JsonPrimitive(responseURL));
        response.add("searchDescription", new JsonPrimitive(responseDescription));
        response.add("searchAuthor", new JsonPrimitive("52°North"));
        response.add("searchDate", new JsonPrimitive(this.permalinkDateFormat.format(new Date())));

        JsonElement jsonTree = gson.toJsonTree(searchResult);
        response.add("result", jsonTree);

        JsonWriter jsonWriter = new JsonWriter(writer);
        jsonWriter.setHtmlSafe(true);
        jsonWriter.setIndent("  ");

        // if performance becomes an issue then try using stream writer, see:
        // http://google-gson.googlecode.com/svn/trunk/gson/docs/javadocs/com/google/gson/stream/JsonWriter.html
        // jsonWriter.beginArray();

        gson.toJson(response, jsonWriter);
    }

    /**
     * 
     * @param req
     * @param resp
     * @param searchResult
     * @param writer
     * @param searchText
     * @throws OwsExceptionReport
     */
    private void createKMLResponse(HttpServletRequest req,
                                   HttpServletResponse resp,
                                   Collection<SirSearchResultElement> searchResult,
                                   PrintWriter writer,
                                   String searchText) throws OwsExceptionReport {
        resp.setContentType(MIME_TYPE_KML);
        resp.setHeader("Content-Disposition", "attachment; filename=" + searchText + "_Open-Sensor-Search.kml");

        KmlDocument doc = KmlDocument.Factory.newInstance();
        KmlType kml = doc.addNewKml();
        // simpleExtensionGroup.setStringValue("RESPONSE TYPE NOT IMPLEMENTED!");

        AbstractFeatureType abstractFeatureGroup = kml.addNewAbstractFeatureGroup();
        abstractFeatureGroup.addNewAuthor().addName("Open Sensor Search");

        // TODO add kml content

        try {
            doc.save(writer, XmlTools.xmlOptionsForNamespaces());
        }
        catch (IOException e) {
            log.error("Error outputting feed to writer", e);
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode, "service", "Error outputting feed to writer");
        }
    }

    /**
     * 
     * @param req
     * @param resp
     * @param searchResult
     * @param writer
     * @param searchText
     * @throws OwsExceptionReport
     */
    private void createRSSResponse(HttpServletRequest req,
                                   HttpServletResponse resp,
                                   Collection<SirSearchResultElement> searchResult,
                                   PrintWriter writer,
                                   String searchText) throws OwsExceptionReport {
        resp.setContentType(MIME_TYPE_RSS);

        // TODO create WireFeed, then reuse for Atom AND RSS, see
        // http://en.wikipedia.org/wiki/RSS#Comparison_with_Atom

        // TODO make GeoRSS!!!
        SyndFeed feed = createFeed(searchResult, searchText);
        feed.setFeedType("rss_2.0");

        outputFeed(writer, feed);
    }

    /**
     * 
     * @param req
     * @param resp
     * @param searchResult
     * @param writer
     * @param searchText
     * @throws OwsExceptionReport
     */
    private void createXMLResponse(HttpServletRequest req,
                                   HttpServletResponse resp,
                                   Collection<SirSearchResultElement> searchResult,
                                   PrintWriter writer,
                                   String searchText) throws OwsExceptionReport {
        log.debug("Creating XML response for {}", searchText);

        resp.setContentType(MIME_TYPE_XML);
        SirSearchSensorResponse sssr = new SirSearchSensorResponse();
        sssr.setSearchResultElements(searchResult);
        sssr.writeTo(writer);

        log.debug("Done with XML response.");
    }

    /**
     * handle only & characters
     * 
     * @param url
     * @return
     */
    private String decode(String url) {
        String s = url.replaceAll("&amp;", "\\&");
        return s;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.GenericServlet#destroy()
     */
    @Override
    public void destroy() {
        log.info("destroy() called...");

        super.destroy();
        SirConfigurator.getInstance().getExecutor().shutdown();

        log.info("done destroy()");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest ,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (log.isDebugEnabled())
            log.debug(" ****** (GET) Connected from: " + req.getRemoteAddr() + " " + req.getRemoteHost());

        // String acceptHeader = req.getHeader("accept");
        // log.debug("Accept header: " + acceptHeader);
        String httpAccept = req.getParameter(ACCEPT_PARAMETER);
        // log.debug("Accept header 2: " + httpAccept);

        String searchText = req.getParameter(QUERY_PARAMETER);

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

        if (searchText == null || searchText.isEmpty()) {
            searchResult = new ArrayList<SirSearchResultElement>();
            searchText = "";
        }
        else {
            // create search criteria
            SirSearchCriteria searchCriteria = new SirSearchCriteria();
            ArrayList<String> searchTexts = new ArrayList<String>();
            searchTexts.add(searchText);
            searchCriteria.setSearchText(searchTexts);
            // create search request
            SirSearchSensorRequest searchRequest = new SirSearchSensorRequest();
            searchRequest.setSimpleResponse(true);
            searchRequest.setVersion(SirConstants.SERVICE_VERSION_0_3_1);
            searchRequest.setSearchCriteria(searchCriteria);

            ISirResponse response = this.listener.receiveRequest(searchRequest);

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
        }

        try {
            if (httpAccept.equals(MIME_TYPE_KML)) {
                createKMLResponse(req, resp, searchResult, writer, searchText);
            }
            else if (httpAccept.equals(MIME_TYPE_RSS)) {
                createRSSResponse(req, resp, searchResult, writer, searchText);
            }
            else if (httpAccept.equals(MIME_TYPE_ATOM)) {
                createAtomResponse(req, resp, searchResult, writer, searchText);
            }
            else if (httpAccept.equals(MIME_TYPE_JSON)) {
                createJSONResponse(req, resp, searchResult, writer, searchText);
            }
            else if (httpAccept.equals(MIME_TYPE_XML)) {
                createXMLResponse(req, resp, searchResult, writer, searchText);
            }
            else if (httpAccept.equals(MIME_TYPE_HTML)) {
                if (searchText.contains("&"))
                    searchText = encode(searchText);

                createHTMLResponse(req, resp, searchResult, writer, searchText);
            }
            else {
                throw new OwsExceptionReport(ExceptionCode.InvalidParameterValue,
                                             ACCEPT_PARAMETER,
                                             "Unsupported output format.");
            }
        }
        catch (OwsExceptionReport e) {
            log.error("Could not create response as {} : {}", httpAccept, e);
            e.getDocument().save(writer, XmlTools.xmlOptionsForNamespaces());
        }

        writer.close();
        resp.flushBuffer(); // commits the response

        log.debug(" *** (GET) Done.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest ,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * 
     * using the original URLEncoder does not work, it always appends the sir URL in front...
     * 
     * // getCapRequest = URLEncoder.encode(getCapRequest, //
     * SirConfigurator.getInstance().getCharacterEncoding());
     * 
     * @param getCapRequest
     * @return
     */
    private String encode(String url) {
        String s = url.replaceAll("\\&", "&amp;");
        return s;
    }

    /**
     * 
     * @param sensorDescription
     * @return
     */
    private String extractDescriptionText(SirSimpleSensorDescription sensorDescription) {
        String ds = sensorDescription.getDescriptionText();

        // remove CDATA (if it exists)
        if (ds.contains(CDATA_START_TAG)) {
            ds = ds.replace(CDATA_START_TAG, "");

            if (ds.endsWith(CDATA_END_TAG))
                ds = ds.substring(0, ds.length() - 1);
        }

        // see if the string contains new line characters
        if (ds.contains("\n"))
            ds = ds.replaceAll("\\n", System.getProperty("line.separator"));

        // encode possibly problematic characters
        if (ds.contains("&"))
            ds = encode(ds);

        return ds;
    }

    /**
     * @param sirSearchResultElement
     */
    private String extractEntryTitle(SirSearchResultElement sirSearchResultElement) {
        StringBuilder sb = new StringBuilder();

        Collection<SirServiceReference> serviceReferences = sirSearchResultElement.getServiceReferences();
        for (SirServiceReference sirServiceReference : serviceReferences) {
            sb.append(sirServiceReference.getServiceSpecificSensorId());
            sb.append(" ");
        }

        return sb.toString();
    }

    /**
     * @param serviceReference
     * @return
     */
    private ObservationOfferingType[] getObservationOfferingArray(SirServiceReference serviceReference) {
        // get capabilities and create links
        URL url;
        try {
            url = new URL(serviceReference.getService().getUrl());
        }
        catch (MalformedURLException e) {
            log.error("Cannot create valid URL for service reference.", e);
            return null;
        }

        // check if url was already requested and use that
        XmlObject caps = null;
        if ( !this.capabilitiesCache.containsKey(url)) {
            // TODO use threads for this, then update the interface one after the other (loader image and
            // AJAX?)
            try {
                caps = Harvester.requestCapabilities(serviceReference.getService().getType(), url.toURI());
            }
            catch (OwsExceptionReport e) {
                log.error("Could not get service capabilities.", e);
                return null;
            }
            catch (URISyntaxException e) {
                log.error("Could not get service capabilities.", e);
                return null;
            }

            this.capabilitiesCache.put(url, caps);
        }
        else if (this.capabilitiesErrorCache.containsKey(url)) {
            log.debug("Had error with capabilities for {} before, not requesting again.", url);
            return null;
        }
        else
            caps = this.capabilitiesCache.get(url);

        // find out if the capabilities document can be handled
        Capabilities sosCaps;
        if (caps instanceof CapabilitiesDocument) {
            CapabilitiesDocument doc = (CapabilitiesDocument) caps;
            sosCaps = doc.getCapabilities();
        }
        else if (caps instanceof ExceptionReportDocument) {
            ExceptionReportDocument erd = (ExceptionReportDocument) caps;
            log.warn("Could not request capabilities from service {}, got ExceptionReportDocument (turn on debug for more info)",
                     url);
            this.capabilitiesErrorCache.put(url, erd);
            log.debug(Arrays.toString(erd.getExceptionReport().getExceptionArray()));
            return null;
        }
        else {
            log.error("No SOS capabilities document returned by service! Instead got: {} [... truncated]",
                      caps.xmlText().substring(0, 200));
            this.capabilitiesErrorCache.put(url, caps);
            return null;
        }

        Contents contents = sosCaps.getContents();
        if (contents == null) {
            log.debug("Contents of capabilities for service {} are null, cannot generate permalinks.", serviceReference);
            this.capabilitiesErrorCache.put(url, caps);
            return null;
        }

        ObservationOfferingList observationOfferingList = contents.getObservationOfferingList();
        if (observationOfferingList == null) {
            log.warn("Contents of observation offerings for service {} are null, cannot generate permalinks.",
                     serviceReference);
            this.capabilitiesErrorCache.put(url, caps);
            return null;
        }

        ObservationOfferingType[] observationOfferingArray = observationOfferingList.getObservationOfferingArray();
        return observationOfferingArray;
    }

    /**
     * 
     * @param sirSearchResultElement
     * @param reference
     * @return
     * @throws MalformedURLException
     *         s
     */
    private URL getTimeSeriesPermalink(SirSearchResultElement sirSearchResultElement, SirServiceReference reference) throws MalformedURLException {
        ObservationOfferingType[] observationOfferingArray = getObservationOfferingArray(reference);

        if (observationOfferingArray == null) {
            // log.debug("Could not get offerings for {}", reference);
            return null;
        }

        // get permalinks from offering
        TimeSeriesPermalinkBuilder builder = new TimeSeriesPermalinkBuilder();

        // get possible offering/obsProp/foi/proc combinations
        for (ObservationOfferingType off : observationOfferingArray) {
            // see if service reference is usable
            if ( !TIME_SERIES_SERVICE_TYPES.contains(reference.getService().getType())) {
                log.debug("Service type {} not supported.", reference.getService().getType());
                continue;
            }

            String offering = off.getId();
            String procedure = reference.getServiceSpecificSensorId();
            String serviceURL = reference.getService().getUrl();

            boolean sensorFound = false;
            ReferenceType[] procedureArray = off.getProcedureArray();
            for (ReferenceType ref : procedureArray) {
                String href = ref.xgetHref().getStringValue();
                if (href.equals(procedure)) {
                    sensorFound = true;
                    break;
                }
            }

            if (sensorFound) {
                // get all combinations of foi and phen
                PhenomenonPropertyType[] observedPropertyArray = off.getObservedPropertyArray();
                ReferenceType[] featureOfInterestArray = off.getFeatureOfInterestArray();

                // use fixed time range that is not too long:
                long now = System.currentTimeMillis();
                TimeRange timeRange = TimeRange.createTimeRangeByMillis(now - 1000 * 60 * 60, now);

                // alternative: use time range from offering
                // TimeGeometricPrimitivePropertyType time = off.getTime();
                // try {
                // TimePeriodType period = TimePeriodType.Factory.parse(time.getDomNode());
                // timeRange = TimeRange.createTimeRangebyDate(start, end);
                // }
                // catch (XmlException e) {
                // log.debug("Time is not a period, cannot utilize it.");
                // }
                // System.out.println(time);

                for (ReferenceType ref : featureOfInterestArray) {
                    String hrefFoi = ref.xgetHref().getStringValue();
                    String feature = hrefFoi;

                    for (PhenomenonPropertyType phen : observedPropertyArray) {
                        String hrefPhen = phen.xgetHref().getStringValue();
                        String phenomenon = hrefPhen;

                        TimeSeriesParameters tsp = new TimeSeriesParameters(serviceURL,
                                                                            offering,
                                                                            procedure,
                                                                            phenomenon,
                                                                            feature);
                        tsp.setTimeRange(timeRange);
                        log.debug("Adding new time series to permalink: {}", tsp);
                        builder.addParameters(tsp);
                    }
                }
            }
        }

        AccessGenerator generator = builder.build();
        URL accessURL = generator.createAccessURL(this.permalinkBaseURL); // generator.createAccessURL(this.permalinkBaseURL);

        if (accessURL.toExternalForm().length() > MAX_GET_URL_CHARACTER_COUNT && this.enableUrlCompression)
            accessURL = generator.createCompressedAccessURL(this.permalinkBaseURL);

        // accessURL = generator.uncompressAccessURL(accessURL);

        return accessURL;
    }

    /**
     * 
     * @param sirSearchResultElement
     * @param serviceReference
     * @return
     * @throws OwsExceptionReport
     */
    @SuppressWarnings("unused")
    private String getTimeseriesViewerPermalink(SirSearchResultElement sirSearchResultElement,
                                                SirServiceReference serviceReference) {
        StringBuilder sb = new StringBuilder();
        ArrayList<PermalinkParameters> links = new ArrayList<OpenSearchSIR.PermalinkParameters>();

        // FIXME move constant to configuration file
        sb.append("http://sensorweb.demo.52north.org/ThinSweClient2.0/Client.html?");

        sb.append("sos=");
        sb.append(serviceReference.getService().getUrl());

        // get offerings from service and add them all (brute force, but no other way)
        // TODO use offering from service reference once it is there (... at some point)
        ObservationOfferingType[] observationOfferingArray = getObservationOfferingArray(serviceReference);
        if (observationOfferingArray == null) {
            log.debug("Could not get service capabilities for {}", serviceReference);
            return null;
        }

        // get permalinks from offering
        Collection<TimeSeriesParameters> timeSeriesParameters = new ArrayList<TimeSeriesParameters>();
        for (ObservationOfferingType off : observationOfferingArray) {
            PermalinkParameters pp = null;
            String offering = off.getId();

            boolean sensorFound = false;
            ReferenceType[] procedureArray = off.getProcedureArray();
            for (ReferenceType ref : procedureArray) {
                String href = ref.xgetHref().getStringValue();
                if (href.equals(serviceReference.getServiceSpecificSensorId()))
                    sensorFound = true;
            }

            if (sensorFound) {
                pp = new PermalinkParameters();
                pp.offering.add(offering);
                pp.proc.add(serviceReference.getServiceSpecificSensorId());

                // find other stuff, or rather just add it
                ReferenceType[] featureOfInterestArray = off.getFeatureOfInterestArray();
                for (ReferenceType ref : featureOfInterestArray) {
                    String href = ref.xgetHref().getStringValue();
                    pp.foi.add(href);
                }

                PhenomenonPropertyType[] observedPropertyArray = off.getObservedPropertyArray();
                for (PhenomenonPropertyType phen : observedPropertyArray) {
                    String href = phen.xgetHref().getStringValue();
                    pp.phen.add(href);
                }

                // must replicate offering elements so that all lists have the same length
                int targetLength = Math.max(Math.max(pp.offering.size(), pp.proc.size()),
                                            Math.max(pp.foi.size(), pp.phen.size()));

                log.debug("I found {} different fois or observed properties, but will only use the first one! Offering: {}",
                          Integer.valueOf(targetLength),
                          offering);
                // FIXME current procedure does not cover all combinations of phenomena and fois...

                links.add(pp);
            }
        }

        if (links.isEmpty()) {
            log.warn("No offering for given sensor found.");
            return null;
        }

        // limit the number of uses offerings
        int maxOfferings = Math.min(links.size(), 4);

        // limit the number of used foi/obsProp/proc for each offering
        int maxElements = 3;

        sb.append("&offering=");
        for (int i = 0; i < maxOfferings; i++) {
            concatenate(sb, links.get(i).offering, maxElements);
            if (i < maxOfferings - 1)
                sb.append(",");
        }

        sb.append("&stations=");
        for (int i = 0; i < maxOfferings; i++) {
            concatenate(sb, links.get(i).foi, maxElements);
            if (i < maxOfferings - 1)
                sb.append(",");
        }

        sb.append("&procedures=");
        for (int i = 0; i < maxOfferings; i++) {
            concatenate(sb, links.get(i).proc, maxElements);
            if (i < maxOfferings - 1)
                sb.append(",");
        }

        sb.append("&phenomenons=");
        for (int i = 0; i < maxOfferings; i++) {
            concatenate(sb, links.get(i).phen, maxElements);
            if (i < maxOfferings - 1)
                sb.append(",");
        }

        Date endDate = new Date();
        Date beginDate = new Date(endDate.getTime() - 1000 * 60 * 60 * 24); // one day
        sb.append("&begin=" + this.permalinkDateFormat.format(beginDate));
        sb.append("&end=" + this.permalinkDateFormat.format(endDate));

        URL permalink;
        try {
            permalink = new URL(sb.toString());
        }
        catch (MalformedURLException e) {
            log.warn("Could not create valid URL with generated string: {}", sb.toString());
            return null;
        }
        log.debug("Created permalink: {}", permalink);

        String encoded = encode(permalink.toExternalForm());
        // try {
        // encoded = URLEncoder.encode(permalink.toExternalForm(),
        // SirConfigurator.getInstance().getCharacterEncoding());
        // }
        // catch (UnsupportedEncodingException e) {
        // log.warn("Could not encode permalink.");
        // return null;
        // }
        log.debug("Encoded permalink: {}", encoded);

        return encoded;
    }

    /**
     * highlight all occurences of searchText using <b>-elements.
     * 
     * @param text
     * @param searchText
     * @return
     */
    private String highlightHTML(String text, String searchText) {
        String s = text;

        StringBuffer regex = new StringBuffer();
        if (this.highlightSearchText) {
            String[] words = searchText.split(" ");
            for (String word : words) {
                // log.debug("Highlighting the word " + word);
                String head = "(?i)("; // case insensitive
                String tail = ")(?!([^<]+)?>>)";

                regex.delete(0, regex.length());
                regex.append(head);
                regex.append(word);
                regex.append(tail);

                s = s.replaceAll(regex.toString(), "<b>$1</b>");
            }
        }

        if (this.linksInSearchText) {
            // TODO remove all <b> tags that are within a URL

            // http://regexlib.com/Search.aspx?k=URL&AspxAutoDetectCookieSupport=1
            // String regex = "((mailto\\\\:|(news|(ht|f)tp(s?))\\\\://){1}\\\\S+)";

            // http://stackoverflow.com/questions/1909534/java-replacing-text-url-with-clickable-html-link
            s = s.replaceAll("(.*://[^<>[:space:]]+[[:alnum:]/])", "<a href=\"$1\">$1</a>");
        }

        return s;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException {
        super.init();

        try {
            this.listener = new SearchSensorListener();
            this.listener.setEncodeURLs(false);
        }
        catch (OwsExceptionReport e) {
            log.error("Could not create SearchSensorListener.", e);
            throw new ServletException(e);
        }

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

        // get the timer servlet
        // TimerServlet timerServlet = (TimerServlet) context.getAttribute(TimerServlet.NAME_IN_CONTEXT);

        // initialize configurator
        // SirConfigurator configurator;
        // try {
        // configurator = SirConfigurator.getInstance(configStream, dbConfigStream, basepath, timerServlet);
        // }
        // catch (OwsExceptionReport e) {
        // throw new UnavailableException(e.getMessage());
        // }
    }

    /**
     * @param writer
     * @param feed
     * @throws OwsExceptionReport
     */
    private void outputFeed(PrintWriter writer, SyndFeed feed) throws OwsExceptionReport {
        SyndFeedOutput output = new SyndFeedOutput();
        try {
            output.output(feed, writer, true);
        }
        catch (IllegalArgumentException e) {
            log.error("Error outputting feed to writer", e);
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode, "service", "Error outputting feed to writer");
        }
        catch (IOException e) {
            log.error("Error outputting feed to writer", e);
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode, "service", "Error outputting feed to writer");
        }
        catch (FeedException e) {
            log.error("Error doing output of feed to writer", e);
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode, "service", "Error outputting feed to writer");
        }
    }

    /**
     * @param req
     * @param resp
     * @throws IOException
     */
    private void redirectMissingHttpAccept(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append(getFullOpenSearchPath());
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

        sb.append(ACCEPT_PARAMETER);
        sb.append("=");
        sb.append(X_DEFAULT_MIME_TYPE);
        log.debug("Redirecting to {}", sb.toString());
        resp.sendRedirect(sb.toString());
    }

    /*
     * 
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OpenSearchSIR [");
        sb.append(this.configurator.getOpenSearchPath());
        sb.append("]");
        return sb.toString();
    }
}