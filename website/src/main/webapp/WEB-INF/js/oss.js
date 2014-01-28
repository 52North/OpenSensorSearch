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
showPosition = function(position) {
	jQuery("#lat").val(position.coords.latitude);
	jQuery("#lng").val(position.coords.longitude);
	jQuery("#radius").val("1000");
	console.log("Retrieved location: " + position.coords.latitude + ", "
			+ position.coords.longitude);

	var str = "You are near: " + Number((position.coords.latitude).toFixed(3))
			+ ", " + Number((position.coords.longitude).toFixed(3));
	// $("#location_info").html(str);

	jQuery("#btnSearchNearby").tooltip("hide").attr("data-original-title", str)
			.tooltip("fixTitle").tooltip("show");
};

validate = function() {
	var q = document.forms["requestform"]["q"].value;
	if (q == null || q == "" || q.toString().trim().length == 0) {
		// $(document).trigger("add-alerts", {
		// message : "Please enter a search term.",
		// priority : "error"
		// });
		return false;
	}
	return true;
};

getOssShareURI = function() {
	return "test"; // TODO integrate this in a modal so that I can set the current sensor
};

configureSocialShare = function() {
	var infotext = '2 clicks for more privacy: Only after you click here the button is activated and you can share this item on a social network. Already at the activation of the button your data is sent to third parties &ndash; see <em>i</em>.';
	if ($('#socialshareprivacy').length > 0) {
		$('#socialshareprivacy')
				.socialSharePrivacy(
						{
							'services' : {
								'facebook' : {
									'status' : 'on',
									'dummy_img' : '',
									'txt_info' : infotext,
									'txt_fb_off' : 'not connected to Facebook',
									'txt_fb_on' : 'connected with Facebook',
									'perma_option' : 'on',
									'display_name' : 'Facebook',
									'referrer_track' : '',
									'language' : 'en_EN',
									'action' : 'recommend',
									'dummy_caption' : 'Recommend'
								},
								'twitter' : {
									'status' : 'on',
									'dummy_img' : '',
									'txt_info' : infotext,
									'txt_twitter_off' : 'not connected to Twitter',
									'txt_twitter_on' : 'connected with Twitter',
									'perma_option' : 'on',
									'display_name' : 'Twitter',
									'referrer_track' : '',
									'tweet_text' : getTweetText,
									'language' : 'en',
									'dummy_caption' : 'Tweet'
								},
								'gplus' : {
									'status' : 'on',
									'dummy_img' : '',
									'txt_info' : infotext,
									'txt_gplus_off' : 'not connected with Google+',
									'txt_gplus_on' : 'connected with Google+',
									'perma_option' : 'on',
									'display_name' : 'Google+',
									'referrer_track' : '',
									'language' : 'en'
								}
							},
							'info_link' : '',
							'txt_help' : 'If you active these fields with one click, information is sent to to Facebook, Twitter or Google and potentially stored.',
							'settings_perma' : 'Activate permantly and consent with data transmission:',
							'cookie_path' : '/oss/',
							'cookie_domain' : document.location.host,
							/*'cookie_expires' : '365',*/
							/*'css_path' : 'socialshareprivacy/socialshareprivacy.css',*/
							'uri' : getOssShareURI
						});
	}
};

jQuery(document)
		.ready(
				function() {
					// configureSocialShare();

					jQuery("#btnSearch").click(function() {
						jQuery("#lat").attr("disabled", true);
						jQuery("#lng").attr("disabled", true);
						jQuery("#radius").attr("disabled", true);
					});

					// activate all tooltips
					jQuery("[data-toggle='tooltip']").tooltip();

					jQuery("#btnSearchNearby")
							.click(
									function() {
										if (navigator.geolocation) {
											navigator.geolocation
													.getCurrentPosition(showPosition);
										} else {
											jQuery("#alerts")
													.append(
															'<div class="alert alert-danger">Your browser does not support	geolocation!<a class="close" data-dismiss="alert" href="#" aria-hidden="true">&times;</a></div>');
										}

										jQuery("#lat").attr("disabled", false);
										jQuery("#lng").attr("disabled", false);
										jQuery("#radius").attr("disabled",
												false);
									});

					jQuery.ajax({
						dataType : "json",
						url : ossApiEndpoint + "/statistics/sensors",
						success : function(data) {
							// console.log(data);
							jQuery("#statsSensors").html(data.sensors);
						}
					});

					jQuery.ajax({
						dataType : "json",
						url : ossApiEndpoint + "/statistics/phenomena",
						success : function(data) {
							// console.log(data);
							jQuery("#statsPhenonema").html(data.phenomena);
						}
					});

					jQuery.ajax({
						dataType : "json",
						url : ossApiEndpoint + "/statistics/services",
						success : function(data) {
							// console.log(data);
							jQuery("#statsServices").html(data.services);
						}
					});
				});
