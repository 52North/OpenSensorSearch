package org.n52.oss.ui.controllers;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.commons.httpclient.methods.multipart.FilePart;

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
	public String index(ModelMap map) {
		return "script/index";
	}

	@RequestMapping("/upload")
	public String upload(ModelMap map) {
		return "script/upload";
	}

	@RequestMapping("/schedule")
	public String harvest(ModelMap map) {
		return "script/schedule";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/upload")
	public String processForm(
			@ModelAttribute(value = "uploadForm") uploadForm form, ModelMap map) {
		String s = form.getFile().getFileItem().getName();
		MultipartEntity multipartEntity = new MultipartEntity();
		// upload the file
		File dest = new File(form.getFile().getName());
		try {
			form.getFile().transferTo(dest);
			multipartEntity.addPart("file", new FileBody(dest));
			multipartEntity.addPart("user", new StringBody("testUserTest"));
			HttpPost post = new HttpPost(
					"http://localhost:8080/SIR/harvest/script/submit");
			post.setEntity(multipartEntity);
			org.apache.http.client.HttpClient client = new DefaultHttpClient();
			HttpResponse resp;
			resp = client.execute(post);
			int responseCode = resp.getStatusLine().getStatusCode();

			map.addAttribute("name", s);
			if (responseCode == 200){
				map.addAttribute("harvestSuccess",true);
				return "script/status";
			}else{
				map.addAttribute("harvestError",true);
				return "script/status";
			}
		} catch (Exception e) {
			map.addAttribute("errorMSG", e);
			return "script/status?fail";
		}
	}

}
