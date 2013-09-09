package org.n52.oss.ui.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/api")
public class ApiController {
    @RequestMapping("/")
    public String index(){
        return "api/index";
    }
}
