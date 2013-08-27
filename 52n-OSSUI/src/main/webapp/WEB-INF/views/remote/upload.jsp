<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Harvest a remote file</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="../${context}/scripts/bootstrap.min.js"></script>

<script>
$(function(){

	$("#upload-pin").addClass("active");
});
</script>
<script>
	function uploadServer() {
		$("#remoteServer").prop("disabled", true);
		$("#uploadserverbtn").prop("disabled", true);
		$.ajax({
			type : "POST",
			url : "http://localhost:8080/OpenSensorSearch/script/remote/server",
			data : $("#harvest-form").serialize(),
			success : function(data) {
				if (data) {
					var auth_token = data['auth_token'];
					$("#authtokenval").val(auth_token);
				}
				$("#remoteServer").prop("disabled", true);
				$("#uploadserverbtn").prop("disabled", true);
			},
			error : function() {
				alert("error");
				$("#remoteServer").prop("disabled", true);
				$("#uploadserverbtn").prop("disabled", true);
			}
		});
	}
</script>

<link href="../${context}/styles/bootstrap.css" rel="stylesheet">
<%@ include file="navBar.jsp"%>
<!-- /container -->
<!--/span-->
<div class="span9">
	<div class="hero-unit">
		<h1>Upload A remote server</h1>
		<p>In the specified field , please enter a valid sensor provider
			for harvesting</p>
		<form id="harvest-form">
			<input type="text" class="span6" name="url"></input>
		</form>
		<input type="text" class="span6" id="authtokenval" value="Auth token"
			readonly></input>
		<p>
		<!-- TODO yakoub : add the data to the harvesting -->
			<a id="uploadserverbtn" href="#" class="btn btn-primary btn-large"
				onclick="uploadServer()">Add server &raquo;</a>
		</p>
	</div>
</div>
</div>

</body>
</html>
