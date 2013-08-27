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
<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>

<script src="./autocomplete.js"></script>
</head>

<body bgcolor="#ffffff" text="#000000">
	<div id="content">

		<div class="search-header">Open Sensor Search</div>

		<div class="center">
			<form name="requestform" method="get"
				action="${service.path}/${service.endpoint.opensearch}">
				<input type="hidden" name="httpAccept" value="text/html" /> <span><input
					name="q" type="text" class="search-input" /> </span> <span><input
					value="Search" type="submit" class="search-button" /> </span>
			</form>
		</div>

		<div class="center">
			<!-- #TODO add links to the RESTful URLs /sensors, /phenomena and /services -->
			<span class="infotext">Searching across <%=serviceStatusBean.getNumberOfSensors()%>
				sensors with <%=serviceStatusBean.getNumberOfPhenomena()%> observed
				properties from <%=serviceStatusBean.getNumberOfServices()%>
				services.
			</span>
		</div>

		<div class="center" style="padding-top: 2em;">
			<span class="infotextHighlight">Is your data missing? <a
				href="mailto:${sir.deploy.contact}">Write us an email!</a></span>
		</div>

		<jsp:include page="footer.jsp" />
	</div>

</body>

</html>
