<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Upload Harvesting Script</title>
</head>
<body>
	<h1>Upload A harvesting JS script</h1>

	<form action="/SIR/harvest/script/submit" method="post"
		enctype="multipart/form-data">

		<p>
			Select a file : <input type="file" name="file" size="45" />
		</p>

		<input type="submit" value="Upload It" />
	</form>

</body>
</html>