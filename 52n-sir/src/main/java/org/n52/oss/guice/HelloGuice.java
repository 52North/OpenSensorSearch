
package org.n52.oss.guice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("helloguice")
public class HelloGuice {

    public HelloGuice() {
        //
    }

    @GET
    @Produces("text/plain")
    public String get(@QueryParam("x")
    String x) {
        return "Howdy Guice. Injected query parameter " + (x != null ? "x = " + x : "x is not injected");
    }

}
