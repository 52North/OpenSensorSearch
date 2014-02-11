/*
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
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
var editor = null;
var defaultString = "<!-- Insert your request here or select one of the examples from the menu above. -->";

$(document).ready(
		function() {
			var datafolder = window.location.href.substring(0,
					window.location.href.lastIndexOf("/form") + 5)
					+ "/requests/";
			console.log("Requests loaded from " + datafolder);

			initExamples(datafolder);

			editor = CodeMirror.fromTextArea(requestTextarea);
			// if (editor == null) {
			// editor = CodeMirror.fromTextArea("requestTextarea", {
			// /* height : "380px", */
			// mode : "xml",
			// /* path : "codemirror/", */
			// lineNumbers : true,
			// content : "test"
			// });
			// }
			
			$("form[name='requestform']").attr("action", sirEndpoint);
			$("#sirEndpointLabel").text(sirEndpoint);
		});

function initExamples(datafolder) {
	var placeholderIndex = "PLACEHOLDER";
	// load files
	var requests = new Array();
	requests[0] = datafolder + "GetCapabilities.xml";
	requests[1] = placeholderIndex;
	requests[2] = placeholderIndex;
	requests[3] = datafolder + "DescribeSensor.xml";

	requests[4] = datafolder + "GetSensorStatus_bySearchCriteria.xml";
	requests[5] = datafolder + "GetSensorStatus_bySensorIDInSIR.xml";
	requests[6] = datafolder + "GetSensorStatus_byServiceDescription.xml";
	requests[7] = placeholderIndex;
	requests[8] = placeholderIndex;
	requests[9] = placeholderIndex;
	requests[10] = datafolder + "HarvestService_SOS.xml";

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

function xmlToString(xmlData) { 

    var xmlString;
    //IE
    if (window.ActiveXObject){
        xmlString = xmlData.xml;
    }
    // code for Mozilla, Firefox, Opera, etc.
    else{
        xmlString = (new XMLSerializer()).serializeToString(xmlData);
    }
    return xmlString;
}

function insertSelected() {
	var selObj = null;
	selObj = document.getElementById("selRequest");
	if (selObj == null) {
		editor.setCode("Could not get element 'selRequest'!");
		return;
	}

	try {
		if (selObj.selectedIndex != 0) {
			// Handle selection of empty drop down entry.
			// requestString =
			// getFile(selObj.options[selObj.selectedIndex].value);
			$.ajax({
				url : selObj.options[selObj.selectedIndex].value,
				success : function(data) {
					var string = xmlToString(data);
					if (data == null) {
						string = "Sorry! There is a problem with the Server, please refresh the page.";
					}
					
					editor.setValue(string);
				}
			});
		} else {
			requestString = defaultString;
		}
	} catch (err) {
		var txt = "";
		txt += "Error loading file: "
				+ selObj.options[selObj.selectedIndex].value;
		txt += "\n\nError: " + err + "\n\n";
		editor.setValue(txt);
	}
}
