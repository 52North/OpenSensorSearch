var express = require("express");
var app = express();

/*
* @author Yakoub
*/

//use post body parser

var sensors=[
{
	id:1,
	keywords:['test','testKeyword']
},
{
	id:2,
	keywords:['test2','testKeyword2']
}
];

app.use(express.bodyParser());

app.get("/sensors",function(req,res){
	var ids = [];
	for(var i=0;i<sensors.length;i++){
		var sensor = sensors[i];
		ids.push(sensor['id']);
	}

	res.json(ids);
});

app.get("/sensors/:id",function(req,res){
	var _id = req.params.id;
	for(var i=0;i<sensors.length;i++){
		if(sensors[i]['id']==_id){
			res.json(sensors[i]);
			break;
		}
	}
});

console.log("Harvesting sensor listening on port 3000");

app.listen(3000);