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
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<link href="../sir.css" rel="stylesheet" type="text/css" />
<link rel="shortcut icon"
	href="https://52north.org/templates/52n/favicon.ico" />

<script src="<%=request.getContextPath()%>/codemirror/codemirror.js"
	type="text/javascript"></script>

<script type="text/javascript">

	var requestEditor = null;
	var responseEditor = null;
	var descriptionEditor = null;
	var defaultString = "<!-- Insert your request here or build one using the input options above. -->";
	var style = "<%=request.getContextPath()%>/codemirror/xmlcolors.css";
	var p = "<%=request.getContextPath()%>/codemirror/";
	
	function load() {

		if(requestEditor == null) {
			initRequestEditor();
		}
		if(responseEditor == null) {
			initResponseEditor();
		}
		if(descriptionEditor == null) {
			initDescriptionEditor();
		}
	}

	function initRequestEditor() {
		var s;
		var ta = document.getElementById('requestStringArea');
		if(ta.value != "") {
			s = document.getElementById('requestStringArea').value;
		}
		else {
			s = defaultString;
		}
		
		requestEditor = CodeMirror.fromTextArea("requestStringArea", {
			parserfile: "parsexml.js",
			height: ta.clientHeight + 'px',
			stylesheet: style,
			path: p,
			lineNumbers: true,
			content: s
		});
	}

	function initResponseEditor() {
		var ta = document.getElementById('responseStringArea');
	
		responseEditor = CodeMirror.fromTextArea("responseStringArea", {
			parserfile: "parsexml.js",
			height: ta.clientHeight + 'px',
			stylesheet: style,
			path: p,
			lineNumbers: true
		});
	}

	function initDescriptionEditor() {
		var ta = document.getElementById('descriptionStringArea');

		// description string area is not given on all pages
		if(ta != null) {
			responseEditor = CodeMirror.fromTextArea("descriptionStringArea", {
				parserfile: "parsexml.js",
				height: ta.clientHeight + 'px',
				stylesheet: style,
				path: p,
				lineNumbers: true
			});
		}
	}
		
	
</script>

