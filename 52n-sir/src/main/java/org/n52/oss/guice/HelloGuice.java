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

package org.n52.oss.guice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.n52.oss.guice.log.InjectLogger;
import org.slf4j.Logger;

@Path("helloguice")
public class HelloGuice {

    @InjectLogger
    private static Logger log;

    public HelloGuice() {
        log.info("NEW {}", this);
    }

    @GET
    @Produces("text/plain")
    public String get(@QueryParam("x")
    String x) {
        log.debug("query: {}", x);

        return "Howdy Guice. Injected query parameter " + (x != null ? "x = " + x : "x is not injected");
    }

}
