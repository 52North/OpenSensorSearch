package org.n52.sir.script;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.n52.sir.SirConfigurator;
import org.n52.sir.harvest.exec.IJSExecute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/script")
public class HarvestResource {
	private static Logger log = LoggerFactory.getLogger(HarvestResource.class);

	private IJSExecute jsEngine;

	@Inject
	public HarvestResource(IJSExecute exec) {
		this.jsEngine = exec;
	}

	@POST
	@Path("/submit")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String uploadHarvester(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@FormDataParam("user") String user) {

		String fileName = fileDetail.getFileName();
		String type = fileDetail.getType();
		String id = "";
		SirConfigurator config = SirConfigurator.getInstance();
		if (config != null) {
			String pathStr = config.getScriptsPath();
			log.info(pathStr);
			File dir = new File(pathStr + user);
			if (!dir.exists())
				dir.mkdir();

			File script = new File(pathStr + user + "/" + fileName);

			try {
				OutputStream writer = new FileOutputStream(script);
				int read =0 ;
				byte [] bytes = new byte[1024];
				while((read=uploadedInputStream.read(bytes))!=-1)
					writer.write(bytes,0,read);
				writer.flush();
				writer.close();
				id = config.getFactory().insertHarvestScriptDAO()
						.insertScript(fileName, fileName, 1);
				log.info("Executing script");
				String result = jsEngine.execute(script);
				log.info("Script result:" + result);
			} catch (Exception e) {
				log.error("Exception on executing script:", e);
				return "";
			}
		}
		log.info(fileName + "." + type + ":was uploaded at:"
				+ System.currentTimeMillis());
		return id;
	}

}
