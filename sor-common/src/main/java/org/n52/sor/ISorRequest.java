/**
 * ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.n52.sor;

import java.util.Arrays;

import org.n52.sor.OwsExceptionReport;
import org.n52.sor.reasoner.IReasoner.MatchingCode;
import org.x52North.sor.x031.GetMatchingDefinitionsRequestDocument.GetMatchingDefinitionsRequest.MatchingType;
import org.x52North.sor.x031.GetMatchingDefinitionsRequestDocument.GetMatchingDefinitionsRequest.MatchingType.Enum;

/**
 * @created 15-Okt-2008 16:25:09
 * @author Jan Schulte
 * @version 1.0
 */
public interface ISorRequest {

    public enum SorMatchingType {
        SUPER_TYPE, EQUIVALENT_TYPE, SUB_TYPE;

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

        public MatchingCode getMatchingCode() {
            if (this.equals(SorMatchingType.SUPER_TYPE))
                return MatchingCode.SUPER_CLASS;
            else if (this.equals(SorMatchingType.EQUIVALENT_TYPE))
                return MatchingCode.EQUIVALENT_CLASS;
            else if (this.equals(SorMatchingType.SUB_TYPE))
                return MatchingCode.SUB_CLASS;
            return null;
        }

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