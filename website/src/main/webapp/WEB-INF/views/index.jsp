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
<html lang="en" ng-app="ossApp" ng-controller="ossCtrl">
<head>

<%@ include file="common-head.jsp"%>

<title ng-bind-template="Open Sensor Search by 52°North | {{q}}">Open
	Sensor Search by 52°North</title>

<script src="lib/jquery.js" type="text/javascript"></script>
<script src="lib/jquery-ui.min.js" type="text/javascript"></script>
<script src="lib/bootstrap.js" type="text/javascript"></script>

<script src="scripts/lib/angular/angular.js"></script>
<script src="scripts/lib/angular/ui-bootstrap-custom-tpls-0.9.0.js"></script>
<!-- <script src="scripts/lib/angular/ui-bootstrap.js"></script> -->

<script src="scripts/controllers.js"></script>
<script src="scripts/oss.js"></script>

<link href="styles/autocomplete.css" type="text/css" rel="stylesheet" />

</head>

<body>
	<div id="wrap" style="margin: 60px 0 0 0;">
		<%@ include file="navigation.jsp"%>

		<div class="container">
			<%-- 			<c:if test="${RegisterSucceded}"> --%>
			<!-- 				<div class="alert alert-error"> -->
			<!-- 					<a class="close" data-dismiss="alert"></a> <strong>Error!</strong> -->
			<!-- 					Your account was created , contact Site administrator for -->
			<!-- 					activation -->
			<!-- 				</div> -->
			<%-- 			</c:if> --%>

			<div ng-controller="ossAlertCtrl" ng-cloak>
				<alert ng-repeat="alert in alerts" type="alert.type"
					close="closeAlert($index)">{{alert.msg}}</alert>
			</div>

			<div id="search">
				<form class="form-inline" ng-submit="ossSearch()">
					<!-- <pre>Model: {{asyncSelected | json}}</pre> -->
					<!-- 					<div class='container-fluid' ng-controller="ossTypeaheadCtrl"> -->
					<!-- 						<pre>Model: {{asyncSelected | json}}</pre> -->
					<!-- 						<label for="q">Search:</label> <input type="text" -->
					<!-- 							name="q" class="form-control" ng-model="asyncSelected" -->
					<!-- 							placeholder="Suggestions will be loaded while typing..." -->
					<!-- 							typeahead="s for suggestions in getSuggestions($viewValue) | filter:$viewValue" -->
					<!-- 							typeahead-loading="loadingLocations" typeahead-min-length="3" -->
					<!-- 							typeahead-wait-ms="300" required> <i -->
					<!-- 							ng-show="loadingLocations" class="glyphicon glyphicon-refresh"></i> -->
					<!-- 						<p class="help-block">Try for example: "washington" or "water" -->
					<!-- 							or "temperature"</p> -->
					<!-- 					</div> -->
					<div>
						<div class='container-fluid'>
							<label for="q">Search for sensor data:</label> <input type="text"
								name="q" class="form-control" ng-model="q"
								placeholder="Search term(s)..." required>
							<p class="help-block">Try for example: "washington" or
								"water" or "temperature"</p>
						</div>
					</div>
					<div>
						<button type="submit" class="btn btn-primary btn-large">Search</button>
						<button type="submit" data-toggle="tooltip"
							data-placement="bottom" title="Uses the Geolocation API"
							class="btn btn-info btn-large" id="btnSearchNearby">Search
							nearby</button>
					</div>

					<input type="hidden" name="httpAccept" value="text/html" /> <input
						name="lat" type="hidden" id="lat" class="form-control"> <input
						name="lng" type="hidden" id="lng" class="form-control"> <input
						name="radius" type="hidden" id="radius" class="form-control">
				</form>

				<div id="searchResult.results">
					<div id="searchResultControl" ng-show="searchResult.results">
						<span id="searchResultStatistics">{{searchResult.results.length}}
							hits</span> | <span id="searchFilter"> Filter: <input
							ng-model="query">
						</span> <span id="searchFormats" ng-controller="ossFormatCtrl"> <span>Response
								format:</span> <select ng-model="selectedFormat"
							ng-options="format.name for format in availableResponseFormats"
							ng-change="update()"></select>
						</span>
					</div>

					<ul id="searchResultList">
						<li
							ng-repeat="result in searchResult.results  | filter:query | orderBy:orderProp">
							<div class="result-header">
								Sensor: <a href="{{apiEndpoint_sensors}}/{{result.sensorId}}" title="RESTful resource for {{result.sensorId}}">{{result.sensorId}}</a>,
								<a href="{{result.sensorDescription.url}}"
									title="SIR DescribeSensor request for sensor {{result.sensorId}}">DescribeSensor</a>
							</div>
							<div class="result-service"
								ng-hide="result.serviceReferences.length != 0">
								{{result.serviceReferences.length}} service(s):
								<ul>
									<li ng-repeat="ref in result.serviceReferences">
										<a href="{{serviceUrl(ref)}}"
										title="{{ref.serviceSpecificSensorId}} @ {{ref.serviceType}}">{{ref.serviceUrl}}</a>
									</li>
								</ul>
							</div>
							<div class="result-label">
								<object class="geolabelEmbed" data="{{geolabelUrl(result)}}"></object>
							</div>
							<div class="result-properties">
								<div>
									<span>Last update: {{result.lastUpdate}} </span> | <span>
										BBOX: {{result.sensorDescription.boundingBox.north}}N,
										{{result.sensorDescription.boundingBox.east}}E,
										{{result.sensorDescription.boundingBox.south}}S,
										{{result.sensorDescription.boundingBox.west}}W </span>
								</div>
								<div class="result-description">
									<p>{{result.sensorDescription.text}}</p>
								</div>
							</div>
							<div class="social">
								<!-- <span><a href="{{feedbackSubmit(result)}}" title="Submit feedback for GEOSS">Submit Feedback</a></span> -->
								<a class="btn btn-xs btn-default" target="_blank"
									title="Submit feedback for GEOSS"
									ng-href="{{createFeedbackSubmitLink(result)}}">Submit Feedback</a>
									{{feedbackSubmit(result)}}
							</div>
						</li>
					</ul>
				</div>

				<div id="searchInfo">
					<p>
						Searching across... <span id="statsSensors">..</span> sensors, <span
							id="statsPhenonema">..</span> phenomena, and <span
							id="statsServices">..</span> services. <span
							class="infotextHighlight">Is your data missing? <a
							href="mailto:d.nuest@52north.org">Write us an email!</a></span> <br />
						<a ng-href="{{urlQuery}}">{{urlQuery}}</a>
					</p>
				</div>
			</div>
		</div>
	</div>

	<div style="clear: both;"></div>

	<%@ include file="footer.jsp"%>

	<script type="text/javascript">
		var queryEndpoint = ossApiEndpoint + "/search";
		$("form[name='requestform']").attr("action", queryEndpoint);
	</script>
</body>
</html>
