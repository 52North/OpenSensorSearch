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
<title>Harvest a remote file</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="../${context}/scripts/bootstrap.min.js"></script>

<script>
	$(function() {

		$("#upload-pin").addClass("active");
	});
</script>
<script>
var licenses = ['optionsRadios1','optionsRadios2','optionsRadios3'];
	function validateForm() {
		var x = document.forms["uploadForm"]["agree"].checked;
		if (x == true) {
			for(var i=0;i<licenses.length;i++)
				if(document.getElementById(licenses[i].toString()).checked)return true;
			
			alert("You need to choose a license first");
			return false;
		} else {
			alert("You need to Accept the license first!");
			return false;
		}

	}
</script>
<link href="../${context}/styles/bootstrap.css" rel="stylesheet">
<%@ include file="navBar.jsp"%>
<!-- /container -->
<!--/span-->
<div class="span9">
	<div class="hero-unit">
		<h1>Upload A remote server</h1>
		<p>Please upload your Javascript File</p>

		<form action="${context}/OSSUI/script/upload" method="post"
			enctype="multipart/form-data" class="form-upload" name="uploadForm"
			onsubmit="return validateForm()">
			<label for="file">File</label>
			<p>
				<input type="file" name="file" class="btn span6" size="45" />
			</p>
			<p>
				<input type="submit" value="Upload It"
					class="btn btn-primary btn-large" />
			</p>
			<p>License</p>

			<label class="radio"> <input type="radio" name="license"
				id="optionsRadios1" value="pddl"> <a
				href="http://opendatacommons.org/licenses/pddl/">Public Domain
					for Data/Database</a>
			</label> <label class="radio"> <input type="radio" name="license"
				id="optionsRadios2" value="ODC"> <a
				href="http://opendatacommons.org/licenses/by/">Attribution for
					Data/Databases</a>
			</label> <label class="radio"> <input type="radio" name="license"
				id="optionsRadios3" value="ODBL"> <a
				href="http://opendatacommons.org/licenses/odbl/">Attribution and
					Share-Alike for Data/Databases</a>

			</label>

			<p>
				<br> <input type="checkbox" name="agree" value="agree">By
				clicking 'Upload It' I agree to put my script under the Apache
				License<br>
		</form>

	</div>
</div>
</div>
</body>
</html>

