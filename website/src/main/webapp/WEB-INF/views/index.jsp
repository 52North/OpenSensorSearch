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
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>

<!DOCTYPE html>
<html lang="en">
<head>

<%@ include file="common-head.jsp"%>

<title>Open Sensor Search by 52°North</title>

</head>

<body>
	<div id="wrap">
		<img class="betaBanner" src="images/beta.png" height="180px" alt="beta banner" />
	
		<%@ include file="navigation.jsp"%>

		<div class="container" style="padding-top: 10%;">
			<c:if test="${RegisterSucceded}">
				<div class="alert alert-error">
					<a class="close" data-dismiss="alert"></a> <strong>Error!</strong>
					Your account was created , contact Site administrator for
					activation
				</div>
			</c:if>

			<%
			    /* http://eltimn.github.io/jquery-bs-alerts/ */
			%>
			<div data-alerts="alerts"></div>

			<div class="row">
				<h1>Open Sensor Search</h1>
			</div>

			<form class="form-inline" name="requestform" method="get"
				action="/oss-service/search" onsubmit="return validate()">
				<div class="row" style="margin-top: 42px;">
					<div class="col-xs-6 col-sm-4 col-md-2">
						<input name="q" id="inputSearch" type="text"
							class="form-control input-large search-query"
							placeholder="Search term..." />
					</div>

					<div class="col-xs-6 col-md-4">
						<button type="submit" class="btn btn-primary btn-large"
							id="btnSearch">Search</button>
						<button type="submit" data-toggle="tooltip"
							data-placement="bottom" title="Uses the Geolocation API"
							class="btn btn-info btn-large" id="btnSearchNearby">Search
							nearby</button>
					</div>
				</div>

				<input type="hidden" name="httpAccept" value="text/html" /> <input
					name="lat" type="hidden" id="lat" class="form-control"> <input
					name="lng" type="hidden" id="lng" class="form-control"> <input
					name="radius" type="hidden" id="radius" class="form-control">
			</form>

			<div class="row" style="margin-top: 42px;">
				<p>
					Searching across... <span id="statsSensors">..</span> sensors, <span
						id="statsPhenonema">..</span> phenomena, and <span
						id="statsServices">..</span> services.
				</p>
				<p>
					<span class="infotextHighlight">Is your data missing? <a
						href="mailto:${sir.deploy.contact}">Write us an email!</a></span>
				</p>
			</div>

			<!-- 			<div class="panel panel-default"> -->
			<!-- 				<div class="panel-heading"> -->
			<!-- 					<h3 class="panel-title">Test</h3> -->
			<!-- 				</div> -->
			<!-- 				<div class="panel-body">[...]</div> -->
			<!-- 			</div> -->

			<!-- 			<div class="pull-right" style="margin-bottom: 10px;"> -->
			<!-- 				<label id="location_info"></label> -->
			<!-- 			</div> -->
		</div>

		<div id="push"></div>

	</div>

	<div id="footer">
		<div class="container">
			<div class="row" style="margin-top: 10px;">
				<dl class="dl-horizontal col-md-6" style="margin: 0px;">
					<dt>52°North</dt>
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
			<p class="text-center" style="margin-top: 10px;">Copyright © 2013
				52°North Initiative for Geospatial Open Source Software GmbH. All
				Rights Reserved.</p>
		</div>
	</div>

	<!-- load scripts -->
	<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
	<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
	<script src="lib/bootstrap.js"></script>
	<script src="lib/jquery.bsAlerts.min.js"></script>

	<script src="scripts/autocomplete.js"></script>
	<script src="scripts/oss.js"></script>

</body>
</html>
