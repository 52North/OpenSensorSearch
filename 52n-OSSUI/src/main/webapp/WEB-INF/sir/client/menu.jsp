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

<%
    final String baseUrl = request.getRequestURL().toString().replace("/index.jsp", "/");
%>
<div id="menu">
	<div class="menu_operationList">
		<div class="menu_title">sensor search</div>
		<%=baseUrl %>
		<ul>
			<li><a href="<%=baseUrl %>pages/searchSensor.jsp">SearchSensor</a></li>
			<li><a href="pages/describeSensor.jsp">DescribeSensor</a></li>
		</ul>
	</div>

	<div class="menu_operationList">
		<div class="menu_title">metadata handling</div>
		<ul>
			<li><a
				href="<%=request.getContextPath()%>/pages/harvestService.jsp">HarvestService</a></li>
			<li><a
				href="<%=request.getContextPath()%>/pages/insertSensorInfo.jsp">InsertSensorInfo</a></li>
			<li><a
				href="<%=request.getContextPath()%>/pages/deleteSensorInfo.jsp">DeleteSensorInfo</a></li>
			<li><a
				href="<%=request.getContextPath()%>/pages/updateSensorDescription.jsp">UpdateSensorDescription</a></li>
		</ul>
	</div>

	<div class="menu_operationList">
		<div class="menu_title">status handling</div>
		<ul>
			<li><a
				href="<%=request.getContextPath()%>/pages/getSensorStatus.jsp">GetSensorStatus</a></li>
			<li><a
				href="<%=request.getContextPath()%>/pages/insertSensorStatus.jsp">InsertSensorStatus</a></li>
			<li><a style="font-style: italic;"
				href="<%=request.getContextPath()%>/pages/unsupportedOperation.jsp">Subscribe[..]Status</a></li>
			<li><a style="font-style: italic;"
				href="<%=request.getContextPath()%>/pages/unsupportedOperation.jsp">Renew[..]Subscription</a></li>
			<li><a style="font-style: italic;"
				href="<%=request.getContextPath()%>/pages/unsupportedOperation.jsp">Cancel[..]Subscription</a></li>
		</ul>
	</div>

	<div class="menu_operationList">
		<div class="menu_title">catalog connection</div>
		<ul>
			<li><a
				href="<%=request.getContextPath()%>/pages/connectToCatalog.jsp">ConnectToCatalog</a></li>
			<li><a
				href="<%=request.getContextPath()%>/pages/disconnectFromCatalog.jsp">DisconnectFromCatalog</a></li>
		</ul>
	</div>

	<div class="menu_operationList">
		<div class="menu_title">other</div>
		<ul>
			<li><a
				href="<%=request.getContextPath()%>/pages/getCapabilities.jsp">GetCapabilities</a></li>
			<li>&nbsp;&nbsp;&nbsp;</li>
			<li><i><a
					href="<%=request.getContextPath()%>/formClient.html">Form
						Client</a></i></li>
			<li><i><a
					href="<%=request.getContextPath()%>/pages/transformSensorML.jsp">SML
						to ebRIM Transformation</a></i></li>
			<li><i><a
					href="<%=request.getContextPath()%>/pages/testSensorML.jsp">Check
						SensorML Conformity</a></i></li>
		</ul>
	</div>
</div>