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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(
        value = "/admin")
public class AdminController {

    @RequestMapping("/validate")
    public String validateUserUI()
    {
        return "admin/validate";
    }

    @RequestMapping(
            method = RequestMethod.POST, value = "/validate")
    public String validateUser(@ModelAttribute(
            value = "username") String username,
            ModelMap map)
    {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String token = userDetails.getPassword();
            HttpPost post = new HttpPost(OSSConstants.BASE_URL+"/OpenSensorSearch/api/v1/user/validate");
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(new BasicNameValuePair("username", username));
            pairs.add(new BasicNameValuePair("auth_token", token));

            post.setEntity(new UrlEncodedFormEntity(pairs));

            HttpClient client = new DefaultHttpClient();
            HttpResponse resp = client.execute(post);
            map.put("ValidationCalled", true);
            if (resp.getStatusLine().getStatusCode() == 200) {
                map.put("ValidationMsg", username+" validated succesfully");
            } else {
                map.put("ValidationMsg", username+" couldn't be validated!");
            }
            return "admin/validate";
        } catch (Exception e) {
            map.put("ValidationCalled", true);
            map.put("ValidationMsg","Validation couldn't be done at the moment!");
            return "admin/validate";
            
        }

    }

}
