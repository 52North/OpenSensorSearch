
package org.n52.sir.ds.pgsql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlTools {

    private static final Logger log = LoggerFactory.getLogger(SqlTools.class);

    public static String escapeSQLString(String text) {
        String s = text;
        if (text.contains("'")) {
            log.debug("Text contains character that has to be escaped before database insertion, namely ' .");
            s = s.replace("'", "\\'");
        }
        return s;
    }

}
