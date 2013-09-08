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
package org.n52.oss.opensearch.listeners.feed;

import org.n52.oss.opensearch.OpenSearchConfigurator;
import org.n52.oss.opensearch.OpenSearchConstants;

import com.google.inject.Inject;

/**
 * 
 * TODO make GeoRSS!!!
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class RssListener extends AbstractFeedListener {

    private static final String FEED_TYPE = "rss_2.0";

    public static final String MIME_TYPE = OpenSearchConstants.APPLICATION_RSS_XML;

    private static final String NAME = "RSS";

    @Inject
    public RssListener(OpenSearchConfigurator configurator) {
        super(configurator);
    }

    @Override
    protected String getFeedType() {
        return FEED_TYPE;
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
