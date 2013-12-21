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

<jsp:useBean id="getDefinitionURIs"
	class="org.n52.sor.client.GetDefinitionURIsBean" scope="page" />
<jsp:setProperty property="*" name="getDefinitionURIs" />

<%
	if (request.getParameter("build") != null) {
	    getDefinitionURIs.buildRequest();
	}

	if (request.getParameter("sendRequest") != null) {
	    getDefinitionURIs.setResponseString(Client
				.sendPostRequest(getDefinitionURIs.getRequestString()));
	}
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Get Definition URIs Request</title>
<jsp:include page="htmlHead.jsp"></jsp:include>
</head>
<body onload="load()">

<div id="content"><jsp:include page="header.jsp"></jsp:include> <jsp:include
	page="menu.jsp"></jsp:include>
<div id="pagecontent">
<h2>Get Definition URIs Request:</h2>

<form action="getDefinitionURIs.jsp" method="post">

<ul class="inputTablesList">
	<li>
	<table style="">
		<tr>
			<td class="inputTitle">Maximum number of results:</td>
			<td><input type="text" class="inputField"
				name="maxNumberOfResults" /></td>
		</tr>
		<tr>
			<td class="inputTitle">Start result element:</td>
			<td><input type="text" class="inputField"
				name="startResultElement" /></td>
		</tr>
		<tr>
			<td class="inputTitle">Search string:</td>
			<td><input type="text" class="inputField" name="searchString" /></td>
		</tr>
	</table>
	</li>
</ul>

<p><input type="submit" name="build" value="Build request" /></p>
</form>

<form action="getDefinitionURIs.jsp" method="post">
<p class="textareaBorder"><textarea id="requestStringArea"
	class="smallTextarea" name="requestString" rows="10" cols="10"><%=getDefinitionURIs.getRequestString()%></textarea></p>
<p><input type="submit" name="sendRequest" value="Send request" /></p>
</form>

<p class="textareaBorder"><textarea id="responseStringArea"
	class="largeTextarea" rows="10" cols="10"><%=getDefinitionURIs.getResponseString()%></textarea></p>

</div>
</div>
</body>
</html>