package org.n52.sir.util;

import java.net.URI;

import net.opengis.ows.x11.VersionType;
import net.opengis.sos.x10.GetCapabilitiesDocument;
import net.opengis.sos.x10.GetCapabilitiesDocument.GetCapabilities;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oss.sir.Client;
import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class OwsClient extends Client {

    private static Logger log = LoggerFactory.getLogger(OwsClient.class);

    @Inject
    public OwsClient(@Named("oss.sir.serviceurl")
    String sirUrl) {
        super(sirUrl);
    }

    public XmlObject requestCapabilities(String serviceType, URI uri) throws OwsExceptionReport {
        String gcDoc = createGetCapabilities(serviceType);

        if (log.isDebugEnabled())
            log.debug("GetCapabilities to be send to " + serviceType + " @ " + uri.toString() + ": " + gcDoc);

        // send getCapabilities request
        XmlObject caps = null;
        XmlObject getCapXmlResponse = null;
        try {
            getCapXmlResponse = xSendPostRequest(XmlObject.Factory.parse(gcDoc), uri);
            caps = XmlObject.Factory.parse(getCapXmlResponse.getDomNode());
        }
        catch (XmlException xmle) {
            String msg = "Error on parsing Capabilities document: " + xmle.getMessage()
                    + (getCapXmlResponse == null ? "" : "\n" + getCapXmlResponse.xmlText());
            log.warn(msg);
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest, null, msg);
            throw se;
        }
        catch (Exception e) {
            String errMsg = "Error doing GetCapabilities to " + serviceType + " @ " + uri.toString() + " : "
                    + e.getMessage();
            log.warn(errMsg);
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest, null, errMsg);
            throw se;
        }

        return caps;
    }

    private String createGetCapabilities(String serviceType) {
        if (serviceType.equals(SirConstants.SOS_SERVICE_TYPE)) {
            GetCapabilitiesDocument gcdoc = GetCapabilitiesDocument.Factory.newInstance();
            GetCapabilities gc = gcdoc.addNewGetCapabilities();
            gc.setService(serviceType);
            VersionType version = gc.addNewAcceptVersions().addNewVersion();
            version.setStringValue(SirConstants.SOS_VERSION);
            return gcdoc.xmlText();
        }
        if (serviceType.equals(SirConstants.SPS_SERVICE_TYPE)) {
            net.opengis.sps.x10.GetCapabilitiesDocument gcdoc = net.opengis.sps.x10.GetCapabilitiesDocument.Factory.newInstance();
            net.opengis.sps.x10.GetCapabilitiesDocument.GetCapabilities gc = gcdoc.addNewGetCapabilities();
            gc.setService(serviceType);
            return gcdoc.xmlText();
        }

        throw new IllegalArgumentException("Service type not supported: " + serviceType);
    }
    
}
