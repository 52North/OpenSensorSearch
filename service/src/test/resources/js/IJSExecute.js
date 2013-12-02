function insert(){
dao = new org.n52.sir.ds.solr.SOLRSearchSensorDAO();
sensor = new org.n52.sir.datastructure.SirSensor();
keywords = new java.util.ArrayList();
keywords.add("harvested");
sensor.setKeywords(keywords);
insert = new org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO();
return insert.insertSensor(sensor);
}
insert();