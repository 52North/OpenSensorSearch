/**
 * ﻿    ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
/** @author Yakoub
 */

package org.n52.oss.ui.controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.n52.oss.ui.OSSConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;

@Controller
@RequestMapping(
        value = "/register")
public class RegisterController {

    @RequestMapping(
            value = "/")
    public String index(ModelMap map)
    {
        return "register/index";
    }

    @RequestMapping(
            value = "/user")
    public String registerUser(@ModelAttribute (value="username") String username,@ModelAttribute (value="password") String password,ModelMap map,RedirectAttributes rs)
    {
        try {
            HttpPost post = new HttpPost(OSSConstants.BASE_URL+"/OpenSensorSearch/api/user/register");
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(new BasicNameValuePair("username", username));
            pairs.add(new BasicNameValuePair("password", password));
            post.setEntity(new UrlEncodedFormEntity(pairs));

            HttpClient client = new DefaultHttpClient();
            HttpResponse resp = client.execute(post);
            StringBuilder result = new StringBuilder();
            String s = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
            while ((s = reader.readLine()) != null)
                result.append(s);

            status reg_result = new Gson().fromJson(result.toString(), status.class);
            if(reg_result.success){
                map.put("RegisterSucceded", true);
                return "redirect:/";
            }
            else{
                String errorMsg = reg_result.reason == null ? "Cannot register currently" : reg_result.reason;
                map.put("RegisterFailed", true);
                map.put("ErrorMsg",errorMsg);
                return "register/index";
            }
                
            
        } catch (Exception e) {
            return null;
        }

    }

    public class status {
        public boolean success;
        public String reason;
    }
}
