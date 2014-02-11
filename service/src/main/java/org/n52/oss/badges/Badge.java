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

package org.n52.oss.badges;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public interface Badge {

    /**
     * the type of resource that a badge can be given to
     */
    public static enum TargetType {

        SENSOR, SERVICE, SENSOR_AND_SERVICE;

        public static TargetType getType(String type) {
            switch (type) {
            case "sensor":
                return SENSOR;
            case "service":
                return SERVICE;
            case "sensor_and_service":
                return SENSOR_AND_SERVICE;
            default:
                break;
            }
            return null;
        }
    }

    public abstract String getId();

    public abstract String getShortName();

    public abstract String getLongName();

    public abstract String getDescription();

    public abstract TargetType getTargetType();

}
