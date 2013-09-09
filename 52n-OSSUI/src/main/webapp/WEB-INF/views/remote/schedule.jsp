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
<title>Harvest a remote server</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<script src="../${context}/scripts/bootstrap.min.js"></script>

<script>
	$(function() {
		$("#datepicker").datepicker();
		$("#schedule-pin").addClass("active");
	});
</script>
<script>
	function scheduleServer() {
		$("#auth_token").prop("disabled", true);
		$("#scheduleserverbtn").prop("disabled", true);
		$("#datepicker").prop("disabled", true);
		var d = $("#datepicker").val()
		$("#datepicker").val(Date.parse(d));
		$
				.ajax({
					type : "POST",
					url : "http://localhost:8080/OpenSensorSearch/script/remote/server/harvest",
					data : $("#schedule-form").serialize(),
					statusCode : {
						200 : function(data) {
							alert("Request sent successfully!");
							$("#auth_token").prop("disabled", false);
							$("#datepicker").prop("disabled", false);
						},
						404 : function() {
							alert("No such authToken!");
							$("#auth_token").prop("disabled", false);
							$("#datepicker").prop("disabled", false);
						},
						500 : function() {
							alert("Internal server error ! please try again later")
							$("#auth_token").prop("disabled", false);
							$("#datepicker").prop("disabled", false);
						}
					}
				});
	}
</script>

<link href="../${context}/styles/bootstrap.css" rel="stylesheet">
<%@ include file="navBar.jsp"%>
<!-- /container -->
<!--/span-->
<div class="span9">
	<div class="hero-unit">
		<h1>Harvest a remote server</h1>
		<p>In the specified fields , please enter a valid auth token and a
			valid time</p>
		<form id="schedule-form">

			 <input type="text"
				class="span6 span4 form-control input-lg" placeholder="Auth token" name="auth_token"></input>
		</form>
	 <input type="text" class="span6 span4 form-control input-lg" placeholder="Date"
			name="date" id="datepicker"></input>
		<p>
			<a id="scheduleserverbtn" href="#" class="btn btn-primary btn-large"
				onclick="scheduleServer()">Schedule harvest &raquo;</a>
		</p>
	</div>
</div>
</div>
</body>
</html>
