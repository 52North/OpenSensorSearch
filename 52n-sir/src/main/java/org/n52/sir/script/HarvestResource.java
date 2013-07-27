package org.n52.sir.script;

import java.io.InputStream;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.n52.sir.SIR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

public class HarvestResource {
    private static Logger log = LoggerFactory.getLogger(HarvestResource.class);

	@POST
	@Path("/submit")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String uploadHarvester(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		
		String fileName = fileDetail.getFileName();
		String type = fileDetail.getType();
		log.info(fileName+"."+type+":was uploaded at:"+System.currentTimeMillis());
		return fileName;
	}
	
}
