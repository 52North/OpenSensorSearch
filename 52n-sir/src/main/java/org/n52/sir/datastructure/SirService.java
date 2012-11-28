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

package org.n52.sir.datastructure;

/**
 * 
 * represents a SIR internal service
 * 
 * @author Jan Schulte
 * 
 */
public class SirService {

    /**
     * service type
     */
    private String type;

    /**
     * service url
     */
    private String url;

    /**
     * @param url
     *        Service url
     * @param type
     *        Service type
     */
    public SirService(String url, String type) {
        this.url = url;
        this.type = type;
    }

    /**
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * @param type
     *        the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @param url
     *        the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SirService: ");
        sb.append("URL: " + this.url);
        sb.append(", Type: " + this.type);
        return sb.toString();
    }
}
