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
package org.n52.sir.ds.solr;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SolrUtils {
	public static final String ISO8061UTC = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static String  getISO8601UTCString(Date d) {
		// Convert date to UTC date
		try{
		SimpleDateFormat formatter = new SimpleDateFormat();
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		SimpleDateFormat simple = new SimpleDateFormat("M/d/yy h:mm a");
		Date UTCDate = simple.parse(formatter.format(d.getTime()));
		
		SimpleDateFormat ISO8061Formatter = new SimpleDateFormat(ISO8061UTC);
		return ISO8061Formatter.format(UTCDate);
		}catch(Exception e){
			return null;
		}
		
	}

}
