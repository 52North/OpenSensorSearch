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

<jsp:useBean id="getCatalogPushInfo"
	class="org.n52.sir.client.CatalogPushInfoBean" scope="application" />
<jsp:setProperty property="*" name="getCatalogPushInfo" />

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Catalog Push Information</title>

<jsp:include page="htmlHead.jsp"></jsp:include>

<script type="text/javascript">
	function scrollInfo() {
		textareaelem = document.getElementById("info");
		textareaelem.scrollTop = textareaelem.scrollHeight;
	}
</script>

</head>
<body onload="scrollInfo();">

	<div id="content">

		<jsp:include page="header.jsp" />
		<jsp:include page="../menu.jsp" />

		<div id="pageContent">

			<h2>Catalog Push Information</h2>
			<p>
				<textarea class="largeTextarea textareaBorder" id="info"
					readonly="readonly" rows="10" cols="10"><%=getCatalogPushInfo.getInfoString()%></textarea>
			</p>
			<p>
				(runtime only, maximum of last
				<%=getCatalogPushInfo.getMaxEventsCount()%>
				events)
			</p>

		</div>

	</div>

</body>
</html>