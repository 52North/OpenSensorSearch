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

import java.util.Collection;

import org.n52.sir.datastructure.SirSearchCriteria;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.ows.OwsExceptionReport;

public class SOLRSearchSensorDAO implements ISearchSensorDAO{

	@Override
	public Collection<SirSearchResultElement> getAllSensors(
			boolean simpleReponse) throws OwsExceptionReport {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SirSearchResultElement getSensorBySensorID(String sensorIdInSir,
			boolean simpleReponse) throws OwsExceptionReport {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SirSearchResultElement getSensorByServiceDescription(
			SirServiceReference servDesc, boolean simpleReponse)
			throws OwsExceptionReport {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<SirSearchResultElement> searchSensor(
			SirSearchCriteria searchCriteria, boolean simpleReponse)
			throws OwsExceptionReport {
		// TODO Auto-generated method stub
		return null;
	}
	

}
