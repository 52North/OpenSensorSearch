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