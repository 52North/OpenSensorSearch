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
package org.n52.sir.listener;

import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Nüst
 * 
 */
public abstract class SensorStatusSubscriptionListener implements ISirRequestListener {

    /**
     * the logger, used to log exceptions and additionally information
     */
    private static Logger log = LoggerFactory.getLogger(SensorStatusSubscriptionListener.class);

    /**
     * 
     */
    public SensorStatusSubscriptionListener() {
        //
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.listener.ISirRequestListener#receiveRequest(org.n52.sir.request.AbstractSirRequest)
     */
    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        log.warn("Unsupported operation called!" + request);

        OwsExceptionReport er = new OwsExceptionReport(ExceptionCode.OperationNotSupported,
                                                       null,
                                                       "The operations SubscribeSensorStatus, RenewSensorStatusSubscription, and CancelSensorStatusSubscription are NOT SUPPORTED!");
        return new ExceptionResponse(er.getDocument());
    }
}
