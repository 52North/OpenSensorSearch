SELECT status.sensor_id_sir, status.property_name, status.property_value, status.uom, status.time 
FROM sensor_service, service, sensor, phenomenon, sensor_phen, status 
WHERE (((true)) AND ((true)) AND ((true)) AND ((true)) AND ((true)) AND ((true)) AND ((true)) 
AND (sensor.sensor_id_sir = sensor_service.sensor_id_sir) 
AND (sensor_service.service_id = service.service_id) 
AND (sensor_phen.sensor_id_sir = sensor.sensor_id_sir) 
AND (phenomenon.phenomenon_id = sensor_phen.phenomenon_id)

AND 

(

((status.property_name = 'urn:osiris:sensorproperties:batterystate') AND (status.property_value = '50') AND (status.property_value <> '55') AND (status.uom = '%'))
OR
((status.property_name = 'urn:osiris:sensorproperties:batterystate3') AND (status.property_value = '50') AND (status.property_value <> '55') AND (status.uom = '%'))


))GROUP BY status.sensor_id_sir, status.property_name, status.property_value, status.uom, status.time; 