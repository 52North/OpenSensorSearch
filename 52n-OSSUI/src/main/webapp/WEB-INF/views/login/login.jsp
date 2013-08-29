<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>

<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="utf-8">
<title>Sign in</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link href="../OSSUI/styles/bootstrap.css" rel="stylesheet">
<style type="text/css">
body {
	padding-top: 40px;
	padding-bottom: 40px;
	background-color: #f5f5f5;
}

.form-signin {
	max-width: 300px;
	padding: 19px 29px 29px;
	margin: 0 auto 20px;
	background-color: #fff;
	border: 1px solid #e5e5e5;
	-webkit-border-radius: 5px;
	-moz-border-radius: 5px;
	border-radius: 5px;
	-webkit-box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
	-moz-box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
	box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
}

.form-signin .form-signin-heading,.form-signin .checkbox {
	margin-bottom: 10px;
}

.form-signin input[type="text"],.form-signin input[type="password"] {
	font-size: 16px;
	height: auto;
	margin-bottom: 15px;
	padding: 7px 9px;
}
</style>
<link href="OSSUI/styles/bootstrap-responsive.css" rel="stylesheet">

<!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
      <script src="../assets/js/html5shiv.js"></script>
    <![endif]-->

<!-- Fav and touch icons -->
<link rel="apple-touch-icon-precomposed" sizes="144x144"
	href="../assets/ico/apple-touch-icon-144-precomposed.png">
<link rel="apple-touch-icon-precomposed" sizes="114x114"
	href="../assets/ico/apple-touch-icon-114-precomposed.png">
<link rel="apple-touch-icon-precomposed" sizes="72x72"
	href="../assets/ico/apple-touch-icon-72-precomposed.png">
<link rel="apple-touch-icon-precomposed"
	href="../assets/ico/apple-touch-icon-57-precomposed.png">
<link rel="shortcut icon" href="../assets/ico/favicon.png">
</head>

<body>
	<c:if test="${LoginFailed}">
		<div class="alert alert-error">
			<a class="close" data-dismiss="alert">×</a> <strong>Error!</strong>
			Wrong user/password combination!
		</div>
	</c:if>
	<div class="container">
		<form method="post" class="form-signin"
			action="<c:url value='j_spring_security_check' />">
			<h2 class="form-signin-heading">Please sign in</h2>
			<input type="text" class="input-block-level"
				placeholder="Email address" id="username_or_email" name="j_username" />
			<input type="password" class="input-block-level"
				placeholder="Password" id="password" name="j_password"> <label
				class="checkbox"> <input type="checkbox" value="remember-me">
				Remember me
			</label>
			<button class="btn btn-large btn-primary" type="submit">Sign
				in</button>
		</form>

	</div>
	<!-- /container -->

	<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
	<script src="../OSSUI/scripts/bootstrap.min.js"></script>

</body>
</html>
