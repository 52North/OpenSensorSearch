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
var ossApp = angular.module("oss.app", [ "ngRoute", "ui.bootstrap",
		"oss.controllers", "ngSanitize", "angucomplete-alt" ]);

ossApp.constant("typeaheadEndpoint", ossApiEndpoint + "/suggest");
ossApp.constant("apiEndpoint", ossApiEndpoint + "/search");
ossApp.constant("apiEndpoint_sensors", ossApiEndpoint + "/sensors");
// ossApp.constant("geolabelEndpoint",
// "http://geoviqua.dev.52north.org/glbservice/api/v1/svg");
ossApp.constant("geolabelEndpoint",
		"http://localhost:8080/glbservice/api/v1/svg");
ossApp.constant("feedbackServerEndpoint",
		"http://geoviqua.stcorp.nl/devel/api/v1/feedback/collections/search");
ossApp.constant("feedbackSubmitEndpoint",
		"https://geoviqua.stcorp.nl/devel/submit_feedback.html");
ossApp.constant("targetCodespace", "http://opensensorsearch.net/");

ossApp.config([ "$routeProvider", function($routeProvider) {
	$routeProvider.when("/conversion", {
		templateUrl : "resources/partials/conversion.html",
		controller : "oss.conversionControl"
	}).when("/discoveryProfile", {
		templateUrl : "resources/partials/discoveryProfile.html",
		controller : "oss.profileControl"
	}).when("/harvesting/developers", {
		templateUrl : "resources/partials/harvesting-developers.html",
		controller : "oss.harvestControl"
	}).when("/harvesting/script", {
		templateUrl : "resources/partials/harvesting-script.html",
		controller : "oss.harvestControl"
	}).when("/harvesting/oss", {
		templateUrl : "resources/partials/harvesting-oss.html",
		controller : "oss.harvestControl"
	}).when("/harvesting/ows", {
		templateUrl : "resources/partials/harvesting-ows.html",
		controller : "oss.harvestControl"
	}).when("/api", {
		templateUrl : "resources/partials/api.html",
		controller : "oss.apiControl"
	}).when("/about", {
		templateUrl : "resources/partials/about.html"
	}).when("/contact", {
		templateUrl : "resources/partials/about.html"
	}).when("/", {
		templateUrl : "resources/partials/search.html",
		controller : "oss.searchControl"
	}).otherwise({
		redirectTo : "/"
	});
} ]);