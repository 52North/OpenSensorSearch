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

import java.util.Collection;
import java.util.Date;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.datastructure.SirInfoToBeInserted;
import org.n52.sir.datastructure.SirInfoToBeInserted_SensorDescription;
import org.n52.sir.datastructure.SirInfoToBeInserted_ServiceReference;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.SirSensorIDInSir;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirInsertSensorInfoRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirInsertSensorInfoResponse;
import org.n52.sir.sml.SensorMLDecoder;
import org.n52.sir.xml.IProfileValidator;
import org.n52.sir.xml.IValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class InsertSensorInfoListener implements ISirRequestListener {

    /**
     * the logger, used to log exceptions and additionally information
     */
    private static Logger log = LoggerFactory.getLogger(InsertSensorInfoListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.InsertSensorInfo.name();

    /**
     * the data access object for the insertSensorInfo operation
     */
    private IInsertSensorInfoDAO insSensInfoDao;

    /**
     * the factory for validators
     */
    private IValidatorFactory validatorFactory;

    public InsertSensorInfoListener() throws OwsExceptionReport {
        SirConfigurator configurator = SirConfigurator.getInstance();

        IDAOFactory factory = configurator.getFactory();
        this.validatorFactory = configurator.getValidatorFactory();

        try {
            this.insSensInfoDao = factory.insertSensorInfoDAO();
        }
        catch (OwsExceptionReport se) {
            log.error("Error while creating the insertSensorInfoDAO", se);
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
        return InsertSensorInfoListener.OPERATION_NAME;
    }

    /**
     * @param response
     * @param insertedSensors
     * @param sensorInfo
     * @param serviceInfos
     * @param sensor
     * @return
     * @throws OwsExceptionReport
     */
    private void insertSensor(SirInsertSensorInfoResponse response,
                              Collection<SirServiceReference> serviceRefs,
                              SirSensor sensor) throws OwsExceptionReport {
    	log.info("InsertSensorCalled");
        if (sensor.getSensorMLDocument() != null) {
            IProfileValidator profileValidator = this.validatorFactory.getSensorMLProfile4DiscoveryValidator();
            boolean isValid = profileValidator.validate(sensor.getSensorMLDocument());
            log.debug("The sensor is valid: " + isValid);
            if (isValid) {
            	/*
            	 * Inserts into solr
            	 */
            	SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO();
            	String sensorIdInSir = dao.insertSensor(sensor);
            	// String sensorIdInSir = this.insSensInfoDao.insertSensor(sensor);
                if (sensorIdInSir != null) {
                    response.setNumberOfNewSensors(response.getNumberOfNewSensors() + 1);
                    response.getInsertedSensors().add(sensorIdInSir);
                    log.debug("Inserted Sensor: " + sensorIdInSir);

                    if (log.isDebugEnabled())
                        log.debug("Inserted Sensor: " + sensorIdInSir);

                    if (serviceRefs != null) {
                        for (SirServiceReference servRef : serviceRefs) {
                            this.insSensInfoDao.addNewReference(new SirSensorIDInSir(sensorIdInSir), servRef);
                            response.setNumberOfNewServiceReferences(response.getNumberOfNewServiceReferences() + 1);
                        }
                        if (log.isDebugEnabled())
                            log.debug("Inserted " + serviceRefs.size() + " for sensor " + sensorIdInSir + ": "
                                    + serviceRefs);
                    }
                }
                else {
                    log.error("Could not insert sensor to database!");
                }
            }
            else {
                log.error("SensorML is not profile conform: "
                        + String.valueOf(profileValidator.getValidationFailuresAsString()));
            }

            profileValidator = null;
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.MissingParameterValue,
                                 "InsertSensorInfoListener.receiveRequest()",
                                 "Missing parameter: To insert a sensor, a sensorInfo element is required!");
            log.error("OWS:",se);
            throw se;
        }
    }

    /**
     * 
     * @param response
     * @param newReference
     * @throws OwsExceptionReport
     */
    private void insertServiceReferences(SirInsertSensorInfoResponse response,
                                         SirInfoToBeInserted_ServiceReference newReference) throws OwsExceptionReport {
        Collection<SirServiceReference> referenceArray = newReference.getServiceReferences();
        for (SirServiceReference sirServiceReference : referenceArray) {
            String id = this.insSensInfoDao.addNewReference(newReference.getSensorIDinSIR(), sirServiceReference);
            if (log.isDebugEnabled())
                log.debug("Inserted service reference for sensor " + id + ": " + sirServiceReference.getService());

            response.setNumberOfNewServiceReferences(response.getNumberOfNewServiceReferences() + 1);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.n52.sir.ISirRequestListener#receiveRequest(org.n52.sir.request. AbstractSirRequest)
     */
    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {

        SirInsertSensorInfoRequest sirRequest = (SirInsertSensorInfoRequest) request;
        SirInsertSensorInfoResponse response = new SirInsertSensorInfoResponse();

        try {
            for (SirInfoToBeInserted infoToBeInserted : sirRequest.getInfoToBeInserted()) {

                if (infoToBeInserted instanceof SirInfoToBeInserted_SensorDescription) {
                    SirInfoToBeInserted_SensorDescription newSensor = (SirInfoToBeInserted_SensorDescription) infoToBeInserted;
                    SirSensor sensor = SensorMLDecoder.decode(newSensor.getSensorDescription());
                    
                    sensor.setLastUpdate(new Date());

                    Collection<SirServiceReference> serviceReferences = newSensor.getServiceReferences();

                    // INSERT
                    insertSensor(response, serviceReferences, sensor);
                }
                else if (infoToBeInserted instanceof SirInfoToBeInserted_ServiceReference) {
                    SirInfoToBeInserted_ServiceReference newReference = (SirInfoToBeInserted_ServiceReference) infoToBeInserted;

                    // INSERT
                    insertServiceReferences(response, newReference);
                }
            }
        }
        catch (OwsExceptionReport e) {
            return new ExceptionResponse(e.getDocument());
        }

        return response;
    }

}
