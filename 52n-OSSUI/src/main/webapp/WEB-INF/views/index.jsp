<%--

    ï»¿    ï»¿Copyright (C) 2012 52Â°North Initiative for Geospatial Open Source Software GmbH

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
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>

<!DOCTYPE html>
<html lang="en">
<head>

<%@ include file="common-head.jsp"%>

<title>Open Sensor Seary by 52°North</title>

<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<script src="./${context }/scripts/autocomplete.js"></script>
<script src="./${context}/scripts/bootstrap.min.js"></script>

</head>

<script>
	function showPosition(position) {
		$("#lat").val(position.coords.latitude);
		$("#lng").val(position.coords.longitude);
		$("#radius").val("1000");
		var str = "You are near:" + position.coords.latitude + ","
				+ position.coords.longitude

		function validate() {
		var q = document.forms["requestform"]["q"].value;
		if (q == null || q == "" || q.toString().trim().length == 0) {
			alert("Please enter a valid query!");
			return false;
		}
		return true;

	}

	$(document).ready(function() {
		
		if (navigator.geolocation) {
			navigator.geolocation
					.getCurrentPosition(showPosition);
		} else {
			$(".alert-msg")
					.append('<div class="alert alert-danger">Your browser doesn\'t support	geolocation!</div>');
		}
		
		$("#notlocation").click(function() {
			$("#lat").attr("disabled", true);
			$("#lng").attr("disabled", true);
			$("#radius").attr("disabled", true);
		});
		$("#location").click(function() {
			$("#lat").attr("disabled", false);
			$("#lng").attr("disabled", false);
			$("#radius").attr("disabled", false);
		});
	});
</script>

<body>

	<div id="wrap">
		<%@ include file="navigation.jsp"%>

		<div class="container" style="padding-top: 70px;">
			<c:if test="${RegisterSucceded}">
				<div class="alert alert-error">
					<a class="close" data-dismiss="alert"></a> <strong>Error!</strong>
					Your account was created , contact Site administrator for
					activation
				</div>
			</c:if>

			<h1>Open Sensor Search</h1>

			<form name="requestform" method="get"
				action="/OpenSensorSearch/search" onsubmit="return validate()">
				<input type="hidden" name="httpAccept" value="text/html" /> <span><input
					name="q" type="text" class="search-input form-control" /> </span> <input
					name="lat" type="hidden" id="lat" class="form-control"> <input
					name="lng" type="hidden" id="lng" class="form-control"> <input
					name="radius" type="hidden" id="radius" class="form-control">

				<span><input value="Search" type="submit"
					class="btn btn-primary btn-large" id="notlocation" /> <input
					value="Search nearby my location" type="submit"
					class="btn btn-primary btn-large" id="location" /> </span>
				<p>
			</form>

			<!-- 			<div class="panel panel-default"> -->
			<!-- 				<div class="panel-heading"> -->
			<!-- 					<h3 class="panel-title">Test</h3> -->
			<!-- 				</div> -->
			<!-- 				<div class="panel-body">[...]</div> -->
			<!-- 			</div> -->

			<div class="pull-right" style="margin-bottom: 10px;">
				<small><label id="location_info"></label></small>
			</div>
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
							<a href="https://github.com/52North/OpenSensorSearch" style="text-decoration:none"><span class="label label-info">Fork us!</span>
						</a>
					</dd>
				</dl>
				<dl class="dl-horizontal col-md-6" style="margin: 0px;">
					<dt>GeoViQua</dt>
					<dd>
						<a href="http://www.geoviqua.org/">http://www.geoviqua.org/</a>
					</dd>
					<!-- 					<dt>GEO label</dt> -->
					<!-- 					<dd> -->
					<!-- 						<a href="http://www.geolabel.info/">http://www.geolabel.info/</a> -->
					<!-- 					</dd> -->
				</dl>
			</div>
			<p class="text-center" style="margin-top: 10px;">Copyright © 2013
				52°North Initiative for Geospatial Open Source Software GmbH. All
				Rights Reserved.</p>
		</div>
	</div>
</body>
</html>
