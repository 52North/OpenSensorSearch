<%--

    ﻿    ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH

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

<div class="navbar navbar-default navbar-fixed-top navbar-inverse">
	<div class="container">
		<a href="http://52north.org" alt="52�North Website"><img
			alt="52N logo" src="./${context}/images/52n-icon.png"
			style="float: right; margin-top: 4px; margin-right: 10px;"
			width="42px" height="42px"></a>

		<div class="navbar-header">
			<button type="button" class="navbar-toggle" data-toggle="collapse"
				data-target=".navbar-collapse">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="#">Open Sensor Search</a>
		</div>

		<div class="navbar-collapse collapse">
			<ul class="nav navbar-nav">
				<li class="active"><a href="${context}/OSSUI/remote/index">Harvest
						a Remote Server</a></li>
				<li><a href="${context}/OSSUI/script/index">Harvest a
						javascript script</a></li>
				<li><a href="${context}/OSSUI/api/developer">Developers
						Documentation</a></li>
				<li><a
					onclick="window.external.AddSearchProvider('${context}/OSSUI/resources/searchPlugin.xml');">Add
						as search plugin</a></li>
				<li><a href="${context}/OSSUI/api/">API Documentation</a></li>
				<li><a href="#contact">Contact</a></li>
			</ul>
		</div>
	</div>
</div>