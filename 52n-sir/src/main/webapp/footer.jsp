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

<div id="footer">
	<p class="infotext">
		Open Sensor Search is powered by the 52&deg;North Sensor Instance
		Registry. <a
			href="http://52north.org/communities/sensorweb/incubation/discovery/"
			title="Sensor Discovery by 52N">Find out more</a>.
	</p>
	<p class="infotext">
		<a href="./">Home</a> | <a href="client.jsp">Extended Client</a> | <a
			href="testClient.html">Form Client</a> | <a href="javascript:void(0)"
			onclick="window.external.AddSearchProvider('${service.url}${service.path}/OpenSearchDoc.xml');">Add
			search to browser</a>
	</p>
	<p class="infotext">
		&copy; 2012 <a href="http://52north.org">52&deg;North Initiative
			for Geospatial Software GmbH</a>
	</p>
</div>