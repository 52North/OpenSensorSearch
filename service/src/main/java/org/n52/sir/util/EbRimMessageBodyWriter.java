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
package org.n52.sir.util;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryPackageDocument;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.impl.RegistryPackageDocumentImpl;

/**
 * https://jersey.java.net/documentation/latest/message-body-workers.html
 * 
 * FIXME this does not work yet, instead I'm casting the response to a org.w3c.dom.Document before returning
 * it:
 * 
 * <code>
 * XmlObject transformed = transformer.transform(sensorMLDocument);
 * Document doc = (Document) transformed.getDomNode();
 * return Response.ok(doc).build();G
 * </code>
 * 
 * @author Daniel
 * 
 */
public class EbRimMessageBodyWriter implements MessageBodyWriter<RegistryPackageDocumentImpl> {

    @Override
    public boolean isWriteable(Class< ? > type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == RegistryPackageDocument.class;
    }

    @Override
    public long getSize(RegistryPackageDocumentImpl t,
                        Class< ? > type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType) {
        return 0;
    }

    @Override
    public void writeTo(RegistryPackageDocumentImpl t,
                        Class< ? > type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(RegistryPackageDocument.class);

            // serialize the entity myBean to the entity output stream
            jaxbContext.createMarshaller().marshal(t, entityStream);
        }
        catch (JAXBException e) {
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

}
