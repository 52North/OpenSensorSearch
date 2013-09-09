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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.n52.oss.ui.uploadForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;

@Controller
@RequestMapping("/script")
public class ScriptController {
    public static LinkedHashMap<String, String> licenses = new LinkedHashMap<String, String>();

    private static Logger log = LoggerFactory.getLogger(ScriptController.class);
    @RequestMapping("/show/")
    public String selectScript(ModelMap map){
        return "script/selectScript";
    }
    @RequestMapping("/show/{scriptId}")
    public String show(@PathVariable String scriptId,
            ModelMap map)
    {
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet("http://localhost:8080/OpenSensorSearch/script/" + scriptId);
        try {
            HttpResponse resp = client.execute(get);
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
            String s = null;
            while ((s = reader.readLine()) != null)
                builder.append(s);
            ScriptContent content = new Gson().fromJson(builder.toString(), ScriptContent.class);
            map.addAttribute("content", content.content);
            return "script/show";
        } catch (Exception e) {
            map.addAttribute("error",e);
            return "script/error";
        }

    }

    @RequestMapping("/index")
    public String index(ModelMap map)
    {
        return "script/index";
    }

    @RequestMapping("/upload")
    public String upload(ModelMap map)
    {
        return "script/upload";
    }

    @RequestMapping("/schedule")
    public String harvest(ModelMap map)
    {
        return "script/schedule";
    }

    @RequestMapping(
            method = RequestMethod.POST, value = "/upload")
    public String processForm(@ModelAttribute(
            value = "uploadForm") uploadForm form,
            ModelMap map)
    {
        String s = form.getFile().getFileItem().getName();
        MultipartEntity multipartEntity = new MultipartEntity();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String token = userDetails.getPassword();

        // upload the file
        File dest = new File(s);
        try {
            System.out.println("Chosen license:" + form.getLicense());
            log.info("Chosen license:" + form.getLicense());
            form.getFile().transferTo(dest);
            UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            multipartEntity.addPart("file", new FileBody(dest));
            multipartEntity.addPart("user", new StringBody(details.getUsername()));
            multipartEntity.addPart("licenseCode", new StringBody(form.getLicense()));
            multipartEntity.addPart("auth_token", new StringBody(token));
            HttpPost post = new HttpPost("http://localhost:8080/OpenSensorSearch/script/submit");
            post.setEntity(multipartEntity);
            org.apache.http.client.HttpClient client = new DefaultHttpClient();
            HttpResponse resp;
            resp = client.execute(post);
            int responseCode = resp.getStatusLine().getStatusCode();
            StringBuilder builder = new StringBuilder();
            String str = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
            while ((str = reader.readLine()) != null)
                builder.append(str);

            if (responseCode == 200) {
                map.addAttribute("harvestSuccess", true);
                map.addAttribute("scriptID", builder.toString());
                map.addAttribute("license", form.getLicense());
                return "script/status";
            } else {
                map.addAttribute("harvestError", true);
                return "script/status";
            }
        } catch (Exception e) {
            map.addAttribute("errorMSG", e);
            return "script/status?fail";
        }
    }

    // private void addLicenseToHeader(File f,License l) throws IOException{
    // RandomAccessFile random = new RandomAccessFile(f, "rw");
    // random.seek(0); // to the beginning
    // random.write(prepareLicenseStr(l).getBytes());
    // random.close();
    // }
    // private String prepareLicenseStr(License l ){
    // StringBuilder builder=new StringBuilder();
    // builder.append("/*");
    // builder.append("This work is licensed under:");
    // builder.append(l.description);
    // builder.append(" For more details please visit:");
    // builder.append(l.link);
    // builder.append("*/");
    // return builder.toString();
    // }

    public class ScriptContent {
        public String content;
    }
}
