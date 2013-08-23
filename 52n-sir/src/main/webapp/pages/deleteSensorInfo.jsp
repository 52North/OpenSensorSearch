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
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<%@page import="org.n52.sir.client.Client"%>

<jsp:useBean id="deleteSensorInfo"
	class="org.n52.sir.client.DeleteSensorInfoBean" scope="page" />
<jsp:setProperty property="*" name="deleteSensorInfo" />

<%
    if (request.getParameter("build") != null) {
        deleteSensorInfo.buildRequest();
    }

    if (request.getParameter("sendRequest") != null) {
        deleteSensorInfo.sendRequest(deleteSensorInfo.getRequestString());
    }
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Delete Sensor Info Request</title>

<jsp:include page="htmlHead.jsp"></jsp:include>

</head>
<body onload="load()">

	<div id="content"><jsp:include page="header.jsp" />
		<jsp:include page="../menu.jsp" />

		<div id="pageContent">

			<h2>Delete Sensor Info Request</h2>

			<form action="deleteSensorInfo.jsp" method="post">

				<ul class="inputTablesList">
					<li>
						<p>Sensor identification (mandatory; provide only one choice):</p>
						<table style="">
							<tr>
								<td colspan="2">by SensorID:</td>
							</tr>
							<tr>
								<td class="inputTitle">Sensor ID In SIR</td>
								<td><input type="text" name="sensorIDinSIR"
									class="inputField"
									value="<%=deleteSensorInfo.getSensorIDinSIR()%>" /></td>
							</tr>
							<tr>
								<td colspan="2">by Service Description:</td>
							</tr>
							<tr>
								<td class="inputTitle">Service URL</td>
								<td><input type="text" name="serviceURL" class="inputField"
									value="<%=deleteSensorInfo.getServiceURL()%>" /></td>
							</tr>
							<tr>
								<td class="inputTitle">Service Type</td>
								<td><input type="text" name="serviceType"
									class="inputField"
									value="<%=deleteSensorInfo.getServiceType()%>" /></td>
							</tr>
							<tr>
								<td class="inputTitle">Service Specific SensorID</td>
								<td><input type="text" name="serviceSpecificSensorID"
									class="inputField"
									value="<%=deleteSensorInfo.getServiceSpecificSensorID()%>" /></td>
							</tr>
						</table>
					</li>
					<li>
						<p>Either Delete Sensor or Service Information that is to be
							deleted:</p>
						<table style="">
							<tr>
								<td colspan="2">Delete Sensor:</td>
							</tr>
							<tr>
								<td class="inputTitle">Delete</td>
								<td><input type="checkbox" name="deleteSensor"
									<%=deleteSensorInfo.isDeleteSensor() ? "CHECKED" : ""%> /></td>
							</tr>
							<tr>
								<td colspan="2">Service Info:</td>
							</tr>
							<tr>
								<td class="inputTitle">Service URL</td>
								<td><input type="text" name="deleteRefURL"
									value="<%=deleteSensorInfo.getDeleteRefURL()%>" /></td>
							</tr>
							<tr>
								<td class="inputTitle">Service Type</td>
								<td><input type="text" name="deleteRefType"
									value="<%=deleteSensorInfo.getDeleteRefType()%>" /></td>
							</tr>
							<tr>
								<td class="inputTitle">Service Specific SensorID</td>
								<td><input type="text" name="deleteRefSensorID"
									value="<%=deleteSensorInfo.getDeleteRefSensorID()%>" /></td>
							</tr>
						</table>
					</li>
				</ul>

				<p>
					<input type="submit" name="build" value="Build request" />
				</p>
			</form>

			<form action="deleteSensorInfo.jsp" method="post">
				<p class="textareaBorder">
					<textarea id="requestStringArea" class="mediumTextarea"
						name="requestString" rows="10" cols="10"><%=deleteSensorInfo.getRequestString()%></textarea>
				</p>
				<p>
					<input type="submit" name="sendRequest" value="Send request" />
				</p>
			</form>

			<p class="textareaBorder">
				<textarea id="responseStringArea" class="mediumTextarea" rows="10"
					cols="10"><%=deleteSensorInfo.getResponseString()%></textarea>
			</p>

		</div>
	</div>
</body>
</html>