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

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * A basic servlet to handle restful requests.
 * 
 * @author <a href="mailto:broering@52north.org">Arne Broering</a>, Daniel Nüst
 * 
 */
public class RestWebService extends HttpServlet {

    private static final long serialVersionUID = 1498881311814410535L;

    protected static final String RESPONSE_CONTENT_TYPE_PLAIN = "text/plain";

    protected static final String RESPONSE_CHARSET = "utf-8";

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (OutputStream out = response.getOutputStream();) {
            response.setContentType(RESPONSE_CONTENT_TYPE_PLAIN);
            out.write("DELETE not yet supported!".getBytes());

            out.flush();
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (OutputStream out = response.getOutputStream();) {
            response.setContentType(RESPONSE_CONTENT_TYPE_PLAIN);
            out.write("POST not yet supported!".getBytes());
            out.flush();
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
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