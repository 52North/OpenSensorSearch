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
package org.n52.sir.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

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
