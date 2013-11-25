INSERT INTO sensor_service (service_id, sensor_id_sir, service_spec_id) SELECT '2','1','SPEC';
INSERT INTO service (service_id, service_url, service_type) SELECT '2','url','SOS';
INSERT INTO sensor (sensor_id_sir, time_start, time_end) SELECT '1','2009-02-01 00:00:00+01','2009-02-01 00:00:00+01';
INSERT INTO sensor_phen (sensor_id_sir, phenomenon_id) VALUES ('1','3');
INSERT INTO phenomenon (phenomenon_id, phenomenon_urn, uom) VALUES ('3','urn', 'uom');