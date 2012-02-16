<%--

    ﻿Copyright (C) 2012
    by 52 North Initiative for Geospatial Open Source Software GmbH

    Contact: Andreas Wytzisk
    52 North Initiative for Geospatial Open Source Software GmbH
    Martin-Luther-King-Weg 24
    48155 Muenster, Germany
    info@52north.org

    This program is free software; you can redistribute and/or modify it under
    the terms of the GNU General Public License version 2 as published by the
    Free Software Foundation.

    This program is distributed WITHOUT ANY WARRANTY; even without the implied
    WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public License along with
    this program (see gnu-gpl v2.txt). If not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
    visit the Free Software Foundation web page, http://www.fsf.org.

--%>
<?xml version="1.0" encoding="utf-8"?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<%@page import="org.n52.sir.client.Client"%>

<jsp:useBean id="insertSensorInfo"
	class="org.n52.sir.client.InsertSensorInfoBean" scope="page" />
<jsp:setProperty property="*" name="insertSensorInfo" />

<%
    if (request.getParameter("build") != null) {
        insertSensorInfo.buildRequest();
    }

    if (request.getParameter("sendRequest") != null) {
        insertSensorInfo.setResponseString(Client.sendPostRequest(insertSensorInfo.getRequestString()));
    }
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Insert Sensor Info Request</title>

<jsp:include page="htmlHead.jsp"></jsp:include>

</head>
<body onload="load()">

<div id="content"><jsp:include page="header.jsp" /> <jsp:include
	page="../menu.jsp" />

<div id="pageContent">

<h2>Insert Sensor Info Request</h2>

<form action="insertSensorInfo.jsp" method="post">

<ul class="inputTablesList">
	<li>
	<p>Info to be inserted (mandatory; multiple elements can be
	inserted manually.) This can contain either sensor description elements
	(x)or service reference elements, this client currenly only supports
	adding one of these:</p>
	</li>
	<li>
	<table style="">
		<tr>
			<td colspan="2">Sensor description information
			(mandatory):</td>
		</tr>
		<tr>
			<td class="inputTitle">Sensor Description (Has to be a valid <code>sml:SensorML</code>
			document containing a <code>sml:member</code> with a <code>sml:System</code>.)</td>
			<td>
			<p class="textareaBorder"><textarea id="descriptionStringArea"
				class="mediumTextarea" name="sensorDescription"
				style="width: 400px;" rows="10" cols="10"><%=insertSensorInfo.getSensorDescription()%></textarea></p>
			</td>
		</tr>
		<tr>
			<td colspan="2">Service Reference to be used for identification (optional; multiple
			service references can be inserted manually.):</td>
		</tr>
		<tr>
			<td class="inputTitle">Service URL</td>
			<td><input type="text" name="serviceInfosServiceURL"
				value="<%=insertSensorInfo.getServiceInfosServiceURL()%>" /></td>
		</tr>
		<tr>
			<td class="inputTitle">Service Type</td>
			<td><input type="text" name="serviceInfosServiceType"
				value="<%=insertSensorInfo.getServiceInfosServiceType()%>" /></td>
		</tr>
		<tr>
			<td class="inputTitle">Service Specific SensorID</td>
			<td><input type="text"
				name="addDescr_serviceInfosServiceSpecificSensorID"
				value="<%=insertSensorInfo.getServiceInfosServiceSpecificSensorID()%>" /></td>
		</tr>
	</table>
	</li>

	<li>
	<table style="">
		<tr>
			<td class="inputTitle" colspan="2">Service reference information (all
			mandatory):</td>
		</tr>
		<tr>
			<td class="inputTitle">Sensor ID in SIR</td>
			<td><input type="text" name="sensorIDinSIR"
				value="<%=insertSensorInfo.getSensorIDinSIR()%>" /></td>
		</tr>
		<tr>
			<td class="inputTitle">Service Reference</td>
		</tr>
		<tr>
			<td class="inputTitle">Service URL</td>
			<td><input type="text" name="addRefURL"
				value="<%=insertSensorInfo.getAddRefURL()%>" /></td>
		</tr>
		<tr>
			<td class="inputTitle">Service Type</td>
			<td><input type="text" name="addRefType"
				value="<%=insertSensorInfo.getAddRefType()%>" /></td>
		</tr>
		<tr>
			<td class="inputTitle">Service Specific SensorID</td>
			<td><input type="text"
				name="addRefSensorID"
				value="<%=insertSensorInfo.getAddRefSensorID()%>" /></td>
		</tr>
	</table>
	</li>

</ul>

<p><input type="submit" name="build" value="Build request" /></p>
</form>

<form action="insertSensorInfo.jsp" method="post">
<p class="textareaBorder"><textarea id="requestStringArea"
	class="mediumTextarea" name="requestString" rows="10" cols="10"><%=insertSensorInfo.getRequestString()%></textarea></p>
<p><input type="submit" name="sendRequest" value="Send request" /></p>
</form>

<p class="textareaBorder"><textarea id="responseStringArea"
	class="mediumTextarea" rows="10" cols="10"><%=insertSensorInfo.getResponseString()%></textarea></p>

</div>
</div>
</body>
</html>