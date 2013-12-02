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
    final String baseUrl = request.getContextPath() + "/sir/client";
%>
<div id="menu">
	<div class="menu_operationList">
		<div class="menu_title">sensor search</div>
		<ul>
			<li><a href="<%=baseUrl%>/searchSensor">SearchSensor</a></li>
			<li><a href="<%=baseUrl%>/describeSensor">DescribeSensor</a></li>
		</ul>
	</div>

	<div class="menu_operationList">
		<div class="menu_title">metadata handling</div>
		<ul>
			<li><a href="<%=baseUrl%>/harvestService">HarvestService</a></li>
			<li><a href="<%=baseUrl%>/insertSensorInfo">InsertSensorInfo</a></li>
			<li><a href="<%=baseUrl%>/deleteSensorInfo">DeleteSensorInfo</a></li>
			<li><a href="<%=baseUrl%>/updateSensorDescription">UpdateSensorDescription</a></li>
		</ul>
	</div>

	<div class="menu_operationList">
		<div class="menu_title">status handling</div>
		<ul>
			<li><a href="<%=baseUrl%>/getSensorStatus">GetSensorStatus</a></li>
			<li><a
				href="<%=baseUrl%>/insertSensorStatus">InsertSensorStatus</a></li>
			<!-- 			<li><a style="font-style: italic;" -->
			<%-- 				href="<%=baseUrl%>/unsupportedOperation">Subscribe[..]Status</a></li> --%>
			<!-- 			<li><a style="font-style: italic;" -->
			<%-- 				href="<%=baseUrl%>/unsupportedOperation">Renew[..]Subscription</a></li> --%>
			<!-- 			<li><a style="font-style: italic;" -->
			<%-- 				href="<%=baseUrl%>/unsupportedOperation">Cancel[..]Subscription</a></li> --%>
		</ul>
	</div>

	<div class="menu_operationList">
		<div class="menu_title">catalog connection</div>
		<ul>
			<li><a href="<%=baseUrl%>/connectToCatalog">ConnectToCatalog</a></li>
			<li><a href="<%=baseUrl%>/disconnectFromCatalog">DisconnectFromCatalog</a></li>
		</ul>
	</div>

	<div class="menu_operationList">
		<div class="menu_title">other</div>
		<ul>
			<li><a href="<%=baseUrl%>/getCapabilities">GetCapabilities</a></li>
			<li>&nbsp;&nbsp;&nbsp;</li>
			<li><i><a href="<%=baseUrl%>/transformSensorML">SML to
						ebRIM Transformation</a></i></li>
			<li><i><a href="<%=baseUrl%>/testSensorML">Check
						SensorML Conformity</a></i></li>
		</ul>
	</div>
</div>