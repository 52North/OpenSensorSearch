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

package org.n52.oss.config;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class PropertyApplicationConstants implements ApplicationConstants {

    private String applicationVersion;
    private String applicationCommit;
    private String applicationTimestampt;

    @Inject
    protected PropertyApplicationConstants(@Named("application.version")
    String version, @Named("application.build.commit")
    String commit, @Named("application.build.timestamp")
    String timestamp) {
        this.applicationVersion = version;
        this.applicationCommit = commit;
        this.applicationTimestampt = timestamp;
    }

    @Override
    public String getApplicationVersion() {
        return this.applicationVersion;
    }

    @Override
    public String getApplicationCommit() {
        return this.applicationCommit;
    }

    @Override
    public String getApplicationTimestamp() {
        return this.applicationTimestampt;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Constants [applicationVersion=");
        builder.append(this.applicationVersion);
        builder.append(", applicationCommit=");
        builder.append(this.applicationCommit);
        builder.append(", applicationTimestampt=");
        builder.append(this.applicationTimestampt);
        builder.append("]");
        return builder.toString();
    }

}
