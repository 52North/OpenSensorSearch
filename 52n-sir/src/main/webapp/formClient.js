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

var datafolder = window.location.href.substring(0, window.location.href
		.lastIndexOf("/") + 1)
		+ "TestRequests/";
var editor = null;
var defaultString = "<!-- Insert your request here or select one of the examples from the menu above. -->";

function load() {
	if (editor == null) {
		initEditor();
	}
	initExamples();
}

function initExamples() {
	var placeholderIndex = "PLACEHOLDER";
	// load files
	var requests = new Array();
	requests[1] = datafolder + "GetCapabilities.xml";
	requests[2] = placeholderIndex;
	requests[3] = datafolder + "DescribeSensor.xml";

	requests[4] = datafolder + "GetSensorStatus_bySearchCriteria.xml";
	requests[5] = datafolder + "GetSensorStatus_bySensorIDInSIR.xml";
	requests[6] = datafolder + "GetSensorStatus_byServiceDescription.xml";
	requests[7] = placeholderIndex;
	requests[8] = placeholderIndex;
	requests[9] = placeholderIndex;
	requests[10] = datafolder + "HarvestService_WeatherSOS.xml";

	requests[11] = datafolder + "InsertSensorInfo_addReference.xml";
	requests[12] = datafolder + "InsertSensorInfo_newSensor.xml";

	requests[13] = datafolder + "UpdateSensorDescription.xml";

	requests[14] = datafolder + "DeleteSensorInfo_deleteReference.xml";
	requests[15] = datafolder + "DeleteSensorInfo.xml";

	requests[16] = datafolder + "InsertSensorStatus.xml";
	requests[17] = placeholderIndex;

	requests[18] = datafolder + "SearchSensor_bySearchCriteria.xml";
	requests[19] = datafolder + "SearchSensor_bySensorIDInSIR.xml";
	requests[20] = datafolder + "SearchSensor_byServiceDescription.xml";

	requests[21] = datafolder + "ConnectToCatalog.xml";
	requests[22] = datafolder + "ConnectToCatalog_NowAndSchedulePeriod.xml";
	requests[23] = datafolder + "DisconnectFromCatalog.xml";

	// fill the select element
	var selRequest = document.getElementById("selRequest");

	for ( var i = 0; i < requests.length; i++) {
		if (requests[i] == placeholderIndex) {
			// skip this one
		} else {
			try {
				var name = requests[i].substring(requests[i]
						.lastIndexOf(datafolder)
						+ datafolder.length, requests[i].length);
				selRequest.add(new Option(name, requests[i]), null);
			} catch (err) {
				var txt = "";
				txt += "Error loading file: " + requests[i];
				txt += "Error: " + err + "\n\n";
				var requestTextarea = document
						.getElementById("requestTextarea").value = "";
				requestTextarea.value += txt;
			}
		}
	}
}

function initEditor() {
	editor = CodeMirror.fromTextArea("requestTextarea", {
		height : "380px",
		parserfile : "parsexml.js",
		stylesheet : "codemirror/xmlcolors.css",
		path : "codemirror/",
		lineNumbers : true,
		content : defaultString
	});
}

function insertSelected() {
	var selObj = null;
	selObj = document.getElementById("selRequest");
	if (selObj == null) {
		editor.setCode("Could not get element 'selRequest'!");
		return;
	}

	try {
		var requestString = "";

		if (selObj.selectedIndex != 0) {
			// Handle selection of empty drop down entry.
			requestString = getFile(selObj.options[selObj.selectedIndex].value);
		} else {
			requestString = defaultString;
		}

		if (requestString == null) {
			requestString = "Sorry! There is a problem with the Server, please refresh the page.";
		}

		editor.setCode(requestString);
	} catch (err) {
		var txt = "";
		txt += "Error loading file: "
				+ selObj.options[selObj.selectedIndex].value;
		txt += "Error: " + err + "\n\n";
		editor.setCode(txt);
	}
}

function getFile(fileName) {
	oxmlhttp = null;
	try {
		oxmlhttp = new XMLHttpRequest();
		oxmlhttp.overrideMimeType("text/xml");
	} catch (e) {
		try {
			oxmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			return null;
		}
	}
	if (!oxmlhttp)
		return null;
	try {
		oxmlhttp.open("POST", fileName, false);
		oxmlhttp.send(null);
	} catch (e) {
		return null;
	}
	return oxmlhttp.responseText;
}