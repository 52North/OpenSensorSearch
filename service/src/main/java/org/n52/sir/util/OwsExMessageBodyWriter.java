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

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.opengis.ows.ExceptionReportDocument;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.inject.Singleton;

/**
 * https://jersey.java.net/documentation/latest/message-body-workers.html
 * 
 * Writer must be binded:
 * http://stackoverflow.com/questions/11216321/guice-jersey-custom-serialization-of-entities
 * 
 * Based on DocumentProvider.class
 * 
 * @author Daniel
 * 
 */
@Singleton
@Produces({MediaType.APPLICATION_XML, MediaType.TEXT_HTML})
@Provider
public class OwsExMessageBodyWriter implements MessageBodyWriter<OwsExceptionReport> {

    private static Logger log = LoggerFactory.getLogger(OwsExMessageBodyWriter.class);

    private final TransformerFactory tf;

    public OwsExMessageBodyWriter() {
        this.tf = TransformerFactory.newInstance();
    }

    @Override
    public boolean isWriteable(Class< ? > type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == OwsExceptionReport.class;
    }

    @Override
    public long getSize(OwsExceptionReport report,
                        Class< ? > type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo(OwsExceptionReport report,
                        Class< ? > type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        log.debug("Writing {}", report);

        // FIX ME: writeTo method not working for OwsExMessageBodyWriter:
        // if (mediaType.equals(MediaType.TEXT_HTML_TYPE)) {
        // PrintWriter writer = new PrintWriter(entityStream);
        // writer.println("<html><body>");
        // ExceptionReportDocument document = myBean.getDocument();
        // writer.println(document.xmlText());
        // writer.println("</body></html>");
        //
        // writer.flush();
        // return;
        // }
        //
        // ExceptionReportDocument document = myBean.getDocument();
        //
        // // serialize the entity myBean to the entity output stream
        // document.save(entityStream);
        // entityStream.flush();

        // next try, does not work:
        // try {
        // JAXBContext jaxbContext = JAXBContext.newInstance(OwsExceptionReport.class);
        //
        // // serialize the entity myBean to the entity output stream
        // jaxbContext.createMarshaller().marshal(report, entityStream);
        // }
        // catch (JAXBException e) {
        // throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        // }

        ExceptionReportDocument document = report.getDocument();
        Document doc = (Document) document.getDomNode();

        try {
            StreamResult sr = new StreamResult(entityStream);
            this.tf.newTransformer().transform(new DOMSource(doc), sr);
        }
        catch (TransformerException ex) {
            throw new WebApplicationException(ex, Status.INTERNAL_SERVER_ERROR);
        }

        entityStream.flush();
        // do not close stream, done by jersey!
    }

}
