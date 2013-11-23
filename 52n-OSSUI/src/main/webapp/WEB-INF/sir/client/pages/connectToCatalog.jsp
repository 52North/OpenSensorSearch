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

<jsp:useBean id="connectToCatalog"
	class="org.n52.sir.client.ConnectToCatalogBean" scope="page" />
<jsp:setProperty property="*" name="connectToCatalog" />

<%
    if (request.getParameter("build") != null) {
        connectToCatalog.buildRequest();
    }

    if (request.getParameter("sendRequest") != null) {
        connectToCatalog.sendRequest(connectToCatalog.getRequestString());
    }
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Connect To Catalog Request</title>

<jsp:include page="htmlHead.jsp"></jsp:include>

</head>
<body onload="load()">

	<div id="content"><jsp:include page="header.jsp" />
		<jsp:include page="../menu.jsp" />

		<div id="pageContent">

			<h2>ConnectToCatalogRequest</h2>

			<form action="connectToCatalog.jsp" method="post">

				<ul class="inputTablesList">
					<li>
						<table>
							<tr>
								<td class="inputTitle">Catalog URL:</td>
								<td><input type="text" class="inputField" name="catalogUrl"
									size="30" value="<%=connectToCatalog.getCatalogUrl()%>" /></td>

							</tr>
							<tr>
								<td class="inputTitle">Push Interval:</td>
								<td><input type="text" name="pushInterval" size="6"
									value="<%=connectToCatalog.getPushInterval()%>" />(seconds,
									'0' for single catalog connection)</td>
							</tr>
						</table>
					</li>
				</ul>

				<p>
					<input type="submit" name="build" value="Build request" />
				</p>
			</form>

			<form action="connectToCatalog.jsp" method="post">
				<p class="textareaBorder">
					<textarea id="requestStringArea" name="requestString"
						class="smallTextarea" rows="10" cols="10"><%=connectToCatalog.getRequestString()%></textarea>
				</p>
				<p>
					<input type="submit" name="sendRequest" value="Send request" />
				</p>
			</form>

			<p class="textareaBorder">
				<textarea id="responseStringArea" class="smallTextarea" rows="10"
					cols="10"><%=connectToCatalog.getResponseString()%></textarea>
			</p>

		</div>

	</div>
</body>
</html>