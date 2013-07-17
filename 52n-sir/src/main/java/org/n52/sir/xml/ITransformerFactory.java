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
package org.n52.sir.xml;

import net.opengis.sensorML.x101.SensorMLDocument.SensorML;

/**
 * 
 * Factory interface to encapsulate all document transformations. The used implementation of this interface
 * can be changed in the main config file.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public interface ITransformerFactory {

    /**
     * 
     * Method returns an instance of an {@link ITransformer} which is to be used for transforming a
     * {@link SensorML} document into a format that is accepted by a catalogue service.
     * 
     * @return
     */
    public abstract ITransformer getSensorMLtoCatalogXMLTransformer();

}
