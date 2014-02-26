<%--

    ﻿Copyright (C) 2013 52°North Initiative for Geospatial Open Source Software GmbH

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
<%@ page language="java" contentType="text/html; utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>

<!DOCTYPE html>
<html lang="en" ng-app="oss.app">
<head>

<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="content-type" content="text/html;charset=utf-8" />

<base
	href="${fn:substring(url, 0, fn:length(url) - fn:length(pageContext.request.requestURI))}${pageContext.request.contextPath}/" />

<link href="http://52north.org/templates/52n-2012/favicon.ico"
	rel="shortcut icon" type="image/x-icon" />

<link href="styles/bootstrap.css" rel="stylesheet">
<link href="styles/bootstrap-responsive.css" rel="stylesheet">
<link href="styles/jquery-ui.css" rel="stylesheet" />

<link href="styles/oss.css" type="text/css" rel="stylesheet" />

<jsp:useBean id="configBean" scope="application"
	class="org.n52.oss.ui.WebsiteConfig" />

<script type="text/javascript">
	var ossApiEndpoint = '<%=configBean.getApiEndpoint()%>';
	console.log("ossApiEndpoint = " + ossApiEndpoint);
	var sirEndpoint = '<%=configBean.getSirEndpoint()%>';
	console.log("sirEndpoint = " + sirEndpoint);
</script>

<!-- <title ng-bind-template="Open Sensor Search by 52°North | {{q}}">Open -->
<!-- 	Sensor Search by 52°North</title> -->
<title>Open Sensor Search by 52°North</title>

<script src="js/lib/jquery.js" type="text/javascript"></script>
<script src="js/lib/jquery-ui.min.js" type="text/javascript"></script>
<script src="js/lib/bootstrap.js" type="text/javascript"></script>

<script src="js/lib/angular/angular.js"></script>
<script src="js/lib/angular/angular-route.js"></script>
<script src="js/lib/angular/ui-bootstrap-custom-tpls-0.10.0.js"></script>

<script src="js/app.js"></script>
<script src="js/controllers.js"></script>
<script src="js/oss.js"></script>

<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
<script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
<script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
<![endif]-->

<script type="text/ng-template" id="feedbackModalContent.html">
        							<div class="modal-header">
										<h3>User feedback for {{current.sensorId}}</h3>
									</div>
									<div class="modal-body">
										<pre>{{feedback}}</pre>
									</div>
									<div class="modal-footer">
										<a class="btn btn-primary" target="_blank" title="Submit feedback for GEOSS"
											ng-href="{{feedbackSubmitLink}}">Submit own feedback</a>
										<button class="btn btn-primary" ng-click="ok()">Close</button>
									</div>
</script>

<link href="styles/autocomplete.css" type="text/css" rel="stylesheet" />

</head>

<body>
	<img class="betaBanner" src="images/beta.png" height="100"
		alt="beta banner" />

	<div class="navbar navbar-default navbar-fixed-top navbar-inverse">
		<div class="container">
			<a href="http://52north.org" title="52&deg;North Website"><img
				alt="52N logo" src="images/52n-icon.png"
				style="float: right; margin-top: 4px; margin-right: 10px;"
				width="42" height="42"></a>

			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#">Open Sensor Search</a>
			</div>

			<div class="navbar-collapse collapse">
				<ul class="nav navbar-nav">
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown">Documentation <b class="caret"></b></a>
						<ul class="dropdown-menu">
							<li class="dropdown-header">OSS</li>
							<li><a
								href="https://52north.org/twiki/bin/view/SensorWeb/OpenSensorSearch">Wiki</a></li>
							<li><a href="#/api">API</a></li>
							<li><a href="#" id="apiEndpointMenuLink">API endpoint</a></li>
							<li class="divider"></li>
							<li class="dropdown-header">Meta</li>
							<li><a
								href="http://52north.org/communities/sensorweb/discovery/">Sensor
									Discovery</a></li>
						</ul></li>
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown">Harvesting <b class="caret"></b></a>
						<ul class="dropdown-menu">
							<li class="dropdown-header">Documentation</li>
							<li><a href="#/harvesting/developers">Script Developers</a></li>
							<li class="dropdown-header">Forms</li>
							<li><a href="#/harvesting/script">Javascript</a></li>
							<li><a href="#/harvesting/oss">OSS Server</a></li>
							<li><a href="#/harvesting/ows">OGC Web Service</a></li>
						</ul></li>
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown">More <b class="caret"></b></a>
						<ul class="dropdown-menu">
							<li class="dropdown-header">Tools</li>
							<li><a href="#/discoveryProfile">Discovery Profile</a></li>
							<li><a href="#/conversion">Conversion</a></li>
							<li class="divider"></li>
							<li class="dropdown-header">SIR</li>
							<li><a href="sir/client">Extended Client</a></li>
							<li><a href="sir/form">Form Client</a></li>
							<li><a
								href="https://52north.org/twiki/bin/view/SensorWeb/SensorInstanceRegistry">Documentation</a></li>
							<li class="divider"></li>
							<li class="dropdown-header">Meta</li>
							<li><a href="#/about">About</a></li>
							<li><a href="#/login">Login</a></li>
							<li><a href="https://github.com/52North/OpenSensorSearch">Source
									Code</a></li>
							<li><a
								onclick="window.external.AddSearchProvider('resources/searchPlugin.xml');">Add
									as search plugin</a></li>
						</ul></li>
				</ul>
			</div>
		</div>
	</div>


	<div id="wrap">
		<div data-alerts="alerts"></div>
		
		<div class="container">
			<div ng-view></div>
		</div>
	</div>

	<div style="clear: both;"></div>

	<div id="footer">
		<div class="container">
			<div class="row" style="margin-top: 10px;">
				<dl class="dl-horizontal col-md-6" style="margin: 0px;">
					<dt>52&deg;North</dt>
					<dd>
						<a href="http://www.52north.org/">http://www.52north.org</a>
					</dd>
					<dt>GitHub Project</dt>
					<dd>
						<a href="https://github.com/52North/OpenSensorSearch">https://github.com/52North/OpenSensorSearch</a>
						<a href="https://github.com/52North/OpenSensorSearch"
							style="text-decoration: none"><span class="label label-info">Fork
								us!</span> </a>
					</dd>
				</dl>
				<dl class="dl-horizontal col-md-6" style="margin: 0px;">
					<dt>GeoViQua</dt>
					<dd>
						<a href="http://www.geoviqua.org/">http://www.geoviqua.org/</a>
					</dd>
					<dt>More Projects</dt>
					<dd>
						<a
							href="http://52north.org/resources/references/sensor-web/osiris">OSIRIS</a>,
						GENESIS
					</dd>
				</dl>
			</div>
			<p class="text-center" style="margin-top: 10px;">Copyright &copy;
				2013 52&deg;North Initiative for Geospatial Open Source Software
				GmbH. All Rights Reserved. | ${project.build.finalName} version
				${version}</p>
		</div>
	</div>

	<!-- 	<script type="text/javascript" src="lib/jquery.socialshareprivacy.min.js"></script> -->
	<script type="text/javascript">
		$("#apiEndpointMenuLink").attr("href", ossApiEndpoint);
		var queryEndpoint = ossApiEndpoint + "/search";
	</script>
</body>
</html>
