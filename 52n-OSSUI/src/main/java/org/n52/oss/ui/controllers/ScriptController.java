package org.n52.oss.ui.controllers;

import org.n52.oss.ui.uploadForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
	@RequestMapping(method=RequestMethod.POST,value="/upload")
	public String processForm(@ModelAttribute(value="uploadForm") uploadForm form,ModelMap map){
		String s= form.getFile().getFileItem().getName();
		map.addAttribute("name",s);
		return "script/success";
	}
	
	
	

	
}
