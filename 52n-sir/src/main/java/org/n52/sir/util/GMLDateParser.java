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
package org.n52.sir.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.n52.sir.SirConfigurator;

/**
 * @author Jan Schulte
 * 
 */
public class GMLDateParser {

    private static final GMLDateParser instance = new GMLDateParser();

    /**
     * 
     * @return
     */
    public static GMLDateParser getInstance() {
        return instance;
    }

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SirConfigurator.getInstance().getGmlDateFormat());

    /**
     * private constructor for singleton pattern
     */
    private GMLDateParser() {
        //
    }

    /**
     * Parses a Calendar object to a string.
     * 
     * @param timestamp
     * @return
     */
    public String parseDate(Calendar timestamp) {
        return this.simpleDateFormat.format(timestamp.getTime());
    }

    /**
     * Parses a string into a Calendar object.
     * 
     * @param time
     *        String to be parsed
     * @return the Calendar Object
     * @throws ParseException
     */
    public Calendar parseString(String time) throws ParseException {
        Calendar cal = Calendar.getInstance();
        Date date = null;
        date = this.simpleDateFormat.parse(time);
        cal.setTime(date);
        return cal;
    }
}
