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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="content-type" content="text/html;charset=utf-8" />

<base
	href="${fn:substring(url, 0, fn:length(url) - fn:length(pageContext.request.requestURI))}${pageContext.request.contextPath}/" />

<link href="http://52north.org/templates/52n-2012/favicon.ico"
	rel="shortcut icon" type="image/x-icon" />

<link href="styles/bootstrap.css" rel="stylesheet">
<link href="styles/bootstrap-responsive.css" rel="stylesheet">
<link href="styles/jquery-ui.css" rel="stylesheet" />

<link type="text/css" rel="stylesheet" href="styles/oss.css" />

<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
<script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
<script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
<![endif]-->

<jsp:useBean id="configBean" scope="application"
	class="org.n52.oss.ui.WebsiteConfig" />

<script type="text/javascript">
	var ossApiEndpoint = "<%=configBean.getApiEndpoint()%>";
	console.log("OSS API endpoint: " + ossApiEndpoint);
	var sirEndpoint = "<%=configBean.getSirEndpoint()%>";
	console.log("SIR endpoint: " + sirEndpoint);
</script>
