package org.n52.oss.ui.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.n52.oss.ui.uploadForm;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
		File dest = new File(s);
		try {
			form.getFile().transferTo(dest);
			
			UserDetails details = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			multipartEntity.addPart("file", new FileBody(dest));
			multipartEntity.addPart("user", new StringBody(details.getUsername()));
			HttpPost post = new HttpPost(
					"http://localhost:8080/OpenSensorSearch/script/submit");
			post.setEntity(multipartEntity);
			org.apache.http.client.HttpClient client = new DefaultHttpClient();
			HttpResponse resp;
			resp = client.execute(post);
			int responseCode = resp.getStatusLine().getStatusCode();
			StringBuilder builder = new StringBuilder();
			String str = null;
			BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
			while((str=reader.readLine())!=null)
				builder.append(str);
			
			if (responseCode == 200){
				map.addAttribute("harvestSuccess",true);
				map.addAttribute("scriptID",builder.toString());
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
