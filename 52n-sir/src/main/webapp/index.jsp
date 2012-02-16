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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<jsp:useBean id="serviceStatusBean"
	class="org.n52.sir.client.ServiceStatusBean" scope="application" />

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>SIR OpenSearch</title>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />

<meta name="Description" content="Open Sensor Search" />
<meta name="Keywords"
	content="Open Sensor Search, Sensor Web, Sensor Instance Registry, Sensor Discovery, OGC, Open Geospatial Consortium, SIR, SWE, Sensor Web Enablement Initiative" />

<link href="sir.css" rel="stylesheet" type="text/css" />
<link rel="search" type="application/opensearchdescription+xml"
	title="Sensor Search"
	href="${service.url}${service.path}/OpenSearchDoc.xml" />
<link rel="shortcut icon"
	href="https://52north.org/templates/52n/favicon.ico" />


</head>

<body bgcolor="#ffffff" text="#000000" onload="load()">
	<div id="content">

		<div class="search-header">Open Sensor Search</div>

		<div class="center">
			<form name="requestform" method="get"
				action="${service.path}/${service.endpoint.opensearch}">
				<input type="hidden" name="httpAccept" value="text/html" />
				<span><input name="q" type="text" class="search-input" /> </span> <span><input
					value="Search" type="submit" class="search-button" /> </span>
			</form>
		</div>

		<div class="center">
			<span class="infotext">Searching across <%=serviceStatusBean.getNumberOfSensors()%>
				sensors from <%=serviceStatusBean.getNumberOfServices()%> services.
			</span>
		</div>

		<jsp:include page="footer.jsp" />
	</div>

</body>

</html>
