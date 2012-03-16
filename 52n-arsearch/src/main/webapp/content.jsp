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

<jsp:useBean id="layarServlet" class="org.n52.ar.layar.LayarServlet"
	scope="application" />

<div id="logos" style="">
	<ul style="list-style-type: none;">
		<li style="display: inline;"><a href="http://geoviqua.org"
			title="GeoViQua - QUAlity aware VIsualisation for the Global Earth Observation system of systems"><img
				src="images/geoviqua.png" height="74" alt="GEOVIQUA logo" /></a></li>
		<li style="display: inline; padding: 0 0 0 60px;"><a
			href="http://cordis.europa.eu/fp7/home_en.html"
			title="European Commission - Seventh Framework Programme (FP7)"><img
				src="images/fp7.png" height="67" alt="FP 7 logo" /></a></li>
	</ul>
</div>

<h1>Open Sensor Search for Augmented Reality</h1>


<h2>Settings</h2>
<p>
	Radius: <input id="radius" value="5000" type="text" onchange="update()" />
	Searchbox: <input id="searchbox" value="" type="text"
		onchange="update()" />
</p>

<h2>Layers</h2>
<ul>
	<li><h3>Junaio</h3> ...</li>
	<li><h3>Layar</h3>
		<div>
			<ul>
				<li><a id="layar_link" href="/">Test</a> using callback URL: <span
					id="layarURL"><%=getServletContext().getInitParameter("layarURL")%></span>.</li>
				<li>Layar url: <a href="layar://layarServlet.getLayerName()">layar://<%=layarServlet.getLayerName()%></a></li>
				<li>Layar url: <a
					href="http://m.layar.com/open/<%=layarServlet.getLayerName()%>">http://m.layar.com/open/<%=layarServlet.getLayerName()%></a></li>
				<li><div id="layar-qrcode"></div></li>
			</ul>
		</div></li>
	<li><h3>Wikitude</h3> ...</li>
</ul>

<p class="infotext">
	Latitude: <span id="lat">0.00</span> Longitude: <span id="lon">0.00</span>
</p>

<p class="infotext">${project.build.finalName}
	${version}-r${buildNumber} as of ${buildTimestamp}</p>

<script type="text/javascript">
	qrlink =
<%="\"layar://" + layarServlet.getLayerName() + "\""%>
	jQuery('#layar-qrcode').qrcode({
		width : 64,
		height : 64,
		text : qrlink
	});
</script>