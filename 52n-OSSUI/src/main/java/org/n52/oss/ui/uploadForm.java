package org.n52.oss.ui;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class uploadForm{
	private String name;
	//added to allow spring to instantiate
	public uploadForm(){
		
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public CommonsMultipartFile getFile() {
		return this.file;
	}
	public void setFile(CommonsMultipartFile file) {
		this.file = file;
	}
	private CommonsMultipartFile file;
	
}
