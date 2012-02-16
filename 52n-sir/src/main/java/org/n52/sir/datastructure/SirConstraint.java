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
        isEqualTo, isNotEqualTo, isGreaterThan, isLessThan, isGreaterThanOrEqualTo, isLessThanOrEqualTo, isBetween
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
     * Value type
     */
    private ValueType valueType;

    /**
     * Value it ValueType is String
     */
    private String valueString;

    /**
     * Value if ValueType is boolean
     */
    private boolean valueBoolean;

    /**
     * Value if ValueType is Double
     */
    private double valueDouble;

    /**
     * lower boundary for constraint isBetween
     */
    private double lowerBoundary;

    /**
     * upper boundary for constraint isBetween
     */
    private double upperBoundary;

    /**
     * @return the consType
     */
    public ConsType getConsType() {
        return this.consType;
    }

    /**
     * @param consType
     *        the consType to set
     */
    public void setConsType(ConsType consType) {
        this.consType = consType;
    }

    /**
     * @return the valueType
     */
    public ValueType getValueType() {
        return this.valueType;
    }

    /**
     * @param valueType
     *        the valueType to set
     */
    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    /**
     * @return the valueString
     */
    public String getValueString() {
        return this.valueString;
    }

    /**
     * @param valueString
     *        the valueString to set
     */
    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    /**
     * @return the valueBoolean
     */
    public boolean isValueBoolean() {
        return this.valueBoolean;
    }

    /**
     * @param valueBoolean
     *        the valueBoolean to set
     */
    public void setValueBoolean(boolean valueBoolean) {
        this.valueBoolean = valueBoolean;
    }

    /**
     * @return the valueDouble
     */
    public double getValueDouble() {
        return this.valueDouble;
    }

    /**
     * @param valueDouble
     *        the valueDouble to set
     */
    public void setValueDouble(double valueDouble) {
        this.valueDouble = valueDouble;
    }

    /**
     * @return the lowerBoundary
     */
    public double getLowerBoundary() {
        return this.lowerBoundary;
    }

    /**
     * @param lowerBoundary
     *        the lowerBoundary to set
     */
    public void setLowerBoundary(double lowerBoundary) {
        this.lowerBoundary = lowerBoundary;
    }

    /**
     * @return the upperBoundary
     */
    public double getUpperBoundary() {
        return this.upperBoundary;
    }

    /**
     * @param upperBoundary
     *        the upperBoundary to set
     */
    public void setUpperBoundary(double upperBoundary) {
        this.upperBoundary = upperBoundary;
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
