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