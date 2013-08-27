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

package org.n52.sir.opensearch;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.opengis.gml.ReferenceType;
import net.opengis.ows.ExceptionReportDocument;
import net.opengis.sos.x10.CapabilitiesDocument;
import net.opengis.sos.x10.CapabilitiesDocument.Capabilities;
import net.opengis.sos.x10.ContentsDocument.Contents;
import net.opengis.sos.x10.ContentsDocument.Contents.ObservationOfferingList;
import net.opengis.sos.x10.ObservationOfferingType;
import net.opengis.swe.x101.PhenomenonPropertyType;

import org.apache.xmlbeans.XmlObject;
import org.n52.ext.ExternalToolsException;
import org.n52.ext.link.AccessLinkCompressor;
import org.n52.ext.link.AccessLinkFactory;
import org.n52.ext.link.sos.TimeRange;
import org.n52.ext.link.sos.TimeSeriesParameters;
import org.n52.ext.link.sos.TimeSeriesPermalinkBuilder;
import org.n52.sir.SirConfigurator;
import org.n52.sir.client.Client;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.datastructure.SirSimpleSensorDescription;
import org.n52.sir.opensearch.instanceSupport.PegelOnlineCPM;
import org.n52.sir.opensearch.instanceSupport.WeatherSosCPM;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * 
 * TODO move text snippets to properties file
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class HtmlListener implements IOpenSearchListener {

    private static final Logger log = LoggerFactory.getLogger(HtmlListener.class);

    private static final String MIME_TYPE = "text/html";

    private static final String NAME = "HTML";

    /**
     * store the service capabilities
     * 
     * TODO delete the cache occasionally so that the capabilities are requested new from time to time
     */
    private HashMap<URL, XmlObject> capabilitiesCache;

    private HashMap<URL, Date> capabilitiesCacheAge;

    /**
     * store the urls where there were problems getting service capabilities
     */
    private HashMap<URL, XmlObject> capabilitiesErrorCache;

    private OpenSearchConfigurator conf;

    private String foundResults_post = "hits.";

    private String foundResults_pre = "";

    private boolean highlightSearchText = true;

    private boolean linksInSearchText = true;

    private String mapImage = "/SIR/images/map.png";

    private String openMap = "Open hits on map.";

    private String openTimeSeries = "Open Sensor in Viewer";

    private String searchButtonText = "Search";

    private String searchResultHeadline = "Open Sensor Search";

    private String searchResultTitle = "Open Sensor Search | Hits for";

    private String sensorInfo_BoundingBox = "BBOX:";

    private String sensorInfo_CatalogID = "Sensor Catalog ID:";

    private String sensorInfo_LastUpdate = "Last update:";

    private String sensorInfo_Title = "Sensor: ";

    private String timeseriesImage = "/SIR/images/timeseries.png";

    private HashMap<String, ICapabilitiesPermalinkMapper> mappers;

    // TODO make this a configuration parameter
    private boolean createTimeSeriesLinks = false;

    @Inject
    Client sirClient;

    public HtmlListener(OpenSearchConfigurator configurator) {
        this.conf = configurator;
        this.conf.addResponseFormat(this);

        this.capabilitiesCache = new HashMap<>();
        this.capabilitiesCacheAge = new HashMap<>();
        this.capabilitiesErrorCache = new HashMap<>();

        this.mappers = new HashMap<>();

        // TODO change this to injection of multiple implemntations, move mapper to a submodule
        ICapabilitiesPermalinkMapper cpm1 = new PegelOnlineCPM();
        add(cpm1);
        ICapabilitiesPermalinkMapper cpm2 = new WeatherSosCPM();
        add(cpm2);

        if (this.createTimeSeriesLinks)
            log.warn("Permalink generation is disabled.");
    }

    private void add(ICapabilitiesPermalinkMapper cpm1) {
        this.mappers.put(cpm1.getServiceURL(), cpm1);
    }

    /**
     * Writes a HTML response for the SearchResultElements in the given writer
     * 
     * @param searchResult
     * @param writer
     * @param searchText
     * @throws UnsupportedEncodingException
     */
    private void createHTMLContent(Collection<SirSearchResultElement> searchResult,
                                   PrintWriter writer,
                                   String searchText) throws UnsupportedEncodingException {
        writer.print("<form name=\"requestform\" method=\"get\" action=\"");
        writer.print(this.conf.getFullOpenSearchPath());
        writer.println("\">");

        writer.print("<div class=\"search-result-header\">");
        writer.print("<a href=\"");
        writer.print(this.conf.getHomeUrl());
        writer.print("\" title=\"Home\">");
        writer.print(this.searchResultHeadline);
        writer.print("</a>");
        writer.println("</div>");

        writer.print("<input name=\"");
        writer.print(OpenSearchConstants.QUERY_PARAMETER);
        writer.print("\" type=\"text\" value=\"");
        writer.print(searchText);
        writer.print("\" class=\"search-input\" />");

        // hidden input for default accept parameter
        writer.print("<input type=\"hidden\" name=\"");
        writer.print(OpenSearchConstants.ACCEPT_PARAMETER);
        writer.print("\" value=\"");
        writer.print(OpenSearchConstants.X_DEFAULT_MIME_TYPE);
        writer.print("\" />");

        writer.print("<input value=\"");
        writer.print(this.searchButtonText);
        writer.println("\" type=\"submit\" />");
        writer.println("</form>");

        writer.print("<span class=\"infotext\">");
        writer.print(this.foundResults_pre);
        writer.print(" ");
        writer.print(searchResult.size());
        writer.print(" ");
        writer.print(this.foundResults_post);
        writer.println("</span>");

        writer.print("<div style=\"float: right; margin: 0; width: 200px;\">");

        // dropdown for response format
        writer.print("<form action=\"");
        writer.print(this.conf.getFullServicePath() + this.conf.getOpenSearchPath());
        writer.print("\" method=\"get\">");
        writer.print("<input type=\"hidden\" name=\"");
        writer.print(OpenSearchConstants.QUERY_PARAMETER);
        writer.print("\" value=\"");
        writer.print(searchText);
        writer.print("\" />");

        // FIXME preserve existing geo parameters
        // writer.print("<input type=\"hidden\" name=\"");
        // writer.print(QUERY_PARAMETER);
        // writer.print("\" value=\"");
        // writer.print(searchText);
        // writer.print("\" />");

        writer.print("<span style=\"float:left;\" class=\"infotext\">");
        writer.print("Response format: ");

        Map<String, String> responseFormats = this.conf.getResponseFormats();

        writer.print("<select name=\"");
        writer.print(OpenSearchConstants.ACCEPT_PARAMETER);
        writer.print("\" onchange=\"this.form.submit();\">");

        for (Entry<String, String> format : responseFormats.entrySet()) {
            if (format.getKey().equals(MIME_TYPE))
                writer.print("<option selected=\"selected\" value=\"");
            else
                writer.print("<option value=\"");
            writer.print(format.getKey());
            writer.print("\">");
            writer.print(format.getValue());
            writer.print("</option>");
        }
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

        for (SirSearchResultElement sirSearchResultElement : searchResult) {
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
        log.debug("Creating HTML response snipped for search result with SIR ID "
                + sirSearchResultElement.getSensorIdInSir());

        StringBuilder sb = new StringBuilder();
        SirSimpleSensorDescription sensorDescription = (SirSimpleSensorDescription) sirSearchResultElement.getSensorDescription();
        sb.append("<div class=\"result-header\">");

        sb.append(this.sensorInfo_Title);

        String url = sensorDescription.getSensorDescriptionURL();
        if (url != null) {
            sb.append("<a href=\"");
            sb.append(Tools.encode(url));
            sb.append("\">");
            sb.append(" ");
            // sb.append(sirSearchResultElement.getSensorIdInSir());
            sb.append(Tools.extractEntryTitle(sirSearchResultElement));
            sb.append("</a>");
        }

        sb.append("</div>");

        for (SirServiceReference reference : sirSearchResultElement.getServiceReferences()) {
            sb.append("<div class=\"result-service\">");
            sb.append("");
            sb.append("Service");
            sb.append(": <a href=\"");
            String getCapRequest = Tools.createGetCapabilitiesRequestURL(reference);
            getCapRequest = Tools.encode(getCapRequest);
            sb.append(getCapRequest);
            sb.append("\">");
            sb.append(reference.getService().getUrl());
            sb.append("</a>");

            if (this.createTimeSeriesLinks) {
                // timeseries link
                String permalinkUrl = null;

                // permalink = getTimeseriesViewerPermalink(sirSearchResultElement, reference);
                try {
                    permalinkUrl = getTimeSeriesPermalink(sirSearchResultElement, reference);
                }
                catch (MalformedURLException e) {
                    log.warn("Could not create permalink for " + reference, e);
                }
                catch (ExternalToolsException e) {
                    log.warn("Could not create permalink for " + reference, e);
                }
                catch (IllegalArgumentException e) {
                    log.warn("Could not create permalink for " + reference, e);
                }

                if (permalinkUrl != null) {
                    sb.append("<span style=\"float: right;\"><a href=\"");
                    sb.append(Tools.encode(permalinkUrl));
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
            }

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
        String text = Tools.extractDescriptionText(sensorDescription);
        text = Tools.highlightHTML(text, searchText, this.highlightSearchText, this.linksInSearchText);
        sb.append(text);
        sb.append("</div>");

        return sb.toString();
    }

    @Override
    public void createResponse(HttpServletRequest req,
                               HttpServletResponse resp,
                               Collection<SirSearchResultElement> searchResult,
                               PrintWriter writer,
                               String searchText) throws OwsExceptionReport {
        String searchT;
        if (searchText.contains("&"))
            searchT = Tools.encode(searchText);
        else
            searchT = searchText;

        writer.print("<?xml version=\"1.0\" encoding=\"");
        writer.print(SirConfigurator.getInstance().getCharacterEncoding().toLowerCase());
        writer.println("\"?>");

        writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        writer.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");

        writer.println("<head>");
        writer.print("<link href=\"");
        writer.print(this.conf.getCssFile());
        writer.println("\" rel=\"stylesheet\" type=\"text/css\" />");

        writer.print("<title>");
        writer.print(this.searchResultTitle);
        writer.print(" '");
        writer.print(searchT);
        writer.println("'</title>");

        writer.println("<link rel=\"shortcut icon\" href=\"https://52north.org/templates/52n/favicon.ico\" />");
        writer.println("</head>");

        writer.println("<body>");
        writer.println("<div id=\"content\">");

        try {
            createHTMLContent(searchResult, writer, searchT);
        }
        catch (UnsupportedEncodingException e) {
            log.error("Error creating HTML content.", e);
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode, "service", "Encoding error: " + e.getMessage());
        }

        writer.println("<div id=\"footer\">");
        writer.println("<p class=\"infotext\">Open Sensor Search is powered by the 52&deg;North Sensor Instance Registry. <a href=\"http://52north.org/communities/sensorweb/incubation/discovery/\" title=\"Sensor Discovery by 52N\">Find out more</a>.");
        writer.println("</p>");
        writer.println("<p class=\"infotext\"><a href=\"./\">Home</a> | <a href=\"client.jsp\">Extended Client</a> | <a href=\"formClient.html\">Form Client</a>");
        writer.println("</p>");
        writer.println("<p class=\"infotext\">&copy; 2012 <a href=\"http://52north.org\">52&deg;North Initiative for Geospatial Software GmbH</a>");
        writer.println("</p>");
        writer.println("</div>");

        writer.println("</div>"); // content
        writer.println("</body>");
        writer.println("</html>");
        writer.flush();
    }

    @Override
    public String getMimeType() {
        return MIME_TYPE;
    }

    @Override
    public String getName() {
        return NAME;
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

        // check if url was already requested, and use that
        XmlObject caps = null;
        if ( !this.capabilitiesCache.containsKey(url)) {
            caps = updateCache(serviceReference, url);
        }
        else if (this.capabilitiesErrorCache.containsKey(url)) {
            log.debug("Had error with capabilities for {} before, not requesting again.", url);
            return null;
        }
        else {
            Calendar maxAge = Calendar.getInstance();
            maxAge.add(Calendar.SECOND, -1 * this.conf.getCapabilitiesCacheMaximumAgeSeconds());

            if (this.capabilitiesCacheAge.get(url).before(maxAge.getTime())) {
                log.debug("Updating cached capabilities, was from {} but maximum age is {}.",
                          this.capabilitiesCacheAge.get(url),
                          maxAge);
                caps = updateCache(serviceReference, url);
            }
            else
                caps = this.capabilitiesCache.get(url);
        }

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
     * @throws ExternalToolsException
     */
    private String getTimeSeriesPermalink(SirSearchResultElement sirSearchResultElement, SirServiceReference reference) throws MalformedURLException,
            ExternalToolsException {
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
            if ( !OpenSearchConstants.TIME_SERIES_SERVICE_TYPES.contains(reference.getService().getType())) {
                log.debug("Service type {} not supported.", reference.getService().getType());
                continue;
            }

            String offering = off.getId();
            String procedure = reference.getServiceSpecificSensorId();
            String serviceURL = reference.getService().getUrl();

            // TODO add service version to SirService
            String serviceVersion = "1.0.0";

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
                Calendar c = Calendar.getInstance();
                Date end = c.getTime();
                c.add(Calendar.DAY_OF_MONTH, -7); // 7 days
                Date start = c.getTime();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:MM:SS");
                TimeRange timeRange = TimeRange.createTimeRange(df.format(start), df.format(end));

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

                // ISSUE: there is no matching between feature of interest and observed property, therefore I
                // can only hack this for a few services atm
                if (serviceCapabilitiesSupported(serviceURL)) {
                    ICapabilitiesPermalinkMapper mapper = getCPMapper(serviceURL);

                    for (ReferenceType ref : featureOfInterestArray) {
                        String feature = ref.xgetHref().getStringValue();

                        if (mapper.isValidCombinationProcFoi(procedure, feature)) {
                            for (PhenomenonPropertyType phen : observedPropertyArray) {
                                String phenomenon = phen.xgetHref().getStringValue();

                                TimeSeriesParameters tsp = new TimeSeriesParameters(serviceURL,
                                                                                    serviceVersion,
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
                else {
                    log.warn("Service " + serviceURL
                            + " is not supported and therefore no permalinks generated because of the unknown "
                            + "mapping from obs prop to foi which could generate very large and useless permalinks.");
                    return null;
                }
            }
        }

        AccessLinkFactory linkFactory = builder.build();
        String accessURL;
        accessURL = linkFactory.createAccessURL(this.conf.getPermalinkBaseURL());

        if (accessURL.length() > OpenSearchConstants.MAX_GET_URL_CHARACTER_COUNT && this.conf.isCompressPermalinks()) {
            AccessLinkCompressor compressor = null;
            log.debug("Not using compressor {}", compressor);
        }

        return accessURL;
    }

    private ICapabilitiesPermalinkMapper getCPMapper(String serviceURL) {
        return this.mappers.get(serviceURL);
    }

    private boolean serviceCapabilitiesSupported(String serviceURL) {
        // TODO implement a service loader mechanism to support several services
        return this.mappers.containsKey(serviceURL);
    }

    /**
     * @param serviceReference
     * @param url
     * @return
     */
    private XmlObject updateCache(SirServiceReference serviceReference, URL url) {
        log.info("Updating chache for service reverence " + serviceReference);

        XmlObject caps;
        // TODO use threads for this, then update the interface one after the other (loader image and
        // AJAX?)

        try {
            caps = this.sirClient.requestCapabilities(serviceReference.getService().getType(), url.toURI());

            if (caps instanceof ExceptionReportDocument) {
                log.debug("Got ExceptionReportDocument as response!\n\n" + caps.xmlText());
                return null;
            }

            this.capabilitiesCache.put(url, caps);
            this.capabilitiesCacheAge.put(url, new Date());
        }
        catch (OwsExceptionReport e) {
            log.error("Could not get service capabilities.", e);
            return null;
        }
        catch (URISyntaxException e) {
            log.error("Could not get service capabilities.", e);
            return null;
        }

        return this.capabilitiesCache.get(url);
    }

}
