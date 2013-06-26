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

import net.opengis.sensorML.x101.KeywordsDocument.Keywords;
import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.xmlbeans.XmlException;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.SirSensorDescription;
import org.n52.sir.datastructure.SirSensorIdentification;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ds.pgsql.PGSQLInsertSensorInfoDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SOLRInsertSensorInfoDAO implements IInsertSensorInfoDAO {
	private static Logger log = LoggerFactory
			.getLogger(SOLRInsertSensorInfoDAO.class);

	@Override
	public String addNewReference(SirSensorIdentification sensIdent,
			SirServiceReference servRef) throws OwsExceptionReport {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteReference(SirSensorIdentification sensIdent,
			SirServiceReference servRef) throws OwsExceptionReport {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteSensor(SirSensorIdentification sensIdent)
			throws OwsExceptionReport {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String insertSensor(SirSensor sensor) throws OwsExceptionReport {
		// TODO Auto-generated method stub
		// Get the keywords first
		try {
			SensorMLDocument document = SensorMLDocument.Factory.parse(sensor
					.getSensorMLDocument().getDomNode());
			// Get the list of keywords

			String id = (document.getSensorML().getMemberArray()[0].getProcess().getIdentificationArray()[0].getIdentifierList().getIdentifierArray()[0].getTerm().getValue());
			
			// No keywords found
			if (document.getSensorML().getMemberArray().length == 0)
				return null;
			String[] keywords = document.getSensorML().getMemberArray()[0]
					.getProcess().getKeywordsArray()[0].getKeywordList().getKeywordArray();
			
			// Get the connection of solr Server
			SolrConnection connection = new SolrConnection();
			SolrInputDocument inputDocument = new SolrInputDocument();

			for (int i = 0; i < keywords.length; i++){
				System.out.println(keywords[i]);
				inputDocument.addField(SolrConstants.KEYWORD, keywords[i]);
			}
			inputDocument.addField(SolrConstants.ID,id);
			
			
			connection.addInputDocument(inputDocument);
			connection.commitChanges();
		} catch (XmlException e) {
			log.error("Cannot parse sensorML:" + e.getLocalizedMessage());
			return null;
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			log.error("SolrException :" + e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("IOException :"+e.getLocalizedMessage());
		}

		return null;
	}

	@Override
	public String updateSensor(SirSensorIdentification sensIdent,
			SirSensor sensor) throws OwsExceptionReport {
		// TODO Auto-generated method stub
		return null;
	}

}
