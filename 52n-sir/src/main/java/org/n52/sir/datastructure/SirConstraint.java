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

/**
 * @author Jan Schulte
 * 
 */
public class SirConstraint {

    /**
     * Types of Constraints
     * 
     * @author Jan Schulte
     * 
     */
    public enum ConsType {
        isBetween, isEqualTo, isGreaterThan, isGreaterThanOrEqualTo, isLessThan, isLessThanOrEqualTo, isNotEqualTo
    }

    /**
     * Value of Type
     * 
     * @author Jan Schulte
     * 
     */
    public enum ValueType {
        Boolean, Double, String
    }

    /**
     * Constraint type
     */
    private ConsType consType;

    /**
     * lower boundary for constraint isBetween
     */
    private double lowerBoundary;

    /**
     * upper boundary for constraint isBetween
     */
    private double upperBoundary;

    /**
     * Value if ValueType is boolean
     */
    private boolean valueBoolean;

    /**
     * Value if ValueType is Double
     */
    private double valueDouble;

    /**
     * Value it ValueType is String
     */
    private String valueString;

    /**
     * Value type
     */
    private ValueType valueType;

    /**
     * @return the consType
     */
    public ConsType getConsType() {
        return this.consType;
    }

    /**
     * @return the lowerBoundary
     */
    public double getLowerBoundary() {
        return this.lowerBoundary;
    }

    /**
     * @return the upperBoundary
     */
    public double getUpperBoundary() {
        return this.upperBoundary;
    }

    /**
     * @return the valueDouble
     */
    public double getValueDouble() {
        return this.valueDouble;
    }

    /**
     * @return the valueString
     */
    public String getValueString() {
        return this.valueString;
    }

    /**
     * @return the valueType
     */
    public ValueType getValueType() {
        return this.valueType;
    }

    /**
     * @return the valueBoolean
     */
    public boolean isValueBoolean() {
        return this.valueBoolean;
    }

    /**
     * @param consType
     *        the consType to set
     */
    public void setConsType(ConsType consType) {
        this.consType = consType;
    }

    /**
     * @param lowerBoundary
     *        the lowerBoundary to set
     */
    public void setLowerBoundary(double lowerBoundary) {
        this.lowerBoundary = lowerBoundary;
    }

    /**
     * @param upperBoundary
     *        the upperBoundary to set
     */
    public void setUpperBoundary(double upperBoundary) {
        this.upperBoundary = upperBoundary;
    }

    /**
     * @param valueBoolean
     *        the valueBoolean to set
     */
    public void setValueBoolean(boolean valueBoolean) {
        this.valueBoolean = valueBoolean;
    }

    /**
     * @param valueDouble
     *        the valueDouble to set
     */
    public void setValueDouble(double valueDouble) {
        this.valueDouble = valueDouble;
    }

    /**
     * @param valueString
     *        the valueString to set
     */
    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    /**
     * @param valueType
     *        the valueType to set
     */
    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (this.consType == ConsType.isEqualTo) {
            sb.append(" is equal to ");
            if (this.valueString != null) {
                sb.append(this.valueString);
            }
            if (this.valueDouble != Double.MAX_VALUE) {
                sb.append(this.valueDouble);
            }
            else {
                sb.append(this.valueBoolean);
            }
        }
        if (this.consType == ConsType.isNotEqualTo) {
            sb.append(" is not equal to ");
            if (this.valueString != null) {
                sb.append(this.valueString);
            }
            if (this.valueDouble != Double.MAX_VALUE) {
                sb.append(this.valueDouble);
            }
            else {
                sb.append(this.valueBoolean);
            }
        }
        if (this.consType == ConsType.isGreaterThan) {
            sb.append(" is greater than to " + this.valueDouble);
        }
        if (this.consType == ConsType.isLessThan) {
            sb.append(" is less than " + this.valueDouble);
        }
        if (this.consType == ConsType.isGreaterThanOrEqualTo) {
            sb.append("is greater than or equal to " + this.valueDouble);
        }
        if (this.consType == ConsType.isLessThanOrEqualTo) {
            sb.append(" is less than or equal to " + this.valueDouble);
        }
        if (this.consType == ConsType.isBetween) {
            sb.append(" is between " + this.lowerBoundary + " and " + this.upperBoundary);
        }
        return sb.toString();
    }
}
