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
var autocompleteEndpoint = ossApiEndpoint + "/suggest";

// http://api.jqueryui.com/autocomplete/
function ossAutocomplete(request, response) {
	var tokens = request.term.split(" ");
	var first = tokens.slice(0, tokens.length - 1).join(" ");
	var value = autocompleteEndpoint + "?q=" + tokens[tokens.length - 1];
	console.log("autocomplete: " + value);

	$.ajax({
		type : "GET",
		dataType: "json",
		url : value,
		success : function(data) {
			console.log("results: " + data.results);
			response(data.results);
		},
		error : function(err) {
			console.log(err);

			var d = err.responseText.toString();
			d = d.substring(1, d.length);
			d = d.substring(0, d.length - 1);
			var availableWords = d.split(",");
			for ( var i = 0; i < availableWords.length; i++)
				availableWords[i] = first + " " + availableWords[i];

			$("#inputSearch").autocomplete({
				source : availableWords
			});
			response({});
		}
	});
}

$(document).ready(function() {

	// http://jqueryui.com/autocomplete/
	$("#inputSearch").autocomplete({
		delay : 500,
		minLength : 3,
		source : ossAutocomplete
	});
});
