/*
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

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
		alert("Sorry, an error occurred. Code: " + error.code + " Message: "
				+ error.message);
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
		navigator.geolocation.getCurrentPosition(saveLocation, errorHandler);
	} else {
		alert("Sorry, your browser does not support geolocation services.");
	}

	update();
}

function update() {
	radius = document.getElementById("radius").value;
	layarURL = document.getElementById("layarURL").innerHTML;
	junaioURL = document.getElementById("junaioURL").innerHTML;
	wikitudeURL = document.getElementById("wikitudeURL").innerHTML;

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

		var junaio_geo_url = junaioURL + "?" + "countryCode=IN"
				+ "&action=refresh"
				+ "&userId=ed48067cda8e1b985dbb8ff3653a2da4fd490a37"
				+ "&layerName=geoviquasensorsearch" + "&p=" + radius + "&m="
				+ 30 + "&l=" + lat + "," + lon;

		var wikitude_geo_url = wikitudeURL + "?" + "&latitude=" + lat
				+ "&longitude=" + lon;

		document.getElementById("layar_link").href = layar_geo_url;
		document.getElementById("junaio_link").href = junaio_geo_url;
		document.getElementById("wikitude_link").href = wikitude_geo_url;
	}
}