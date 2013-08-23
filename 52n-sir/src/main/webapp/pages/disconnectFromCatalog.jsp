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

<jsp:useBean id="disconnectFromCatalog"
	class="org.n52.sir.client.DisconnectFromCatalogBean" scope="page" />
<jsp:setProperty property="*" name="disconnectFromCatalog" />

<%
    if (request.getParameter("build") != null) {
        disconnectFromCatalog.buildRequest();
    }

    if (request.getParameter("sendRequest") != null) {
        disconnectFromCatalog.sendRequest(disconnectFromCatalog.getRequestString());
    }
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Disconnect From Catalog Request</title>

<jsp:include page="htmlHead.jsp"></jsp:include>

</head>
<body onload="load()">

	<div id="content"><jsp:include page="header.jsp" />
		<jsp:include page="../menu.jsp" />

		<div id="pageContent">

			<h2>Disconnect From Catalog Request</h2>

			<form action="disconnectFromCatalog.jsp" method="post">

				<ul class="inputTablesList">
					<li>
						<table style="">
							<tr>
								<td class="inputTitle">Catalog URL:</td>
								<td><input type="text" class="inputField" name="catalogUrl"
									size="50" value="<%=disconnectFromCatalog.getCatalogUrl()%>" /></td>
							</tr>
						</table>
					</li>
				</ul>

				<p>
					<input type="submit" name="build" value="Build request" />
				</p>
			</form>

			<form action="disconnectFromCatalog.jsp" method="post">
				<p class="textareaBorder">
					<textarea id="requestStringArea" name="requestString"
						class="smallTextarea" rows="10" cols="10"><%=disconnectFromCatalog.getRequestString()%></textarea>
				</p>
				<p>
					<input type="submit" name="sendRequest" value="Send request" />
				</p>
			</form>

			<p class="textareaBorder">
				<textarea id="responseStringArea" class="mediumTextarea" rows="10"
					cols="10"><%=disconnectFromCatalog.getResponseString()%></textarea>
			</p>

		</div>

	</div>
</body>
</html>