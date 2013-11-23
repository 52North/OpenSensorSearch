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

<jsp:useBean id="getCapabilities"
	class="org.n52.sir.client.GetCapabilitiesBean" scope="page" />
<jsp:setProperty property="*" name="getCapabilities" />

<%
    if (request.getParameter("build") != null) {
        getCapabilities.buildRequest();
    }

    if (request.getParameter("sendRequest") != null) {
        getCapabilities.sendRequest(getCapabilities.getRequestString());
    }
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Get Capabilities Request</title>

<jsp:include page="htmlHead.jsp"></jsp:include>

</head>
<body onload="load()">

	<div id="content"><jsp:include page="header.jsp" />
		<jsp:include page="../menu.jsp" />

		<div id="pageContent">

			<h2>Get Capabilities Request</h2>

			<form action="getCapabilities.jsp" method="post">
				<ul class="inputTablesList">
					<li>
						<table style="">
							<tr>
								<td class="inputTitle">Service:</td>
								<td><select name="service">
										<option selected="selected">SIR</option>
								</select></td>
							</tr>
							<tr>
								<td class="inputTitle">Update Sequence:</td>
								<td><input type="text" class="inputField"
									name="updateSequence" size="60"
									value="<%=getCapabilities.getUpdateSequence()%>" /></td>
							</tr>
							<tr>
								<td class="inputTitle">Accept Versions:</td>
								<td><input type="text" class="inputField"
									name="acceptVersions" size="60"
									value="<%=getCapabilities.getAcceptVersions()%>" /></td>
							</tr>
							<tr>
								<td class="inputTitle">Sections:</td>
							</tr>
							<tr>
								<td class="inputTitle">Service Identification:</td>
								<td><input type="checkbox" name="serviceIdentification"
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
								<td><input type="text" disabled="disabled"
									value="not implemented yet" /></td>
							</tr>
						</table>
					</li>
				</ul>

				<p>
					<input type="submit" name="build" value="Build request" />
				</p>
			</form>
			<form action="getCapabilities.jsp" method="post">
				<p class="textareaBorder">
					<textarea id="requestStringArea" class="mediumTextarea"
						name="requestString" cols="10" rows="10"><%=getCapabilities.getRequestString()%></textarea>
				</p>
				<p>
					<input type="submit" name="sendRequest" value="Send request" />
				</p>
			</form>
			<p class="textareaBorder">
				<textarea id="responseStringArea" class="largeTextarea" cols="10"
					rows="10"><%=getCapabilities.getResponseString()%></textarea>
			</p>

		</div>
	</div>
</body>
</html>