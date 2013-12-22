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

<div id="menu">
	<div class="menu_operationList">
		<div class="menu_title">sor operations</div>
		<ul>
			<li><a href="<%= request.getContextPath() %>/pages/getCapabilities.jsp">GetCapabilities</a></li>
			<li><a href="<%= request.getContextPath() %>/pages/getDefinitionURIs.jsp">GetDefinitionURIs</a></li>
			<li><a href="<%= request.getContextPath() %>/pages/getDefinition.jsp">GetDefinition</a></li>
			<li><a href="<%= request.getContextPath() %>/pages/getMatchingDefinitions.jsp">GetMatchingDefinitions</a></li>
		</ul>
	</div>
	<div class="menu_operationList">
		<div class="menu_title">definition handling</div>
		<ul>
			<li><a href="<%= request.getContextPath() %>/pages/insertDefinition.jsp">InsertDefinition</a></li>
			<li><a href="<%= request.getContextPath() %>/pages/deleteDefinition.jsp">DeleteDefinition</a></li>
		</ul>
	</div>
	<div class="menu_operationList">
		<div class="menu_title">other</div>
		<ul>
			<li><a href="<%= request.getContextPath() %>/testClient.html">Textbox-based Test Client</a></li>
			<li><a href="<%= request.getContextPath() %>/REST/">RESTful Web Service</a></li>
		</ul>
	</div>
</div>