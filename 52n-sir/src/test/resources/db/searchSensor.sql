SELECT sensor_service.sensor_id_sir, service.service_url, service.service_type, sensor.sensorml, sensor.text, sensor_service.service_spec_id 
FROM sensor_service, service, sensor, phenomenon, sensor_phen WHERE (
-- service criteria URL
((service.service_url = 'http://mars.uni-muenster.de:8080/OWS5SOS/sos') OR (false))
-- service criteria type
AND ((service.service_type = 'SOS') OR (false)) 
-- search text
AND (('urn:ogc:object:procedure:CITE:WeatherService:NYC' = ANY (sensor.text)) OR ('urn:ogc:object:procedure:CITE:WeatherService:TEB' = ANY (sensor.text)))
-- phenomeon
AND ((phenomenon.phenomenon_urn = 'urn:ogc:def:phenomenon:OGC:1.0.30:waterlevel') OR (phenomenon.phenomenon_urn = 'urn:x-ogc:def:property:OGC::RelativeHumidity'))
-- uom
AND ((phenomenon.uom = 'cm') OR (phenomenon.uom = '%'))
-- bounding box
AND (CONTAINS (GeometryFromText('POLYGON((-180 90,180 90,180 -90,-180 -90,-180 90))',-1),sensor.bbox))
-- timeperiod 
AND ((sensor.time_start BETWEEN '2009-09-08 00:00:01+01' AND '2009-09-11 00:00:00+01') OR (sensor.time_end BETWEEN '2009-09-08 00:00:01+01' AND '2009-09-11 00:00:00+01'))
AND (sensor.sensor_id_sir = sensor_service.sensor_id_sir) 
AND (sensor_service.service_id = service.service_id)
AND (sensor_phen.sensor_id_sir = sensor.sensor_id_sir)
AND (phenomenon.phenomenon_id = sensor_phen.phenomenon_id));

