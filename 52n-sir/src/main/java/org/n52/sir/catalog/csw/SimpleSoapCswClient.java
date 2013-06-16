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
package org.n52.sir.catalog.csw;

import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sir.SirConfigurator;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.util.SoapTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Daniel Nüst (d.nuest@52north.org)
 */
public class SimpleSoapCswClient {

    private static Logger log = LoggerFactory.getLogger(SimpleSoapCswClient.class);

    private SOAPConnectionFactory connfactory;

    private boolean doNotCheck = false;

    private boolean extendedDebugToConsole = false;

    private boolean sendNothing = false;

    private TransformerFactory transformerFact;

    private URL url;

    /**
     * 
     * @param url
     * @throws OwsExceptionReport
     */
    public SimpleSoapCswClient(URL url, boolean doNotCheck) throws OwsExceptionReport {
        this.url = url;
        this.doNotCheck = doNotCheck;

        try {
            this.transformerFact = TransformerFactory.newInstance();
        }
        catch (TransformerFactoryConfigurationError e) {
            log.error("Could not instantiate a TransformerFactory!", e);
            throw new OwsExceptionReport("Could not instantiate a TransformerFactory!", e);
        }

        this.extendedDebugToConsole = SirConfigurator.getInstance().isExtendedDebugToConsole();
        if (this.extendedDebugToConsole)
            log.warn("*** Extended logging of all outgoing and incoming message is ENABLED. Be aware of large logfiles! ***");

        try {
            this.connfactory = SOAPConnectionFactory.newInstance();
        }
        catch (UnsupportedOperationException e) {
            log.error("Error creating connection factory - please check VM capabilities!", e);
            throw new OwsExceptionReport("Error creating connection factory - please check VM capabilities!", e);
        }
        catch (SOAPException e) {
            log.error("Error creating connection factory - please check VM capabilities!", e);
            throw new OwsExceptionReport("Error creating connection factory - please check VM capabilities!", e);
        }
    }

    /**
     * 
     * add soap envelope and body
     * 
     * @param doc
     * @return
     * @throws SOAPException
     */
    private SOAPMessage buildMessage(Document doc) throws SOAPException {
        MessageFactory mfact = MessageFactory.newInstance();
        SOAPMessage smsg = mfact.createMessage();
        SOAPPart prt = smsg.getSOAPPart();
        SOAPEnvelope env = prt.getEnvelope();
        SOAPBody bdy = env.getBody();
        bdy.addDocument(doc);
        smsg.saveChanges();

        return smsg;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SimpleSoapCswClient other = (SimpleSoapCswClient) obj;
        if (this.connfactory == null) {
            if (other.connfactory != null)
                return false;
        }
        else if ( !this.connfactory.equals(other.connfactory))
            return false;
        if (this.url == null) {
            if (other.url != null)
                return false;
        }
        else
            try {
                if ( !this.url.toURI().equals(other.url.toURI()))
                    return false;
            }
            catch (URISyntaxException e) {
                log.error("Uri error.", e);
            }
        return true;
    }

    /**
     * 
     * @param response
     * @return
     * @throws OwsExceptionReport
     * @throws SOAPException
     * @throws TransformerException
     * @throws XmlException
     */
    public XmlObject extractContent(SOAPMessage message) throws OwsExceptionReport {
        try {
            // extract the content of the reply
            Source contentSource = message.getSOAPPart().getContent();

            // create transformer
            Transformer transformer = this.transformerFact.newTransformer();

            // set the output for the transformation
            StringWriter stringWriter = new StringWriter();
            StreamResult result = new StreamResult(stringWriter);

            // do the transformation to the set result
            transformer.transform(contentSource, result);
            stringWriter.flush();
            String responseString = new String(stringWriter.getBuffer());

            // create xmlobject
            XmlObject o = XmlObject.Factory.parse(responseString);

            // find the content, so something different than envelope or body, and return it
            Node contentNode = findContent(o.getDomNode().getChildNodes());
            o = XmlObject.Factory.parse(contentNode);

            return o;
        }
        catch (TransformerException e) {
            log.error("Could not process received document.");
            OwsExceptionReport report = new OwsExceptionReport("Could not transform content of SOAPMessage.", e);
            throw report;
        }
        catch (XmlException e) {
            log.error("Could not parse received content.");
            OwsExceptionReport report = new OwsExceptionReport("Could not parse content of SOAPMessage to XmlObject.",
                                                               e);
            throw report;
        }
        catch (SOAPException e) {
            log.error("Could not get content from SOAPMessage.");
            OwsExceptionReport report = new OwsExceptionReport("Could get content from SOAPMessage.", e);
            throw report;
        }
    }

