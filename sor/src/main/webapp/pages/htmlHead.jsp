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
    
<link href="../sor.css" rel="stylesheet" type="text/css" />

<script src="<%= request.getContextPath() %>/codemirror/codemirror.js" type="text/javascript"></script>

<script type="text/javascript">

	var requestEditor = null;
	var responseEditor = null;
	var defaultString = "<!-- Insert your request here or build one using the input options above. -->";
	var style = "<%= request.getContextPath() %>/codemirror/xmlcolors.css";
	var p = "<%= request.getContextPath() %>/codemirror/";
	var url = "http://<%= request.getLocalName()%>:<%= request.getLocalPort()%>/SOR/SOR";
	
	function load()
	{
		if(requestEditor == null) {
			initRequestEditor();
		}
		if(responseEditor == null) {
			initResponseEditor();
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
	
</script>

