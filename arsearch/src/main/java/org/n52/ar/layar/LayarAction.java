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
package org.n52.ar.layar;

import java.io.IOException;
import java.net.URI;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * 
 * Based on code by Mansour Raad, http://thunderheadxpler.blogspot.com/2010_03_01_archive.html
 * 
 * @author Daniel
 * 
 */
public class LayarAction {
    
    public String label;
    
    public URI uri;

    public void toJSON(final JsonGenerator generator) throws IOException {
        generator.writeStartObject();
        generator.writeStringField("uri", this.uri.toString());
        generator.writeStringField("label", this.label);
        generator.writeEndObject();
    }
}