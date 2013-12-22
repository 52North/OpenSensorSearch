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
package org.n52.sor;

import java.util.ArrayList;

import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.ows.x11.ExceptionReportDocument.ExceptionReport;
import net.opengis.ows.x11.ExceptionType;

/**
 * 
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class OwsExceptionReport extends Exception {

    /**
     * ExceptionCodes as defined in the OWS Common Implementation Specification
     */
    public enum ExceptionCode {
        OperationNotSupported, MissingParameterValue, InvalidParameterValue, VersionNegotiationFailed, InvalidUpdateSequence, OptionNotSupported, NoApplicableCode, InvalidRequest
    }

    private static final long serialVersionUID = 8265500105420582340L;

    private ArrayList<ExceptionType> excs = new ArrayList<>();

    /**
     * 
     * @param code
     * @param locator
     * @param message
     */
    public OwsExceptionReport(ExceptionCode code, String locator, String message) {
        ExceptionType et = ExceptionType.Factory.newInstance();
        et.setExceptionCode(code.toString());
        if (locator != null) {
            et.setLocator(locator);
        }
        et.addExceptionText(message);
        this.excs.add(et);
    }

    /**
     * 
     * @param code
     * @param locator
     * @param message
     */
    public void addCodedException(ExceptionCode code, String locator, String message) {
        ExceptionType et = ExceptionType.Factory.newInstance();
        et.setExceptionCode(code.toString());
        if (locator != null) {
            et.setLocator(locator);
        }
        et.addExceptionText(message);
        this.excs.add(et);
    }

    /**
     * 
     * @return
     */
    public ExceptionReportDocument getDocument() {
        ExceptionReportDocument erd = ExceptionReportDocument.Factory.newInstance();
        ExceptionReport er = ExceptionReport.Factory.newInstance();
        er.setVersion("1.0.0");
        er.setExceptionArray(this.excs.toArray(new ExceptionType[this.excs.size()]));
        erd.setExceptionReport(er);
        return erd;
    }
}