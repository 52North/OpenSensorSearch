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

<%@page import="org.n52.sor.request.SorGetMatchingDefinitionsRequest"%>
<%@page import="org.n52.sor.client.Client"%>

<jsp:useBean id="getMatchingDefinitions"
	class="org.n52.sor.client.GetMatchingDefinitionsBean" scope="page" />
<jsp:setProperty property="*" name="getMatchingDefinitions" />

<%
	if (request.getParameter("build") != null) {
		getMatchingDefinitions.buildRequest();
	}

	if (request.getParameter("sendRequest") != null) {
		getMatchingDefinitions.setResponseString(Client
				.sendPostRequest(getMatchingDefinitions
						.getRequestString()));
	}
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Get Matching Definitions Request</title>
<jsp:include page="htmlHead.jsp"></jsp:include>
</head>

<body onload="load()">

<div id="content"><jsp:include page="header.jsp" /> <jsp:include
	page="menu.jsp"></jsp:include>
<div id="pageContent">
<h2>Get Matching Definitions Request:</h2>

<form action="getMatchingDefinitions.jsp" method="post">

<ul class="inputTablesList">
	<li>
	<table style="">
		<tr>
			<td class="inputTitle">Input URI:</td>
			<td>
				<select id="phenomenonList" name="inputURI" size="10">
				<%
				String[] uris = getMatchingDefinitions.getAvailableURIs();
				for(String s : uris) {
			    %>
				<option value="<%=s%>"><%=s%></option>
				<%
				}
				%>
				</select>
			</td>
		</tr>
		<tr>
			<td class="inputTitle">Matching Type:</td>
			<td>
				<select name="matchingTypeString" class="inputField">
					<%
					SorGetMatchingDefinitionsRequest.SorMatchingType[] types = getMatchingDefinitions.getMatchingTypes();
					for(SorGetMatchingDefinitionsRequest.SorMatchingType t : types) {
				    %>
					<option value="<%=t%>"><%=t.toString()%></option>
					<%
					}
					%>
				</select>
			</td>
		</tr>
		<tr>
			<td class="inputTitle">Search Depth:</td>
			<td><select name="searchDepth" class="inputField">
				<option value="1" selected="selected">1</option>
				<option value="2">2</option>
				<option value="3">3</option>
				<option value="4">4</option>
				<option value="5">5</option>
			</select></td>
		</tr>
	</table>
	</li>
</ul>

<p><input type="submit" name="build" value="Build request" /></p>
</form>
<form action="getMatchingDefinitions.jsp" method="post">
<p class="textareaBorder"><textarea id="requestStringArea"
	class="smallTextarea" name="requestString" rows="10" cols="10"><%=getMatchingDefinitions.getRequestString()%></textarea></p>
<p><input type="submit" name="sendRequest" value="Send request" /></p>
</form>
<p class="textareaBorder"><textarea id="responseStringArea"
	class="mediumTextarea" rows="10" cols="10"><%=getMatchingDefinitions.getResponseString()%></textarea></p>
</div>
</div>
</body>
</html>