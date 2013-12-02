package org.n52.oss.sir.api;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria.Phenomenon.SORParameters.MatchingType;
import org.x52North.sor.x031.GetMatchingDefinitionsRequestDocument.GetMatchingDefinitionsRequest;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public enum SirMatchingType {
    EQUIVALENT_TYPE, SUB_TYPE, SUPER_TYPE;

    /**
     * 
     * @param schemaMatchingType
     * @return
     * @throws OwsExceptionReport
     */
    public static SirMatchingType getSirMatchingType(MatchingType schemaMatchingType) throws OwsExceptionReport {
        if (schemaMatchingType.equals(MatchingType.SUPER_TYPE)) {
            return SirMatchingType.SUPER_TYPE;
        }
        else if (schemaMatchingType.equals(MatchingType.EQUIVALENT_TYPE)) {
            return SirMatchingType.EQUIVALENT_TYPE;
        }
        else if (schemaMatchingType.equals(MatchingType.SUB_TYPE)) {
            return SirMatchingType.SUB_TYPE;
        }
        else {
            throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                         "MatchingType",
                                         "Your request was invalid: MatchingType parameter is missing or wrong!");
        }
    }

    /**
     * 
     * @param string
     * @return
     * @throws OwsExceptionReport
     */
    public static SirMatchingType getSirMatchingType(String string) throws OwsExceptionReport {
        if (string.equalsIgnoreCase(SirMatchingType.SUPER_TYPE.toString())) {
            return SirMatchingType.SUPER_TYPE;
        }
        else if (string.equalsIgnoreCase(SirMatchingType.EQUIVALENT_TYPE.toString())) {
            return SirMatchingType.EQUIVALENT_TYPE;
        }
        else if (string.equalsIgnoreCase(SirMatchingType.SUB_TYPE.toString())) {
            return SirMatchingType.SUB_TYPE;
        }
        else {
            throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                         "MatchingType",
                                         "Your request was invalid: MatchingType parameter is missing or wrong!");
        }
    }

    /**
     * 
     * @param sirMatchingType
     * @return
     * @throws OwsExceptionReport
     */
    public static org.x52North.sor.x031.GetMatchingDefinitionsRequestDocument.GetMatchingDefinitionsRequest.MatchingType.Enum getSorMatchingType(SirMatchingType sirMatchingType) throws OwsExceptionReport {
        if (sirMatchingType.equals(SirMatchingType.SUPER_TYPE)) {
            return GetMatchingDefinitionsRequest.MatchingType.SUPER_TYPE;
        }
        else if (sirMatchingType.equals(SirMatchingType.EQUIVALENT_TYPE)) {
            return GetMatchingDefinitionsRequest.MatchingType.EQUIVALENT_TYPE;
        }
        else if (sirMatchingType.equals(SirMatchingType.SUB_TYPE)) {
            return GetMatchingDefinitionsRequest.MatchingType.SUB_TYPE;
        }

        OwsExceptionReport er = new OwsExceptionReport();
        er.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                             "MatchingType",
                             "MatchingType not supported!");
        throw er;
    }

    /**
     * 
     * @return
     * @throws OwsExceptionReport
     */
    public org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria.Phenomenon.SORParameters.MatchingType.Enum getSchemaMatchingType() throws OwsExceptionReport {
        if (this.equals(SirMatchingType.SUPER_TYPE)) {
            return org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria.Phenomenon.SORParameters.MatchingType.SUPER_TYPE;
        }
        else if (this.equals(SirMatchingType.EQUIVALENT_TYPE)) {
            return org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria.Phenomenon.SORParameters.MatchingType.EQUIVALENT_TYPE;
        }
        else if (this.equals(SirMatchingType.SUB_TYPE)) {
            return org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria.Phenomenon.SORParameters.MatchingType.SUB_TYPE;
        }

        OwsExceptionReport er = new OwsExceptionReport();
        er.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                             "MatchingType",
                             "MatchingType not supported!");
        throw er;
    }
}