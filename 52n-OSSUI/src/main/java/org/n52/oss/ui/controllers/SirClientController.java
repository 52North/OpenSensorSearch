package org.n52.oss.ui.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/sir")
public class SirClientController {

    @RequestMapping(value = "/client")
    public String login(ModelMap map) {
        return "../sir/client/index";
    }

    @RequestMapping(value = "/form")
    public String loginWithFail(ModelMap map) {
        return "../sir/form/index";
    }

}
