/**
 * Copyright 2012 52°North Initiative for Geospatial Open Source Software GmbH
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
package org.n52.ar.layar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * 
 * Based on code by Mansour Raad, http://thunderheadxpler.blogspot.com/2010_03_01_archive.html
 * 
 * See http://layar.com/documentation/browser/api/getpois-response/
 * 
 * @author Daniel
 * 
 */
public class LayarResponse {

    public static final int CODE_ERROR = 20;

    public static final int CODE_OK = 0;

    public int errorCode = CODE_OK; // 0 = "ok";

    public String errorString = "OK";

    public Collection<Hotspot> hotspots = new ArrayList<Hotspot>();

    public String layer;

    private boolean morePages = false;

    private String nextPageKey;

    public String showMessage;

    /**
     * 
     * "hotspots": [{
     * 
     * "id": "test_1",
     * 
     * "anchor": { "geolocation": { "lat": 52.3729, "lon": 4.93 } },
     * 
     * "text": { "title": "The Layar Office", "description": "The Location of the Layar Office", "footnote":
     * "Powered by Layar" },
     * 
     * "imageURL": "http:\/\/custom.layar.nl\/layarimage.jpeg", }
     * 
     * ]
     * 
     * See http://layar.com/documentation/browser/api/getpois-response/hotspots/
     * 
     * @param generator
     * @param hotspots2
     * @throws IOException
     * @throws JsonGenerationException
     */
    private void createHotspots(JsonGenerator generator) throws JsonGenerationException, IOException {
        generator.writeFieldName("hotspots");
        generator.writeStartArray();
        for (Hotspot poi : this.hotspots) {

            generator.writeStartObject();
            generator.writeStringField("id", poi.id);

            // generator.writeFieldName("actions");
            // generator.writeStartArray();
            // if (layarPOI.actions != null) {
            // for (final LayarAction layarAction : layarPOI.actions) {
            // layarAction.toJSON(generator);
            // }
            // }
            // generator.writeEndArray();

            generator.writeObjectFieldStart("anchor");
            generator.writeObjectFieldStart("geolocation");
            generator.writeNumberField("lat", poi.lat);
            generator.writeNumberField("lon", poi.lon);
            generator.writeNumberField("alt", poi.alt);
            generator.writeEndObject();
            generator.writeEndObject();

            // generator.writeNumberField("distance", layarPOI.distance);
            // generator.writeNumberField("type", layarPOI.type);
            // generator.writeStringField("title", layarPOI.title);
            generator.writeObjectFieldStart("text");
            generator.writeStringField("title", poi.title);
            generator.writeStringField("description", poi.description);
            generator.writeStringField("footnote", "Service URL: ...");
            generator.writeEndObject();

            generator.writeStringField("attribution", poi.attribution);
            if (poi.imageURL != null) {
                generator.writeStringField("imageURL", poi.imageURL.toString());
            }
            else {
                generator.writeNullField("imageURL");
            }
            generator.writeEndObject();
        }
        generator.writeEndArray();

    }

    public void toJSON(final JsonGenerator generator) throws IOException {
        generator.writeStartObject();

        /*
         * mandatory:
         */
        generator.writeStringField("layer", this.layer);
        if (this.errorCode != CODE_ERROR && this.hotspots.size() < 1) {
            this.errorString = "No POI found. Please increase the search range to try again.";
            this.errorCode = CODE_ERROR;
        }
        generator.writeStringField("errorString", this.errorString);
        generator.writeNumberField("errorCode", this.errorCode);

        // paging is not implemented yet
        generator.writeBooleanField("morePages", this.morePages);
        if (this.nextPageKey != null) {
            generator.writeStringField("nextPageKey", this.nextPageKey);
        }
        else {
            generator.writeNullField("nextPageKey");
        }
        // generator.writeNumberField("refreshInterval", 3600);
        // generator.writeNumberField("refreshDistance", 50);
        // generator.writeBooleanField("fullRefresh", false);
        // actions for the entire layer: http://layar.com/documentation/browser/api/getpois-response/actions/
        if (this.errorCode == CODE_OK)
            this.showMessage = "You got " + this.hotspots.size() + " hits in the area around you!";
        generator.writeStringField("showMessage", this.showMessage);
        // deletedHotspots
        // animations
        // showDialog: title, description, iconURL, actions
        generator.writeNullField("biwStyle");

        /*
         * hotspots (mandatory):
         */
        createHotspots(generator);

        generator.writeEndObject();
    }

}