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
package org.n52.oss.testdata.sml;

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
     * 
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
