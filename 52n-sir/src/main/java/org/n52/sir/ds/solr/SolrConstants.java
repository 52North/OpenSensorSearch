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
    public static final String START_DATE = "beginPosition";
    public static final String END_DATE = "endPosition";
    public static final String DESCRIPTION = "description";
    public static final String CLASSIFIER = "classifier";
    public static final String IDENTIFICATION = "identification";
    public static final String CONTACTS = "contact";
    public static final String INTERFACE = "interface";
    public static final String INPUT = "input";
    public static final String OUTPUT = "output";
    public static final String EDISMAX = "keyword^1 uniqueID^1 location^1 bboxcenter^1 beginPosition^1 endPosition^1 description^1 classifier^1 identification^1 contact^1 interface^1 input^1 output^1";
    /**
     * TODO Remove this an configure it later in SIR configurations
     */
    public static final String SOLR_URL = "http://localhost:8983/solr";
}
