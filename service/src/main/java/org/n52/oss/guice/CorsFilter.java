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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

@Singleton
public class CorsFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger(CorsFilter.class);

    public static String VALID_METHODS = "HEAD, GET, OPTIONS, POST";

    public CorsFilter() {
        log.info("NEW {}", this);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("INIT {}", this);

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // simple:
        resp.addHeader("Access-Control-Allow-Origin", "*");
        chain.doFilter(request, response);

        // complex:

        // No Origin header present means this is not a cross-domain request
        // String origin = req.getHeader("Origin");
        // if (origin == null) {
        // // Return standard response if OPTIONS request w/o Origin header
        // if ("OPTIONS".equalsIgnoreCase(httpReq.getMethod())) {
        // resp.setHeader("Allow", VALID_METHODS);
        // resp.setStatus(200);
        // return;
        // }
        // }
        // else {
        // // This is a cross-domain request, add headers allowing access
        // resp.setHeader("Access-Control-Allow-Origin", origin);
        // resp.setHeader("Access-Control-Allow-Methods", VALID_METHODS);
        //
        // String headers = req.getHeader("Access-Control-Request-Headers");
        // if (headers != null)
        // resp.setHeader("Access-Control-Allow-Headers", headers);
        //
        // // Allow caching cross-domain permission
        // resp.setHeader("Access-Control-Max-Age", "3600");
        // }
        // // Pass request down the chain, except for OPTIONS
        // if ( !"OPTIONS".equalsIgnoreCase(req.getMethod())) {
        // chain.doFilter(req, resp);
        // }
    }

    @Override
    public void destroy() {
        log.info("DESTROY {}", this);
    }

}
