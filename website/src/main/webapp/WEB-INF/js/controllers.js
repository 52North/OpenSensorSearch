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
var ossApp = angular.module("ossApp", [ "ui.bootstrap" ]);

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

ossApp.controller("ossCtrl", [
		"$scope",
		"$http",
		"apiEndpoint",
		"apiEndpoint_sensors",
		"geolabelEndpoint",
		"feedbackServerEndpoint",
		"feedbackSubmitEndpoint",
		"targetCodespace",
		function($scope, $http, apiEndpoint, apiEndpoint_sensors,
				geolabelEndpoint, feedbackServerEndpoint,
				feedbackSubmitEndpoint, targetCodespace) {
			$scope.orderProp = "lastUpdate";

			$scope.apiEndpoint = apiEndpoint;
			$scope.apiEndpoint_sensors = apiEndpoint_sensors;
			$scope.geolabelEndpoint = geolabelEndpoint;
			$scope.feedbackServerEndpoint = feedbackServerEndpoint;
			$scope.feedbackSubmitEndpoint = feedbackSubmitEndpoint;
			$scope.targetCodespace = targetCodespace;

			$scope.q = '';

			$scope.ossSearch = function() {
				$scope.urlQuery = $scope.apiEndpoint + "?q=" + $scope.q;
				console.log("Sending query: " + $scope.urlQuery);

				$http({
					url : $scope.urlQuery,
					dataType : "json",
					method : "GET",
				}).success(function(data, status) {
					console.log("[OSS] got result:" + data);
					$scope.searchResult = data;
					$scope.status = status;
				}).error(function(data, status) {
					console.log("[OSS] got error:" + data);
				});
			};

			$scope.serviceUrl = function(reference) {
				var url = "";
				switch (reference.serviceType) {
				case "SOS":
					url = reference.serviceUrl
							+ "?REQUEST=GetCapabilities&amp;SERVICE=SOS";
					break;
				default:
					url = reference.serviceUrl;
					break;
				}

				return url;
			};

			$scope.createCodeAndCodespace = function(result) {
				var cacs = "target_code=" + result.sensorId
						+ "&target_codespace=" + $scope.targetCodespace
						+ "sensor";
				return cacs;
			};

			$scope.feedbackUrl = function(result, format) {
				var url = $scope.feedbackServerEndpoint + "?format=" + format
						+ "&" + $scope.createCodeAndCodespace(result);
				// alert("feedback url: " + url);
				return url;
			};

			// TODO add parent_metadata from SOS to URL
			$scope.geolabelUrl = function(result) {
				var feedbackUrl = $scope.feedbackUrl(result, "xml");
				var url = $scope.geolabelEndpoint + "?metadata="
						+ encodeURIComponent(result.sensorDescription.url)
						+ "&feedback=" + encodeURIComponent(feedbackUrl);
				// alert("geolabel url: " + url);
				return url;
			};

			$scope.createFeedbackSubmitLink = function(result) {
				// example:
				// https://geoviqua.stcorp.nl/devel/submit_feedback.html?target_code=fdd05b42-07e1-44b4-aa7f-8c7c82060fe3&target_codespace=opengis.uab.cat&source_page
				var url = $scope.feedbackSubmitEndpoint + "?"
						+ $scope.createCodeAndCodespace(result);
				// alert("feedback link for " + result.sensorId + ": " + url);
				return url;
			};
		} ]);

ossApp.controller("ossTypeaheadCtrl", [
		"$scope",
		"$http",
		"typeaheadEndpoint",
		function($scope, $http, typeaheadEndpoint) {

			$scope.selected = undefined;
			// Any function returning a promise object can be
			// used to load values
			// asynchronously
			$scope.getSuggestions = function(val) {
				console.log("typeahead: " + val);
				return $http.get(typeaheadEndpoint, {
					params : {
						q : val
					}
				}).then(
						function(res) {
							var suggestions = [];
							console.log(res.data);

							angular.forEach(res.data.results, function(item) {
								suggestions.push(item);
							});

							console.log(suggestions.length + " suggestions: "
									+ suggestions);

							return [ 'Alabama', 'Alaska', 'Arizona' ];
							// return suggestions;
						});
			};
		} ]);

ossApp.controller('ossFormatCtrl', function($scope) {
	$scope.availableResponseFormats = [ {
		// mimeType : "text/html",
		// name : "HTML"
		// }, {
		mimeType : "application/xml",
		name : "XML"
	}, {
		mimeType : "application/rss+xml",
		name : "RSS"
	}, {
		mimeType : "application/atom+xml",
		name : "Atom"
	}, {
		mimeType : "application/vnd.google-earth.kml+xml",
		name : "KML"
	}, {
		mimeType : "application/json",
		name : "JSON"
	} ];

	$scope.selectedFormat = $scope.availableResponseFormats[0]; // html
	$scope.update = function() {
		alert("Selected format: " + $scope.selectedFormat.name
				+ " with mimeType " + $scope.selectedFormat.mimeType);
	};
});

ossApp.controller("ossAlertCtrl", [ "$scope", function($scope) {
	// $scope.alerts = [ {
	// type : 'danger',
	// msg : 'Oh snap! Change a few things up and try submitting again.'
	// }, {
	// type : 'success',
	// msg : 'Well done! You successfully read this important alert message.'
	// } ];

	$scope.addAlert = function() {
		$scope.alerts.push({
			msg : "Another alert!"
		});
	};

	$scope.closeAlert = function(index) {
		$scope.alerts.splice(index, 1);
	};
} ]);

ossApp.controller("ossFeedbackModalCtrl", [ "$scope", "$modal", "$log",
		function($scope, $modal, $log) {
			// alert("from scope: " + $scope.result);
			$scope.open = function() {
				var modalInstance = $modal.open({
					templateUrl : 'feedbackModalContent.html',
					controller : ModalInstanceCtrl,
					resolve : {
						currentResult : function() {
							return $scope.result;
						},
						feedbackSubmitLink : function() {
							return $scope.createFeedbackSubmitLink($scope.result);
						},
						feedbackUrl : function() {
							return $scope.feedbackUrl($scope.result, "json");
						}
					}
				});

				modalInstance.result.then(function() {
					$log.info('Modal dismissed at: ' + new Date());
				});
			};
		} ]);

var ModalInstanceCtrl = [ "$scope", "$http", "$modalInstance", "currentResult", "feedbackUrl", "feedbackSubmitLink",
		function($scope, $http, $modalInstance, currentResult, feedbackUrl,
				feedbackSubmitLink) {
			$scope.current = currentResult;
			$scope.feedbackUrl = feedbackUrl;
			$scope.feedbackSubmitLink = feedbackSubmitLink;
			$scope.feedback = "loading...";
			
			console.log("[OSS] showing feedback for " + $scope.current + " using " + feedbackUrl);
			
			$scope.displayFeedback = function(data) {
				// $modalInstance.close($scope.selected.item);
				//alert("here: " + data);
				$scope.feedback = data;
			};
			
			$scope.ok = function() {
				// $modalInstance.close($scope.selected.item);
				$modalInstance.dismiss("submitOwnFeedback");
			};

			$scope.cancel = function() {
				$modalInstance.dismiss("cancel");
			};
			
			$http.get(feedbackUrl).success($scope.displayFeedback);

		} ];
