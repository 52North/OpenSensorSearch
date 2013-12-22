<%--

    ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH

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
﻿<?xml version="1.0" encoding="utf-8"?>
<%--

    ﻿Copyright (C) 2012
    by 52 North Initiative for Geospatial Open Source Software GmbH

    Contact: Andreas Wytzisk
    52 North Initiative for Geospatial Open Source Software GmbH
    Martin-Luther-King-Weg 24
    48155 Muenster, Germany
    info@52north.org

    This program is free software; you can redistribute and/or modify it under
    the terms of the GNU General Public License version 2 as published by the
    Free Software Foundation.

    This program is distributed WITHOUT ANY WARRANTY; even without the implied
    WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public License along with
    this program (see gnu-gpl v2.txt). If not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
    visit the Free Software Foundation web page, http://www.fsf.org.

--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<head>
<title>Open Sensor Search for Augmented Reality</title>

<link href="http://52north.org/templates/52n/favicon.ico"
	rel="shortcut icon" type="image/x-icon" />
<link rel="stylesheet"
	href="http://52north.org/templates/52n/css/external-sites-template/external-site_52n-template-2011_site.css"
	type="text/css" media="all" />
<link rel="stylesheet"
	href="http://52north.org/templates/52n/css/external-sites-template/external-site_52n-template-2011_print.css"
	type="text/css" media="print" />
<link rel="stylesheet"
	href="http://52north.org/templates/52n/52n_menus/52n_cssmenu/52n.cssmenu.css"
	type="text/css" />

<meta http-equiv="Content-Language" content="en" />

<jsp:include page="head.jsp" />

</head>

