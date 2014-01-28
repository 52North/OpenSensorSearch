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