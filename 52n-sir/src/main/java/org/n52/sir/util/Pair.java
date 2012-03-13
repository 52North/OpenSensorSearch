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

/**
 * 
 * Simple class to hold a pair of two objects that belong together somehow.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class Pair<T1, T2> {

    private T1 first;

    private T2 second;

    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    /**
     * @return the first
     */
    public T1 getFirst() {
        return this.first;
    }

    /**
     * @return the second
     */
    public T2 getSecond() {
        return this.second;
    }

    /**
     * @param first
     *        the first to set
     */
    public void setFirst(T1 first) {
        this.first = first;
    }

    /**
     * @param second
     *        the second to set
     */
    public void setSecond(T2 second) {
        this.second = second;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pair <");
        sb.append(this.first.getClass().getName());
        sb.append(", ");
        sb.append(this.second.getClass().getName());
        sb.append("> [first=");
        sb.append(this.first);
        sb.append(", second=");
        sb.append(this.first.getClass().getName());
        sb.append("]");
        return sb.toString();
    }
}
