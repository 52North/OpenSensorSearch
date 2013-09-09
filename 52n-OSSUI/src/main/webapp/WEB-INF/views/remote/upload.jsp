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
<title>Harvest a remote file</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="../${context}/scripts/bootstrap.min.js"></script>
<style>

.form-signin {
	max-width: 550px;
	padding: 15px;
	margin: 0 auto;
}

.form-signin .form-signin-heading,.form-signin .checkbox {
	margin-bottom: 10px;
}

.form-signin .checkbox {
	font-weight: normal;
}

.form-signin .form-control {
	position: relative;
	font-size: 16px;
	height: auto;
	padding: 10px;
	-webkit-box-sizing: border-box;
	-moz-box-sizing: border-box;
	box-sizing: border-box;
}

.form-signin .form-control:focus {
	z-index: 2;
}

.form-signin input[type="text"] {
	margin-bottom: -1px;
	border-bottom-left-radius: 0;
	border-bottom-right-radius: 0;
}

.form-signin input[type="password"] {
	margin-bottom: 10px;
	border-top-left-radius: 0;
	border-top-right-radius: 0;
}

</style>
<script>
	$(function() {

		$("#upload-pin").addClass("active");
	});
</script>
<script>
	function uploadServer() {
		$("#remoteServer").prop("disabled", true);
		$("#uploadserverbtn").prop("disabled", true);
		$
				.ajax({
					type : "POST",
					url : "http://localhost:8080/OpenSensorSearch/script/remote/server",
					data : $("#harvest-form").serialize(),
					success : function(data) {
						if (data) {
							var auth_token = data['auth_token'];
							$("#authtokenval").val(auth_token);
						}
						$("#remoteServer").prop("disabled", true);
						$("#uploadserverbtn").prop("disabled", true);
					},
					error : function() {
						alert("error");
						$("#remoteServer").prop("disabled", true);
						$("#uploadserverbtn").prop("disabled", true);
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
		<h1>Upload A remote server</h1>
		<p>In the specified field , please enter a valid sensor provider
			for harvesting</p>
		<form id="harvest-form" class="form-signin">
			<input type="text" class="form-control" placeholder="Remote Server"
				name="url" /> <input type="text" class="form-control"
				id="authtokenval" value="Auth token" readonly>

			<!-- TODO yakoub : add the data to the harvesting -->
			<a id="uploadserverbtn" href="#"
				class="btn btn-lg btn-primary btn-block" onclick="uploadServer()">Add
				server &raquo;</a>

		</form>
	</div>
</div>
</div>
</body>
</html>
