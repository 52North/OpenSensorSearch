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
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
<title>Discovery Profile Validation | Open Sensor Search</title>

<%@ include file="../common-head.jsp"%>

</head>

<body>

	<div id="wrap">
		<%@ include file="../navigation.jsp"%>

		<div class="container" style="padding-top: 60px;">
			<div data-alerts="alerts"></div>

			<h1>Discovery Profile Validation</h1>

			<p>Test a SensorML document for conformity with SensorML profile
				for discovery.</p>

			<!-- TODO: add listing of available checks based on API endpoint /oss-service/api/v1/check -->
			<!-- TODO: add selection of output formats -->


			<form id="testform">
				<div class="form-group">
					<label for="input" class="control-label">SensorML Document</label>
					<textarea id="input" name="input" class="form-control" rows="5"></textarea>
					<span class="help-block"> Insert the SensorML description of
						a sensor. To find out more about the discovery profile check out
						the <a
						href="https://portal.opengeospatial.org/files/?artifact_id=37944">OGC
							discussion paper</a>.
						<button id="btnAddExample" type="button"
							class="btn btn-default btn-xs">Add an example file</button>
					</span>
				</div>

				<div class="form-group">
					<button type="submit" class="btn btn-lg btn-primary">Check!</button>
					<div class="btn-group" data-toggle="buttons" id="responseFormat"
						style="top: 8px;" title="Format of the check result"
						data-toggle="tooltip">
						<label for="inputJson" class="btn btn-default btn-sm active">
							<input type="radio" name="format" id="inputJson" value="json"
							checked="checked"> JSON
						</label> <label for="inputXml" class="btn btn-default btn-sm"> <input
							type="radio" name="format" id="inputXml" value="xml"> XML
						</label>
					</div>
				</div>


				<div id="resultGroup" class="form-group" style="margin-top: 42px;">
					<label for="input" class="control-label">Check output</label>
					<textarea id="output" name="output" class="form-control" rows="3"></textarea>
					<span class="help-block">The output is the result of
						applying <a href="http://en.wikipedia.org/wiki/Schematron"
						title="Schematron @ Wikipedia">Schematron</a> rules to the
						document. You can download the used profile as a <a
						href="https://raw.github.com/52North/OpenSensorSearch/master/service/src/main/resources/SensorML_Profile_for_Discovery.sch"
						title="SensorML Profile for Discovery - Schematron">Schematron
							file</a>.
					</span>
				</div>
			</form>

		</div>

		<div id="push"></div>

	</div>

	<%@ include file="../footer.jsp"%>

	<script type="text/javascript">
		var requestEditor = null;
		var responseEditor = null;
		var descriptionEditor = null;

		$(document).ready(function() {
			// 			var inArea = document.getElementById("input");
			// 			CodeMirror.fromTextArea(inArea, {
			// 				mode : "text/xml",
			// 				lineNumbers : true,
			// 				value : defaultString
			// 			});
			// 			var outArea = document.getElementById("output");
			// 			CodeMirror.fromTextArea(outArea);

			$("responseFormat").tooltip({
				container : "body",
				placement : "top"
			});
		});

		$("#input").bind("input propertychange", function() {
			$("#resultGroup").removeClass("has-success");
			$("#resultGroup").removeClass("has-error");
			$("#output").val("");
		});

		$('#btnAddExample')
				.click(
						function() {
							$
									.ajax({
										type : "GET",
										url : "resources/SensorML-discovery-profile-example.xml",
										success : function(data) {
											console.log("Loaded example file:");
											var xmlstr = data.xml ? data.xml
													: (new XMLSerializer())
															.serializeToString(data);
											console.log(xmlstr);
											$("#input").val(xmlstr);
										}
									});
						});

		$("#testform")
				.on(
						"submit",
						function(e) {
							e.preventDefault();
							var url = ossApiEndpoint + "/check/sml";
							var payload = $("#input").val();
							var format = $("input:radio[name='format']:checked")
									.val();
							console.log("Sending request to " + url
									+ ", response format " + format
									+ ", data: " + payload);

							$
									.ajax(
											{
												type : "POST",
												cache : false,
												url : url,
												data : payload, //$(this).serialize(),
												dataType : format, // "json", //"xml",
												success : function(data) {
													console.log(data);
													var output;
													if (format == "json") {
														output = JSON
																.stringify(data);
													} else if (format == "xml") {
														output = data.xml ? data.xml
																: (new XMLSerializer())
																		.serializeToString(data);
													} else {
														output = data;
													}
													$("#output").val(output);

													var status = data.status;
													if (status == "valid") {
														$("#resultGroup")
																.addClass(
																		"has-success");
													}
													if (status == "invalid") {
														$("#resultGroup")
																.addClass(
																		"has-error");
													}
												}
											})
									.fail(
											function(data) {
												console.log(data);
												$("#output")
														.val(
																data.status
																		+ " "
																		+ data.statusText
																		+ "\n\n"
																		+ data.responseText);
											});

						});
	</script>

</body>
</html>