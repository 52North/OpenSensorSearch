/*
 * ﻿Copyright (C) 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var ossApiUrl = "http://localhost:8080/oss-service/api/v1";

function showPosition(position) {
	$("#lat").val(position.coords.latitude);
	$("#lng").val(position.coords.longitude);
	$("#radius").val("1000");
	console.log("Retrieved location: " + position.coords.latitude + ", " + position.coords.longitude);
	
	var str = "You are near: " + Number((position.coords.latitude).toFixed(3)) + ", "
			+ Number((position.coords.longitude).toFixed(3));
	// $("#location_info").html(str);

	$('#btnSearchNearby').tooltip('hide').attr('data-original-title', str)
			.tooltip('fixTitle').tooltip('show');
}

function validate() {
	// close open alerts
	// $(".close").click(function() {
	// $(".alert").alert();
	// });

	var q = document.forms["requestform"]["q"].value;
	if (q == null || q == "" || q.toString().trim().length == 0) {
		// $(document).trigger("add-alerts", {
		// message : "Please enter a search term.",
		// priority : "error"
		// });

		return false;
	}
	return true;
}

$(document)
		.ready(
				function() {
					$("#btnSearch").click(function() {
						$("#lat").attr("disabled", true);
						$("#lng").attr("disabled", true);
						$("#radius").attr("disabled", true);
					});

					// activate all tooltips
					$("[data-toggle='tooltip']").tooltip();

					$("#btnSearchNearby")
							.click(
									function() {
										if (navigator.geolocation) {
											navigator.geolocation
													.getCurrentPosition(showPosition);
										} else {
											$("#alerts")
													.append(
															'<div class="alert alert-danger">Your browser does not support	geolocation!<a class="close" data-dismiss="alert" href="#" aria-hidden="true">&times;</a></div>');
										}

										$("#lat").attr("disabled", false);
										$("#lng").attr("disabled", false);
										$("#radius").attr("disabled", false);
									});
					
					// load statistics
					console.log("API URL: " + ossApiUrl);
						
					$.ajax({
						dataType : "json",
						url : ossApiUrl + "/statistics/sensors",
						success : function(data) {
							// 				console.log(data);
							$("#statsSensors").html(data.sensors);
						}
					});

					$.ajax({
						dataType : "json",
						url : ossApiUrl + "/statistics/phenomena",
						success : function(data) {
							// 				console.log(data);
							$("#statsPhenonema").html(data.phenomena);
						}
					});

					$.ajax({
						dataType : "json",
						url : ossApiUrl + "/statistics/services",
						success : function(data) {
							// 				console.log(data);
							$("#statsServices").html(data.services);
						}
					});
				});
