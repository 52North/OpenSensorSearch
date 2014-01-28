/*
 * Copyright 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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