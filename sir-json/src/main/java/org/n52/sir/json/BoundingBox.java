/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.sir.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"lowerCornerPoint", "upperCornerPoint", "center"})
public class BoundingBox {

    public static final int NO_SRID = -1;

    private double east;

    private double north;

    private double south;

    private int srid = NO_SRID;

    private double west;

    public BoundingBox() {
        // empty constructor for deserialization
    }

    public BoundingBox(double east, double south, double west, double north) {
        this.east = east;
        this.south = south;
        this.west = west;
        this.north = north;
    }

    public double getEast() {
        return this.east;
    }

    public double[] getLowerCornerPoint() {
        return new double[] {Math.min(this.south, this.north), Math.min(this.east, this.west)};
    }

    public double getNorth() {
        return this.north;
    }

    public double getSouth() {
        return this.south;
    }

    public int getSrid() {
        return this.srid;
    }

    public double[] getUpperCornerPoint() {
        return new double[] {Math.max(this.south, this.north), Math.max(this.east, this.west)};
    }

    public double getWest() {
        return this.west;
    }

    public void setEast(double east) {
        this.east = east;
    }

    public void setNorth(double north) {
        this.north = north;
    }

    public void setSouth(double south) {
        this.south = south;
    }

    public void setSrid(int srid) {
        this.srid = srid;
    }

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

    public void union(BoundingBox other) {
        this.south = Math.min(this.south, other.getSouth());
        this.east = Math.min(this.east, other.getEast());
        this.north = Math.max(this.north, other.getNorth());
        this.west = Math.max(this.west, other.getWest());
    }

    public double[] getCenter() {
        return new double[] { (this.south + this.north) / 2, (this.east + this.west) / 2};
    }

}
