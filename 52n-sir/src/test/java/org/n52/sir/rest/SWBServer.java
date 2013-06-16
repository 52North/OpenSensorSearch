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
package org.n52.sir.rest;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;

/**
 * 
 * http://nexnet.wordpress.com/2011/09/02/restlet-quickstart/
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class SWBServer extends ServerResource {

    public static void main(final String[] args) throws Exception {
        // Create a new Component.
        final Component component = new Component();

        // Add a new HTTP server listening on port 8182.
        component.getServers().add(Protocol.HTTP, 8182);

        final Router router = new Router(component.getContext().createChildContext());

        router.attach("/test", SWBServer.class);

        // Attach the sample application.
        component.getDefaultHost().attach("/restlet", router);

        // Start the component.
        component.start();
    }

    @Override
    @Get
    public String toString() {
        return "hello, my world";
    }

}
