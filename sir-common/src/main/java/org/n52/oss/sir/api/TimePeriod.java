/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.n52.oss.sir.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class TimePeriod {

    private static Logger log = LoggerFactory.getLogger(TimePeriod.class);

    /**
     * http://schemas.stylusstudio.com/opengis/n9b256d84/simpleType_TimeIndeterminateValueType.html
     * 
     */
    public static class IndeterminateTime {

        // dateformater for ISO 8601 Date format
        private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        public static enum IndeterminateTimeType {
            BEFORE, AFTER, NOW, UNKNOWN;
        }

        public Date d = null;

        public IndeterminateTimeType itt;

        public boolean isIndeterminate() {
            return this.itt != null;
        }

        public boolean isDeterminate() {
            return this.d != null;
        }

        public IndeterminateTime(Date d) {
            this.d = d;
        }

        public IndeterminateTime(IndeterminateTimeType t) {
            this.itt = t;
        }

        public IndeterminateTime(String s) {
            try {
                this.d = sdf.parse(s);
            }
            catch (ParseException e) {
                log.warn("Error parsing IndeterminateTime from String {}", s, e);
            }

            if (this.d == null) {
                IndeterminateTimeType t = IndeterminateTimeType.valueOf(s.toUpperCase());
                if (t != null)
                    this.itt = t;
            }

            log.error("NEW {} from {}", this, s);
        }

        public static IndeterminateTimeType getType(String s) {
            return IndeterminateTimeType.valueOf(s.toUpperCase());
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("IndeterminateTime [");
            if (this.d != null) {
                builder.append("d=");
                builder.append(this.d);
                builder.append(", ");
            }
            if (this.itt != null) {
                builder.append("itt=");
                builder.append(this.itt);
            }
            builder.append("]");
            return builder.toString();
        }

    }

    private IndeterminateTime endTime = new IndeterminateTime(new GregorianCalendar(2099, 12, 31).getTime());

    private IndeterminateTime startTime = new IndeterminateTime(new Date(0));

    /**
     * combindes this time period and the given time period for the maximal extend.
     * 
     * @param other
     */
    public void union(TimePeriod other) {
        if (this.startTime.d.after(other.getStartTime().d)) {
            this.startTime.d.setTime(other.getStartTime().d.getTime());
        }
        if (this.endTime.d.before(other.getEndTime().d)) {
            this.endTime.d.setTime(other.getEndTime().d.getTime());
        }
    }

    public IndeterminateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(IndeterminateTime endTime) {
        this.endTime = endTime;
    }

    public IndeterminateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(IndeterminateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TimePeriod [");
        if (this.endTime != null) {
            builder.append("endTime=");
            builder.append(this.endTime);
            builder.append(", ");
        }
        if (this.startTime != null) {
            builder.append("startTime=");
            builder.append(this.startTime);
        }
        builder.append("]");
        return builder.toString();
    }

}
