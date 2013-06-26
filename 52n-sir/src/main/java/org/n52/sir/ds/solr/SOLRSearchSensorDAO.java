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
