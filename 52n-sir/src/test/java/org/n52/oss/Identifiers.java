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
package org.n52.oss;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.n52.sir.api.IdentifierGenerator;
import org.n52.sir.util.ShortAlphanumericIdentifierGenerator;

public class Identifiers {
    
    @Test
    public void identifiersAreGenerated() {
        IdentifierGenerator gen = new ShortAlphanumericIdentifierGenerator();
        String id0 = gen.generate();
        System.out.println("Generated id: " + id0);
        String id1 = gen.generate();
        System.out.println("Generated id: " + id1);
        String id2 = gen.generate();
        System.out.println("Generated id: " + id2);
        
        assertThat(id0, not(equalTo(id1)));
        assertThat(id0, not(equalTo(id2)));
        assertThat(id1, not(equalTo(id2)));
        
        assertTrue(StringUtils.isAlphanumeric(id0));
        assertTrue(StringUtils.isAlphanumeric(id1));
        assertTrue(StringUtils.isAlphanumeric(id2));
        
        assertTrue(StringUtils.isAllLowerCase(id0.replaceAll("[\\d.]", "")));
        assertTrue(StringUtils.isAllLowerCase(id1.replaceAll("[\\d.]", "")));
        assertTrue(StringUtils.isAllLowerCase(id2.replaceAll("[\\d.]", "")));
    }
    
    @Test
    public void listOfIdentifiersIsGenerated() {
        IdentifierGenerator gen = new ShortAlphanumericIdentifierGenerator();
        int size = 10;
        Collection<String> ids = gen.generate(size);
        System.out.println("Generated ids: " + Arrays.toString(ids.toArray()));
        
        assertThat(ids, hasSize(size));
    }

}
