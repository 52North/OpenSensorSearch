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
<%@page import="org.n52.sir.datastructure.SirSearchCriteria_Phenomenon"%>

<jsp:useBean id="searchSensor"
	class="org.n52.sir.client.SearchSensorBean" scope="page" />
<jsp:setProperty property="*" name="searchSensor" />

<%
    if (request.getParameter("build") != null) {
        searchSensor.buildRequest();
    }

    if (request.getParameter("sendRequest") != null) {
        searchSensor.setResponseString(searchSensor.sendRequest(searchSensor.getRequestString()));
    }
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Search Sensor Request</title>

<jsp:include page="htmlHead.jsp"></jsp:include>

</head>
<body onload="load()">

	<div id="content"><jsp:include page="header.jsp" />
		<jsp:include page="../menu.jsp" />

		<div id="pageContent">

			<h2>Search Sensor Request</h2>

			<form action="searchSensor.jsp" method="post">

				<ul class="inputTablesList">
					<li>
						<p>Search with sensor identification, using either by ID or by
							service reference:</p>
						<table style="">
							<tr>
								<td colspan="2">Search by ID:</td>
								<td />
							</tr>
							<tr>
								<td class="inputTitle">Sensor ID In SIR</td>
								<td><input type="text" class="inputField"
									name="sensorIDInSIRValue" size="100"
									value="<%=searchSensor.getSensorIDInSIRValue()%>" /></td>
							</tr>
							<tr>
								<td colspan="2">Search by service reference:</td>
								<td />
							</tr>
							<tr>
								<td class="inputTitle">Service URL</td>
								<td><input type="text" class="inputField" name="serviceURL"
									size="100" value="<%=searchSensor.getServiceURL()%>" /></td>
							</tr>
							<tr>
								<td class="inputTitle">Service Type</td>
								<td><input type="text" class="inputField"
									name="serviceType" size="100"
									value="<%=searchSensor.getServiceType()%>" /></td>
							</tr>
							<tr>
								<td class="inputTitle">Service Specific SensorID</td>
								<td><input type="text" class="inputField"
									name="serviceSpecificSensorID" size="100"
									value="<%=searchSensor.getServiceSpecificSensorID()%>" /></td>
							</tr>
						</table>
					</li>

					<li>
						<p>Search with search criteria (every group is optional):</p>
						<table style="">
							<tr>
								<td colspan="2">Service Criteria (multiple elements
									possible):</td>
							</tr>
							<tr>
								<td class="inputTitle">Service URL</td>
								<td><input type="text" class="inputField"
									name="serviceCriteriaURL"
									value="<%=searchSensor.getServiceCriteriaURL()%>" size="100" /></td>
							</tr>
							<tr>
								<td class="inputTitle">Service type</td>
								<td><input type="text" class="inputField"
									name="serviceCriteriaType"
									value="<%=searchSensor.getServiceCriteriaType()%>" size="100" /></td>
							</tr>
							<tr>
								<td class="inputTitle">Search text (multiple elements
									seperated by ';')</td>
								<td><input type="text" class="inputField" name="searchText"
									value="<%=searchSensor.getSearchText()%>" size="100" /></td>
							</tr>
							<tr>
								<td colspan="2">Phenomenon (with optional SOR parameters):</td>
							</tr>
							<tr>
								<td class="inputTitle">Phenomenon Name</td>
								<td><input type="text" class="inputField"
									name="phenomenonName"
									value="<%=searchSensor.getPhenomenonName()%>" size="100" /></td>
							</tr>
							<tr>
								<td class="inputTitle">SOR URL</td>
								<td><input type="text" class="inputField" name="sorUrl"
									value="<%=searchSensor.getSorUrl()%>" size="100" /></td>
							</tr>
							<tr>
								<td class="inputTitle">SOR Matching Type</td>
								<td><select name="sorMatchingType" class="inputField">
										<%
										    SirSearchCriteria_Phenomenon.SirMatchingType[] types = searchSensor.getMatchingTypes();
										    for (SirSearchCriteria_Phenomenon.SirMatchingType t : types) {
										%>
										<option value="<%=t%>"><%=t.toString()%></option>
										<%
										    }
										%>
								</select></td>
							</tr>
							<tr>
								<td class="inputTitle">SOR Search Depth</td>
								<td><input type="text" class="inputField"
									name="sorSearchDepth"
									value="<%=searchSensor.getSorSearchDepth()%>" size="100" /></td>
							</tr>
							<tr>
								<td class="inputTitle">Uom (multiple elements seperated by
									';')</td>
								<td><input type="text" class="inputField" name="uom"
									value="<%=searchSensor.getUom()%>" size="100" /></td>
							</tr>
							<tr>
								<td colspan="2">Bounding (format: 'lat lon'):</td>
							</tr>
							<tr>
								<td class="inputTitle">Upper Corner</td>
								<td><input type="text" class="inputField"
									name="upperCorner" value="<%=searchSensor.getUpperCorner()%>"
									size="30" /></td>
							</tr>
							<tr>
								<td class="inputTitle">Lower Corner</td>
								<td><input type="text" class="inputField"
									name="lowerCorner" value="<%=searchSensor.getLowerCorner()%>"
									size="30" /></td>
							</tr>
							<tr>
								<td colspan="2">Time Period:</td>
							</tr>
							<tr>
								<td class="inputTitle">Start</td>
								<td><input type="text" class="inputField"
									name="timePeriodStart"
									value="<%=searchSensor.getTimePeriodStart()%>" size="30" /></td>
							</tr>
							<tr>
								<td class="inputTitle">End</td>
								<td><input type="text" class="inputField"
									name="timePeriodEnd"
									value="<%=searchSensor.getTimePeriodEnd()%>" size="30" /></td>
							</tr>

						</table>
					</li>

					<li>
						<table>
							<tr>
								<td class="inputTitle">Simple Response</td>
								<td><input type="checkbox" class="inputField"
									name="simpleResponse" /></td>
							</tr>
						</table>
					</li>
				</ul>

				<p>
					<input type="submit" name="build" value="Build request" />
				</p>
			</form>
			<form action="searchSensor.jsp" method="post">
				<p class="textareaBorder">
					<textarea id="requestStringArea" class="mediumTextarea"
						name="requestString" rows="10" cols="10"><%=searchSensor.getRequestString()%></textarea>
				</p>
				<p>
					<input type="submit" name="sendRequest" value="Send request" />
				</p>
			</form>
			<p class="textareaBorder">
				<textarea id="responseStringArea" class="mediumTextarea" rows="10"
					cols="10"><%=searchSensor.getResponseString()%></textarea>
			</p>

		</div>
	</div>
</body>
</html>