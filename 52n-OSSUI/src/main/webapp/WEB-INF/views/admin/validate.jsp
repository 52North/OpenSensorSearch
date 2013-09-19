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
<title>Validate a user by name</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="../${context}/scripts/bootstrap.min.js"></script>

<script>
	$(function() {

		$("#upload-pin").addClass("active");
	});
</script>
<script>
	function validateForm() {
		var x = document.forms["validateForm"]["username"].value;
		if (x == null || x == "") {
			alert("Enter a valid username !");
			return false;
		} else
			return true;

	}
</script>
<link href="../${context}/styles/bootstrap.css" rel="stylesheet">
<%@ include file="navBar.jsp"%>
<!-- /container -->
<!--/span-->
<div class="span9">

	<c:if test="${ValidationCalled}">
		<div class="alert alert-error">
			<a class="close" data-dismiss="alert"></a> <strong>Error!</strong>
			${ValidationMsg}
		</div>
	</c:if>
	<div class="hero-unit">
		<h1>Validate a user by username</h1>
		<p>Please Enter the username</p>

		<form action="${context}/OSSUI/admin/validate" method="post"
			class="form-upload" name="validateForm"
			onsubmit="return validateForm()">
			<label for="username">Username</label>
			<p>
				<input type="text" name="username" class="form-control" />
			</p>
			<p>
				<input type="submit" value="Validate User"
					class="btn btn-primary btn-large" />
			</p>
		</form>

	</div>
</div>
</div>
</body>
</html>

