/*
 * ﻿    ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
function showPosition(position) {
	$("#lat").val(position.coords.latitude);
	$("#lng").val(position.coords.longitude);
	$("#radius").val("1000");
	var str = "You are near:" + position.coords.latitude + ","
			+ position.coords.longitude;
    $("#location_info").html(str);
}

function validate() {
	var q = document.forms["requestform"]["q"].value;
	if (q == null || q == "" || q.toString().trim().length == 0) {
		alert("Please enter a valid query!");
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

					$("#btnSearchNearby")
							.click(
									function() {
										if (navigator.geolocation) {
											navigator.geolocation
													.getCurrentPosition(showPosition);
										} else {
											$(".alert-msg")
													.append(
															'<div class="alert alert-danger">Your browser doesn\'t support	geolocation!</div>');
										}

										$("#lat").attr("disabled", false);
										$("#lng").attr("disabled", false);
										$("#radius").attr("disabled", false);
									});
				});
