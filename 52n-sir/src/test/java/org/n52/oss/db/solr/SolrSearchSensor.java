
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
    public void wordslistForEmptySearchCriteria() {
        SOLRSearchSensorDAO dao = new SOLRSearchSensorDAO();
        SirSearchCriteria searchCriteria = new SirSearchCriteria();
        String actual = dao.createWordslist(searchCriteria);
        String expected = "";

        assertThat("wordslist is correct", actual, is(equalTo(expected)));
    }

}
