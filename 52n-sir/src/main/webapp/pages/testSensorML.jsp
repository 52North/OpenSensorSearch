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

<jsp:useBean id="testSensorML"
	class="org.n52.sir.client.TestSensorMLBean" scope="page" />
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