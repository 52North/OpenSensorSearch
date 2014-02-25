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

package org.n52.oss;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNull;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.n52.oss.sir.api.TimePeriod;
import org.n52.oss.sir.api.TimePeriod.IndeterminateTime;
import org.n52.oss.sir.api.TimePeriod.IndeterminateTime.IndeterminateTimeType;
import org.n52.sir.ds.pgsql.SqlTools;

public class IndeterminateTimeTest {

    private TimePeriod foreverTimePeriod;
    private TimePeriod testTimePeriod;
    private Date testStart;
    private Date testEnd;

    @Before
    public void prepare() {
        this.foreverTimePeriod = new TimePeriod();
        this.foreverTimePeriod.setStartTime(new IndeterminateTime(IndeterminateTimeType.UNKNOWN));
        this.foreverTimePeriod.setEndTime(new IndeterminateTime(IndeterminateTimeType.UNKNOWN));

        this.testTimePeriod = new TimePeriod();
        this.testStart = new Date(0);
        this.testTimePeriod.setStartTime(new IndeterminateTime(this.testStart));
        this.testEnd = new Date(42);
        this.testTimePeriod.setEndTime(new IndeterminateTime(this.testEnd));
    }

    @Test
    public void normalTimePeriodContaintsCorrectDates() {
        assertThat("start date is correct", this.testTimePeriod.getStartTime().d, is(equalTo(this.testStart)));
        assertThat("start is determinate", this.testTimePeriod.getStartTime().isDeterminate(), is(equalTo(true)));

        assertThat("end date is correct", this.testTimePeriod.getEndTime().d, is(equalTo(this.testEnd)));
        assertThat("end is determinate", this.testTimePeriod.getEndTime().isDeterminate(), is(equalTo(true)));
    }

    @Test
    public void unknownStartTimeIsHandled() {
        assertThat("start date is correct", this.foreverTimePeriod.getStartTime().d, is(equalTo(isNull())));
        assertThat("start date is correct",
                   this.foreverTimePeriod.getStartTime().itt,
                   is(equalTo(IndeterminateTimeType.UNKNOWN)));
        assertThat("start is indeterminate", this.foreverTimePeriod.getStartTime().isIndeterminate(), is(true));
    }

    @Test
    public void unknownEndTimeIsHandled() {
        assertThat("end date is correct", this.foreverTimePeriod.getEndTime().d, is(equalTo(isNull())));
        assertThat("end date is correct",
                   this.foreverTimePeriod.getEndTime().itt,
                   is(equalTo(IndeterminateTimeType.UNKNOWN)));
        assertThat("end is indeterminate", this.foreverTimePeriod.getEndTime().isIndeterminate(), is(true));
    }

    @Test
    public void correctSqlStartTime() {
        String s = SqlTools.getStartDate(this.foreverTimePeriod);
        assertThat("unknown start date is '-infinity'", s, is(equalTo("-infinity")));
    }

    @Test
    public void correctSqlEndTime() {
        String s = SqlTools.getEndDate(this.foreverTimePeriod);
        assertThat("unknown end date is 'infinity'", s, is(equalTo("infinity")));
    }

}
