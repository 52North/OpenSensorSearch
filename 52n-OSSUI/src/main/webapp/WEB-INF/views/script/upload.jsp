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
<link href="../${context}/styles/bootstrap.css" rel="stylesheet">
<%@ include file="navBar.jsp"%>
<!-- /container -->
<!--/span-->
<div class="span9">
	<div class="hero-unit">
		<h1>Upload A remote server</h1>
		<p>Please upload your Javascript File</p>

		<form action="${context}/OSSUI/script/upload" method="post"
			enctype="multipart/form-data" class="form-upload"  name="uploadForm">
			<label for="file">File</label>
			<p>
				<input type="file" name="file"
					class="btn span6" size="45" />
			</p>
			

			<p><input type="submit" value="Upload It"
				class="btn btn-primary btn-large" /></p>
		</form>

	</div>
</div>
</div>
</body>
</html>

