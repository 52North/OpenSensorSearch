
package org.n52.oss.ui.beans;

import org.x52North.sir.x032.VersionAttribute;
import org.x52North.sir.x032.VersionAttribute.Version.Enum;

/**
 * TODO consider moving this to common module
 * 
 * @author Daniel
 * 
 */
public class ClientConstants {

    public enum CapabilitiesSection {
        All, Contents, OperationsMetadata, ServiceIdentification, ServiceProvider
    }

    public static final String SERVICE_NAME = "SIR";

    public static String[] getAcceptedServiceVersions() {
        return new String[] {"0.3.0", "0.3.1", "0.3.2"};
    }

    public static Enum getServiceVersionEnum() {
        return VersionAttribute.Version.X_0_3_2;
    }

}
