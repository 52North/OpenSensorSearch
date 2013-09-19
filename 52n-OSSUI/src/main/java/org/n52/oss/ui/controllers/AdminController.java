package org.n52.oss.ui.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.n52.oss.ui.uploadForm;
import org.n52.oss.ui.controllers.RegisterController.status;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;

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
            HttpPost post = new HttpPost("http://localhost:8080/OpenSensorSearch/api/user/validate");
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
