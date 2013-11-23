/**
 * ﻿    ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
/** @author Yakoub
 */

package org.n52.oss.ui;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class UploadForm {
    private String name;

    private CommonsMultipartFile file;

    private String license;

    public UploadForm() {
        // added to allow spring to instantiate
    }

    public CommonsMultipartFile getFile() {
        return this.file;
    }

    public String getLicense() {
        return this.license;
    }

    public String getName() {
        return this.name;
    }

    public void setFile(CommonsMultipartFile file) {
        this.file = file;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public void setName(String name) {
        this.name = name;
    }
}
