
package org.n52.oss.util;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IgnoreDateValueComparator extends DefaultComparator {
    public IgnoreDateValueComparator(JSONCompareMode mode) {
        super(mode);
    }

    @SuppressWarnings("unused")
    private static Logger log = LoggerFactory.getLogger(IgnoreDateValueComparator.class);

    @Override
    public void compareValues(String prefix, Object expectedValue, Object actualValue, JSONCompareResult result) throws JSONException {
        // log.debug("Compare {} with {}", expectedValue, actualValue);
        if (prefix.equals("date")) {
            if ( !expectedValue.getClass().equals(Long.class))
                result.fail("date field is not a long.");
            if ( !actualValue.getClass().equals(Long.class))
                result.fail("date field is not a long.");
        }
        else
            super.compareValues(prefix, expectedValue, actualValue, result);
    }

}