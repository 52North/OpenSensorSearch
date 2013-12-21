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