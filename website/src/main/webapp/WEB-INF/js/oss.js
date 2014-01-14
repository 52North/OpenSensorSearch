/*
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
function showPosition(position) {
	jQuery("#lat").val(position.coords.latitude);
	jQuery("#lng").val(position.coords.longitude);
	jQuery("#radius").val("1000");
	console.log("Retrieved location: " + position.coords.latitude + ", " + position.coords.longitude);
	
	var str = "You are near: " + Number((position.coords.latitude).toFixed(3)) + ", "
			+ Number((position.coords.longitude).toFixed(3));
	// $("#location_info").html(str);

	jQuery("#btnSearchNearby").tooltip("hide").attr("data-original-title", str)
			.tooltip("fixTitle").tooltip("show");
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

jQuery(document)
		.ready(
				function() {
					jQuery("#btnSearch").click(function() {
						jQuery("#lat").attr("disabled", true);
						jQuery("#lng").attr("disabled", true);
						jQuery("#radius").attr("disabled", true);
					});

					// activate all tooltips
					jQuery("[data-toggle='tooltip']").tooltip();

					jQuery("#btnSearchNearby")
							.click(
									function() {
										if (navigator.geolocation) {
											navigator.geolocation
													.getCurrentPosition(showPosition);
										} else {
											jQuery("#alerts")
													.append(
															'<div class="alert alert-danger">Your browser does not support	geolocation!<a class="close" data-dismiss="alert" href="#" aria-hidden="true">&times;</a></div>');
										}

										jQuery("#lat").attr("disabled", false);
										jQuery("#lng").attr("disabled", false);
										jQuery("#radius").attr("disabled", false);
									});
					
					jQuery.ajax({
						dataType : "json",
						url : ossApiEndpoint + "/statistics/sensors",
						success : function(data) {
							// 				console.log(data);
							jQuery("#statsSensors").html(data.sensors);
						}
					});

					jQuery.ajax({
						dataType : "json",
						url : ossApiEndpoint + "/statistics/phenomena",
						success : function(data) {
							// 				console.log(data);
							jQuery("#statsPhenonema").html(data.phenomena);
						}
					});

					jQuery.ajax({
						dataType : "json",
						url : ossApiEndpoint + "/statistics/services",
						success : function(data) {
							// 				console.log(data);
							jQuery("#statsServices").html(data.services);
						}
					});
				});
