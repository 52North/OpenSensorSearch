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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.IHarvestServiceDAO;
import org.n52.sir.listener.harvest.FileHarvester;
import org.n52.sir.listener.harvest.IOOSHarvester;
import org.n52.sir.listener.harvest.SOSServiceHarvester;
import org.n52.sir.listener.harvest.SPSServiceHarvester;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirHarvestServiceRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte, Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class HarvestServiceListener implements ISirRequestListener {

    /**
     * the logger, used to log exceptions and additionally information
     */
    protected static Logger log = LoggerFactory.getLogger(HarvestServiceListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.HarvestService.name();

    private ExecutorService exec;

    /**
     * the data access object for the harvestService operation
     */
    private IHarvestServiceDAO harvServDao;

    /**
     * 
     * @throws OwsExceptionReport
     */
    public HarvestServiceListener() throws OwsExceptionReport {
        this.exec = SirConfigurator.getInstance().getExecutor();

        try {
            IDAOFactory factory = SirConfigurator.getInstance().getFactory();
            this.harvServDao = factory.harvestServiceDAO();
        }
        catch (OwsExceptionReport se) {
            log.error("Error while creating the harvestServiceDAO", se);
            throw se;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ISirRequestListener#getOperationName()
     */
    @Override
    public String getOperationName() {
        return HarvestServiceListener.OPERATION_NAME;
    }

    /**
     * 
     * @param harvServReq
     * @return
     * @throws OwsExceptionReport
     */
    private ISirResponse harvestIOOSCatalog(SirHarvestServiceRequest request) throws OwsExceptionReport {
        if (log.isDebugEnabled())
            log.debug("Start harvest IOOSCatalog: " + request.getServiceUrl());

        FileHarvester harvester = new IOOSHarvester(request, this.harvServDao);
        Future<ISirResponse> future = this.exec.submit(harvester);

        try {
            ISirResponse response = future.get();
            return response;
        }
        catch (InterruptedException e) {
            throw new OwsExceptionReport("Could not harvest service " + request.getServiceUrl(), e);
        }
        catch (ExecutionException e) {
            throw new OwsExceptionReport("Could not harvest service " + request.getServiceUrl(), e);
        }
    }

    /**
     * 
     * @param request
     * @return
     * @throws OwsExceptionReport
     */
    private ISirResponse harvestSOS(SirHarvestServiceRequest request) throws OwsExceptionReport {
        if (log.isDebugEnabled())
            log.debug("Start harvest SOS: " + request.getServiceUrl());

        // TODO harvesting interval
        SOSServiceHarvester harvester = new SOSServiceHarvester(request, this.harvServDao);
        Future<ISirResponse> future = this.exec.submit(harvester);

        try {
            ISirResponse response = future.get();
            return response;
        }
        catch (InterruptedException e) {
            throw new OwsExceptionReport("Could not harvest service " + request.getServiceUrl(), e);
        }
        catch (ExecutionException e) {
            throw new OwsExceptionReport("Could not harvest service " + request.getServiceUrl(), e);
        }
    }

    /**
     * 
     * @param request
     * @return
     * @throws OwsExceptionReport
     */
    private ISirResponse harvestSPS(SirHarvestServiceRequest request) throws OwsExceptionReport {
        if (log.isDebugEnabled())
            log.debug("Start harvest SPS: " + request.getServiceUrl());

        SPSServiceHarvester harvester = new SPSServiceHarvester(request, this.harvServDao);
        Future<ISirResponse> future = this.exec.submit(harvester);

        try {
            ISirResponse response = future.get();
            return response;
        }
        catch (InterruptedException e) {
            throw new OwsExceptionReport("Could not harvest service " + request.getServiceUrl(), e);
        }
        catch (ExecutionException e) {
            throw new OwsExceptionReport("Could not harvest service " + request.getServiceUrl(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.n52.sir.ISirRequestListener#receiveRequest(org.n52.sir.request. AbstractSirRequest)
     */
    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        SirHarvestServiceRequest harvServReq = (SirHarvestServiceRequest) request;
        ISirResponse response = null;

        // harvest a SOS
        if (harvServReq.getServiceType().equalsIgnoreCase(SirConstants.SOS_SERVICE_TYPE)) {
            try {
                response = harvestSOS(harvServReq);
            }
            catch (OwsExceptionReport e) {
                return new ExceptionResponse(e.getDocument());
            }
        }
        // harvest a SPS
        else if (harvServReq.getServiceType().equalsIgnoreCase(SirConstants.SPS_SERVICE_TYPE)) {
            try {
                response = harvestSPS(harvServReq);
            }
            catch (OwsExceptionReport e) {
                return new ExceptionResponse(e.getDocument());
            }
        }
        // harvest the IOOSCatalog (xml file)
        else if (harvServReq.getServiceType().equalsIgnoreCase(SirConstants.IOOSCATAL0G_SERVICE_TYPE)) {
            try {
                response = harvestIOOSCatalog(harvServReq);
            }
            catch (OwsExceptionReport e) {
                return new ExceptionResponse(e.getDocument());
            }
        }
        else {
            OwsExceptionReport report = new OwsExceptionReport(ExceptionCode.InvalidParameterValue,
                                                               "serviceType",
                                                               "Harvesting for the given service type '"
                                                                       + harvServReq.getServiceType()
                                                                       + "' not supported!");
            return new ExceptionResponse(report.getDocument());
        }

        return response;
    }

}
