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
/**
 * @author Yakoub
 */

package org.n52.sir.ds.solr;

import java.io.IOException;
import java.util.Collection;

import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SystemType;
import net.opengis.swe.x101.PositionType;
import net.opengis.swe.x101.VectorType;
import net.opengis.swe.x101.VectorType.Coordinate;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.SirSensorIdentification;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SOLRInsertSensorInfoDAO implements IInsertSensorInfoDAO {
    private static Logger log = LoggerFactory.getLogger(SOLRInsertSensorInfoDAO.class);

    @Override
    public String addNewReference(SirSensorIdentification sensIdent, SirServiceReference servRef) throws OwsExceptionReport {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String deleteReference(SirSensorIdentification sensIdent, SirServiceReference servRef) throws OwsExceptionReport {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String deleteSensor(SirSensorIdentification sensIdent) throws OwsExceptionReport {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String insertSensor(SirSensor sensor) throws OwsExceptionReport {
        // TODO Auto-generated method stub
        // Get the keywords first
        try {
            String id = sensor.getSensorIDInSIR();
            
            // TODO implement a mechanism to get access to the various identifiers
            // Map<String, String> futherIds = sensor.getIdentifiers();

            Collection<String> keywords = sensor.getText();

            // Get the connection of solr Server
            SolrConnection connection = new SolrConnection();
            SolrInputDocument inputDocument = new SolrInputDocument();

            for (String s : keywords) {
                inputDocument.addField(SolrConstants.KEYWORD, s);
            }
            
            inputDocument.addField(SolrConstants.ID, id);

            // Getting position
            SensorMLDocument document = (SensorMLDocument) sensor.getSensorMLDocument();
            SystemType type = (SystemType) document.getSensorML().getMemberArray(0).getProcess();

            // TODO moh-yakoub: add getPosition method to SirSensor
            // sensor.getPosition();
            
            // TODO moh-yakoub: move this parsing code to the decoder!
            PositionType p = type.getPosition().getPosition();
            VectorType vector = (p.getLocation().getVector());
            Coordinate[] coordinates = vector.getCoordinateArray();
            StringBuilder latitude = new StringBuilder();
            StringBuilder longitude = new StringBuilder();

            for (Coordinate cord : coordinates) {
                if (cord.getName().equals("latitude"))
                    latitude.append(cord.getQuantity().getValue());
                else if (cord.getName().equals("longitude"))
                    longitude.append(cord.getQuantity().getValue());
            }
            if ( (latitude.toString().length()) > 0 && (longitude.toString().length() > 0))
                inputDocument.addField(SolrConstants.LOCATION, latitude + "," + longitude);

            connection.addInputDocument(inputDocument);
            connection.commitChanges();
        }
        catch (SolrServerException e) {
            log.error("SolrException", e);
        }
        catch (IOException e) {
            log.error("IOException", e);
        }

        return null;
    }

    @Override
    public String updateSensor(SirSensorIdentification sensIdent, SirSensor sensor) throws OwsExceptionReport {
        // TODO Auto-generated method stub
        return null;
    }

}
