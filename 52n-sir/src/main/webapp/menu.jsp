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
		<div class="menu_title">sensor search</div>
		<ul>
			<li><a href="<%= request.getContextPath() %>/pages/searchSensor.jsp">SearchSensor</a></li>
			<li><a href="<%= request.getContextPath() %>/pages/describeSensor.jsp">DescribeSensor</a></li>
		</ul>
	</div>
	
	<div class="menu_operationList">
		<div class="menu_title">metadata handling</div>
		<ul>
			<li><a href="<%= request.getContextPath() %>/pages/harvestService.jsp">HarvestService</a></li>
			<li><a href="<%= request.getContextPath() %>/pages/insertSensorInfo.jsp">InsertSensorInfo</a></li>
			<li><a href="<%= request.getContextPath() %>/pages/deleteSensorInfo.jsp">DeleteSensorInfo</a></li>
			<li><a href="<%= request.getContextPath() %>/pages/updateSensorDescription.jsp">UpdateSensorDescription</a></li>
		</ul>
	</div>
	
	<div class="menu_operationList">
		<div class="menu_title">status handling</div>
		<ul>
			<li><a href="<%= request.getContextPath() %>/pages/getSensorStatus.jsp">GetSensorStatus</a></li>
			<li><a href="<%= request.getContextPath() %>/pages/insertSensorStatus.jsp">InsertSensorStatus</a></li>
			<li><a style="font-style: italic;" href="<%= request.getContextPath() %>/pages/unsupportedOperation.jsp">Subscribe[..]Status</a></li>
			<li><a style="font-style: italic;" href="<%= request.getContextPath() %>/pages/unsupportedOperation.jsp">Renew[..]Subscription</a></li>
			<li><a style="font-style: italic;" href="<%= request.getContextPath() %>/pages/unsupportedOperation.jsp">Cancel[..]Subscription</a></li>
		</ul>
	</div>
	
	<div class="menu_operationList">
		<div class="menu_title">catalog connection</div>
		<ul>
			<li><a href="<%= request.getContextPath() %>/pages/connectToCatalog.jsp">ConnectToCatalog</a></li>
			<li><a href="<%= request.getContextPath() %>/pages/disconnectFromCatalog.jsp">DisconnectFromCatalog</a></li>
			<li>&nbsp;&nbsp;&nbsp;</li>
			<li><i><a href="<%= request.getContextPath() %>/pages/catalogPushInfo.jsp">Catalog Connection Info</a></i></li>
		</ul>
	</div>
	
	<div class="menu_operationList">
		<div class="menu_title">other</div>
		<ul>
			<li><a href="<%= request.getContextPath() %>/pages/getCapabilities.jsp">GetCapabilities</a></li>
			<li>&nbsp;&nbsp;&nbsp;</li>
			<li><i><a href="<%=request.getContextPath() %>/formClient.html">Form Client</a></i></li>
			<li><i><a href="<%=request.getContextPath() %>/pages/transformSensorML.jsp">SML to ebRIM Transformation</a></i></li>
			<li><i><a href="<%= request.getContextPath() %>/pages/testSensorML.jsp">Check SensorML Conformity</a></i></li>
		</ul>
	</div>
</div>