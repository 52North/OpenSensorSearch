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

    public static final RegistryPackageDocument TRANSFORMATION_ERROR_OBJECT = RegistryPackageDocument.Factory.newInstance();

    public static final RegistryPackageDocument PROCESSING_ERROR_OBJECT = RegistryPackageDocument.Factory.newInstance();

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
     * @param copy
     * @return
     * @throws TransformerException
     * @throws XmlException
     */
    public abstract XmlObject transform(SystemType copy) throws XmlException, TransformerException;

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
     * @param sensor
     * @return
     * @throws TransformerException
     * @throws XmlException
     */
    public abstract XmlObject transform(SirSensorDescription sensor) throws XmlException, TransformerException;

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

}