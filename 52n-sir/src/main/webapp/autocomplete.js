/*
 * ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
$(document).ready(function() {
	$(".search-input").keyup(function() {
		var p = $(".search-input");
		var written = (p.val());
		if (written.toString().length > 1) {
			var tokens = written.toString().split(" ");
			var first = tokens.slice(0, tokens.length - 1).join(" ");
			var value = "/OpenSensorSearch/autocomplete?text=" + tokens[tokens.length - 1];
			$.get(value, function(data) {
				var d = data.toString();
				var available = d.split(",");
				for ( var i = 0; i < available.length; i++)
					available[i] = first + " " + available[i];
				$(".search-input").autocomplete({
					source : available
				});
			});
		}
	})
});