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
<jsp:useBean id="junaioServlet" class="org.n52.ar.junaio.JunaioServlet"
	scope="application" />
<jsp:useBean id="wikitudeServlet"
	class="org.n52.ar.wikitude.WikitudeServlet" scope="application" />

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

Open Sensor Search shows many different sensors (weather stations, air
quality, monitoring sensors) in existing augmented reality applications.
The service component is based on the
<a href="http://52north.org/communities/sensorweb/discovery/index.html">52&deg;North
	Sensor Instance Registry (SIR)</a>
. It can be used for retrieving sensor metadata and information about
data access.

<h2>Junaio</h2>
<table>
	<tr>
		<td><img src="images/LOGO-junaio.png" width=180></td>
		<td>
			<ul>
				<li><a id="junaio_link" href="/">Test</a> using callback URL: <span
					id="junaioURL"><%=getServletContext().getInitParameter("junaioURL")%></span>.</li>
				<li>Layar url: <a
					href="http://ww.junaio.com/downloadnow/index/channel<%=junaioServlet.getLayerName()%>">http://ww.junaio.com/downloadnow/index/channel<%=junaioServlet.getLayerName()%></a></li>
				<li>Can be found under the key word: Open Sensor Search</li>
				<li>Screenshot can be found <a href="images/JunaioScreenshot.png">here</a></li>
			</ul>
		</td>
		<td><div id="junaio-qrcode"></div></td>
	</tr>
</table>

<h2>Layar</h2>
<table>
	<tr>
		<td><img src="images/layar-logo.jpg" width=180></td>
		<td>
			<ul>
				<li><a id="layar_link" href="/">Test</a> using callback URL: <span
					id="layarURL"><%=getServletContext().getInitParameter("layarURL")%></span>.</li>
				<li>Layar url: <a href="layar://layarServlet.getLayerName()">layar://<%=layarServlet.getLayerName()%></a></li>
				<li>Layar url: <a
					href="http://m.layar.com/open/<%=layarServlet.getLayerName()%>">http://m.layar.com/open/<%=layarServlet.getLayerName()%></a></li>
				<li>Can be found under the key word: Open Sensor Search</li>
				<li>Screenshot can be found <a href="images/LayerScreenshot.png">here</a></li>
			</ul>
		</td>
		<td><div id="layar-qrcode"></div></td>
	</tr>
</table>

<h2>Wikitude</h2>
<table>
	<tr>
		<td><img src="images/Wikitude_Logo.png" width=180></td>
		<td>
			<ul>
				<li><a id="wikitude_link" href="/">Test</a> using callback URL:
					<span id="wikitudeURL"><%=getServletContext().getInitParameter("wikitudeURL")%></span>.</li>
				<li>Wikitude url: <a
					href="http://arlink.wikitude.com?dk=<%=wikitudeServlet.getLayerName()%>">http://arlink.wikitude.com?dk=<%=wikitudeServlet.getLayerName()%></a></li>
				<li>Can be found under the key word: opensensorsearch</li>
				<li>Screenshot available soon</li>
			</ul>
		</td>
		<td><div id="wikitude-qrcode"></div></td>
	</tr>
</table>

<h2>Test URLs Settings</h2>
<p>
	Radius: <input id="radius" value="5000" type="text" onchange="update()" />
	Searchbox: <input id="searchbox" value="" type="text"
		onchange="update()" />
</p>

<p class="infotext">
	Latitude: <span id="lat">0.00</span> | Longitude: <span id="lon">0.00</span>
</p>

<p class="infotext">${project.build.finalName}
	${version}-r${buildNumber} as of ${buildTimestamp}</p>

<script type="text/javascript">
	wikitudeQrLink =
<%="\"http://arlink.wikitude.com?dk="
					+ wikitudeServlet.getLayerName() + "\""%>
	jQuery('#wikitude-qrcode').qrcode({
		width : 96,
		height : 96,
		text : wikitudeQrLink
	});
</script>
<script type="text/javascript">
	qrlink =
<%="\"layar://" + layarServlet.getLayerName() + "\""%>
	jQuery('#layar-qrcode').qrcode({
		width : 96,
		height : 96,
		text : qrlink
	});
</script>
<script type="text/javascript">
	junaioQRlink =
<%="\"http://www.junaio.com/downloadnow/index/channel/"
					+ junaioServlet.getLayerName() + "\""%>
	jQuery('#junaio-qrcode').qrcode({
		width : 96,
		height : 96,
		text : qrlink
	});
</script>