<body class="composite" onload="load()">
	<div id="bg_h">
		<!-- 
		<div id="logo_h_top">
			<img src="52n-logo-v2.1.png" alt="52°North Logo" />
		</div>  -->
	</div>
	<div id="navigation_h">

		<div id="wopper" class="wopper">
			<div id="ja-mainnavwrap">
				<div id="ja-mainnav">
					<ul id="ja-cssmenu" class="clearfix">
						<!-- Durch die Klasse "active" fü Element <li> und <a> wird diese Menu hervorgehoben angezeigt -->
						<li><a href="http://52north.org/communities/sensorweb/"
							class="menu-item0 first-item" id="menu1"
							title="Sensor Web Community"><span class="menu-title">Sensor
									Web Community</span></a></li>

						<li><a href="http://52north.org/communities/geostatistics/"
							class="menu-item1" id="menu2" title="Geostatistics Community"><span
								class="menu-title">Geostatistics Community</span></a></li>
						<li class="menu-item1"><a
							href="https://wiki.52north.org/bin/view/Geostatistics/GeoViQua"
							class="menu-item2" id="menu3" title="GeoViQua Wiki"><span
								class="menu-title">GeoViQua Wiki</span></a></li>
						<%
						    /*
																<li class="havechild"><a
																	href="http://bugzilla.52north.org/buglist.cgi?product=52N+Sensor+Web&amp;bug_status=__open__"
																	class="menu-item3" id="menu4" title="Bugzilla"><span
																		class="menu-title">Bugzilla</span></a>
																	<ul>
																		<li><a
																			href="https://bugzilla.52north.org/buglist.cgi?product=52N%20Sensor%20Web&amp;component=Discovery&amp;resolution=---"
																			class=" first-item" id="menu41" title="ArcGIS SOS Adapter"><span
																				class="menu-title">Sensor Web Discovery</span></a></li>
																		<li class="havesubchild"><a
																			href="https://bugzilla.52north.org/buglist.cgi?product=52N%20Sensor%20Web&amp;component=SOS&amp;resolution=---"
																			id="menu42" title="SOS"><span class="menu-title">SOS</span></a></li>
																	</ul></li>
									 */
						%>
						<li class="menu-item4"><a
							href="http://52north.org/communities/" class="menu-item4"
							id="menu5" title="52&deg;North Initiative"><span
								class="menu-title">52&deg;North Initiative</span></a></li>
					</ul>
					<div class="published-date_h">
						<span id="publishDate">Last Published: ${buildTimestamp}</span>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="wrapper">
		<!-- LeftColumn kann deaktiviert werden, aber dann sollte in dieser Datei die Eigenschaft padding der Klasse #bodyColumn angepasst werden. Z.B. padding: 0 10px 0 10px; -->
		<div id="bodyColumn" style="padding: 0 10px 0 10px;">
			<div id="contentBox">
				<div class="section">
					<!-- hier kommt der eigentliche Inhalt der Seite -->
					<jsp:include page="content.jsp" />
				</div>
				<!-- End of div "section" -->
			</div>
			<!-- End of div "contentBox" -->
		</div>
	</div>
	<div id="footer">
		<div id="f_top">
			<div id="f_navigation">
				<div class="fn_box">
					<h3>Communities</h3>
					<ul class="fn_list">
						<li><a href="http://52north.org/communities/sensorweb/">Sensor
								Web</a></li>
						<li><a href="http://52north.org/communities/geoprocessing">Geoprocessing</a></li>
						<li><a href="http://52north.org/communities/ilwis/overview">ILWIS</a></li>
						<li><a
							href="http://52north.org/communities/earth-observation/overview">Earth
								Observation</a></li>
						<li><a href="http://52north.org/communities/security/">Security
								&amp; Geo-RM</a></li>
						<li><a href="http://52north.org/communities/semantics/">Semantics</a></li>
						<li><a
							href="http://52north.org/communities/geostatistics/overview">Geostatistics</a></li>
						<li><a href="http://52north.org/communities/3d-community">3D
								Community</a></li>
						<li><a
							href="http://52north.org/communities/metadata-management-community/">Metadata
								Management</a></li>
					</ul>
				</div>
				<div class="fn_box">
					<h3>Get Involved</h3>
					<ul class="fn_list">
						<li><a
							href="http://52north.org/about/get-involved/partnership-levels">Partnership
								Levels</a></li>
						<li><a
							href="http://52north.org/about/get-involved/license-agreement">License
								Agreement</a></li>
					</ul>
				</div>
				<div class="fn_box">
					<h3>Affiliations</h3>
					<p>The 52&deg;North affiliations:</p>
					<a href="http://www.opengeospatial.org/" target="_blank"
						title="OGC Assiciate Members"><img border="0" alt=""
						src="http://52north.org/images/logos/ogc.gif" /></a> <br /> <a
						href="http://www.sensorweb-alliance.org/" target="_blank"
						title="Sensor Web Alliance"><img border="0" alt=""
						src="http://52north.org/images/logos/swa.gif" /></a>
				</div>
				<div class="fn_box">
					<h3>Cooperation partners</h3>
					<p>The 52&deg;North principal cooperation partners</p>
					<table cellpadding="0" border="0">
						<tbody>
							<tr>
								<td><a href="http://ifgi.uni-muenster.de/" target="_blank"
									title="Institute for Geoinformatics"><img height="40"
										border="0" width="117" alt=""
										src="http://52north.org/images/logos/ifgi.gif" /></a></td>
								<td><a href="http://www.conterra.de/" target="_blank"
									title="con terra GmbH"><img height="40" border="0"
										width="81" alt=""
										src="http://52north.org/images/logos/conterra_new.png" /></a></td>
							</tr>
							<tr>
								<td><a href="http://www.esri.com/" target="_blank"
									title="ESRI"><img height="40" border="0" width="110" alt=""
										src="http://52north.org/images/logos/esri_new.gif" /></a></td>
								<td><a href="http://www.itc.nl/" target="_blank"
									title="International Institute for Geo-Information  Science and Earth    Observation"><img
										height="40" border="0" width="34" alt=""
										src="http://52north.org/images/logos/itc.gif" /></a></td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<div id="f_bottom">
			<ul>
				<li><a href="http://52north.org/about/contact">Contact</a></li>
				<li><a href="http://52north.org/about/52north/disclaimer">Disclaimer</a></li>
			</ul>
			<small>Copyright &copy; </small>
			<script type="text/javascript">
			<!--
				var now = new Date();
				document.write("<small>" + now.getFullYear() + "</small>");
			//-->
			</script>
			<noscript>
				<small>2012</small>
			</noscript>
			<small>52&deg;North Initiative for Geospatial Open Source
				Software GmbH. All Rights Reserved.</small>
		</div>
	</div>
</body>

</html>