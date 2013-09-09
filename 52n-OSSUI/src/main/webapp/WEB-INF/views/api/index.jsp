<!DOCTYPE html>
<html>
<head>
  <title>Swagger UI</title>
  <link href='//fonts.googleapis.com/css?family=Droid+Sans:400,700' rel='stylesheet' type='text/css'/>
  <link href='../${context}/styles/highlight.default.css' media='screen' rel='stylesheet' type='text/css'/>
  <link href='../${context}/styles/screen.css' media='screen' rel='stylesheet' type='text/css'/>
  <script type="text/javascript" src="../${context}/lib/shred.bundle.js" /></script>  
  <script src='../${context}/lib/jquery-1.8.0.min.js' type='text/javascript'></script>
  <script src='../${context}/lib/jquery.slideto.min.js' type='text/javascript'></script>
  <script src='../${context}/lib/jquery.wiggle.min.js' type='text/javascript'></script>
  <script src='../${context}/lib/jquery.ba-bbq.min.js' type='text/javascript'></script>
  <script src='../${context}/lib/handlebars-1.0.0.js' type='text/javascript'></script>
  <script src='../${context}/lib/underscore-min.js' type='text/javascript'></script>
  <script src='../${context}/lib/backbone-min.js' type='text/javascript'></script>
  <script src='../${context}/lib/swagger.js' type='text/javascript'></script>
  <script src='../${context}/lib/swagger-ui.js' type='text/javascript'></script>
  <script src='../${context}/lib/highlight.7.3.pack.js' type='text/javascript'></script>
  <script type="text/javascript">
    $(function () {
      window.swaggerUi = new SwaggerUi({
      url: "http://localhost:8080/OpenSensorSearch/api-docs",
      dom_id: "swagger-ui-container",
      supportedSubmitMethods: ['get', 'post', 'put', 'delete'],
      onComplete: function(swaggerApi, swaggerUi){
        if(console) {
          console.log("Loaded SwaggerUI")
        }
        $('pre code').each(function(i, e) {hljs.highlightBlock(e)});
      },
      onFailure: function(data) {
        if(console) {
          console.log("Unable to Load SwaggerUI");
          console.log(data);
        }
      },
      docExpansion: "none"
    });

    
    window.swaggerUi.load();
  });

  </script>
</head>

<body>
<div id='header'>
  <div class="swagger-ui-wrap">
    <a id="logo" href="http://swagger.wordnik.com">swagger</a>

    <form id='api_selector'>
      <div class='input icon-btn'>
      </div>
    </form>
  </div>
</div>

<div id="message-bar" class="swagger-ui-wrap">
  &nbsp;
</div>

<div id="swagger-ui-container" class="swagger-ui-wrap">

</div>

</body>

</html>
