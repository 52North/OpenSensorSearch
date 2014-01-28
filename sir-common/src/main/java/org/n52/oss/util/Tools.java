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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Nüst
 * 
 */
public class Tools {

    private static Logger log = LoggerFactory.getLogger(Tools.class);

    public static boolean atLeastOneIsNotEmpty(String[] strings) {
        for (String string : strings) {
            if ( !string.isEmpty())
                return true;
        }
        return false;
    }

    public static String getStackTrace(Throwable t) {
        StringBuilder sb = new StringBuilder("STACKTRACE: ");
        sb.append(t.toString());
        sb.append("\n");

        for (StackTraceElement element : t.getStackTrace()) {
            sb.append(element);
            sb.append("\n");
        }
        return sb.toString();
    }

    public static boolean noneEmpty(String[] strings) {
        for (String string : strings) {
            if (string.isEmpty())
                return false;
        }
        return true;
    }

    /**
     * remove trailing and leading white spaces, replace newline characters with space character.
     */
    public static String simplifyString(String stringToSimplify) {
        String s = stringToSimplify.trim();
        s = s.replaceAll("\n", " ");
        return s;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ( (line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        is.close();
        return sb.toString();
    }
}
