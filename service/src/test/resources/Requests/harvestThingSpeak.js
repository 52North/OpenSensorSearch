/*
 * Load Rhino ENV and JQuery dependencoies
 */
function harvestChannel(id){
	var req = new org.n52.sir.script.HTTPRequest();
	var query = "http://api.thingspeak.com/channels/"+id+"/feed.json";
	var respStr = req.doGet(query);
	if(respStr){
		var obj = JSON.parse(respStr);
		if(obj){
			if(obj['channel']){
				var channel = obj['channel'];
				var description = channel['description'];
				var id = channel['id'];
				var latitude = channel['latitude'];
				var longitude = channel['longitude'];
				dao = new org.n52.sir.ds.solr.SOLRSearchSensorDAO();
				sensor = new org.n52.sir.datastructure.SirSensor();
				sensor.setDescription(description);
				sensor.setLongitude(longitude);
				sensor.setLatitude(latitude);
				insert = new org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO();
				return insert.insertSensor(sensor);
			}else return -1;
		}else return -1;
	}else return -1;
}
harvestChannel(4155);

