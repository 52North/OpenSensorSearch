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
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<link href="../sir.css" rel="stylesheet" type="text/css" />
<link rel="shortcut icon"
	href="https://52north.org/templates/52n/favicon.ico" />

<script src="<%=request.getContextPath()%>/lib/jquery.js"
	type="text/javascript"></script>

<script src="<%=request.getContextPath()%>/lib/codemirror/codemirror.js"
	type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/lib/codemirror/xml.js"
	type="text/javascript"></script>

<script type="text/javascript">
	var requestEditor = null;
	var responseEditor = null;
	var descriptionEditor = null;
	var defaultString = "<!-- Insert your request here or build one using the input options above. -->";

	function load() {

		if (requestEditor == null) {
			initRequestEditor();
		}
		if (responseEditor == null) {
			initResponseEditor();
		}
		if (descriptionEditor == null) {
			initDescriptionEditor();
		}
	}

	function initRequestEditor() {
		var s;
		var area = document.getElementById('requestStringArea');
		if (area.value != "") {
			s = area.value;
		} else {
			s = defaultString;
		}

		requestEditor = CodeMirror.fromTextArea("requestStringArea");
		requestEditor.setValue(s);
	}

	function initResponseEditor() {
		var area = document.getElementById('responseStringArea');
		if (area != null)
			responseEditor = CodeMirror.fromTextArea("responseStringArea");
	}

	function initDescriptionEditor() {
		var area = document.getElementById('descriptionStringArea');

		// description string area is not given on all pages
		if (area != null) {
			responseEditor = CodeMirror.fromTextArea("descriptionStringArea");
		}
	}
</script>

