<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Harvesting status</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link href="../${context}/styles/bootstrap.css" rel="stylesheet">
<%@ include file="navBar.jsp"%>
<!-- /container -->
<!--/span-->
<div class="span9">
	<div class="hero-unit">
		<h3>Uploading script status</h3>

		<c:if test="${harvestError}">
			<div class="alert alert-error">
				<a class="close" data-dismiss="alert">×</a> <strong>Error!</strong>
				Error on harvesting ${errorMSG}
			</div>
		</c:if>
		<c:if test="${harvestSuccess}">
			<div class="alert alert-success">
				<a class="close" data-dismiss="alert">×</a> <strong>Successful!</strong>
				Script uploaded successfully! , your Script Id : ${scriptID}
			</div>
		</c:if>
		
	</div>
</div>
</div>

<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="../${context}/scripts/bootstrap.min.js"></script>
</body>
</html>
