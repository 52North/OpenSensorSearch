package org.n52.oss.ui.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/remote")
public class RemoteController {
	
	@RequestMapping("/index")
	public String index(ModelMap map){
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
