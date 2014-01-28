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
package org.n52.sir.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

import org.n52.oss.util.XmlTools;

/**
 * 
 * Some helper methods when dealing with SOAP classes (from the package <code>java.xml.soap</code>).
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class SoapTools {

    private static String inspect(Detail d) {
        StringBuilder sb = new StringBuilder();
        sb.append("Detail [");

        Iterator< ? > iter = d.getDetailEntries();
        while (iter.hasNext()) {
            DetailEntry entry = (DetailEntry) iter.next();
            sb.append(" [entry=");
            sb.append(XmlTools.xmlToString(entry));
        }
        sb.append("]");
        return sb.toString();
    }

    public static String inspect(SOAPFault fault) {
        StringBuilder sb = new StringBuilder();
        sb.append("Fault [faultcode: ");
        sb.append(fault.getFaultCode());
        sb.append(", faultstring: ");
        sb.append(fault.getFaultString());
        sb.append(", faultactor: ");
        sb.append(fault.getFaultActor());
        sb.append(", detail: ");
        Detail d = fault.getDetail();
        sb.append(inspect(d));
        sb.append("]");
        return sb.toString();
    }

    public static String toString(SOAPMessage message) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            message.writeTo(out);
            out.close();
        }
        catch (SOAPException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String s = new String(out.toByteArray());
        return s;
    }
}
