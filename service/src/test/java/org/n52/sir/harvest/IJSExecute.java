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

package org.n52.sir.harvest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;
import org.n52.sir.harvest.exec.impl.RhinoJSExecute;

public class IJSExecute {

    @Test
    public void testSimple() {
        // simple test for basic arithmetics
        RhinoJSExecute exec = new RhinoJSExecute();
        String result = exec.execute("function f(x){return x+1;};f(2);");
        assertEquals(Integer.parseInt(result), 3);
    }

    @Test
    public void testInsertSensor() {
        File f = new File(ClassLoader.getSystemResource("js/IJSExecute.js").getFile());
        RhinoJSExecute exec = new RhinoJSExecute();
        String result = exec.execute(f);
        assertNotNull(result);
    }

}
