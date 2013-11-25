
package org.n52.sir.listener.harvest;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.request.SirHarvestServiceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tools {

    private static Logger log = LoggerFactory.getLogger(Tools.class);

    public static URI url2Uri(SirHarvestServiceRequest requestP) throws OwsExceptionReport {
        URI uri;
        try {
            uri = new URL(requestP.getServiceUrl()).toURI();
        }
        catch (MalformedURLException e) {
            String msg = "Error creating URI from given service url '" + requestP.getServiceUrl()
                    + "', cannot proceed with harvesting!";
            log.error(msg, e);
            throw new OwsExceptionReport(ExceptionCode.InvalidParameterValue,
                                         "ServiceURL",
                                         "Service url is not a valid URL.");
        }
        catch (URISyntaxException e) {
            String msg = "Error creating URI from given service url '" + requestP.getServiceUrl()
                    + "', cannot proceed with harvesting!";
            log.error(msg, e);
            throw new OwsExceptionReport(ExceptionCode.InvalidParameterValue,
                                         "ServiceURL",
                                         "Service url is not a valid URL.");
        }
        return uri;
    }

}
