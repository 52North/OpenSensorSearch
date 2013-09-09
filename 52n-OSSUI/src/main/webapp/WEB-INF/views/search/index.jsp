<%--

    ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Search The OSS sensors</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<script src="./${context }/scripts/autocomplete.js"></script>
<script src="./${context}/scripts/bootstrap.min.js"></script>
<link href="./${context}/styles/bootstrap.css" rel="stylesheet">
<%@ include file="navBar.jsp"%>
<!-- /container -->
<!--/span-->
<style>
.form-control{
max-width: 200px;
}
</style>
<script>
	$(document)
			.ready(
					function() {
						if (navigator.geolocation) {
							navigator.geolocation
									.getCurrentPosition(showPosition);
						} else {
							$(".alert-msg")
									.append(
											'<div class="alert alert-danger">Your browser doesn\'t support	geolocation!</div>');

						}
					});
</script>
<script>
function showPosition(position) {
	$("#lat").val(position.coords.latitude);
	$("#lng").val(position.coords.longitude);
	$("#radius").val("1000");
	var str = "You're near:"+position.coords.latitude+","+position.coords.longitude
	$("#location_info").html(str);
}

</script>
<script>
function validate(){
	var q = document.forms["requestform"]["q"].value;
	if(q == null || q =="" || q.toString().trim().length == 0){
		alert("Please enter a valid query!");
		return false;
	}
	return true;
	
}
</script>
<script>
$(document).ready(function(){
	$("#notlocation").click(function(){
		$("#lat").attr("disabled",true);
		$("#lng").attr("disabled",true);
		$("#radius").attr("disabled",true);
	});
	$("#location").click(function(){
		$("#lat").attr("disabled",false);
		$("#lng").attr("disabled",false);
		$("#radius").attr("disabled",false);
	});
});

</script>
<div class="container">
	<div class="jumbotron">
		<h3>Search OSS Sensors Database</h3>
		<form name="requestform" method="get"
			action="/OpenSensorSearch/search" onsubmit="return validate()">
			<input type="hidden" name="httpAccept" value="text/html" /> <span><input
				name="q" type="text" class="search-input form-control" /> </span> 
			<input name="lat" type="hidden"
				placeholder="location" id="lat" class="form-control">
			<input name="lng" type="hidden"
				placeholder="location" id="lng" class="form-control">
			<input name="radius" type="hidden"
				placeholder="location" id="radius" class="form-control">
			
			<span><input
				value="Search" type="submit" class="btn btn-primary btn-large" id="notlocation" />
				<input
				value="Search nearby my location" type="submit" class="btn btn-primary btn-large" id="location"/> 
				</span>
			<p>
		</form>
		<label id="location_info"></label>
		<div class="alert-msg"></div>
	</div>
</div>
</body>
</html>
