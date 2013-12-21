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

<%@page import="org.n52.sor.client.Client"%>

<jsp:useBean id="getCapabilities"
	class="org.n52.sor.client.GetCapabilitiesBean" scope="page" />
<jsp:setProperty property="*" name="getCapabilities" />

<%
    if (request.getParameter("build") != null) {
        getCapabilities.buildRequest();
    }

    if (request.getParameter("sendRequest") != null) {
        getCapabilities.setResponseString(Client.sendPostRequest(getCapabilities.getRequestString()));
    }
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Get Capabilities Request</title>
<jsp:include page="htmlHead.jsp" />
</head>
<body onload="load()">

<div id="content"><jsp:include page="header.jsp" /> <jsp:include
	page="menu.jsp"></jsp:include>
<div id="pageContent">
<h2>Get Capabilities Request:</h2>

<form action="getCapabilities.jsp" method="post">

<ul class="inputTablesList">
	<li>
	<table>
		<tr>
			<td class="inputTitle">Service:</td>
			<td><select name="service">
				<option selected="selected">SOR</option>
			</select></td>
		</tr>
		<tr>
			<td class="inputTitle">Update Sequence:</td>
			<td><input type="text" class="inputField" name="updateSequence"
				value="<%=getCapabilities.getUpdateSequence()%>" /></td>
		</tr>
		<tr>
			<td class="inputTitle">Accept Versions:</td>
			<td><input type="text" class="inputField" name="acceptVersions"
				value="<%=getCapabilities.getAcceptVersions()%>" /></td>
		</tr>
	</table>
	</li>
	<li>
	<p>Sections:</p>
	<table>
		<tr>
			<td class="inputTitle">Service Identification:</td>
			<td><input type="checkbox" class="inputField"
				name="serviceIdentification"
				<%=getCapabilities.isServiceIdentification() ? "CHECKED" : ""%> /></td>
		</tr>
		<tr>
			<td class="inputTitle">Service Provider:</td>
			<td><input type="checkbox" name="serviceProvider"
				<%=getCapabilities.isServiceProvider() ? "CHECKED" : ""%> /></td>
		</tr>
		<tr>
			<td class="inputTitle">Operations Metadata:</td>
			<td><input type="checkbox" name="operationsMetadata"
				<%=getCapabilities.isOperationsMetadata() ? "CHECKED" : ""%> /></td>
		</tr>
		<tr>
			<td class="inputTitle">Contents:</td>
			<td><input type="checkbox" name="contents"
				<%=getCapabilities.isContents() ? "CHECKED" : ""%> /></td>
		</tr>
		<tr>
			<td class="inputTitle">All:</td>
			<td><input type="checkbox" name="all"
				<%=getCapabilities.isAll() ? "CHECKED" : ""%> /></td>
		</tr>
		<tr>
			<td class="inputTitle">Accept Formats:</td>
			<td><input type="text" class="inputField" disabled="disabled"
				value="not implemented yet" /></td>
		</tr>
	</table>
	</li>
</ul>

<p><input type="submit" name="build" value="Build request" /></p>
</form>

<form action="getCapabilities.jsp" method="post">
<p class="textareaBorder"><textarea id="requestStringArea"
	class="mediumTextarea" name="requestString" rows="10" cols="10"><%=getCapabilities.getRequestString()%></textarea></p>
<p><input type="submit" name="sendRequest" value="Send request" /></p>
</form>
<p class="textareaBorder"><textarea id="responseStringArea"
	class="hugeTextarea" rows="10" cols="10"><%=getCapabilities.getResponseString()%></textarea></p>
</div>
</div>
</body>
</html>