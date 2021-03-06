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

<jsp:include page="htmlHead.jsp"></jsp:include>

</head>

<body>

	<div id="content"><jsp:include page="header.jsp" /><jsp:include
			page="../menu.jsp" />

		<h1>Discovery Profile Validation</h1>

		<p>Test a SensorML document for conformity with SensorML profile
			for discovery. Insert the SensorML description of a sensor:</p>

		<!-- TODO: add listing of available checks based on API endpoint .../api/v1/check -->
		<!-- TODO: add selection of output formats -->

		<form id="testform">
			<p class="textareaBorder">
				<textarea id="input" name="request" class="smallTextarea" rows="10"
					cols="10">...</textarea>
			</p>
			<p>
				<input type="submit" name="testDoc" value="Validate" />
			</p>
		</form>

		<p>Test Result:</p>

		<p class="textareaBorder">
			<textarea id="output" class="largeTextarea" rows="10" cols="10">...</textarea>
		</p>

		<p>
			You can download the used profile here: <a
				href="https://raw.github.com/52North/OpenSensorSearch/master/service/src/main/resources/SensorML_Profile_for_Discovery.sch"
				title="SensorML Profile for Discovery - Schematron">Schematron
				File</a>.
		</p>

	</div>

	<script type="text/javascript">
		$("#testform").on(
				"submit",
				function(e) {
					e.preventDefault();
					var url = ossApiEndpoint + "/check/sml";
					var payload = $("#input").val();
					console.log("Sending request to " + url + " with data: "
							+ payload);
					$.ajax({
						type : "POST",
						cache : false,
						url : url,
						data : payload, //$(this).serialize(),
						dataType : "json", //"xml",
						success : function(data) {
							console.log(data);
							// 									var xmlstr = data.xml ? data.xml
							// 											: (new XMLSerializer())
							// 													.serializeToString(data);
							$("#output").val(JSON.stringify(data));
						}
					}).fail(
							function(data) {
								console.log(data);
								$("#output").val(
										data.status + " " + data.statusText
												+ "\n\n" + data.responseText);
							});

				});
	</script>

</body>
</html>