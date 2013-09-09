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

package org.n52.sir.ows;

import java.util.ArrayList;
import java.util.Arrays;

import net.opengis.ows.ExceptionReportDocument;
import net.opengis.ows.ExceptionReportDocument.ExceptionReport;
import net.opengis.ows.ExceptionType;

import org.apache.xmlbeans.XmlCursor;
import org.n52.sir.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the ows service exception. The exception codes are defined according the ows common spec.
 * version 1.0
 * 
 * @author Alexander C. Walkowski, Daniel Nüst (minor improvements)
 * 
 */
public class OwsExceptionReport extends Exception {

    /**
     * ExceptionCodes as defined in the OWS Common Implementation Specification 0.3.0
     */
    public enum ExceptionCode {
        InvalidParameterValue, InvalidRequest, InvalidUpdateSequence, MissingParameterValue, NoApplicableCode, NoDataAvailable, OperationNotSupported, VersionNegotiationFailed
    }

    /** Exception levels */
    public enum ExceptionLevel {
        DetailedExceptions, PlainExceptions
    }

    private static Logger log = LoggerFactory.getLogger(OwsExceptionReport.class.getName());

    private static final long serialVersionUID = 9069373009339881302L;

    private ExceptionLevel excLevel = null;

    private ArrayList<ExceptionType> excs = new ArrayList<>();

    /**
     * standard constructor without parameters, sets the ExceptionLevel on PlainExceptions
     * 
     */
    public OwsExceptionReport() {
        this.excLevel = ExceptionLevel.DetailedExceptions;
    }

    /**
     * 
     * constructor with one coded exception
     * 
     * @param code
     * @param locator
     * @param message
     */
    public OwsExceptionReport(ExceptionCode code, String locator, String message) {
        super();

        addCodedException(code, locator, message);
    }

    public OwsExceptionReport(ExceptionLevel excLevelIn) {
        this.excLevel = excLevelIn;
    }

    /**
     * 
     * @param message
     *        String containing the message of this exception
     * @param cause
     *        Throwable cause of this exception
     */
    public OwsExceptionReport(String message, Throwable cause) {
        super(message, cause);
        this.excLevel = ExceptionLevel.DetailedExceptions;
    }

    /**
     * 
     * @param cause
     *        Throwable cause of this exception
     */
    public OwsExceptionReport(Throwable cause) {
        super(cause);
        this.excLevel = ExceptionLevel.DetailedExceptions;
    }

    /**
     * adds a coded Exception to this service exception with code, locator and the exception itself as
     * parameters
     * 
     * @param code
     *        ExceptionCode of the added exception
     * @param locator
     *        String locator of the added exception
     * @param e
     *        Exception which should be added
     */
    public void addCodedException(ExceptionCode code, String locator, Exception e) {

        ExceptionType et = ExceptionType.Factory.newInstance();
        et.setExceptionCode(code.toString());
        if (locator != null) {
            et.setLocator(locator);
        }

        String name = e.getClass().getName();
        String message = e.getMessage();
        StackTraceElement[] stackTraces = e.getStackTrace();

        StringBuffer sb = new StringBuffer();
        sb.append("[EXC] internal service exception");
        if (this.excLevel.compareTo(ExceptionLevel.PlainExceptions) == 0) {
            sb.append(". Message: " + message);
        }
        else if (this.excLevel.compareTo(ExceptionLevel.DetailedExceptions) == 0) {
            sb.append(": " + name + "\n");
            sb.append("[EXC] message: " + message + "\n");
            for (int i = 0; i < stackTraces.length; i++) {
                StackTraceElement element = stackTraces[i];
                sb.append("[EXC]" + element.toString() + "\n");
            }
        }
        else {
            log.warn("addCodedException: unknown ExceptionLevel " + "(" + this.excLevel.toString() + ")occurred.");
        }

        et.addExceptionText(sb.toString());
        // i guess there is a better way to format an exception

        this.excs.add(et);
    }

    /**
     * adds a coded Exception with ExceptionCode,locator and a single String message to this exception
     * 
     * @param code
     *        Exception code of the exception to add
     * @param locator
     *        String locator of the exception to add
     * @param message
     *        String message of the exception to add
     */
    public void addCodedException(ExceptionCode code, String locator, String message) {
        addCodedException(code, locator, new String[] {message});
    }

    /**
     * adds a coded exception to this exception with code, locator and messages as parameters
     * 
     * @param code
     *        ExceptionCode of the added exception
     * @param locator
     *        String locator of this exception
     * @param messages
     *        String[] messages of this exception
     */
    public void addCodedException(ExceptionCode code, String locator, String[] messages) {
        ExceptionType et = ExceptionType.Factory.newInstance();
        et.setExceptionCode(code.toString());
        if (locator != null)
            et.setLocator(locator);
        for (int i = 0; i < messages.length; i++) {
            String string = messages[i];
            et.addExceptionText(string);
        }
        this.excs.add(et);
    }

    /**
     * adds a ServiceException to this exception
     * 
     * @param seIn
     *        ServiceException which should be added
     */
    public void addServiceException(OwsExceptionReport seIn) {
        this.excs.addAll(seIn.getExceptions());
    }

    /**
     * checks whether the ExceptionCode parameter is contain in this exception
     * 
     * @param ec
     *        ExceptionCode which should be checked
     * @return Returns boolean true if ExceptionCode is contained, otherwise false
     */
    public boolean containsCode(ExceptionCode ec) {
        for (ExceptionType et : this.excs) {
            if (et.getExceptionCode().equalsIgnoreCase(ec.toString()))
                return true;
        }
        return false;
    }

    /**
     * checks whether this service exception contains another exception
     * 
     * @return Returns true if this service exception contains another exception
     */
    public boolean containsExceptions() {
        return this.excs.size() > 0;
    }

    /**
     * 
     * @return Returns the ExceptionReportDocument XmlBean created from this service exception
     */
    public ExceptionReportDocument getDocument() {

        ExceptionReportDocument erd = ExceptionReportDocument.Factory.newInstance();
        ExceptionReport er = erd.addNewExceptionReport(); // ExceptionReport.Factory.newInstance();
        // er.setLanguage("en");
        er.setVersion(OWSConstants.OWS_VERSION);

        for (ExceptionType e : this.excs) {
            ExceptionType newException = er.addNewException();
            newException.setExceptionCode(e.getExceptionCode());
            newException.setLocator(e.getLocator());
            newException.setExceptionTextArray(e.getExceptionTextArray());
        }
        // er.setExceptionArray(this.excs.toArray(new ExceptionType[this.excs.size()]));
        // erd.setExceptionReport(er);

        XmlCursor c = erd.newCursor(); // Cursor on the documentc.toStartDoc();
        c.toFirstChild();
        c.setAttributeText(XmlTools.SCHEMA_LOCATION_ATTRIBUTE_QNAME, OWSConstants.NAMESPACE + " "
                + OWSConstants.SCHEMA_LOCATION);
        c.dispose();
        return erd;
    }

    /**
     * 
     * @return Returns the ExceptionTypes of this exception
     */
    public ArrayList<ExceptionType> getExceptions() {
        return this.excs;
    }

    @Override
    public String getMessage() {
        String superMsg = super.getMessage();
        if (superMsg == null) {
            StringBuilder sb = new StringBuilder();
            for (ExceptionType e : this.excs) {
                sb.append("[");
                sb.append(Arrays.toString(e.getExceptionTextArray()));
                sb.append("]");
            }
            return sb.toString();
        }
        return superMsg;
    }
}