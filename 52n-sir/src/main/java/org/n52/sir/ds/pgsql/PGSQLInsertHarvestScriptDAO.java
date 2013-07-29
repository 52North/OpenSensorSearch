/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.sir.ds.pgsql;

import java.sql.Connection;
import java.sql.Statement;

import org.n52.sir.ds.IInsertHarvestScriptDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PGSQLInsertHarvestScriptDAO implements IInsertHarvestScriptDAO {
	/**
	 * the logger, used to log exceptions and additionally information
	 */
	private static Logger log = LoggerFactory
			.getLogger(PGSQLInsertHarvestScriptDAO.class);

	/**
	 * Connection pool for creating connections to the DB
	 */
	private PGConnectionPool cpool;

	@Override
	public String insertScript(String path, String username, int version) {
		String insert;
		Connection con = null;
		Statement stmt = null;

		try {
			con = this.cpool.getConnection();
			stmt = con.createStatement();
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getScriptPath(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}

}
