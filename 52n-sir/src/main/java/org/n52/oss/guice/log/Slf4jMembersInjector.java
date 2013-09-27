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

package org.n52.oss.guice.log;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.MembersInjector;

/**
 * 
 * @author Daniel
 * 
 * @param <T>
 */
public class Slf4jMembersInjector<T> implements MembersInjector<T> {
    private final Field field;
    private final Logger logger;

    public Slf4jMembersInjector(Field aField) {
        this.field = aField;
        this.logger = LoggerFactory.getLogger(this.field.getDeclaringClass());
        this.field.setAccessible(true);
    }

    @Override
    public void injectMembers(T theType) {
        try {
            this.field.set(theType, this.logger);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}