    /**
     * 
     * untested method...
     * 
     * @param message
     * @param elementName
     * @return
     * @throws OwsExceptionReport
     */
    public XmlObject extractContent(SOAPMessage message, String namespaceURI, String localElementName) throws OwsExceptionReport {
        try {
            // extract the content of the reply
            Source contentSource = message.getSOAPPart().getContent();

            // create transformer
            Transformer transformer = this.transformerFact.newTransformer();

            // set the output for the transformation
            StringWriter stringWriter = new StringWriter();
            StreamResult result = new StreamResult(stringWriter);

            // do the transformation to the set result
            transformer.transform(contentSource, result);
            stringWriter.flush();
            String responseString = new String(stringWriter.getBuffer());

            // create xmlobject
            XmlObject o = XmlObject.Factory.parse(responseString);

            NodeList elements = message.getSOAPBody().getElementsByTagNameNS(namespaceURI, localElementName);
            o = XmlObject.Factory.parse(elements.item(0));

            if (elements.getLength() > 1)
                log.warn("Got more than one matching element by tag name and namespace, returning only first!");

            return o;
        }
        catch (TransformerException e) {
            log.error("Could not process received document.");
            OwsExceptionReport report = new OwsExceptionReport("Could not transform content of SOAPMessage.", e);
            throw report;
        }
        catch (XmlException e) {
            log.error("Could not parse received content.");
            OwsExceptionReport report = new OwsExceptionReport("Could not parse content of SOAPMessage to XmlObject.",
                                                               e);
            throw report;
        }
        catch (SOAPException e) {
            log.error("Could not get content from SOAPMessage.");
            OwsExceptionReport report = new OwsExceptionReport("Could get content from SOAPMessage.", e);
            throw report;
        }
    }

    /**
     * 
     * @param message
     * @return
     * @throws OwsExceptionReport
     */
    public SOAPFault extractFault(SOAPMessage message) throws OwsExceptionReport {
        try {
            return message.getSOAPBody().getFault();
        }
        catch (SOAPException e) {
            log.error("Could not get SOAPBody from message.");
            OwsExceptionReport report = new OwsExceptionReport("Could get fault from SOAPMessage.", e);
            throw report;
        }
    }

    /**
     * 
     * @param childNodes
     * @return
     */
    private Node findContent(NodeList childNodes) {
        Node currentNode = childNodes.item(0);
        if (currentNode.getLocalName().equals("Envelope") || currentNode.getLocalName().equals("Body")) {
            return findContent(currentNode.getChildNodes());
        }
        return currentNode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (this.connfactory == null) ? 0 : this.connfactory.hashCode());
        try {
            result = prime * result + ( (this.url.toURI() == null) ? 0 : this.url.toURI().hashCode());
        }
        catch (URISyntaxException e) {
            log.error("URI not hashable.", e);
        }
        return result;
    }

    /**
     * @return the doNotCheck
     */
    public boolean isDoNotCheck() {
        return this.doNotCheck;
    }

    /**
     * 
     * @param doc
     * @return
     * @throws OwsExceptionReport
     */
    public SOAPMessage send(Document doc) throws OwsExceptionReport {
        SOAPMessage responseMessage;

        try {
            // build the message
            SOAPMessage smsg = buildMessage(doc);

            if (this.extendedDebugToConsole) {
                log.debug("########## SENDING #########\n" + SoapTools.toString(smsg));
            }

            if ( !this.sendNothing) {
                // send the message
                SOAPConnection con = this.connfactory.createConnection();

                // handle response
                responseMessage = con.call(smsg, this.url);
                if (this.extendedDebugToConsole) {
                    log.debug("########## RECEIVED ##########\n" + SoapTools.toString(responseMessage));
                }
                con.close();
                return responseMessage;
            }

            log.warn("Fake mode - DID NOT SEND ANYTHING, CHANGE VARIABLE IN SimpleSoapCswClient!");
            throw new OwsExceptionReport("Fake mode, did not send the document!", null);
        }
        catch (SOAPException e) {
            log.error("Could not send document to service!", e);
            OwsExceptionReport er = new OwsExceptionReport("Could not send document to service using SOAP!",
                                                           e.getCause());
            er.addCodedException(ExceptionCode.InvalidRequest, "-", e);
            throw er;
        }
    }

    /**
     * @param doNotCheck
     *        the doNotCheck to set
     */
    public void setDoNotCheck(boolean doNotCheck) {
        this.doNotCheck = doNotCheck;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SimpleSoapCswClient [url=");
        sb.append(this.url);
        sb.append(", doNotCheck=");
        sb.append(this.doNotCheck);
        sb.append("]");
        return sb.toString();
    }
}
