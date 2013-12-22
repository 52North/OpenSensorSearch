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
package org.n52.sor;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SorTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite(SorTestSuite.class.getName());
        // $JUnit-BEGIN$
        suite.addTestSuite(GetMatchingDefinitions.class);
        suite.addTestSuite(GetCapabilities.class);
        suite.addTestSuite(DeleteDefinition.class);
        suite.addTestSuite(GetDefinitionURIs.class);
        suite.addTestSuite(GetMatchingDefinitions.class);
        suite.addTestSuite(InsertDefinition.class);
        // $JUnit-END$
        return suite;
    }

}