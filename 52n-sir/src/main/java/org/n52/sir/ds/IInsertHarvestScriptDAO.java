package org.n52.sir.ds;


public interface IInsertHarvestScriptDAO {
	//returns the identifier for the sensor
	 public String insertScript(String path,String username,int version);
	 //get the path for retrieval
	 public String getScriptPath(String identifier);
	 
}
