function insert(){
dao = new org.n52.sir.ds.solr.SOLRSearchSensorDAO();
sensor = new org.n52.sir.datastructure.SirSensor();
keywords = new java.util.ArrayList();
keywords.add("javascript");
keywords.add("harvest");
sensor.setKeywords(keywords);
//set contacts
contacts = new java.util.ArrayList();
contacts.add("rhino");
contacts.add("52north");
sensor.setContacts(contacts);
//add location
sensor.setLongitude("1.5");
sensor.setLatitude("3");

insert = new org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO();
return insert.insertSensor(sensor);
}
insert();