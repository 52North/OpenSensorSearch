package org.n52.oss.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class Config {
	
	static{
		try {
			getLicenses();
		}catch(Exception e){
			//log here
		}
	}
	
	
	public static List<License> licenses;
	
	public static List<License>getLicensesList(){
		return licenses;
	}
		
	private static List<License> getLicenses() throws JsonSyntaxException, JsonIOException, FileNotFoundException{
		File f = new File(ClassLoader.getSystemResource("licenses.json").getFile());
		Gson gson = new Gson();
		Licenses list = gson.fromJson(new FileReader(f),Licenses.class);
		return list.licenses;
	}
}
