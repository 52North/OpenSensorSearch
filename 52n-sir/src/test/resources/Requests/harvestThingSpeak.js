/*
 * Load Rhino ENV and JQuery dependencoies
 */
load('d:/env.rhino.js');
load('d:/jquery.js');
function harvestChannel(id){
	var req = new org.n52.sir.script.HTTPRequest();
	var query = "http://api.thingspeak.com/channels/"+id+"/feed/last.json?callback=";
	$.get(query,function(data){
		console.log(data);
	});

}
harvestChannel(4155);

