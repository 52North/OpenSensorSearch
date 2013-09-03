package org.n52.sir.datastructure;

import java.util.Comparator;

public class SirProximtyComparator implements Comparator<SirSearchResultElement> {
    private double longitude;

    private double latitude;

    public SirProximtyComparator(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public int compare(SirSearchResultElement o1,
            SirSearchResultElement o2)
    {
        SirBoundingBox b1 = ((SirSimpleSensorDescription) o1.getSensorDescription()).getBoundingBox();
        SirBoundingBox b2 = ((SirSimpleSensorDescription) o2.getSensorDescription()).getBoundingBox();
        double[] c1 = b1.getCenter();
        double[] c2 = b2.getCenter();
        
        double d1 =Math.pow((c1[0]-this.longitude),2)+Math.pow(c1[1]-this.latitude,2);
        double d2 =Math.pow((c2[0]-this.longitude),2)+Math.pow(c2[1]-this.latitude,2);
        return (int)(d1-d2);
    }
}
