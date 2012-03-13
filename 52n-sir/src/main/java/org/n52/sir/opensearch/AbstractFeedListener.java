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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirSimpleSensorDescription;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
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
public abstract class AbstractFeedListener implements IOpenSearchListener {

    protected static final Logger log = LoggerFactory.getLogger(AbstractFeedListener.class);

    private OpenSearchConfigurator conf;

    public AbstractFeedListener(OpenSearchConfigurator configurator) {
        this.conf = configurator;
        this.conf.addResponseFormat(this);
    }

    @Override
    public void createResponse(HttpServletRequest req,
                               HttpServletResponse resp,
                               Collection<SirSearchResultElement> searchResult,
                               PrintWriter writer,
                               String searchText) throws OwsExceptionReport {
        // TODO Auto-generated method stub

        resp.setContentType(getMimeType());

        // TODO create WireFeed, then reuse for Atom AND RSS, see
        // http://en.wikipedia.org/wiki/RSS#Comparison_with_Atom
        SyndFeed feed = createFeed(searchResult, searchText);
        feed.setFeedType(getFeedType());

        outputFeed(writer, feed);

    }

    protected abstract String getFeedType();

    /**
     * @param writer
     * @param feed
     * @throws OwsExceptionReport
     */
    protected void outputFeed(PrintWriter writer, SyndFeed feed) throws OwsExceptionReport {
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
     * 
     * @param searchResult
     * @param searchText
     * @return
     */
    protected SyndFeed createFeed(Collection<SirSearchResultElement> searchResult, String searchText) {
        SyndFeed feed = new SyndFeedImpl();

        feed.setTitle("Sensor Search for " + searchText);
        String channelURL = this.conf.getFullOpenSearchPath() + "?" + OpenSearchConstants.QUERY_PARAMETER + "="
                + searchText + "&" + OpenSearchConstants.ACCEPT_PARAMETER + "=" + getMimeType();
        feed.setLink(channelURL);
        feed.setPublishedDate(new Date());
        feed.setAuthor(this.conf.getFeedAuthor());
        // feed.setContributors(contributors) // TODO add all service contacts
        // feed.setCategories(categories) // TODO user tags for categories
        feed.setEncoding(this.conf.getCharacterEncoding());
        SyndImage image = new SyndImageImpl();
        image.setUrl("http://52north.org/templates/52n/images/52n-logo.gif");
        image.setLink(this.conf.getFullServicePath().toString());
        image.setTitle("52°North Logo");
        image.setDescription("Logo of the provider of Open Sensor Search: 52°North");
        feed.setImage(image);
        feed.setDescription("These are the sensors for the keywords '" + searchText + "' from Open Sensor Search ("
                + this.conf.getFullServicePath().toString() + ").");

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
     * @param ssre
     * @return
     */
    protected SyndEntry createFeedEntry(SirSearchResultElement ssre) {
        SirSimpleSensorDescription sensorDescription = (SirSimpleSensorDescription) ssre.getSensorDescription();

        SyndEntry entry = new SyndEntryImpl();
        // SyndContent title = new SyndContentImpl();
        // title.setType(MIME_TYPE_HTML)
        entry.setTitle(ssre.getSensorIdInSir());
        try {
            // String link = URLDecoder.decode(sensorDescription.getSensorDescriptionURL(),
            // this.configurator.getCharacterEncoding());
            String link = Tools.decode(URLDecoder.decode(sensorDescription.getSensorDescriptionURL(),
                                                         this.conf.getCharacterEncoding()));

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
        descr.setType(OpenSearchConstants.MIME_TYPE_PLAIN); // alternative e.g. text/html
        descr.setValue(Tools.extractDescriptionText(sensorDescription));
        entry.setDescription(descr);

        return entry;
    }

}
