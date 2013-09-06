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
	
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span3">
				<div class="well sidebar-nav">
					<ul class="nav nav-list">
						<li class="nav-header">Upload</li>
						<li id="upload-pin"><a href="${context}/OSSUI/script/upload">Upload a
								Javascript file</a></li>
						<li class="nav-header">Harvest</li>
						<li id="schedule-pin"><a href="${context}/OSSUI/script/schedule">Schedule a harvest</a></li>
					</ul>
				</div>
				<!--/.well -->
			</div>

			<!-- /container -->