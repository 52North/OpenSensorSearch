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
<%@ page language="java" contentType="text/html; utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>

<!DOCTYPE html>
<html lang="en">
<head>

<%@ include file="common-head.jsp"%>

<title>Open Sensor Search by 52°North</title>

</head>

<body>
	<div id="wrap">
		<%@ include file="navigation.jsp"%>

		<div class="container" style="padding-top: 10%;">
			<div data-alerts="alerts"></div>

			<h1>Open Sensor Search - Under Construction!</h1>

			<p>Sorry about this, but Open Sensor Search is just moving to a
				public beta and this feature does not work yet.</p>
		</div>

	</div>


	<%@ include file="footer.jsp"%>

	<!-- load page specific scripts -->
	<script src="scripts/autocomplete.js"></script>
	<script src="scripts/oss.js"></script>

</body>
</html>
