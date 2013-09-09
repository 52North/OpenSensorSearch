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
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>OpenSensor Search</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>

<script src="/OpenSensorSearch/autocomplete.js"></script>
<script src="/OpenSensorSearch/bootstrap.min.js"></script>
<link href="/OpenSensorSearch/bootstrap.css"
	rel="stylesheet">
<%@ include file="navBar.jsp"%>
<!-- /container -->
<!--/span-->
<div class="span9">
	<div class="hero-unit">
		<h4>OpenSensorSearch Engine</h4>
		<form name="requestform" method="get"
			action="${service.path}/${service.endpoint.opensearch}">
			<input type="hidden" name="httpAccept" value="text/html" /> <span><input
				name="q" type="text" class="search-input" /> </span> <span><input
				value="Search" type="submit" class="search-button" /> </span>
		</form>

	</div>
</div>
</body>
</html>
