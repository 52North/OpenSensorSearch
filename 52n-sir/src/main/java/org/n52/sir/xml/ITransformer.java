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

import java.io.FileNotFoundException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SystemType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sir.datastructure.SirSensorDescription;

import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryPackageDocument;

/**
 * 
 * An object that implements this interface can be used for transformation of one XML document to another. The
 * transformation can include validation.
 * 
 * The transformation can be based on different kinds of inputs and outputs: Apart from the method relying on
 * java.xml.transform classes there is a convenience method for XMLBeans using {@link XmlObject}.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public interface ITransformer {

    public static final boolean IS_VALIDATING_DEFAULT = true;

    public static final RegistryPackageDocument PROCESSING_ERROR_OBJECT = RegistryPackageDocument.Factory.newInstance();

    public static final RegistryPackageDocument TRANSFORMATION_ERROR_OBJECT = RegistryPackageDocument.Factory.newInstance();

    /**
     * 
     * @return If the transformer perfoms validation of incoming and outgoing documents.
     */
    public abstract boolean isValidating();

    /**
     * 
     * @param Set
     *        if the transformer perfoms a validation before and after transformation.
     */
    public abstract void setValidating(boolean b);

    /**
     * 
     * @param sourceDoc
     * @return
     * @throws XmlException
     * @throws TransformerException
     */
    public abstract XmlObject transform(SensorMLDocument smlDoc) throws XmlException, TransformerException;

    /**
     * 
     * @param sensor
     * @return
     * @throws TransformerException
     * @throws XmlException
     */
    public abstract XmlObject transform(SirSensorDescription sensor) throws XmlException, TransformerException;

    /**
     * 
     * @param input
     * @return
     * @throws TransformerException
     * @throws FileNotFoundException
     */
    public abstract Result transform(Source input) throws TransformerException, FileNotFoundException;

    /**
     * 
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws TransformerException
     */
    public abstract Result transform(String file) throws FileNotFoundException, TransformerException;

    /**
     * 
     * @param copy
     * @return
     * @throws TransformerException
     * @throws XmlException
     */
    public abstract XmlObject transform(SystemType copy) throws XmlException, TransformerException;

}