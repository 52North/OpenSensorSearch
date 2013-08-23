package org.n52.oss.ui.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/script")
public class ScriptController {
	
	@RequestMapping("/index")
	public String index(ModelMap map){
		return "script/index";
	}
	@RequestMapping("/upload")
	public String upload(ModelMap map){
		return "script/upload";
	}
	@RequestMapping("/schedule")
	public String harvest(ModelMap map){
		return "script/schedule";
	}
	@RequestMapping("/status")
	public String status(ModelMap map){
		return "script/status";
	}

}
