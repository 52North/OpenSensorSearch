
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

import com.google.inject.Singleton;

import net.opengis.ows.ExceptionReportDocument;

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
