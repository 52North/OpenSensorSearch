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

import java.util.Collection;

import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.ows.OwsExceptionReport;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public interface BadgeDAO {

    public abstract String addEarnedBadge(SirSensor sensor, String badgeId) throws OwsExceptionReport;

    public abstract String addEarnedBadge(SirSensor sensor, Badge badge) throws OwsExceptionReport;

    public abstract String removeEarnedBadge(SirSensor sensor, String badgeId) throws OwsExceptionReport;

    public abstract String removeEarnedBadge(SirSensor sensor, Badge badge) throws OwsExceptionReport;

    public abstract String getBadgesForSensor(String sensorId) throws OwsExceptionReport;

    public abstract String getBadgesForService(String sensorId) throws OwsExceptionReport;

    public abstract Collection<Badge> getAllBadges();

    public abstract Badge getBadge(String badgeId);

    /**
     * 
     * @param b
     *        new badge to store
     * @return the id of the newly created badge
     */
    public abstract String addBadge(Badge b);

    /**
     * 
     * @param b
     *        badge to delete
     * @return true if the badge was deleted and removed from all bearing resources
     */
    public abstract boolean removeBadge(Badge b);

    /**
     * @return the ids of all sensors bearing a the given badge
     */
    public abstract Collection<String> getBearingSensors(Badge b);

    /**
     * @return the ids of all sensors bearing a the given badge
     */
    public abstract Collection<String> getBearingServices(Badge b);

}