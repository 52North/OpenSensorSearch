<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
<script src="js/bootstrap.min.js"></script>
<title>Upload Harvesting Script</title>
</head>

<style>
body {
	padding-top: 40px;
	padding-bottom: 40px;
	background-color: #f5f5f5;
}

.form-upload {
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

.form-upload .form-upload-heading,.form-upload .checkbox {
	margin-bottom: 10px;
}

.form-upload input[type="text"],.form-upload input[type="password"] {
	font-size: 16px;
	height: auto;
	margin-bottom: 15px;
	padding: 7px 9px;
}
</style>

<body>

	<form action="/SIR/harvest/script/submit" method="post"
		enctype="multipart/form-data" class="form-upload">
		<h2 class="form-signin-heading">Please Upload your script</h2>
		<p>
			Select a file : <input type="file" name="file" class="btn" size="45" />
		</p>
		<p>
			Username : <input type="text" name="user" />
		</p>

		<input type="submit" value="Upload It" class="btn" />
	</form>

</body>
</html>