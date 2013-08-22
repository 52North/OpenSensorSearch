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

import java.util.ArrayList;
import java.util.Collection;

import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.datastructure.SirSimpleSensorDescription;

/**
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class Tools {

    public static void concatenateWithLimit(StringBuilder sb, ArrayList<String> list, int maxElements) {
        ArrayList<String> myList = new ArrayList<>();
        while (myList.size() < maxElements) {
            // duplicate!
            myList.addAll(list);
        }

        int i = 0;
        while (i < maxElements) {
            String s = myList.get(i);
            sb.append(s);

            i++;
            if (i == maxElements)
                break;

            sb.append(",");
        }
    }

    public static String createGetCapabilitiesRequestURL(SirServiceReference reference) {
        return reference.getService().getUrl() + "?REQUEST=GetCapabilities&SERVICE=SOS";
    }

    /**
     * handle only & characters
     * 
     * @param url
     * @return
     */
    public static String decode(String url) {
        String s = url.replaceAll("&amp;", "\\&");
        return s;
    }

    /**
     * 
     * using the original URLEncoder does not work, it always appends the sir URL in front...
     * 
     * // getCapRequest = URLEncoder.encode(getCapRequest, //
     * SirConfigurator.getInstance().getCharacterEncoding());
     * 
     * @param getCapRequest
     * @return
     */
    public static String encode(String url) {
        String s = url.replaceAll("\\&", "&amp;");
        return s;
    }

    public static String extractDescriptionText(SirSimpleSensorDescription sensorDescription) {
        String ds = sensorDescription.getDescriptionText();

        // remove CDATA (if it exists)
        if (ds.contains(OpenSearchConstants.CDATA_START_TAG)) {
            ds = ds.replace(OpenSearchConstants.CDATA_START_TAG, "");

            if (ds.endsWith(OpenSearchConstants.CDATA_END_TAG))
                ds = ds.substring(0, ds.length() - 1);
        }

        // see if the string contains new line characters
        if (ds.contains("\n"))
            ds = ds.replaceAll("\\n", System.getProperty("line.separator"));

        // encode possibly problematic characters
        if (ds.contains("&"))
            ds = encode(ds);

        return ds;
    }

    public static String extractEntryTitle(SirSearchResultElement sirSearchResultElement) {
        StringBuilder sb = new StringBuilder();

        Collection<SirServiceReference> serviceReferences = sirSearchResultElement.getServiceReferences();
        for (SirServiceReference sirServiceReference : serviceReferences) {
            sb.append(sirServiceReference.getServiceSpecificSensorId());
            sb.append(" ");
        }

        return sb.toString();
    }

    /**
     * highlight all occurences of searchText using <b>-elements.
     * 
     * @param text
     * @param searchText
     * @param highlightSearchText
     * @param addLinksInSearchText
     * @return
     */
    public static String highlightHTML(String text,
                                       String searchText,
                                       boolean highlightSearchText,
                                       boolean addLinksInSearchText) {
        String s = text;

        StringBuffer regex = new StringBuffer();
        if (highlightSearchText) {
            String[] words = searchText.split(" ");
            for (String word : words) {
                // log.debug("Highlighting the word " + word);
                String head = "(?i)("; // case insensitive
                String tail = ")(?!([^<]+)?>>)";

                regex.delete(0, regex.length());
                regex.append(head);
                regex.append(word);
                regex.append(tail);

                s = s.replaceAll(regex.toString(), "<b>$1</b>");
            }
        }

        if (addLinksInSearchText) {
            // TODO remove all <b> tags that are within a URL

            // http://regexlib.com/Search.aspx?k=URL&AspxAutoDetectCookieSupport=1
            // String regex = "((mailto\\\\:|(news|(ht|f)tp(s?))\\\\://){1}\\\\S+)";

            // http://stackoverflow.com/questions/1909534/java-replacing-text-url-with-clickable-html-link
            s = s.replaceAll("(.*://[^<>[:space:]]+[[:alnum:]/])", "<a href=\"$1\">$1</a>");
        }

        return s;
    }

}
