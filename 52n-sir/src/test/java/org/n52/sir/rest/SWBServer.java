/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
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
