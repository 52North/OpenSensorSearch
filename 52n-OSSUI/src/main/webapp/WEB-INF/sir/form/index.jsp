<!--

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

-->
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
	
<!DOCTYPE html>
<html lang="en">

<head>
<title>SIR Form Client</title>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />

<link href="sir.css" rel="stylesheet" type="text/css" />
<link rel="shortcut icon"
	href="https://52north.org/templates/52n/favicon.ico" />

<script src="codemirror/codemirror.js" type="text/javascript"></script>

<script type="text/javascript" src="formClient.js"></script>

</head>
<body bgcolor="#ffffff" text="#000000" onload="load()">
	<div id="content">
		<div id="header">
			<div id="headline">
				<span class="title">SIR Form Client</span><span class="infotext">Version
					0.4</span>
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
				<option value=" "></option>
			</select>
		</h3>

		<div class="infotext">
			<p>
				You can change the examples in the folder <font face="monospace">[project-directory]/WebContent/TestRequests/</font>.
			</p>
		</div>

		<div class="request-form">
			<form name="requestform" method="post"
				action="${service.path}/${service.endpoint.service}">
				<div class="textareaBorder">
					<textarea name="request" id="requestTextarea" class="largeTextarea"
						rows="10" cols="10"></textarea>
				</div>
				<div class="request-form-buttons">
					<input value="Send"
						onclick="requestform.action = urlform.url.value" type="submit" />
					<input value="Clear" name="reset" type="reset"
						onclick="document.getElementById('selRequest').selectedIndex = 0; insertSelected();" />
				</div>
			</form>
			<p class="infotext">${project.build.finalName}
				${version}-r${buildNumber} as of ${buildTimestamp}</p>
			<p class="infotext">This TestClient was successfully tested in
				Firefox 3.0, Safari 4.0.3, Opera 10.10, Chrome 4.0 and
				InternetExplorer 8.0.6001.18702 and should work properly in Firefox
				1.0 or higher, Safari 1.2 or higher, Opera 8 or higher and
				InternetExplorer 5 or higher. The editor is based on CodeMirror
				(http://marijn.haverbeke.nl/codemirror/).</p>
		</div>

		<div class="center">
			<a href="http://validator.w3.org/check?uri=referer"><img
				src="http://www.w3.org/Icons/valid-xhtml11" alt="Valid XHTML 1.1" />
			</a> <a href="http://jigsaw.w3.org/css-validator/check/referer"><img
				src="http://jigsaw.w3.org/css-validator/images/vcss"
				alt="CSS is valid!" /> </a>
		</div>

	</div>
</body>
</html>
