BEGIN;
INSERT INTO service (service_id, service_url,service_type) SELECT '1','http://v-swe.uni-muenster.de:8080/WeatherSOS/sos','SOS'
--SELECT service_id FROM service WHERE (service_url='http://v-swe.uni-muenster.de:8080/WeatherSOS/sos' AND service_type='SOS');
--INSERT INTO sensor (sensor_id_sir, time_start, time_end) SELECT '-1348717092', '2009-02-01 00:00:00+01', '2009-02-01 00:00:00+01';
--INSERT INTO phenomenon (phenomenon_urn, uom) SELECT 'urn:x-ogc:def:property:OGC::RelativeHumidity', '%' WHERE NOT EXISTS (SELECT phenomenon_urn, uom FROM phenomenon WHERE (phenomenon_urn = 'urn:x-ogc:def:property:OGC::RelativeHumidity' AND uom = '%'));
--SELECT phenomenon_id FROM phenomenon WHERE (phenomenon_urn='urn:x-ogc:def:property:OGC::RelativeHumidity' AND uom='%');
--INSERT INTO sensor_phen (sensor_id_sir, phenomenon_id) SELECT '-1348717092', '2' WHERE NOT EXISTS (SELECT sensor_id_sir, phenomenon_id FROM sensor_phen WHERE (sensor_id_sir='-1348717092' AND phenomenon_id='2'));
--INSERT INTO sensor_service (service_id, sensor_id_sir, service_spec_id) SELECT (SELECT service_id FROM service WHERE (service_url = 'http://v-swe.uni-muenster.de:8080/WeatherSOS/sos' AND service_type = 'SOS')), '-1348717092', 'urn:ogc:object:feature:OSIRIS-HWS:efeb807b-bd24-4128-a920-f6729bcdd111' WHERE NOT EXISTS (SELECT service_id, sensor_id_sir, service_spec_id FROM sensor_service WHERE (service_id=(SELECT service_id FROM service WHERE (service_url = 'http://v-swe.uni-muenster.de:8080/WeatherSOS/sos' AND service_type = 'SOS')) AND sensor_id_sir = '-1348717092' AND service_spec_id = 'urn:ogc:object:feature:OSIRIS-HWS:efeb807b-bd24-4128-a920-f6729bcdd111'));
COMMIT;
