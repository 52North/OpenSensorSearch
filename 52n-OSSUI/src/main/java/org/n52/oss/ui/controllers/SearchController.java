package org.n52.oss.ui.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class SearchController {
	@RequestMapping("/")
	public String index(){
		return "search/index";
	}
}
