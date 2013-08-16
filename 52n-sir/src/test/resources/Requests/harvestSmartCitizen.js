function harvestSmartCitizenChannel(id){
	var req = new org.n52.sir.script.HTTPRequest();
	var query = "http://api.smartcitizen.me/v0.0.1/"+id+"/lastpost.json";
	var respStr = req.doGet(query);
	if(respStr){
		var obj = JSON.parse(respStr);
		if(obj){
			if(obj['devices']){
				var devices = obj['devices'];
				var count = 0;
				var ids = [];
				for(var i=0;i<devices.length;i++){
					var channel = devices[i]
					var description = channel['description'];
					var id = channel['id'];
					var latitude = channel['geo_lat'];
					var longitude = channel['geo_long'];
					
					// TODO add an interface that hides the DAO
					// oss = new org.n52.sir.script.Sensors();
					// var keywords = ["keyword", "keyword1"];
					// oss.insertSensor(id, description, latitude, longitude, keywords, ...);
					
					// alternative:
					// Sensor newSensor = new org.n52.sir.script.Sensor();
					// newSensor.set(...);
					
					dao = new org.n52.sir.ds.solr.SOLRSearchSensorDAO();
					sensor = new org.n52.sir.datastructure.SirSensor();
					sensor.setDescription(description);
					sensor.setLongitude(longitude);
					sensor.setLatitude(latitude);
					insert = new org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO();
					insert.insertSensor(sensor);
					count++ ;
				}
				return count;
				
			}else return -1;
		}else return -1;
	}else return -1;
}
harvestSmartCitizenChannel("6e0428e19cf2bff1a9c05d14d0400bf4");

