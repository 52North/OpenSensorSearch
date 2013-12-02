/**
 * ﻿Copyright (C) 2013 52°North Initiative for Geospatial Open Source Software GmbH
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

package org.n52.oss.ui.beans;

import org.x52North.sir.x032.VersionAttribute;
import org.x52North.sir.x032.VersionAttribute.Version.Enum;

/**
 * TODO consider moving this to common module
 * 
 * @author Daniel
 * 
 */
public class ClientConstants {

    public enum CapabilitiesSection {
        All, Contents, OperationsMetadata, ServiceIdentification, ServiceProvider
    }

    public static final String SERVICE_NAME = "SIR";

    public static String[] getAcceptedServiceVersions() {
        return new String[] {"0.3.0", "0.3.1", "0.3.2"};
    }

    public static Enum getServiceVersionEnum() {
        return VersionAttribute.Version.X_0_3_2;
    }

}
