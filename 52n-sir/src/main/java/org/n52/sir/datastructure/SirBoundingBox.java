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

import java.util.ArrayList;
import java.util.List;

import org.postgis.Geometry;
import org.postgis.Point;
import org.postgis.Polygon;

/**
 * @author Jan Schulte
 * 
 */
public class SirBoundingBox {

    private int dimension;

    /**
     * east coordinate
     */
    private double east;

    /**
     * north coordinate
     */
    private double north;

    /**
     * south coordinate
     */
    private double south;

    private int srid;

    /**
     * west coordinate
     */
    private double west;

    public SirBoundingBox(double east, double south, double west, double north) {
        this.east = east;
        this.south = south;
        this.west = west;
        this.north = north;
    }

    /**
     * 
     * @param boundingGeom
     */
    public SirBoundingBox(Geometry boundingGeom) {
        if (boundingGeom instanceof Polygon) {
            Polygon p = (Polygon) boundingGeom;
            this.east = Double.MAX_VALUE;
            this.south = Double.MAX_VALUE;
            this.west = Double.MIN_VALUE;
            this.north = Double.MIN_VALUE;
            for (int i = 0; i < p.numPoints(); i++) {
                Point point = p.getPoint(i);
                double x = point.getX();
                double y = point.getY();
                this.east = Math.min(this.east, x);
                this.west = Math.max(this.west, x);
                this.north = Math.max(this.north, y);
                this.south = Math.min(this.south, y);
            }
            this.srid = p.getSrid();
            this.dimension = p.getDimension();
        }
        else
            throw new UnsupportedOperationException("Cannot create bounding box from given geometry: " + boundingGeom);
    }

    /**
     * @return the dimension
     */
    public int getDimension() {
        return this.dimension;
    }

    /**
     * @return the east
     */
    public double getEast() {
        return this.east;
    }

    /**
     * 
     * @return
     */
    public List<String> getLowerCorner() {
        ArrayList<String> corner = new ArrayList<String>();
        // coordinate order: lat lon
        corner.add(Double.toString(Math.min(this.south, this.north)));
        corner.add(Double.toString(Math.min(this.east, this.west)));
        return corner;
    }

    /**
     * 
     * @return
     */
    public double[] getLowerCornerPoint() {
        return new double[] {Math.min(this.south, this.north), Math.min(this.east, this.west)};
    }

    /**
     * @return the north
     */
    public double getNorth() {
        return this.north;
    }

    /**
     * @return the south
     */
    public double getSouth() {
        return this.south;
    }

    /**
     * @return the srid
     */
    public int getSrid() {
        return this.srid;
    }

    /**
     * 
     * @return
     */
    public List<String> getUpperCorner() {
        ArrayList<String> corner = new ArrayList<String>();
        // coordiante order: lat lon
        corner.add(Double.toString(Math.max(this.south, this.north)));
        corner.add(Double.toString(Math.max(this.east, this.west)));
        return corner;
    }

    /**
     * 
     * @return
     */
    public double[] getUpperCornerPoint() {
        return new double[] {Math.max(this.south, this.north), Math.max(this.east, this.west)};
    }

    /**
     * @return the west
     */
    public double getWest() {
        return this.west;
    }

    /**
     * @param dimension
     *        the dimension to set
     */
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    /**
     * @param east
     *        the east to set
     */
    public void setEast(double east) {
        this.east = east;
    }

    /**
     * @param north
     *        the north to set
     */
    public void setNorth(double north) {
        this.north = north;
    }

    /**
     * @param south
     *        the south to set
     */
    public void setSouth(double south) {
        this.south = south;
    }

    /**
     * @param srid
     *        the srid to set
     */
    public void setSrid(int srid) {
        this.srid = srid;
    }

    /**
     * @param west
     *        the west to set
     */
    public void setWest(double west) {
        this.west = west;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Bounding Box: ");
        sb.append("East: " + this.east);
        sb.append(", South: " + this.south);
        sb.append(", West: " + this.west);
        sb.append(", North: " + this.north);
        return sb.toString();
    }

    /**
     * 
     * @param currentBB
     */
    public void union(SirBoundingBox other) {
        this.south = Math.min(this.south, other.getSouth());
        this.east = Math.min(this.east, other.getEast());
        this.north = Math.max(this.north, other.getNorth());
        this.west = Math.max(this.west, other.getWest());
    }

}
