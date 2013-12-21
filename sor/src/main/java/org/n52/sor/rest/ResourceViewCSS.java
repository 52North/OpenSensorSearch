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

package org.n52.sor.rest;

public class ResourceViewCSS {

    private static final String HEADING_BG_COLOR = "#cde2ed";

    private static final String HEADING_FONT_COLOR = "black";

    private static final String SHADING_BG_COLOR = "#cde2ed";

    private static final String BODY = "BODY {font-family:Tahoma,Arial,sans-serif;color:black;background-color:white; font-size: 100%;}";

    private static final String HEADLINES = "H1 {color:" + HEADING_FONT_COLOR + "; background-color:"
            + HEADING_BG_COLOR + "; font-size: 1.4em;} " + "H2 {color:" + HEADING_FONT_COLOR
            + "; font-size: 1.2em; font-weight: bold;} ";

    private static final String LINKS = "A {color : black;}" + "A.name {color : black;}";

    private static final String TABLE = "table { border-collapse: collapse; width: 100%; } td { padding: 0.6em; text-align: left; }";

    private static final String CLASSES = ".header { padding: 2px; border-bottom: 1px solid " + HEADING_FONT_COLOR
            + "; }" + ".footer { color:" + HEADING_FONT_COLOR + "; background-color:" + HEADING_BG_COLOR
            + "; font-size: 1.2em; padding: 2px; border-top: 1px solid " + HEADING_FONT_COLOR + "; }"
            + ".shaded { background-color: " + SHADING_BG_COLOR + "}" + "#content { font-family: monospace; } "
            + ".center { text-align:center; }";

    private static final String IMG = "img { border: none; margin: 0 10px; } ";

    public static final String TOMCAT_CSS = BODY + HEADLINES + LINKS + CLASSES + TABLE + IMG;
}