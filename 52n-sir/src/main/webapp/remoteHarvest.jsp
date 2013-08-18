<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Harvest a remote file</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link href="css/bootstrap.css" rel="stylesheet">
<style type="text/css">
body {
	padding-top: 40px;
	padding-bottom: 40px;
	background-color: #f5f5f5;
}

.sidebar-nav {
	padding: 9px 0;
}
</style>
<script>
function uploadServer(){
	  $("#remoteServer").prop("disabled",true);
			$.ajax({
			  type: "POST",
			  url: "http://localhost:8080/SIR/harvest/script/remote/server",
			  data: $("#harvest-form").serialize(),
			  success: function(data){
	        if(data){
	          var auth_token = data['auth_token'];
	          $("#authtokenval").val(auth_token);
	        }
	      },
	      error : function(){alert("error");}
			});
}
</script>
<link href="css/bootstrap-responsive.css" rel="stylesheet">

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

	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container-fluid">
				<button type="button" class="btn btn-navbar" data-toggle="collapse"
					data-target=".nav-collapse">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="brand" href="#">OpenSensorSearch</a>
				<div class="nav-collapse collapse">
					<p class="navbar-text pull-right">
						Logged in as <a href="#" class="navbar-link">Username</a>
					</p>
					<ul class="nav">
						<li class="active"><a href="#">Harvest a Remote Server</a></li>
						<li><a href="#about">Harvest a javascript script</a></li>
						<li><a href="#contact">Contact</a></li>
					</ul>
				</div>
				<!--/.nav-collapse -->
			</div>
		</div>
	</div>
	<!-- /container -->

	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span3">
				<div class="well sidebar-nav">
					<ul class="nav nav-list">
						<li class="nav-header">Upload</li>
						<li class="active"><a href="#">Upload a remote server</a></li>
						<li class="nav-header">Harvest</li>
						<li><a href="#">Schedule a harvest</a></li>
						<li class="nav-header">Monitor</li>
						<li><a href="#">Check harvest job status</a></li>

					</ul>
				</div>
				<!--/.well -->
			</div>
			<!--/span-->
			<div class="span9">
				<div class="hero-unit">
					<h1>Upload A remote server</h1>
					<p>In the specified field , please enter a valid sensor
						provider for harvesting</p>
					<form id="harvest-form">
						<input type="text" class="span4" name="url"></input>
					</form>
					<input type="text" class="span4" id="authtokenval" disabled="true" value="Auth token"></input>
					<p>
						<a href="#" class="btn btn-primary btn-large"
							onclick="uploadServer()">Add server &raquo;</a>
					</p>
				</div>
			</div>
		</div>

		<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
		<script src="js/bootstrap.min.js"></script>
</body>
</html>
