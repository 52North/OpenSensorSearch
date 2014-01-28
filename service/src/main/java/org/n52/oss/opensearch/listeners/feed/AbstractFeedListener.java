/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.oss.opensearch.listeners.feed;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.n52.oss.opensearch.OpenSearchConfigurator;
import org.n52.oss.opensearch.OpenSearchConstants;
import org.n52.oss.opensearch.listeners.OpenSearchListener;
import org.n52.oss.opensearch.listeners.OpenSearchTools;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.api.SirSimpleSensorDescription;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public abstract class AbstractFeedListener implements OpenSearchListener {

    protected static final Logger log = LoggerFactory.getLogger(AbstractFeedListener.class);

    private OpenSearchConfigurator conf;

    private URI openSearchEndpoint;

    private URI homeUri;

    public AbstractFeedListener(OpenSearchConfigurator configurator) {
        this.conf = configurator;
        this.conf.addResponseFormat(this);
    }

    protected SyndFeed createFeed(Collection<SirSearchResultElement> searchResult, String query) {
        SyndFeed feed = new SyndFeedImpl();

        String searchUri = this.openSearchEndpoint.toString();
        String website = this.homeUri.toString();

        feed.setTitle("Sensor Search for " + query);
        String channelURL = searchUri + "?" + OpenSearchConstants.QUERY_PARAM + "=" + query
                + "&" + OpenSearchConstants.FORMAT_PARAM + "=" + getMimeType();
        feed.setLink(channelURL);
        feed.setPublishedDate(new Date());
        feed.setAuthor(this.conf.getFeedAuthor());
        // feed.setContributors(contributors) // TODO add all service contacts
        // feed.setCategories(categories) // TODO user tags for categories

        SyndImage image = new SyndImageImpl();
        image.setUrl("http://52north.org/templates/52n/images/52n-logo.gif");
        image.setLink(website);
        image.setTitle("52°North Logo");
        image.setDescription("Logo of the provider of Open Sensor Search: 52°North");
        feed.setImage(image);
        feed.setDescription("These are the sensors for the keywords '" + query + "' from Open Sensor Search ("
                + website + ").");

        List<SyndEntry> entries = new ArrayList<>();
        for (SirSearchResultElement ssre : searchResult) {
            SyndEntry e = createFeedEntry(ssre);
            entries.add(e);
        }
        feed.setEntries(entries);

        return feed;
    }

    protected SyndEntry createFeedEntry(SirSearchResultElement ssre) {
        SirSimpleSensorDescription sensorDescription = (SirSimpleSensorDescription) ssre.getSensorDescription();

        SyndEntry entry = new SyndEntryImpl();
        // SyndContent title = new SyndContentImpl();
        // title.setType(MIME_TYPE_HTML)
        entry.setTitle(ssre.getSensorId());
        try {
            // String link = URLDecoder.decode(sensorDescription.getSensorDescriptionURL(),
            // this.configurator.getCharacterEncoding());
            String link = OpenSearchTools.decode(URLDecoder.decode(sensorDescription.getSensorDescriptionURL(),
                                                         OpenSearchConstants.URL_DECODE_ENCODING));

            entry.setLink(link);
        }
        catch (UnsupportedEncodingException e) {
            log.warn("Could not create URL for sensor {}", ssre.getSensorId());
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
        descr.setType(OpenSearchConstants.MIME_TYPE_PLAIN); // alternative e.g. text/html
        descr.setValue(OpenSearchTools.extractDescriptionText(sensorDescription));
        entry.setDescription(descr);

        return entry;
    }

    @Override
    public Response createResponse(final Collection<SirSearchResultElement> searchResult,
                                   final MultivaluedMap<String, String> params) throws OwsExceptionReport {
        if (this.openSearchEndpoint == null || this.homeUri == null) {
            log.error("Could not create response because OpenSearch endpoint or home URI are not set: {}", this);
            return Response.serverError().build();
        }

        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                String query = params.getFirst(OpenSearchConstants.QUERY_PARAM);

                OutputStreamWriter writer = new OutputStreamWriter(new BufferedOutputStream(os));

                // TODO create WireFeed, then reuse for Atom AND RSS, see
                // http://en.wikipedia.org/wiki/RSS#Comparison_with_Atom
                SyndFeed feed = createFeed(searchResult, query);
                feed.setFeedType(getFeedType());

                SyndFeedOutput output = new SyndFeedOutput();

                try {
                    output.output(feed, writer, true);
                }
                catch (IllegalArgumentException | FeedException e) {
                    log.error("Error outputting feed to writer", e);
                    throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
                }

            }
        };

        return Response.ok(stream).build();
    }

    @Override
    public void setOpenSearchEndpoint(URI uri) {
        this.openSearchEndpoint = uri;
    }

    @Override
    public void setHomeURI(URI uri) {
        this.homeUri = uri;
    }

    protected abstract String getFeedType();

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AbstractFeedListener [");
        if (this.conf != null) {
            builder.append("conf=");
            builder.append(this.conf);
            builder.append(", ");
        }
        if (this.openSearchEndpoint != null) {
            builder.append("openSearchEndpoint=");
            builder.append(this.openSearchEndpoint);
            builder.append(", ");
        }
        if (this.homeUri != null) {
            builder.append("homeUri=");
            builder.append(this.homeUri);
        }
        builder.append("]");
        return builder.toString();
    }

}
