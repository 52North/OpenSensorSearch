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

package org.n52.sir.util;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.RandomStringUtils;
import org.n52.sir.api.IdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShortAlphanumericIdentifierGenerator implements IdentifierGenerator {

    private static Logger log = LoggerFactory.getLogger(ShortAlphanumericIdentifierGenerator.class);

    public static final int ID_LENGTH = 10;

    public ShortAlphanumericIdentifierGenerator() {
        log.debug("NEW {}", this);
    }

    /* (non-Javadoc)
     * @see org.n52.sir.util.IdentifierGenerator#generate()
     */
    @Override
    public String generate() {
        return RandomStringUtils.randomAlphanumeric(ID_LENGTH).toLowerCase();
    }
    
    /* (non-Javadoc)
     * @see org.n52.sir.util.IdentifierGenerator#generate(int)
     */
    @Override
    public Collection<String> generate(int count) {
        ArrayList<String> ids = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            ids.add(generate());
        }
        return ids;
    }

}
