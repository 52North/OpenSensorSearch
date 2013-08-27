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
package org.n52.sir.ds.solr;

public class SolrConstants {
    // All the fields names of the Solr Index

    public static final String ID = "id";
    public static final String KEYWORD = "keyword";
    public static final String UNIQUE_ID = "uniqueID";
    public static final String LONG_NAME = "longname";
    public static final String SHORT_NAME = "shortname";
    public static final String LOCATION = "location";
    public static final String BBOX_CENTER = "bboxcenter";
    public static final String START_DATE = "dtstart";
    public static final String END_DATE = "dtend";
    public static final String DESCRIPTION = "description";
    public static final String CLASSIFIER = "classifier";
    public static final String IDENTIFICATION = "identification";
    public static final String CONTACTS = "contact";
    public static final String INTERFACE = "interface";
    public static final String INPUT = "input";
    public static final String OUTPUT = "output";
    public static final String EDISMAX = "keyword^1 uniqueID^1 location^1 bboxcenter^1 description^1 classifier^1 identification^1 contact^1 interface^1 input^1 output^1";
    public static final String AND_OP ="AND";
    public static final String OR_OP = "OR";
    
    /**
     * TODO Remove this an configure it later in SIR configurations
     */
    public static final String SOLR_URL = "http://localhost:8983/solr";
}
