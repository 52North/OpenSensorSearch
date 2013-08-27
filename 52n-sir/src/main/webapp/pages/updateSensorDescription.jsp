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
<?xml version="1.0" encoding="utf-8"?>

<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<%@page import="org.n52.sir.client.Client"%>

<jsp:useBean id="updateSensorDescr"
	class="org.n52.sir.client.UpdateSensorDescriptionBean" scope="page" />
<jsp:setProperty property="*" name="updateSensorDescr" />

<%
    if (request.getParameter("build") != null) {
        updateSensorDescr.buildRequest();
    }

    if (request.getParameter("sendRequest") != null) {
        updateSensorDescr.setResponseString(updateSensorDescr.sendRequest(updateSensorDescr.getRequestString()));
    }
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Update Sensor Description Request</title>

<jsp:include page="htmlHead.jsp"></jsp:include>

</head>
<body onload="load()">

	<div id="content"><jsp:include page="header.jsp" />
		<jsp:include page="../menu.jsp" />

		<div id="pageContent">

			<h2>Update Sensor Description Request</h2>

			<form action="updateSensorDescription.jsp" method="post">

				<ul class="inputTablesList">
					<li>
						<p>Sensor identification (mandatory; provide only one choice,
							and only if deleting/updating a sensor!):</p>
						<table style="">
							<tr>
								<td colspan="2">by SensorID:</td>
								<td />
							</tr>
							<tr>
								<td class="inputTitle">Sensor ID In SIR</td>
								<td><input type="text" name="sensorIDinSIR"
									class="inputField"
									value="<%=updateSensorDescr.getSensorIDinSIR()%>" /></td>
							</tr>
						</table>
					</li>
					<li>
						<table style="">
							<tr>
								<td colspan="2">by Service Description:</td>
								<td />
							</tr>
							<tr>
								<td class="inputTitle">Service URL</td>
								<td><input type="text" name="serviceURL" class="inputField"
									value="<%=updateSensorDescr.getServiceURL()%>" /></td>
							</tr>
							<tr>
								<td class="inputTitle">Service Type</td>
								<td><input type="text" name="serviceType"
									class="inputField"
									value="<%=updateSensorDescr.getServiceType()%>" /></td>
							</tr>
							<tr>
								<td class="inputTitle">Service Specific SensorID</td>
								<td><input type="text" name="serviceSpecificSensorID"
									class="inputField"
									value="<%=updateSensorDescr.getServiceSpecificSensorID()%>" /></td>
							</tr>
						</table>
					</li>
					<li>
						<p>Sensor Description (mandatory):</p>
						<table style="">
							<tr>
								<td colspan="2"></td>
							</tr>
							<tr>
								<td colspan="2">Needs to be a valid <code>sml:SensorML</code>
									document containing one <code>sml:member</code> with one <code>sml:System</code>.
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<p class="textareaBorder">
										<textarea id="descriptionStringArea" class="mediumTextarea"
											name="sensorDescription" style="width: 600px;" rows="10"
											cols="10"><%=updateSensorDescr.getSensorDescription()%></textarea>
									</p>
								</td>
							</tr>
						</table>
					</li>
				</ul>

				<p>
					<input type="submit" name="build" value="Build request" />
				</p>
			</form>

			<form action="updateSensorDescription.jsp" method="post">
				<p class="textareaBorder">
					<textarea id="requestStringArea" class="mediumTextarea"
						name="requestString" rows="10" cols="10"><%=updateSensorDescr.getRequestString()%></textarea>
				</p>
				<p>
					<input type="submit" name="sendRequest" value="Send request" />
				</p>
			</form>

			<p class="textareaBorder">
				<textarea id="responseStringArea" class="mediumTextarea" rows="10"
					cols="10"><%=updateSensorDescr.getResponseString()%></textarea>
			</p>

		</div>
	</div>
</body>
</html>