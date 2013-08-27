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
<%@page
	import="org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria"%>

<jsp:useBean id="getSensorStatus"
	class="org.n52.sir.client.GetSensorStatusBean" scope="page" />

<jsp:setProperty property="*" name="getSensorStatus" />

<%
    if (request.getParameter("build") != null) {
        getSensorStatus.buildRequest();
    }

    if (request.getParameter("sendRequest") != null) {
        getSensorStatus.sendRequest(getSensorStatus.getRequestString());
    }
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Get Sensor Status Request</title>

<jsp:include page="htmlHead.jsp"></jsp:include>

</head>
<body onload="load()">

	<div id="content"><jsp:include page="header.jsp" />
		<jsp:include page="../menu.jsp" />

		<div id="pageContent">

			<h2>Get Sensor Status Request</h2>

			<p>The status request can contain either sensor identification or
				search criteria.</p>

			<form action="getSensorStatus.jsp" method="post">

				<ul class="inputTablesList">
					<li>
						<p>Sensor Identification:</p>
						<table>
							<tr>
								<td colspan="2">Sensor Identification can be done either by
									sensor ID in SIR or by service reference (using three
									parameters):</td>
							</tr>
							<tr>
								<td class="inputTitle">Sensor ID In SIR</td>
								<td><input type="text" class="inputField"
									name="sensorIDInSIR" size="100"
									value="<%=getSensorStatus.getSensorIDInSIR()%>" /></td>
							</tr>
							<tr>
								<td colspan="2">&nbsp;</td>
							</tr>
							<tr>
								<td class="inputTitle">Service URL</td>
								<td><input type="text" class="inputField" name="serviceURL"
									size="100" value="<%=getSensorStatus.getServiceURL()%>" /></td>
							</tr>
							<tr>
								<td class="inputTitle">Service Type</td>
								<td><input type="text" class="inputField"
									name="serviceType" size="100"
									value="<%=getSensorStatus.getServiceType()%>" /></td>
							</tr>
							<tr>
								<td class="inputTitle">Service Specific SensorID</td>
								<td><input type="text" class="inputField"
									name="serviceSpecificSensorID" size="100"
									value="<%=getSensorStatus.getServiceSpecificSensorID()%>" /></td>
							</tr>
						</table>
					</li>
					<li>
						<p>Search Criteria:</p>
						<table>
							<tr>
								<td colspan="2">Service Criteria:</td>
							</tr>
							<tr>
								<td class="inputTitle">Service URL</td>
								<td><input type="text" class="inputField"
									name="serviceCriteriaURL"
									value="<%=getSensorStatus.getServiceCriteriaURL()%>" size="100" /></td>
							</tr>
							<tr>
								<td class="inputTitle">Service Type</td>
								<td><select name="serviceCriteriaType">
										<option selected="selected">SOS</option>
								</select></td>
							</tr>
							<tr>
								<td class="inputTitle">Search Text (seperated by ';')</td>
								<td><input type="text" class="inputField" name="searchText"
									value="<%=getSensorStatus.getSearchText()%>" size="100" /></td>
							</tr>
							<tr>
								<td colspan="2">Phenomenon (multiple possible manually, SOR
									parameters are optional):</td>
							</tr>
							<tr>
								<td class="inputTitle">Phenonemon Name</td>
								<td><input type="text" class="inputField" name="phenomenon"
									value="<%=getSensorStatus.getPhenomenonName()%>" size="100" /></td>
							</tr>
							<tr>
								<td class="inputTitle">Matching Type</td>
								<td><select name="matchingTypeString" class="inputField">
										<%
										    SirSearchCriteria_Phenomenon.SirMatchingType[] types = getSensorStatus.getMatchingTypes();
										    for (SirSearchCriteria_Phenomenon.SirMatchingType t : types) {
										%>
										<option value="<%=t%>"><%=t.toString()%></option>
										<%
										    }
										%>
								</select></td>
							</tr>
							<tr>
								<td class="inputTitle">Search Depth</td>
								<td><select name="searchDepth" class="inputField">
										<option value="1" selected="selected">1</option>
										<option value="2">2</option>
										<option value="3">3</option>
										<option value="4">4</option>
										<option value="5">5</option>
								</select></td>
							</tr>
							<tr>
								<td class="inputTitle">Uom (seperated by ';')</td>
								<td><input type="text" class="inputField" name="uom"
									value="<%=getSensorStatus.getUom()%>" size="100" /></td>
							</tr>
							<tr>
								<td colspan="2">Bounding Box (for ows:BoundingBoxType, e.g.
									'lon lat'):</td>
							</tr>
							<tr>
								<td class="inputTitle">Upper Corner</td>
								<td><input type="text" class="inputField"
									name="upperCorner"
									value="<%=getSensorStatus.getUpperCorner()%>" size="30" /></td>
							</tr>
							<tr>
								<td class="inputTitle">Lower Corner</td>
								<td><input type="text" class="inputField"
									name="lowerCorner"
									value="<%=getSensorStatus.getLowerCorner()%>" size="30" /></td>
							</tr>
							<tr>
								<td colspan="2">Time period (for gml:TimeInstantType, e.g.
									'1990-01-01 00:00:00'):</td>
							</tr>
							<tr>
								<td class="inputTitle">Start</td>
								<td><input type="text" class="inputField"
									name="timePeriodStart"
									value="<%=getSensorStatus.getTimePeriodStart()%>" size="30" /></td>
							</tr>
							<tr>
								<td class="inputTitle">End</td>
								<td><input type="text" class="inputField"
									name="timePeriodEnd"
									value="<%=getSensorStatus.getTimePeriodEnd()%>" size="30" /></td>
							</tr>

						</table>
					</li>
					<li>
						<p>Property Filter</p>
						<table>
							<tr>
								<td class="inputTitle">PropertyName</td>
								<td><input type="text" class="inputField"
									name="propertyName"
									value="<%=getSensorStatus.getPropertyName()%>" size="100" /></td>
							</tr>
							<tr>
								<td class="inputTitle">PropertyConstraint (choose one):</td>
							</tr>
							<tr>
								<td class="inputTitle">Uom Constraint</td>
								<td><input type="text" class="inputField"
									name="uomConstraint"
									value="<%=getSensorStatus.getUomConstraint()%>" size="30" /></td>
							</tr>
							<tr>
								<td class="inputTitle">is equal to</td>
								<td><input type="text" class="inputField" name="isEqualTo"
									value="<%=getSensorStatus.getIsEqualTo()%>" size="30" /></td>
							</tr>
							<tr>
								<td class="inputTitle">is not equal to</td>
								<td><input type="text" class="inputField"
									name="isNotEqualTo"
									value="<%=getSensorStatus.getIsNotEqualTo()%>" size="30" /></td>
							</tr>
							<tr>
								<td class="inputTitle">is greater than</td>
								<td><input type="text" class="inputField"
									name="isGreaterThan"
									value="<%=getSensorStatus.getIsGreaterThan()%>" size="30" /></td>
							</tr>
							<tr>
								<td class="inputTitle">is less than</td>
								<td><input type="text" class="inputField" name="isLessThan"
									value="<%=getSensorStatus.getIsLessThan()%>" size="30" /></td>
							</tr>
							<tr>
								<td class="inputTitle">is greater than or equal to</td>
								<td><input type="text" class="inputField"
									name="isGreaterThanOrEqualTo"
									value="<%=getSensorStatus.getIsGreaterThanOrEqualTo()%>"
									size="30" /></td>
							</tr>
							<tr>
								<td class="inputTitle">is less than or equal to</td>
								<td><input type="text" class="inputField"
									name="isLessThanOrEqualTo"
									value="<%=getSensorStatus.getIsLessThanOrEqualTo()%>" size="30" /></td>
							</tr>
							<tr>
								<td class="inputTitle">is between</td>
								<td><input type="text" class="inputField"
									name="isBetweenLowerBoundary"
									value="<%=getSensorStatus.getIsBetweenLowerBoundary()%>"
									size="30" /> <input type="text" class="inputField"
									name="isBetweenUpperBoundary"
									value="<%=getSensorStatus.getIsBetweenUpperBoundary()%>"
									size="30" /></td>
							</tr>
						</table>
					</li>
				</ul>

				<p>
					<input type="submit" name="build" value="Build request" />
				</p>
			</form>
			<form action="getSensorStatus.jsp" method="post">
				<p class="textareaBorder">
					<textarea id="requestStringArea" class="mediumTextarea"
						name="requestString" rows="10" cols="10"><%=getSensorStatus.getRequestString()%></textarea>
				</p>
				<p>
					<input type="submit" name="sendRequest" value="Send request" />
				</p>
			</form>
			<p class="textareaBorder">
				<textarea id="responseStringArea" class="mediumTextarea" rows="10"
					cols="10"><%=getSensorStatus.getResponseString()%></textarea>
			</p>

		</div>
	</div>
</body>
</html>