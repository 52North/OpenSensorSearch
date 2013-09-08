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

package org.n52.sir.ows;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import net.opengis.ows.ExceptionReportDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * https://jersey.java.net/documentation/latest/message-body-workers.html
 * 
 * Writer must be binded:
 * http://stackoverflow.com/questions/11216321/guice-jersey-custom-serialization-of-entities
 * 
 * @author Daniel
 * 
 */
@Singleton
@Produces({MediaType.APPLICATION_XML, MediaType.TEXT_HTML})
@Provider
public class OwsExMessageBodyWriter implements MessageBodyWriter<OwsExceptionReport> {

    private static Logger log = LoggerFactory.getLogger(OwsExMessageBodyWriter.class);

    @Override
    public boolean isWriteable(Class< ? > type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == OwsExceptionReport.class;
    }

    @Override
    public long getSize(OwsExceptionReport myBean,
                        Class< ? > type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo(OwsExceptionReport myBean,
                        Class< ? > type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        log.debug("Writing {}", myBean);

        // FIXME writeTo method not working for OwsExMessageBodyWriter
        if (mediaType.equals(MediaType.TEXT_HTML_TYPE)) {
            PrintWriter writer = new PrintWriter(entityStream);
            writer.println("<html><body>");
            ExceptionReportDocument document = myBean.getDocument();
            writer.println(document.xmlText());
            writer.println("</body></html>");

            writer.flush();
            return;
        }

        ExceptionReportDocument document = myBean.getDocument();

        // serialize the entity myBean to the entity output stream
        document.save(entityStream);
        entityStream.flush();

        // do not close stream, done by jersey!
    }

}
