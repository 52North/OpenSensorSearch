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

package org.n52.sir.ds.pgsql;

import org.n52.oss.sir.api.TimePeriod;
import org.n52.oss.sir.api.TimePeriod.IndeterminateTime;
import org.n52.oss.sir.api.TimePeriod.IndeterminateTime.IndeterminateTimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlTools {

    private static final Logger log = LoggerFactory.getLogger(SqlTools.class);

    public static String escapeSQLString(String text) {
        String s = text;
        if (text.contains("'")) {
            log.debug("Text contains character that has to be escaped before database insertion, namely ' .");
            s = s.replace("'", "\\'");
        }
        return s;
    }

    public static String getStartDate(final TimePeriod period) {
        IndeterminateTime startTime = period.getStartTime();
        if (startTime.isIndeterminate() && startTime.itt.equals(IndeterminateTimeType.UNKNOWN))
            return "-infinity";

        return getDateStringForDB(startTime);
    }

    private static String getDateStringForDB(IndeterminateTime t) {
        if (t.isDeterminate())
            return t.d.toString();

        log.error("Cannot create DB time string from time {}", t);
        return null;
    }

    public static String getEndDate(TimePeriod period) {
        IndeterminateTime endTime = period.getEndTime();
        if (endTime.isIndeterminate() && endTime.itt.equals(IndeterminateTimeType.UNKNOWN))
            return "infinity";

        return getDateStringForDB(endTime);
    }

}
