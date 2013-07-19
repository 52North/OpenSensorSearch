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
