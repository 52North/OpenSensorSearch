<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Search The OSS sensors</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<script src="./${context }/scripts/autocomplete.js"></script>
<script src="./${context}/scripts/bootstrap.min.js"></script>
<link href="./${context}/styles/bootstrap.css" rel="stylesheet">

<%@ include file="navBar.jsp"%>
<!-- /container -->
<!--/span-->



<div class="container">
	<div class="jumbotron">
		<h1>Search OSS Sensors Database</h1>
		<form name="requestform" method="get"
			action="${service.path}/${service.endpoint.opensearch}">
			<input type="hidden" name="httpAccept" value="text/html" /> <span><input
				name="q" type="text" class="search-input" /> </span> <span><input
				value="Search" type="submit" class="btn btn-primary btn-large" /> </span>
		</form>
	</div>
</div>
</body>
</html>
