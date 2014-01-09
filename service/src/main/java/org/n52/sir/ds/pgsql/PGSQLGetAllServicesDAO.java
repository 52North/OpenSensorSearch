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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import org.n52.oss.sir.api.SirService;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionLevel;
import org.n52.sir.ds.IGetAllServicesDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class PGSQLGetAllServicesDAO implements IGetAllServicesDAO {

    private static Logger log = LoggerFactory.getLogger(PGSQLGetAllServicesDAO.class);

    private PGConnectionPool cpool;

    public PGSQLGetAllServicesDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    @Override
    public Collection<SirService> getServices() throws OwsExceptionReport {
        ArrayList<SirService> result = new ArrayList<>();

        StringBuffer query = new StringBuffer();
        query.append("SELECT ");
        query.append(PGDAOConstants.serviceUrl);
        query.append(", ");
        query.append(PGDAOConstants.serviceType);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(";");

        // execute query
        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            log.debug(">>>Database Query: {}", query.toString());
            ResultSet rs = stmt.executeQuery(query.toString());

            // if no phenomenon available give back empty list
            if (rs == null) {
                return result;
            }

            // get result as string
            while (rs.next()) {
                SirService serv = new SirService(rs.getString(PGDAOConstants.serviceUrl),
                                                 rs.getString(PGDAOConstants.serviceType));
                result.add(serv);
            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while query services for the getAllServices from database!", sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }

        return result;
    }

}
