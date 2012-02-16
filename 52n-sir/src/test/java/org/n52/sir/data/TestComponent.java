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
package org.n52.sir.data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.opengis.sensorML.x101.ContactDocument.Contact;

/**
 * @author Daniel Nüst (d.nuest@52north.org)
 *
 */
public class TestComponent extends TestSensor {

    protected String parentSystemUniqueID;

    /**
     * 
     * @param gmlDescription
     * @param keywords
     * @param uniqueId
     * @param longName
     * @param shortName
     * @param intendedApplication
     * @param sensorType
     * @param validTimeBegin
     * @param validTimeEnd
     * @param bboxLowerCorner
     * @param bboxUpperCorner
     * @param locationId
     * @param contact
     * @param latLonPosition
     * @param altitude
     * @param interfaces
     * @param inputs
     * @param output
     * @param parentSystemUniqueID
     */
    public TestComponent(String gmlDescription,
                         List<String> keywords,
                         String uniqueId,
                         String longName,
                         String shortName,
                         String intendedApplication,
                         String sensorType,
                         String validTimeBegin,
                         String validTimeEnd,
                         double[] bboxLowerCorner,
                         double[] bboxUpperCorner,
                         String locationId,
                         Contact contact,
                         double[] latLonPosition,
                         double altitude,
                         List<Map<String, String>> interfaces,
                         List<Map<String, String>> inputs,
                         List<Map<String, String>> output,
                         String parentSystemUniqueID) {
        super(gmlDescription,
              keywords,
              uniqueId,
              longName,
              shortName,
              intendedApplication,
              sensorType,
              validTimeBegin,
              validTimeEnd,
              bboxLowerCorner,
              bboxUpperCorner,
              locationId,
              contact,
              latLonPosition,
              altitude,
              interfaces,
              inputs,
              output,
              null);
        this.parentSystemUniqueID = parentSystemUniqueID;
    }

    /*
     * (non-Javadoc)
     * @see org.n52.sir.data.TestSensor#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TestComponent [uniqueId: ");
        sb.append(this.uniqueId);
        sb.append(", parent: ");
        sb.append(this.parentSystemUniqueID);
        sb.append(", keywords: ");
        sb.append(Arrays.toString(this.keywords.toArray()));
        sb.append(", gmlDescription: ");
        sb.append(this.gmlDescription);
        sb.append(", intendedApplication: ");
        sb.append(this.intendedApplication);
        sb.append(", sensorType: ");
        sb.append(this.sensorType);
        sb.append(", validTime (begin, end): (");
        sb.append(this.validTimeBegin);
        sb.append(", ");
        sb.append(this.validTimeEnd);
        sb.append(", latLonPosition, altitude: ");
        sb.append(Arrays.toString(this.latLonPosition));
        sb.append(", ");
        sb.append(this.altitude);
        sb.append(", bbox (upperCorner, lowerCorner): (");
        sb.append(Arrays.toString(this.bboxUpperCorner));
        sb.append(", ");
        sb.append(Arrays.toString(this.bboxLowerCorner));
        sb.append("]");
        return sb.toString();
    }
    
}
