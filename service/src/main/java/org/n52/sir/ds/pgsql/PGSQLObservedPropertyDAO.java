/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.sir.ds.pgsql;

import org.n52.oss.sir.api.ObservedProperty;

public class PGSQLObservedPropertyDAO {

    protected String insertObservedPropertyCommand(ObservedProperty obsProp) {
        StringBuffer cmd = new StringBuffer();

        cmd.append("INSERT INTO ");
        cmd.append(PGDAOConstants.phenomenon);
        cmd.append(" (");
        cmd.append(PGDAOConstants.phenomenonUrn);
        cmd.append(", ");
        cmd.append(PGDAOConstants.phenomenonUom);
        cmd.append(") SELECT '");
        cmd.append(obsProp.getUrn());
        cmd.append("', '");
        cmd.append(obsProp.getUom());
        cmd.append("' WHERE NOT EXISTS (SELECT ");
        cmd.append(PGDAOConstants.phenomenonUrn);
        cmd.append(", ");
        cmd.append(PGDAOConstants.phenomenonUom);
        cmd.append(" FROM ");
        cmd.append(PGDAOConstants.phenomenon);
        cmd.append(" WHERE (");
        cmd.append(PGDAOConstants.phenomenonUrn);
        cmd.append(" = '");
        cmd.append(obsProp.getUrn());
        cmd.append("' AND ");
        cmd.append(PGDAOConstants.phenomenonUom);
        cmd.append(" = '");
        cmd.append(obsProp.getUom());
        cmd.append("')) RETURNING ");
        cmd.append(PGDAOConstants.obsPropId);

        return cmd.toString();
    }

    protected String getIdQuery(ObservedProperty obsProp) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.obsPropId);
        query.append(" FROM ");
        query.append(PGDAOConstants.phenomenon);
        query.append(" WHERE (");
        query.append(PGDAOConstants.phenomenonUrn);
        query.append("='");
        query.append(obsProp.getUrn());
        query.append("' AND ");
        query.append(PGDAOConstants.phenomenonUom);
        query.append("='");
        query.append(obsProp.getUom());
        query.append("');");

        return query.toString();
    }

    protected String insertSensorObsPropCommand(String sensorDbId, String obsPropDbId) {
        StringBuilder cmd = new StringBuilder();

        cmd.append("INSERT INTO ");
        cmd.append(PGDAOConstants.sensorPhen);
        cmd.append(" (");
        cmd.append(PGDAOConstants.sensorIdSirOfSensPhen);
        cmd.append(", ");
        cmd.append(PGDAOConstants.phenomeonIdOfSensPhen);
        cmd.append(") SELECT '");
        cmd.append(sensorDbId);
        cmd.append("', '");
        cmd.append(obsPropDbId);
        cmd.append("' WHERE NOT EXISTS (SELECT ");
        cmd.append(PGDAOConstants.sensorIdSirOfSensPhen);
        cmd.append(", ");
        cmd.append(PGDAOConstants.phenomeonIdOfSensPhen);
        cmd.append(" FROM ");
        cmd.append(PGDAOConstants.sensorPhen);
        cmd.append(" WHERE (");
        cmd.append(PGDAOConstants.sensorIdSirOfSensPhen);
        cmd.append("='");
        cmd.append(sensorDbId);
        cmd.append("' AND ");
        cmd.append(PGDAOConstants.phenomeonIdOfSensPhen);
        cmd.append("='");
        cmd.append(obsPropDbId);
        cmd.append("'));");

        return cmd.toString();
    }

    protected String updateObservedPropertyCommand(ObservedProperty obsProp) {
        StringBuilder cmd = new StringBuilder();

        cmd.append("UPDATE ");
        cmd.append(PGDAOConstants.phenomenon);
        cmd.append(" SET ");
        cmd.append(PGDAOConstants.phenomenonUrn);
        cmd.append(" = '");
        cmd.append(obsProp.getUrn());
        cmd.append("', ");
        cmd.append(PGDAOConstants.phenomenonUom);
        cmd.append(" = '");
        cmd.append(obsProp.getUom());
        cmd.append("' WHERE ");
        cmd.append(PGDAOConstants.obsPropId);
        cmd.append(" = ");
        cmd.append(obsProp.getId());
        cmd.append(";");

        return cmd.toString();
    }

}
