<%--

    ﻿Copyright (C) 2013 52°North Initiative for Geospatial Open Source Software GmbH

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

<div id="footer">
	<div class="container">
		<div class="row" style="margin-top: 10px;">
			<dl class="dl-horizontal col-md-6" style="margin: 0px;">
				<dt>52�North</dt>
				<dd>
					<a href="http://www.52north.org/">http://www.52north.org</a>
				</dd>
				<dt>GitHub Project</dt>
				<dd>
					<a href="https://github.com/52North/OpenSensorSearch">https://github.com/52North/OpenSensorSearch</a>
					<a href="https://github.com/52North/OpenSensorSearch"
						style="text-decoration: none"><span class="label label-info">Fork
							us!</span> </a>
				</dd>
			</dl>
			<dl class="dl-horizontal col-md-6" style="margin: 0px;">
				<dt>GeoViQua</dt>
				<dd>
					<a href="http://www.geoviqua.org/">http://www.geoviqua.org/</a>
				</dd>
				<dt>More Projects</dt>
				<dd>
					<a href="http://52north.org/resources/references/sensor-web/osiris">OSIRIS</a>,
					GENESIS
				</dd>
			</dl>
		</div>
		<p class="text-center" style="margin-top: 10px;">Copyright &copy; 2013
			52&deg;North Initiative for Geospatial Open Source Software GmbH. All
			Rights Reserved. | ${project.build.finalName} version ${version}</p>
	</div>
</div>

<!-- load scripts -->
<script src="lib/jquery.js" type="text/javascript"></script>
<script src="lib/jquery-ui.min.js" type="text/javascript"></script>
<script src="lib/bootstrap.js" type="text/javascript"></script>
<script src="lib/jquery.bsAlerts.min.js" type="text/javascript"></script>

<script type="text/javascript">
	$("#apiEndpointMenuLink").attr("href", ossApiEndpoint);
</script>