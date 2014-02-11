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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.n52.sir.json.MapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Util {

    private static Logger log = LoggerFactory.getLogger(Util.class);

    public static String readResourceFile(String s) {
        URL resource = Util.class.getResource(s);
        Path path;
        try {
            path = Paths.get(resource.toURI());
        }
        catch (URISyntaxException e) {
            log.error("Could not read from resource path " + s, e);
            return "";
        }
        return readResourceFile(path);
    }

    public static String readResourceFile(Path p) {
        log.debug("Reading {}", p);
        StringBuilder sb = new StringBuilder();
        try (Reader reader = new FileReader(p.toFile());) {

            int data = reader.read();
            while (data != -1) {
                char dataChar = (char) data;
                sb.append(dataChar);
                data = reader.read();
            }
            reader.close();
        }
        catch (IOException e) {
            log.error("Could not read from file " + p, e);
        }

        return sb.toString();
    }

    public static String entityToString(Response response) throws JsonGenerationException,
            JsonMappingException,
            IOException {
        StringWriter writer = new StringWriter();
        MapperFactory.getMapper().writeValue(writer, response.getEntity());
        return writer.toString();
    }

    public static String getResponsePayload(HttpResponse response) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String s;
        while ( (s = reader.readLine()) != null)
            builder.append(s);

        return builder.toString().trim();
    }

}
