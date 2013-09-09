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
<title>Show script</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link href="../../${context}/styles/bootstrap.css" rel="stylesheet">
<script
	src="https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js"></script>

<style type="text/css">
body {
	padding-top: 40px;
	padding-bottom: 40px;
	background-color: #f5f5f5;
}

.navbar-static-top {
	margin-bottom: 19px;
}

.sidebar-nav {
	padding: 9px 0;
}
</style>
<link href="../${context}/styles/bootstrap-responsive.css"
	rel="stylesheet">

<!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
      <script src="../assets/js/html5shiv.js"></script>
    <![endif]-->

<!-- Fav and touch icons -->
<link rel="apple-touch-icon-precomposed" sizes="144x144"
	href="../assets/ico/apple-touch-icon-144-precomposed.png">
<link rel="apple-touch-icon-precomposed" sizes="114x114"
	href="../assets/ico/apple-touch-icon-114-precomposed.png">
<link rel="apple-touch-icon-precomposed" sizes="72x72"
	href="../assets/ico/apple-touch-icon-72-precomposed.png">
<link rel="apple-touch-icon-precomposed"
	href="../assets/ico/apple-touch-icon-57-precomposed.png">
<link rel="shortcut icon" href="../assets/ico/favicon.png">
<script>
	function showScript() {
		var val = $("#scriptId").val();
		if (val.toString().trim().length == 0) {
			alert("Enter a scriptId first")
		} else {
			if (parseInt(val)) {
				var url = "${context}/OSSUI/script/show/" + val;
				window.location.assign(url);
			}else{
				alert("Please enter a valid scriptId");
			}
		}
	}
</script>
</head>

<body>

	<div class="navbar navbar-default navbar-static-top">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-collapse">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#">OpenSensorSearch</a>
			</div>
			<div class="navbar-collapse collapse">
				<ul class="nav navbar-nav">
					<li class="active"><a href="${context}/OSSUI/remote/index">Harvest
							a Remote Server</a></li>
					<li><a href="${context}/OSSUI/script/index">Harvest a
							javascript script</a></li>
					<li><a href="#contact">Contact</a></li>

				</ul>
			</div>
			<!--/.nav-collapse -->
		</div>
	</div>

	<!-- /container -->
	<!--/span-->
	<div class="span9">
		<div class="hero-unit">
			<form id="schedule-form">
				<input type="text" class="span4 form-control input-lg"
					placeholder="Script Id" name="script_Id" id="scriptId" />
				<p>
					<a id="scheduleserverbtn" href="#"
						class="btn btn-primary btn-large" onclick="showScript()">Show
						Script! &raquo;</a>

				</p>
			</form>
		</div>
	</div>
	</div>

	<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
	<script src="../../${context}/scripts/bootstrap.min.js"></script>
</body>
</html>
