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

package org.n52.sor.request;

import java.util.Arrays;

import org.n52.sor.OwsExceptionReport;
import org.n52.sor.reasoner.JenaReasoner.MatchingCode;
import org.x52North.sor.x031.GetMatchingDefinitionsRequestDocument.GetMatchingDefinitionsRequest.MatchingType;
import org.x52North.sor.x031.GetMatchingDefinitionsRequestDocument.GetMatchingDefinitionsRequest.MatchingType.Enum;

/**
 * @created 15-Okt-2008 16:25:09
 * @author Jan Schulte
 * @version 1.0
 */
public interface ISorRequest {

    /**
     * 
     * @author Daniel Nüst
     * 
     */
    public enum SorMatchingType {
        SUPER_TYPE, EQUIVALENT_TYPE, SUB_TYPE;

        /**
         * 
         * @param schemaMatchingType
         * @return
         * @throws OwsExceptionReport
         */
        public static SorMatchingType getSorMatchingType(MatchingType.Enum schemaMatchingType) throws OwsExceptionReport {
            if (schemaMatchingType.equals(MatchingType.SUPER_TYPE)) {
                return SorMatchingType.SUPER_TYPE;
            }
            else if (schemaMatchingType.equals(MatchingType.EQUIVALENT_TYPE)) {
                return SorMatchingType.EQUIVALENT_TYPE;
            }
            else if (schemaMatchingType.equals(MatchingType.SUB_TYPE)) {
                return SorMatchingType.SUB_TYPE;
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
        public static SorMatchingType getSorMatchingType(String string) throws OwsExceptionReport {
            if (string.equalsIgnoreCase(SorMatchingType.SUPER_TYPE.toString())) {
                return SorMatchingType.SUPER_TYPE;
            }
            else if (string.equalsIgnoreCase(SorMatchingType.EQUIVALENT_TYPE.toString())) {
                return SorMatchingType.EQUIVALENT_TYPE;
            }
            else if (string.equalsIgnoreCase(SorMatchingType.SUB_TYPE.toString())) {
                return SorMatchingType.SUB_TYPE;
            }
            else {
                throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                             "MatchingType",
                                             "Your request was invalid: MatchingType parameter is missing or wrong!");
            }
        }

        /**
         * 
         * @return
         */
        public MatchingCode getJenaMatchingCode() {
            if (this.equals(SorMatchingType.SUPER_TYPE))
                return MatchingCode.SUPER_CLASS;
            else if (this.equals(SorMatchingType.EQUIVALENT_TYPE))
                return MatchingCode.EQUIVALENT_CLASS;
            else if (this.equals(SorMatchingType.SUB_TYPE))
                return MatchingCode.SUB_CLASS;
            return null;
        }

        /**
         * 
         * @return
         * @throws OwsExceptionReport
         */
        public Enum getSchemaMatchingType() throws OwsExceptionReport {
            if (this.equals(SorMatchingType.SUPER_TYPE)) {
                return MatchingType.SUPER_TYPE;
            }
            else if (this.equals(SorMatchingType.EQUIVALENT_TYPE)) {
                return MatchingType.EQUIVALENT_TYPE;
            }
            else if (this.equals(SorMatchingType.SUB_TYPE)) {
                return MatchingType.SUB_TYPE;
            }

            throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         "MatchingType",
                                         "MatchingType not supported! Choose one of "
                                                 + Arrays.toString(ISorRequest.SorMatchingType.values()));
        }
    }

}