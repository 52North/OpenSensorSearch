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

<%@page import="org.n52.sor.client.Client"%>

<jsp:useBean id="insertDefinition"
	class="org.n52.sor.client.InsertDefinitionBean" scope="page" />
<jsp:setProperty property="*" name="insertDefinition" />

<%
	if (request.getParameter("build") != null) {
		insertDefinition.buildRequest();
	}

	if (request.getParameter("sendRequest") != null) {
		insertDefinition.setResponseString(Client
				.sendPostRequest(insertDefinition.getRequestString()));
	}
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Insert Definition Request</title>
<jsp:include page="htmlHead.jsp"></jsp:include>
</head>

<body onload="load()">

<div id="content"><jsp:include page="header.jsp"></jsp:include> <jsp:include
	page="menu.jsp"></jsp:include>
<div id="pageContent">
<h2>Insert Definition Request:</h2>

<form action="insertDefinition.jsp" method="post">

<ul class="inputTablesList">
	<li>
	<table style="">
		<tr>
			<td class="inputTitle">Phenomenon:</td>
			<td><textarea name="phenomenon" cols="80" rows="10"><%= insertDefinition.getPhenomenon() %></textarea></td>
		</tr>
	</table>
	</li>
</ul>

<p><input type="submit" name="build" value="Build request" /></p>
</form>

<form action="insertDefinition.jsp" method="post">
<p class="textareaBorder"><textarea id="requestStringArea"
	class="smallTextarea" name="requestString" cols="10" rows="10"><%=insertDefinition.getRequestString()%></textarea></p>
<p><input type="submit" name="sendRequest" value="Send request" /></p>
</form>

<p class="textareaBorder"><textarea id="responseStringArea"
	class="largeTextarea" cols="10" rows="10"><%=insertDefinition.getResponseString()%></textarea></p>

</div>
</div>
</body>
</html>