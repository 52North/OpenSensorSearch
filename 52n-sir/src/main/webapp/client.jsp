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
<title>Sensor Instance Registry - Test Client</title>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />

<link href="sir.css" rel="stylesheet" type="text/css" />
<link rel="shortcut icon"
	href="https://52north.org/templates/52n/favicon.ico" />

</head>

<body>
	<div id="content"><jsp:include page="pages/header.jsp" />
		<jsp:include page="menu.jsp" />

		<p>For more information about the Sensor Instance Registry please
			consult the following resources:</p>

		<ul>
			<li>Sensor Discovery @ 52°North: <a
				href="http://52north.org/communities/sensorweb/incubation/discovery/"
				title="Sensor Discovery at 52°North">http://52north.org/communities/sensorweb/incubation/discovery/</a></li>
			<li>Jirka S., Nuest D. (2010): <i>OGC&#0174; Sensor Instance
					Registry Discussion Paper</i>. Public Discussion Paper. Open Geospatial
				Consortium: 10-171. <a
				href="http://portal.opengeospatial.org/files/?artifact_id=40609"
				title="OGC Discussion Paper 10-171">Download from OGC</a>.
			</li>
			<li>S. Jirka, A. Bröring and C. Stasch: <i>Discovery
					Mechanisms for the Sensor Web</i> (<a
				href="http://www.mdpi.com/1424-8220/9/4/2661"
				title="Publication Download">Open Access download</a>).
			</li>
		</ul>

		<p>
			Select one of the requests or <i>additional functions</i> from the
			menu above to start.
		</p>

		<p class="infotext">${project.build.finalName}
			${version}-r${buildNumber} as of ${buildTimestamp}</p>

		<p class="infotext">
			This TestClient should work properly in Firefox 1.0 or higher, Safari
			1.2 or higher, Opera 8 or higher and InternetExplorer 5 or higher.
			The editor is based on CodeMirror (<a
				href="http://marijn.haverbeke.nl/codemirror/" title="CodeMirror">http://marijn.haverbeke.nl/codemirror/</a>).
		</p>
	</div>

	<div class="center">
		<a href="http://validator.w3.org/check?uri=referer"> <img
			src="http://www.w3.org/Icons/valid-xhtml11" alt="Valid XHTML 1.1" />
		</a> <a href="http://jigsaw.w3.org/css-validator/check/referer"> <img
			src="http://jigsaw.w3.org/css-validator/images/vcss"
			alt="CSS is valid!" />
		</a>
	</div>

</body>
</html>