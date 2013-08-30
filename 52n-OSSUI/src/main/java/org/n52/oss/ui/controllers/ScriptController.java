package org.n52.oss.ui.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.n52.oss.config.Config;
import org.n52.oss.config.License;
import org.n52.oss.ui.uploadForm;
import org.n52.sir.SirConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	public static LinkedHashMap<String, String> licenses = new LinkedHashMap<String, String>();
	private static Logger log = LoggerFactory.getLogger(ScriptController.class);
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
			log.info("Chosen license:"+form.getLicense());
			log.info("Adding  license header");
			form.getFile().transferTo(dest);
			List<License> lists = Config.licenses;
			log.info("found : "+lists.size()+" headers");
			Iterator<License> iterator = lists.iterator();
			while(iterator.hasNext()){
				License l= iterator.next();
				if(l.code.equals(form.getLicense()))
				{
					addLicenseToHeader(dest, l);
					break;
				}
			}
			
			UserDetails details = (UserDetails) SecurityContextHolder
					.getContext().getAuthentication().getPrincipal();
			multipartEntity.addPart("file", new FileBody(dest));
			multipartEntity.addPart("user",
					new StringBody(details.getUsername()));
			HttpPost post = new HttpPost(
					"http://localhost:8080/OpenSensorSearch/script/submit");
			post.setEntity(multipartEntity);
			org.apache.http.client.HttpClient client = new DefaultHttpClient();
			HttpResponse resp;
			resp = client.execute(post);
			int responseCode = resp.getStatusLine().getStatusCode();
			StringBuilder builder = new StringBuilder();
			String str = null;
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					resp.getEntity().getContent()));
			while ((str = reader.readLine()) != null)
				builder.append(str);

			if (responseCode == 200) {
				map.addAttribute("harvestSuccess", true);
				map.addAttribute("scriptID", builder.toString());
				map.addAttribute("license",form.getLicense());
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
	private void addLicenseToHeader(File f,License l) throws IOException{
		RandomAccessFile random = new RandomAccessFile(f, "rw");
		random.seek(0); // to the beginning
		random.write(prepareLicenseStr(l).getBytes());
		random.close();
	}
	private String prepareLicenseStr(License l ){
		StringBuilder builder=new StringBuilder();
		builder.append("/*");
		builder.append("This work is licensed under:");
		builder.append(l.description);
		builder.append(" For more details please visit:");
		builder.append(l.link);
		builder.append("*/");
		return builder.toString();
	}

}
