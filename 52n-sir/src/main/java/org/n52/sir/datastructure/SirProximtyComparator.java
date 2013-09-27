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
/**
 * @author Yakoub
 */

package org.n52.sir.datastructure;


import java.util.Comparator;

public class SirProximtyComparator implements Comparator<SirSearchResultElement> {
    private double longitude;

    private double latitude;

    public SirProximtyComparator(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public int compare(SirSearchResultElement o1,
            SirSearchResultElement o2)
    {
        SirBoundingBox b1 = ((SirSimpleSensorDescription) o1.getSensorDescription()).getBoundingBox();
        SirBoundingBox b2 = ((SirSimpleSensorDescription) o2.getSensorDescription()).getBoundingBox();
        double[] c1 = b1.getCenter();
        double[] c2 = b2.getCenter();
        
        double d1 =Math.pow((c1[0]-this.longitude),2)+Math.pow(c1[1]-this.latitude,2);
        double d2 =Math.pow((c2[0]-this.longitude),2)+Math.pow(c2[1]-this.latitude,2);
        return (int)(d1-d2);
    }
}
