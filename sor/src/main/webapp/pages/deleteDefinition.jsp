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

<jsp:useBean id="deleteDefinition"
	class="org.n52.sor.client.DeleteDefinitionBean" scope="page" />
<jsp:setProperty property="*" name="deleteDefinition" />

<%
    if (request.getParameter("build") != null) {
        deleteDefinition.buildRequest();
    }

    if (request.getParameter("sendRequest") != null) {
        deleteDefinition.setResponseString(Client.sendPostRequest(deleteDefinition.getRequestString()));
    }
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Delete Definition Request</title>
<jsp:include page="htmlHead.jsp"></jsp:include>
</head>

<body onload="load()">

<div id="content"><jsp:include page="header.jsp"></jsp:include> <jsp:include
	page="menu.jsp"></jsp:include>
<div id="pageContent">
<h2>Delete Definition Request:</h2>

<form action="deleteDefinition.jsp" method="post">

<ul class="inputTablesList">
	<li>
	<table style="">
		<tr>
			<td class="inputTitle">Definition URI:</td>
			<td><select id="phenomenonList" name="definitionURI" size="10">
				<%
				    String[] uris = deleteDefinition.getAvailableURIs();
				    for (String s : uris) {
				%>
				<option value="<%=s%>"><%=s%></option>
				<%
				    }
				%>
			</select></td>
		</tr>
	</table>
	</li>
</ul>

<p><input type="submit" name="build" value="Build request" /></p>
</form>

<form action="deleteDefinition.jsp" method="post">
<p class="textareaBorder"><textarea id="requestStringArea"
	class="smallTextarea" name="requestString" rows="10" cols="10"><%=deleteDefinition.getRequestString()%></textarea></p>
<p><input type="submit" name="sendRequest" value="Send request" /></p>
</form>

<p class="textareaBorder"><textarea id="responseStringArea"
	class="largeTextarea" rows="10" cols="10"><%=deleteDefinition.getResponseString()%></textarea></p>

</div>
</div>
</body>
</html>