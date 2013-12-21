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

package org.n52.sor.listener;

import org.n52.sor.OwsExceptionReport;
import org.n52.sor.OwsExceptionReport.ExceptionCode;
import org.n52.sor.PhenomenonManager;
import org.n52.sor.datastructures.IDictionaryEntry;
import org.n52.sor.request.ISorRequest;
import org.n52.sor.request.SorGetDefinitionRequest;
import org.n52.sor.response.ISorResponse;
import org.n52.sor.response.SorGetDefinitionResponse;

public class GetDefinitionListener implements IRequestListener {

    /*
     * @see ISorProcess#receiveRequest(ISorRequest)
     */
    @Override
    public ISorResponse receiveRequest(ISorRequest request) throws OwsExceptionReport {
        SorGetDefinitionRequest getDefReq = (SorGetDefinitionRequest) request;
        SorGetDefinitionResponse getDefResp = new SorGetDefinitionResponse();

        for (IDictionaryEntry phenom : PhenomenonManager.getInstance().getPhenomenaList()) {
            if (phenom.getIdentifier().equals(getDefReq.getInputURI())) {
                getDefResp.setPhenomenonDefinition(phenom);
                break;
            }
        }

        if (getDefResp.getPhenomenonDefinition() == null) {
            // phenomenon not found!
            throw new OwsExceptionReport(ExceptionCode.InvalidParameterValue,
                                         "InputURI",
                                         "Definition for the given input not found!");
        }

        getDefResp.setInputURI(getDefReq.getInputURI());
        return getDefResp;
    }

}