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
<?xml version="1.0" encoding="utf-8"?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<jsp:useBean id="layarServlet" class="org.n52.ar.layar.LayarServlet"
	scope="application" />

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<head>
<title>Open Sensor Search for Augmented Reality</title>

<script language="javascript">
	// http://dev.w3.org/geo/api/spec-source.html
	var radius;
	var host;
	var currentLocation = null;

	// report errors to user
	function errorHandler(error) {
		switch (error.code) {
		case error.PERMISSION_DENIED:
			alert("Could not get position as permission was denied.");
			break;
		case error.POSITION_UNAVAILABLE:
			alert("Could not get position as this information is not available at this time.");
			break;
		case error.TIMEOUT:
			alert("Attempt to get position timed out.");
			break;
		default:
			alert("Sorry, an error occurred. Code: " + error.code
					+ " Message: " + error.message);
			break;
		}
	}

	function saveLocation(location) {
		currentLocation = location;

		update();
	}

	function load() {
		// check browser can support geolocation, if so get the current position
		if (navigator.geolocation) {
			navigator.geolocation
					.getCurrentPosition(saveLocation, errorHandler);
		} else {
			alert("Sorry, your browser does not support geolocation services.");
		}

		update();
	}

	function update() {
		radius = document.getElementById("radius").value;
		layarURL = document.getElementById("layarURL").innerHTML;

		if (currentLocation != null) {
			lat = currentLocation.coords.latitude;
			lon = currentLocation.coords.longitude;

			document.getElementById("lat").innerHTML = lat;
			document.getElementById("lon").innerHTML = lon;

			var layar_geo_url = layarURL + "?" + "countryCode=IN"
					+ "&action=refresh"
					+ "&userId=ed48067cda8e1b985dbb8ff3653a2da4fd490a37"
					+ "&layerName=geoviquasensorsearch" + "&radius=" + radius
					+ "&lat=" + lat + "&lon=" + lon;
			document.getElementById("layar_link").href = layar_geo_url;
		}
	}
</script>

</head>

<body onload="load()">
	<h1>Open Sensor Search for Augmented Reality</h1>

	<p>TODO: use 52N/GeoViQua styling</p>

	<p>
		Latitude: <span id="lat">0.00</span> Longitude: <span id="lon">0.00</span>
	</p>
	<p>
		Layar URL: <span id="layarURL"><%=getServletContext().getInitParameter("layarURL")%></span>
	</p>
	<p>
		Radius: <input id="radius" value="5000" type="text"
			onchange="update()" /> Searchbox: <input id="searchbox" value=""
			type="text" onchange="update()" />
	</p>

	<h2>Layers</h2>
	<ul>
		<li><h3>Layar</h3>
			<div>
				<h4>Test URLs</h4>
				<ul>
					<li><a id="layar_link" href="/">Layar:
							geoviquasensorsearch</a></li>
					<li>Layar url: layar://</li>
				</ul>
			</div></li>
		<li><h3>Junaio</h3> ...</li>
		<li><h3>Wikitude</h3> ...</li>
	</ul>

	<p class="infotext">${project.build.finalName}
		${version}-r${buildNumber} as of ${buildTimestamp}</p>
</body>
</html>