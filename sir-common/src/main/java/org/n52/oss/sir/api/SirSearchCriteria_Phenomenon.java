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
package org.n52.oss.sir.api;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.util.Tools;
import org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria.Phenomenon;
import org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria.Phenomenon.SORParameters;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class SirSearchCriteria_Phenomenon {

    private SirMatchingType matchingType;

    private String phenomenonName;

    private int searchDepth;

    private String sorUrl;

    /**
     * 
     * @param phenomenon
     * @throws OwsExceptionReport
     */
    public SirSearchCriteria_Phenomenon(Phenomenon phenomenon) throws OwsExceptionReport {

        this.phenomenonName = phenomenon.getPhenomenonName();

        if (phenomenon.isSetSORParameters()) {
            SORParameters sorParams = phenomenon.getSORParameters();
            this.sorUrl = sorParams.getSORURL();
            this.searchDepth = sorParams.getSearchDepth();
            // TODO test if this still works after schema overhaul
            this.matchingType = SirMatchingType.getSirMatchingType(sorParams.getMatchingType().toString());
        }
    }

    /**
     * @param phenomenonName
     * 
     */
    public SirSearchCriteria_Phenomenon(String phenomenonName) {
        this.phenomenonName = phenomenonName;
    }

    /**
     * @param phenomenonName
     * @param sorUrl
     * @param matchingType
     * @param searchDepth
     */
    public SirSearchCriteria_Phenomenon(String phenomenonName,
                                        String sorUrl,
                                        SirMatchingType matchingType,
                                        int searchDepth) {
        this.phenomenonName = phenomenonName;
        this.sorUrl = sorUrl;
        this.matchingType = matchingType;
        this.searchDepth = searchDepth;
    }

    /**
     * @return the matchingType
     */
    public SirMatchingType getMatchingType() {
        return this.matchingType;
    }

    /**
     * @return the phenomenonName
     */
    public String getPhenomenonName() {
        return this.phenomenonName;
    }

    /**
     * @return the searchDepth
     */
    public int getSearchDepth() {
        return this.searchDepth;
    }

    /**
     * @return the sorUrl
     */
    public String getSorUrl() {
        return this.sorUrl;
    }

    /**
     * @param matchingType
     *        the matchingType to set
     */
    public void setMatchingType(SirMatchingType matchingType) {
        this.matchingType = matchingType;
    }

    /**
     * @param phenomenonName
     *        the phenomenonName to set
     */
    public void setPhenomenonName(String phenomenonName) {
        this.phenomenonName = phenomenonName;
    }

    /**
     * @param searchDepth
     *        the searchDepth to set
     */
    public void setSearchDepth(int searchDepth) {
        this.searchDepth = searchDepth;
    }

    /**
     * @param sorUrl
     *        the sorUrl to set
     */
    public void setSorUrl(String sorUrl) {
        this.sorUrl = sorUrl;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SirSearchCriteria_Phenomenon [phenomenonName: ");
        sb.append(this.phenomenonName);
        sb.append(", SOR parameters: URL = ");
        sb.append(this.sorUrl);
        sb.append(", matching type = ");
        sb.append(this.matchingType);
        sb.append(", search depth = ");
        sb.append(this.searchDepth);
        sb.append("]");
        return sb.toString();
    }

    /**
     * 
     * @return true if all parameters for SOR are given
     */
    public boolean usesSOR() {
        if (this.sorUrl == null || this.matchingType == null)
            return false;
        return Tools.noneEmpty(new String[] {this.sorUrl,
                                             this.matchingType.toString(),
                                             Integer.toString(this.searchDepth)});
    }

}