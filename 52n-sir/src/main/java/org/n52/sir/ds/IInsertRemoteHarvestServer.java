package org.n52.sir.ds;

public interface IInsertRemoteHarvestServer {
	//takes the url and returns an id
	public String insertRemoteServer(String url);
	//gets the last state
	//returns one of pending - harvested (Successfully | failure )- not harvested 
	public int getRemoteServerHarvestState(String authToken);
	//harvest
	public void harvestRemoteServer(String authToken);
	//schedule to be added

}
