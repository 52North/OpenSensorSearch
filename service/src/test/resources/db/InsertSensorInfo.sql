-- add reference String WORKS
-- INSERT INTO service (service_url,service_type) SELECT 'http://mars.uni-muenster.de:8080/OWS5SOS/sos','SOS' WHERE NOT EXISTS (SELECT service_url,service_type FROM service WHERE (service_url='http://mars.uni-muenster.de:8080/OWS5SOS/sos' AND service_type='SOS'));
-- INSERT INTO sensor_service (service_id,sensor_id_sir,service_spec_id) SELECT (SELECT service_id FROM service WHERE (service_url='http://v-swe.uni-muenster.de:8080/WeatherSOS/sos' AND service_type='SOS')), '361464115','ServiceSpecID';

-- delete reference String DONT WORKS
-- DELETE FROM sensor_service WHERE (service_id=(SELECT service_id FROM service WHERE (service_url='http://mars.uni-muenster.de:8080/OWS5SOS/sos' AND service_type='SOS')) AND (sensor_id_sir = '2025009438') AND (service_spec_id = 'urn:ogc:object:procedure:CITE:WeatherService:LGA'));
DELETE FROM service USING sensor_service WHERE NOT EXISTS (SELECT service_id FROM service WHERE (service_url='http://mars.uni-muenster.de:8080/OWS5SOS/sos' AND service_type='SOS'))

-- get sensorID in SIR WORKS
-- SELECT sensor_service.sensor_id_sir FROM sensor_service, service, sensor WHERE ((service.service_url = 'http://v-swe.uni-muenster.de:8080/WeatherSOS/sos') AND (service.service_type = 'SOS') AND (sensor_service.service_spec_id = 'urn:ogc:object:feature:OSIRIS-HWS:efeb807b-bd24-4128-a920-f6729bcdd111') AND (sensor.sensor_id_sir = sensor_service.sensor_id_sir) AND (sensor_service.service_id = service.service_id));

-- delete sensor
--DELETE FROM sensor
--DELETE FROM sensor_service
--DELETE FROM service
--DELETE FROM phenomenon
--DELETE FROM sensor_phen

-- insert sensor
--INSERT INTO sensor
--INSERT INTO phenomenon
--INSERT INTO sensor_phen
--INSERT INTO service
--INSERT INTO sensor_service


-- update sensor
--UPDATE sensor
--UPDATE sensor_service
