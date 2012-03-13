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
package org.n52.sir.datastructure;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class SirTimePeriod {

    /**
     * end of measure
     */
    private Date endTime = new GregorianCalendar(2099, 11, 31).getTime();

    /**
     * start of measure
     */
    private Date startTime = new Date(0);

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return this.endTime;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return this.startTime;
    }

    /**
     * @param endTime
     *        the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @param startTime
     *        the startTime to set
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(this.startTime);
        sb.append(" - ");
        sb.append(this.endTime);
        sb.append("]");
        return sb.toString();
    }

    /**
     * combindes this time period and the given time period for the maximal extend.
     * 
     * @param other
     */
    public void union(SirTimePeriod other) {
        if (this.startTime.after(other.getStartTime())) {
            this.startTime.setTime(other.getStartTime().getTime());
        }
        if (this.endTime.before(other.getEndTime())) {
            this.endTime.setTime(other.getEndTime().getTime());
        }
    }

}
