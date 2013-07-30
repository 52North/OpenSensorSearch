package org.n52.sir.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
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
		if (SirConfigurator.getInstance() != null) {
			String pathStr = SirConfigurator.getInstance().getScriptsPath();
			File f = new File(pathStr + user);
			if (!f.exists())
				f.mkdir();
			File script = new File(pathStr + user + "/" + fileName);
			try {
				PrintWriter pw = new PrintWriter(script);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(uploadedInputStream));
				String s = null;
				while ((s = reader.readLine()) != null)
					pw.println(s);
				pw.flush();
//				pw.close();
//				reader.close();
				id = SirConfigurator.getInstance().getFactory()
						.insertHarvestScriptDAO()
						.insertScript(fileName, fileName, 1);
				log.info("Executing script");
				String result = jsEngine.execute(script);
				log.info("Script result:"+result);
			} catch (Exception e) {
				log.error("Exception on executing script:",e);
				return "";
			}
		}
		log.info(fileName + "." + type + ":was uploaded at:"
				+ System.currentTimeMillis());
		return id;
	}

}
