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

package org.n52.oss.db.solr;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;
import org.n52.sir.datastructure.SirSearchCriteria;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;

public class SolrSearchSensor {

    @Test
    public void wordslistIsCreatedCorrectlyFromSearchCriteria() {
        SOLRSearchSensorDAO dao = new SOLRSearchSensorDAO();
        SirSearchCriteria searchCriteria = new SirSearchCriteria();
        searchCriteria.setSearchText(Arrays.asList(new String[] {"this", "is my", "searchText"}));
        String actual = dao.createWordslist(searchCriteria);
        String expected = "this+is my+searchText";

        assertThat("wordslist is correct", actual, is(equalTo(expected)));
    }

    @Test
    public void wordslistForNullSearchCriteria() {
        SOLRSearchSensorDAO dao = new SOLRSearchSensorDAO();
        SirSearchCriteria searchCriteria = new SirSearchCriteria();
        String actual = dao.createWordslist(searchCriteria);
        String expected = "";

        assertThat("wordslist is correct", actual, is(equalTo(expected)));
    }

    @Test
    public void wordslistForEmptySearchCriteria() {
        SOLRSearchSensorDAO dao = new SOLRSearchSensorDAO();
        SirSearchCriteria searchCriteria = new SirSearchCriteria();
        searchCriteria.setSearchText(Arrays.asList(new String[] {}));
        String actual = dao.createWordslist(searchCriteria);
        String expected = "";

        assertThat("wordslist is correct", actual, is(equalTo(expected)));
    }

    @Test
    public void wordslistForEmptyStringSearchCriteria() {
        SOLRSearchSensorDAO dao = new SOLRSearchSensorDAO();
        SirSearchCriteria searchCriteria = new SirSearchCriteria();
        searchCriteria.setSearchText(Arrays.asList(new String[] {"", "", ""}));
        String actual = dao.createWordslist(searchCriteria);
        String expected = "";

        assertThat("wordslist is correct", actual, is(equalTo(expected)));
    }

}
