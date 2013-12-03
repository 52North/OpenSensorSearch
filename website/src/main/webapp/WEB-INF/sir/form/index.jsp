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
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>

<%@page import="org.n52.oss.ui.WebsiteConfig"%>

<!DOCTYPE html>
<html lang="en">

<head>
<title>SIR Form Client</title>

<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="content-type" content="text/html;charset=utf-8" />

<link href="sir.css" rel="stylesheet" type="text/css" />
<link href="../lib/codemirror/codemirror.css" rel="stylesheet"
	type="text/css" />
<link rel="shortcut icon"
	href="http://52north.org/templates/52n-2012/favicon.ico" />

<jsp:useBean id="configBean" scope="application"
	class="org.n52.oss.ui.WebsiteConfig" />
<script type="text/javascript">
	var sirEndpoint = "<%=configBean.getSirEndpoint()%>
	";
	console.log("sirEndpoint = " + sirEndpoint);
</script>

</head>
<body>
	<div id="content">
		<div id="header">
			<div id="headline">
				<span class="title">SIR Form Client</span>
			</div>
			<div class="infotext">
				For more information about the Sensor Instance Registry please
				consult S. Jirka, A. Bröring and C. Stasch: <i>Discovery
					Mechanisms for the Sensor Web</i> (<a
					href="http://www.mdpi.com/1424-8220/9/4/2661"
					title="Publication Download">Open Access download</a>) and the OGC
				Sensor Instance Registry Discussion Paper (<a
					href="http://portal.opengeospatial.org/files/?artifact_id=40609"
					title="Discussion Paper Download">download</a>).
			</div>
		</div>

		<h3>
			Request Examples:&nbsp;&nbsp;<select id="selRequest"
				onchange="insertSelected();">
				<option value="empty">&nbsp;</option>
			</select>
		</h3>

		<div class="infotext">
			<p>
				You can change the examples in the folder <span class="path">[project-directory]/WEB-INF/sir/form/requests/</span>.
			</p>
		</div>

		<div class="request-form">
			<form name="requestform" method="post"
				action="<%=new WebsiteConfig().getSirEndpoint()%>">
				<div class="textareaBorder">
					<textarea name="request" id="requestTextarea" class="largeTextarea"
						rows="10" cols="10"></textarea>
				</div>
				<div class="request-form-buttons">
					<input value="Send" type="submit" /> <input value="Clear"
						name="reset" type="reset"
						onclick="document.getElementById('selRequest').selectedIndex = 0; insertSelected();" />
				</div>
			</form>
			<p class="infotext">${project.build.finalName} ${version} | The editor is based on CodeMirror
				(http://marijn.haverbeke.nl/codemirror/).</p>
		</div>
	</div>

	<script src="http://code.jquery.com/jquery-1.9.1.js"
		type="text/javascript"></script>
	<%
	    /* compressed version from : codemirror, xml, active-line, foldcode, foldgutter, xml-fold, */
	%>
	<script src="../lib/codemirror/codemirror.js" type="text/javascript"></script>
	<script src="../lib/codemirror/xml.js" type="text/javascript"></script>

	<script type="text/javascript" src="form/formClient.js"></script>


</body>
</html>
