/**
 * ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * @author Yakoub
 */
package org.n52.sir.ds.pgsql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import org.joda.time.DateTime;
import org.n52.sir.ds.IInsertRemoteHarvestServer;
import org.n52.sir.util.SHA1HashGenerator;
import org.n52.sir.util.ShortAlphanumericIdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PGSQLInsertRemoteHarvestServer implements IInsertRemoteHarvestServer {
	private static Logger log = LoggerFactory
			.getLogger(PGSQLInsertRemoteHarvestServer.class);


	/**
	 * Connection pool for creating connections to the DB
	 */
	private PGConnectionPool cpool;
	
	public  PGSQLInsertRemoteHarvestServer(PGConnectionPool cpool){
		this.cpool = cpool;
		
	}
	

	@Override
	public String insertRemoteServer(String url) {
		Connection con = null;
		Statement stmt = null;
		
		try {
			con = this.cpool.getConnection();
			stmt = con.createStatement();
			String insertQuery = insertRemoteServerString(url);
			log.info(insertQuery);
			stmt.execute(insertQuery);
			String authtoken = null;
			ResultSet rs = stmt.executeQuery(searchByURLQuery(url));
			if(rs.next()){
				authtoken = rs.getString(PGDAOConstants.AUTH_TOKEN);
			}
			return authtoken;
		} catch (Exception e) {
			log.error("Cannot insert harvest Script",e);
			return null;
		}

	}
	
	public String getRemoteSensorServer(String auth_token){
		Connection con = null;
		Statement stmt = null;
		try {
			con = this.cpool.getConnection();
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(searchByAuthTokenQuery(auth_token));
			String url = null;;
			if(rs.next()){
				 url = rs.getString(PGDAOConstants.SERVER_URL);
			}
			return url;
		} catch (Exception e) {
			log.error("Cannot insert harvest Script",e);
			return null;
		}

	}

	@Override
	public int getRemoteServerHarvestState(String authToken) {
		return 0;
	}

	@Override
	public String harvestRemoteServer(String authToken) {
		return getRemoteSensorServer(authToken);
	}

	private String insertRemoteServerString(String url){
		String hash = new Date().getTime()+url;
		String _hash = new SHA1HashGenerator().generate(hash);
		if(_hash == null){
			log.error("Cannot create SHA1 hash");
			return null;
		}
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO ");
		query.append(PGDAOConstants.remoteHarvestSensor);
		query.append("(");
		query.append(PGDAOConstants.SERVER_URL);
		query.append(",");
		query.append(PGDAOConstants.AUTH_TOKEN);
		query.append(") values(");
		query.append("'");
		query.append(url);
		query.append("'");
		query.append(",");
		query.append("'");
		query.append(_hash);
		query.append("'");
		query.append(");");
		log.info(query.toString());
		return query.toString();
	}
	private String searchByAuthTokenQuery(String auth_token){
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT ");
		builder.append(PGDAOConstants.SERVER_URL);
		builder.append(" FROM ");
		builder.append(PGDAOConstants.remoteHarvestSensor);
		builder.append(" WHERE ");
		builder.append(PGDAOConstants.AUTH_TOKEN);
		builder.append(" LIKE ");
		builder.append("'");
		builder.append(auth_token);
		builder.append("'");
		return builder.toString();
	}
	private String searchByURLQuery(String url){
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT ");
		builder.append(PGDAOConstants.AUTH_TOKEN);
		builder.append(" FROM ");
		builder.append(PGDAOConstants.remoteHarvestSensor);
		builder.append(" WHERE ");
		builder.append(PGDAOConstants.SERVER_URL);
		builder.append(" LIKE ");
		builder.append("'");
		builder.append(url);
		builder.append("'");
		return builder.toString();
	}
	
	private String updateState(String auth_token,int state){
		//TODO yakoub implement updating harvest state HAR31
		return "";
	}

}