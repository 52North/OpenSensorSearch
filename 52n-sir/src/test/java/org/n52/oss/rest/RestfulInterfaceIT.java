
package org.n52.oss.rest;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class RestfulInterfaceIT {

    private WebResource service;
    private static String testId = "1";

    @Before
    public void prepare() {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        this.service = client.resource(getBaseURI());
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost:8080/api/v1").build();
    }

    @Test
    public void sensorResourceIsReturned() {
        String string = this.service.path("sensor").path(testId).accept(MediaType.APPLICATION_JSON).get(String.class);
        // TODO implement integration test for restful interface:
        // http://localhost:8080/OpenSensorSearch/api/v1/sensors/1?detailed=false
        // http://localhost:8080/OpenSensorSearch/api/v1/sensors/1
        System.out.println(string);
    }

    @Test
    public void detailedSensorResourceIsReturned() {
        String string = this.service.path("sensor").path(testId).accept(MediaType.APPLICATION_JSON).get(String.class);
        // TODO implement integration test for restful interface:
        // http://localhost:8080/OpenSensorSearch/api/v1/sensors/1?detailed=true
        System.out.println(string);
    }

}
