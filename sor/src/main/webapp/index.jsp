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

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Sensor Observable Registry - Test Client</title>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />

<link href="sor.css" rel="stylesheet" type="text/css" />

</head>

<body>
<div id="content"><jsp:include page="pages/header.jsp" /> <jsp:include
	page="pages/menu.jsp" /> <%
     /* @ include file="menu.jsp" */
 %>

<p>For more information about the Sensor Observable Registry please
consult S. Jirka and A. Bröring: <i>OGC Sensor Observable Registry
Discussion Paper</i> (<a
	href="http://portal.opengeospatial.org/files/index.php?artifact_id=35471"
	title="Publication Download">Open Access download</a>).</p>

<p>Select one of the requests or additional functions from the menu
above to start.</p>

<p class="infotext">This TestClient should work properly in Firefox
1.0 or higher, Safari 1.2 or higher, Opera 8 or higher and
InternetExplorer 5 or higher. The editor is based on CodeMirror
(http://marijn.haverbeke.nl/codemirror/).</p>
</div>

<div class="center"><a
	href="http://validator.w3.org/check?uri=referer"> <img
	src="http://www.w3.org/Icons/valid-xhtml11" alt="Valid XHTML 1.1" /> </a>

<a href="http://jigsaw.w3.org/css-validator/check/referer"> <img
	src="http://jigsaw.w3.org/css-validator/images/vcss"
	alt="CSS is valid!" /> </a></div>

</body>
</html>