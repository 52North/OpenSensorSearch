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

package org.n52.sir.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

import javax.xml.transform.TransformerException;

import net.opengis.ows.x11.BoundingBoxType;
import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.datastructure.SirBoundingBox;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirSensorDescription;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.datastructure.SirSimpleSensorDescription;
import org.n52.sir.datastructure.SirXmlSensorDescription;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.util.XmlTools;
import org.restlet.engine.io.WriterOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniMuenster.swsl.sir.SearchSensorResponseDocument;
import de.uniMuenster.swsl.sir.SearchSensorResponseDocument.SearchSensorResponse;
import de.uniMuenster.swsl.sir.SearchSensorResponseDocument.SearchSensorResponse.SearchResultElement;
import de.uniMuenster.swsl.sir.ServiceReferenceDocument.ServiceReference;
import de.uniMuenster.swsl.sir.SimpleSensorDescriptionDocument.SimpleSensorDescription;

/**
 * @author Jan Schulte
 * 
 */
public class SirSearchSensorResponse implements ISirResponse {

    private static Logger log = LoggerFactory.getLogger(SirSearchSensorResponse.class);

    /**
     * the search result elements
     */
    private Collection<SirSearchResultElement> searchResultElements;

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.response.ISirResponse#getByteArray()
     */
    @Override
    public byte[] getByteArray() throws IOException, TransformerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SearchSensorResponseDocument searchSensorRespDoc = parseToResponseDocument();
        searchSensorRespDoc.save(baos, XmlTools.xmlOptionsForNamespaces());
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.response.ISirResponse#getContentLength()
     */
    @Override
    public int getContentLength() throws IOException, TransformerException {
        return getByteArray().length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.response.ISirResponse#getContentType()
     */
    @Override
    public String getContentType() {
        return SirConstants.CONTENT_TYPE_XML;
    }

    /**
     * @return the searchResultElements
     */
    public Collection<SirSearchResultElement> getSearchResultElements() {
        return this.searchResultElements;
    }

    /**
     * 
     * @return
     */
    private SearchSensorResponseDocument parseToResponseDocument() {
        SearchSensorResponseDocument document = SearchSensorResponseDocument.Factory.newInstance();
        SearchSensorResponse searchSensResp = document.addNewSearchSensorResponse();

        for (SirSearchResultElement searchResult : this.searchResultElements) {
            SearchResultElement elem = searchSensResp.addNewSearchResultElement();

            if (searchResult.getSensorIdInSir() != null) {
                // sensorID in SIR
                elem.setSensorIDInSIR(searchResult.getSensorIdInSir());

                SirSensorDescription description = searchResult.getSensorDescription();

                // sensorDescription
                if (description != null) {
                    if (description instanceof SirXmlSensorDescription) {
                        SirXmlSensorDescription xmlDescr = (SirXmlSensorDescription) description;

                        XmlObject sml = null;
                        try {
                            sml = XmlObject.Factory.parse(xmlDescr.getDescription().getDomNode());
                        }
                        catch (XmlException e) {
                            log.error("Could not parse XML sensor description stored in database for sensor "
                                    + searchResult.getSensorIdInSir() + ".", e);
                        }

                        // handle different cases of data in database... after fixing the insertion, onle
                        // SensorMLDocument should be saved from now on.
                        if (sml instanceof SensorMLDocument) {
                            SensorMLDocument smlDoc = (SensorMLDocument) sml;

                            if (smlDoc.getSensorML().getMemberArray().length > 1)
                                log.warn("More than one member in SensorML, but only using first one!");

                            elem.setSensorDescription(smlDoc.getSensorML().getMemberArray(0).getProcess());
                        }
                        else if (sml instanceof AbstractProcessType) {
                            AbstractProcessType process = (AbstractProcessType) sml;
                            elem.setSensorDescription(process);
                        }
                        else if (sml instanceof XmlAnyTypeImpl) {
                            XmlAnyTypeImpl anyType = (XmlAnyTypeImpl) sml;
                            log.warn("Could not detect type of XML, trying to parse to AbstractProcessType");
                            try {
                                AbstractProcessType process = AbstractProcessType.Factory.parse(anyType.getDomNode());
                                elem.setSensorDescription(process);
                            }
                            catch (XmlException e) {
                                log.error("Could not parse XML sensor description stored in database for sensor "
                                        + searchResult.getSensorIdInSir() + ".", e);
                            }
                        }
                    }
                    else if (description instanceof SirSimpleSensorDescription) {
                        SirSimpleSensorDescription simpleDescr = (SirSimpleSensorDescription) description;
                        SimpleSensorDescription newSSDescr = elem.addNewSimpleSensorDescription();
                        newSSDescr.setDescriptionText(simpleDescr.getDescriptionText());
                        newSSDescr.setSensorDescriptionURL(simpleDescr.getSensorDescriptionURL());

                        SirBoundingBox boundingBox = simpleDescr.getBoundingBox();
                        if (boundingBox != null) {
                            BoundingBoxType bbox = newSSDescr.addNewObservedBoundingBox();
                            bbox.setCrs(Integer.toString(boundingBox.getSrid()));
                            bbox.setDimensions(BigInteger.valueOf(boundingBox.getDimension()));
                            List<String> ll = boundingBox.getLowerCorner();
                            bbox.setLowerCorner(ll);
                            List<String> uu = boundingBox.getUpperCorner();
                            bbox.setUpperCorner(uu);
                        }
                        else
                            log.debug("No bounding box given, possibly not supported in service version.");
                    }
                    else {
                        log.error("Unsupported SirSensorDescription!\n" + description);
                    }
                }

                // ServiceReference
                for (SirServiceReference servDesc : searchResult.getServiceReferences()) {
                    ServiceReference temp = elem.addNewServiceReference();
                    temp.setServiceSpecificSensorID(servDesc.getServiceSpecificSensorId());
                    temp.setServiceType(servDesc.getService().getType());
                    temp.setServiceURL(servDesc.getService().getUrl());
                }

            }
        }

        XmlTools.addSirAndSensorMLSchemaLocation(searchSensResp);

        if (SirConfigurator.getInstance().isValidateResponses()) {
            if ( !document.validate())
                log.warn("Service created invalid document!\n" + XmlTools.validateAndIterateErrors(document));
        }

        return document;
    }

    /**
     * @param searchResultElements
     *        the searchResultElements to set
     */
    public void setSearchResultElements(Collection<SirSearchResultElement> searchResultElements) {
        this.searchResultElements = searchResultElements;
    }

    /**
     * 
     * @param writer
     * @throws OwsExceptionReport
     */
    public void writeTo(Writer writer) throws OwsExceptionReport {
        SearchSensorResponseDocument searchSensorRespDoc = parseToResponseDocument();
        try {
            WriterOutputStream wos = new WriterOutputStream(writer);
            searchSensorRespDoc.save(wos, XmlTools.xmlOptionsForNamespaces());
        }
        catch (IOException e) {
            log.error("Could not write response document to writer.", e);
            throw new OwsExceptionReport(ExceptionCode.NoApplicableCode,
                                         "server",
                                         "Could not write response to output writer.");
        }
    }

}
