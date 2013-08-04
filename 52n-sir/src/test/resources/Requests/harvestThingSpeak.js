/*
 * Load Rhino ENV and JQuery dependencoies
 */
function harvestChannel(id){
	var req = new org.n52.sir.script.HTTPRequest();
	var query = "http://api.thingspeak.com/channels/"+id+"/feed.json";
	var respStr = req.doGet(query);
	var obj = JSON.parse(respStr);
	java.lang.System.out.println(respStr);
	
}
harvestChannel(4155);

