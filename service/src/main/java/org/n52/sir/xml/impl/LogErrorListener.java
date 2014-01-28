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
package org.n52.sir.xml.impl;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogErrorListener implements ErrorListener {

    private static Logger log = LoggerFactory.getLogger(LogErrorListener.class);

    @Override
    public void error(TransformerException ex) throws TransformerException {
        log.error("[StylesheetError] ", ex);
    }

    @Override
    public void fatalError(TransformerException ex) throws TransformerException {
        log.error("[StylesheetError] ", ex);
    }

    @Override
    public void warning(TransformerException ex) {
        log.warn("[StylesheetError] ", ex);
    }

}
