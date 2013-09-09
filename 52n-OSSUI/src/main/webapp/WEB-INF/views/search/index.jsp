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
	var str = position.coords.latitude+","+position.coords.longitude;
	$("#loc").val(str)
}

</script>


<div class="container">
	<div class="jumbotron">
		<h3>Search OSS Sensors Database</h3>
		<form name="requestform" method="get"
			action="${service.path}/${service.endpoint.opensearch}">
			<input type="hidden" name="httpAccept" value="text/html" /> <span><input
				name="q" type="text" class="search-input form-control" /> </span> 
			<input type="text" name="location" readonly="readonly"
				placeholder="location" id="loc" class="form-control">
			<span><input
				value="Search" type="submit" class="btn btn-primary btn-large" /> </span>
			<p>
		</form>
		<div class="alert-msg"></div>
	</div>
</div>
</body>
</html>
