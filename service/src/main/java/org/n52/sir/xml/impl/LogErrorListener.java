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
