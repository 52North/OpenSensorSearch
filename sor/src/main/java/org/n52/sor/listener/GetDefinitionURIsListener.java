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

import java.util.ArrayList;
import java.util.List;

import org.n52.sor.OwsExceptionReport;
import org.n52.sor.OwsExceptionReport.ExceptionCode;
import org.n52.sor.PhenomenonManager;
import org.n52.sor.datastructures.IDictionaryEntry;
import org.n52.sor.request.ISorRequest;
import org.n52.sor.request.SorGetDefinitionURIsRequest;
import org.n52.sor.response.ISorResponse;
import org.n52.sor.response.SorGetDefinitionURIsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetDefinitionURIsListener implements IRequestListener {

    private static Logger log = LoggerFactory.getLogger(GetDefinitionURIsListener.class);

    /*
     * @see ISorProcess#receiveRequest(ISorRequest)
     */
    @Override
    public ISorResponse receiveRequest(ISorRequest request) throws OwsExceptionReport {
        SorGetDefinitionURIsRequest sorRequest = (SorGetDefinitionURIsRequest) request;
        SorGetDefinitionURIsResponse getDefResp = new SorGetDefinitionURIsResponse();

        ArrayList<IDictionaryEntry> phenomList = (ArrayList<IDictionaryEntry>) PhenomenonManager.getInstance().getPhenomenaList();
        List<String> defs = new ArrayList<>();

        if (sorRequest.getSearchString() != null) {
            // search string is given, check all elements and add them to ouput list if they match

            String search = sorRequest.getSearchString();
            if (log.isDebugEnabled()) {
                log.debug("Searching for String '" + search + "'");
            }

            for (IDictionaryEntry de : phenomList) {
                if (de.getIdentifier().contains(search)) {
                    defs.add(de.getIdentifier());
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("Found search string in " + defs.size() + " of " + phenomList.size() + " dictionary entries.");
            }
        }
        else {
            // no search string, add all elements to output list

            for (IDictionaryEntry de : phenomList) {
                defs.add(de.getIdentifier());
            }
        }

        if (sorRequest.getStartResultElement() > defs.size()) {
            log.warn("Start result element is larger than found element list, returning empty list!");
            throw new OwsExceptionReport(ExceptionCode.InvalidParameterValue,
                                         "StartResultElement",
                                         "StartResusltElement is larger than number of found elements!");
        }

        // create sublist of maximum length and return
        defs = defs.subList(sorRequest.getStartResultElement(),
                            Math.min(sorRequest.getStartResultElement() + sorRequest.getMaxNumberOfResults(),
                                     defs.size()));

        getDefResp.setDefinitionList(defs);
        return getDefResp;
    }
}