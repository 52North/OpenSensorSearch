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
package org.n52.oss.sir.api;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class SirTimePeriod {

    /**
     * end of measure
     */
    private Date endTime = new GregorianCalendar(2099, 11, 31).getTime();

    /**
     * start of measure
     */
    private Date startTime = new Date(0);

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return this.endTime;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return this.startTime;
    }

    /**
     * @param endTime
     *        the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @param startTime
     *        the startTime to set
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(this.startTime);
        sb.append(" - ");
        sb.append(this.endTime);
        sb.append("]");
        return sb.toString();
    }

    /**
     * combindes this time period and the given time period for the maximal extend.
     * 
     * @param other
     */
    public void union(SirTimePeriod other) {
        if (this.startTime.after(other.getStartTime())) {
            this.startTime.setTime(other.getStartTime().getTime());
        }
        if (this.endTime.before(other.getEndTime())) {
            this.endTime.setTime(other.getEndTime().getTime());
        }
    }

}
