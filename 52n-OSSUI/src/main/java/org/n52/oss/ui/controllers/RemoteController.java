/**
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
/** @author Yakoub
 */

package org.n52.oss.ui.controllers;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/remote")
public class RemoteController {
	
	@RequestMapping("/index")
	public String index(ModelMap map){
		UserDetails userDetails =
				 (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String token = userDetails.getPassword();
		System.out.println("Login token:"+token);
		map.addAttribute("token",token);
		return "remote/index";
	}
	@RequestMapping("/upload")
	public String upload(ModelMap map){
		return "remote/upload";
	}
	@RequestMapping("/schedule")
	public String harvest(ModelMap map){
		return "remote/schedule";
	}
	@RequestMapping("/status")
	public String status(ModelMap map){
		return "remote/status";
	}

}
