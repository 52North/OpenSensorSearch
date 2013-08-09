function harvestOWS(){
	var req =  new org.n52.sir.script.OWSHarvestingRequest();
	var url = "http://sensorweb.demo.52north.org/EO2HeavenSOS/sos";
	var type = "SOS";
	var insertedSensors = req.harvestOWSService(url, type);
	if(insertedSensors){
		return insertedSensors;
	}else{
		return -1;
	}
}
	
harvestOWS();

