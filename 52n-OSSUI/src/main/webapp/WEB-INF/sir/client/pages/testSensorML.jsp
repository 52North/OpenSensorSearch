<%--

    ﻿Copyright (C) 2013 52°North Initiative for Geospatial Open Source Software GmbH

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

<%@page import="org.n52.oss.sir.Client"%>

<jsp:useBean id="testSensorML"
	class="org.n52.oss.ui.beans.TestSensorMLBean" scope="page" />
<jsp:setProperty property="*" name="testSensorML" />

<%
    if (request.getParameter("testDoc") != null) {
        testSensorML.requestTest();
    }
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Discovery Profile Validation</title>

<jsp:include page="htmlHead.jsp"></jsp:include>

</head>

<body onload="load()">

<div id="content"><jsp:include page="header.jsp" /><jsp:include
	page="../menu.jsp" />

<h2>Discovery Profile Validation</h2>

<p>Test a SensorML document for conformity with SensorML profile for
discovery. Insert the SensorML description of a sensor:</p>

<form action="testSensorML.jsp" method="post">
<p class="textareaBorder"><textarea id="requestStringArea"
	name="requestString" class="smallTextarea" rows="10" cols="10"><%=testSensorML.getRequestString()%></textarea></p>
<p><input type="submit" name="testDoc" value="Validate" /></p>
</form>

<p>Test Result:</p>

<p class="textareaBorder"><textarea id="responseStringArea"
	class="largeTextarea" rows="10" cols="10"><%=testSensorML.getResponseString()%></textarea></p>

<p>You can download the used profile here: <a
	href="<%=testSensorML.getSchematronDownloadLink()%>"
	title="SensorML Profile for Discovery - Schematron">Schematron File</a>.</p>
	
</div>
</body>
</html>