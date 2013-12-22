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

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;

/**
 * 
 * A basic servlet to handle restful requests.
 * 
 * @author <a href="mailto:broering@52north.org">Arne Broering</a>, Daniel Nüst
 * 
 */
public class RestWebService {

    protected static final String RESPONSE_CONTENT_TYPE_PLAIN = "text/plain";

    protected static final String RESPONSE_CHARSET = "utf-8";

    @DELETE
    public void doDelete(@Context
    HttpServletRequest request, @Context
    HttpServletResponse response) throws IOException {
        try (OutputStream out = response.getOutputStream();) {
            response.setContentType(RESPONSE_CONTENT_TYPE_PLAIN);
            out.write("DELETE not yet supported!".getBytes());

            out.flush();
        }
    }

    @POST
    public void doPost(@Context
    HttpServletRequest request, @Context
    HttpServletResponse response) throws IOException {
        try (OutputStream out = response.getOutputStream();) {
            response.setContentType(RESPONSE_CONTENT_TYPE_PLAIN);
            out.write("POST not yet supported!".getBytes());
            out.flush();
        }
    }

    @PUT
    public void doPut(@Context
    HttpServletRequest request, @Context
    HttpServletResponse response) throws IOException {
        try (OutputStream out = response.getOutputStream();) {
            response.setContentType(RESPONSE_CONTENT_TYPE_PLAIN);
            out.write("PUT not yet supported!".getBytes());
            out.flush();
        }
    }

    /**
     * Supporting Method.
     * 
     * @return the resources behind the "servletPath" of a request as a String (without the last '/').<br>
     *         E.g.:
     * 
     *         <pre>
     *         http://myHost.com/servletPath/res1/res2/ ==- 'res1/res2'
     *         http://myHost.com/servletPath/res1       ==- 'res1'
     *         http://myHost.com/servletPath/           ==- ''
     *         http://myHost.com/servletPath            ==- ''
     * </pre>
     */
    protected String getResourcesString(String requestURL, String servletPath) {

        // 'i' is the index of the first character after the "/" behind the "servletPath".
        int i = requestURL.indexOf(servletPath) + servletPath.length() + 1;

        if (i < requestURL.length()) {
            String res = requestURL.substring(i);
            if (res.endsWith("/")) {
                res = res.substring(0, res.length() - 1);
            }
            return res;
        }
        return "";
    }

    /**
     * Supporting Method.
     * 
     * @return the resources behind the "servletPath" of a request as an array of Strings. <br>
     *         E.g.:
     * 
     *         <pre>
     *         http://myHost.com/servletPath/res1/res2/ ==- String[] {'res1', 'res2'}
     *         http://myHost.com/servletPath/res1       ==- String[] {'res1'}
     *         http://myHost.com/servletPath/           ==- String[] {}
     *         http://myHost.com/servletPath            ==- String[] {}
     * </pre>
     * 
     */
    protected String[] getResourcesStringArray(String requestURL, String servletPath) {
        String resourcesString = getResourcesString(requestURL, servletPath);

        if (resourcesString.length() == 0) {
            return new String[] {};
        }
        else if (resourcesString.contains("/")) {
            return resourcesString.split("/");
        }
        else {
            return new String[] {resourcesString};
        }
    }

    /**
     * Renders the HTML resource view.
     */
    protected String renderResourcesHtml(String requestURL,
                                         String servletPath,
                                         String resourceType,
                                         String[] availableResources) {
        StringBuilder sb = new StringBuilder();

        String titleAndHeadline = "Resource Listing for: /" + getResourcesString(requestURL, servletPath);

        // Render the page header
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n");

        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n<head>\n");
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=");
        sb.append(RESPONSE_CHARSET);
        sb.append("\" />\n");

        sb.append("<title>");
        sb.append(titleAndHeadline);
        sb.append("</title>\n");

        sb.append("<style type=\"text/css\"><!--");
        sb.append(ResourceViewCSS.TOMCAT_CSS);
        sb.append("--></style>\n");

        sb.append("</head>\n");

        sb.append("<body>\n");
        sb.append("<h1 class=\"header\">");
        sb.append(titleAndHeadline);
        sb.append("</h1>\n");

        sb.append("<h2>");
        sb.append(resourceType);
        sb.append("</h2>\n");

        sb.append("<div id=\"content\"><table>\n");

        // Render the resources
        boolean shade = true;

        for (String resource : availableResources) {
            sb.append("<tr");
            if (shade)
                sb.append(" class=\"shaded\"");
            sb.append(">");
            shade = !shade;

            sb.append("<td>");
            sb.append("<a href=\"");
            if (requestURL.endsWith("/")) {
                sb.append(requestURL);
                sb.append(resource);
            }
            else {
                sb.append(requestURL);
                sb.append("/");
                sb.append(resource);
            }
            sb.append("\" title=\"Link to the resource ");
            sb.append(resource);
            sb.append("\">");
            sb.append(resource);
            sb.append("</a></td>\n");
            sb.append("</tr>\n");
        }
        sb.append("</table></div>\r\n");

        // Render the page footer
        sb.append("<p class=\"footer\">52&#176;North&nbsp;-&nbsp;RESTful&nbsp;Web&nbsp;Services</p>");
        sb.append("<p class=\"center\"><a href=\"http://validator.w3.org/check?uri=referer\"><img src=\"http://www.w3.org/Icons/valid-xhtml11\" alt=\"Valid XHTML 1.1\" /></a><a href=\"http://jigsaw.w3.org/css-validator/check/referer\"><img src=\"http://jigsaw.w3.org/css-validator/images/vcss\" alt=\"CSS is valid!\" /></a></p>");
        sb.append("</body>\r\n");
        sb.append("</html>\r\n");

        return sb.toString();
    }

}