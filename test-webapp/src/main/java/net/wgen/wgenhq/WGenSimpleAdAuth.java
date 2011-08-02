package net.wgen.wgenhq;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;
import net.wgen.wgenhq.WGenSimpleAdAuthSoap;

@WebServiceClient(name = "SimpleAdAuth", 
                  wsdlLocation = "file:/Users/dcoleman/dev/slitest/test-webapp/src/main/wsdl/wgenhq.wsdl",
                  targetNamespace = "https://dcoleman-wks2.wgenhq.net:8443/sftest/") 
public class WGenSimpleAdAuth extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("https://dcoleman-wks2.wgenhq.net:8443/sftest/", "SimpleAdAuth");
    public final static QName WGenSimpleAdAuthSoap = new QName("https://dcoleman-wks2.wgenhq.net:8443/sftest/", "SimpleAdAuthSoap");
    public final static QName WGenSimpleAdAuthSoap12 = new QName("https://dcoleman-wks2.wgenhq.net:8443/sftest/", "SimpleAdAuthSoap12");
    static {
        URL url = null;
        try {
            url = new URL("file:/Users/dcoleman/dev/slitest/test-webapp/src/main/wsdl/wgenhq.wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(WGenSimpleAdAuth.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "file:/Users/dcoleman/dev/slitest/test-webapp/src/main/wsdl/wgenhq.wsdl");
        }
        WSDL_LOCATION = url;
    }

    public WGenSimpleAdAuth(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public WGenSimpleAdAuth(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public WGenSimpleAdAuth() {
        super(WSDL_LOCATION, SERVICE);
    }
    

    /**
     *
     * @return
     *     returns SimpleAdAuthSoap
     */
    @WebEndpoint(name = "SimpleAdAuthSoap")
    public WGenSimpleAdAuthSoap getSimpleAdAuthSoap() {
        return super.getPort(WGenSimpleAdAuthSoap, WGenSimpleAdAuthSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns SimpleAdAuthSoap
     */
    @WebEndpoint(name = "SimpleAdAuthSoap")
    public WGenSimpleAdAuthSoap getSimpleAdAuthSoap(WebServiceFeature... features) {
        return super.getPort(WGenSimpleAdAuthSoap, WGenSimpleAdAuthSoap.class, features);
    }
    /**
     *
     * @return
     *     returns SimpleAdAuthSoap
     */
//    @WebEndpoint(name = "WGenSimpleAdAuthSoap12")
//    public WGenSimpleAdAuthSoap getSimpleAdAuthSoap12() {
//        return super.getPort(WGenSimpleAdAuthSoap12, WGenSimpleAdAuthSoap.class);
//    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns SimpleAdAuthSoap
     */
//    @WebEndpoint(name = "WGenSimpleAdAuthSoap12")
//    public WGenSimpleAdAuthSoap getSimpleAdAuthSoap12(WebServiceFeature... features) {
//        return super.getPort(WGenSimpleAdAuthSoap12, WGenSimpleAdAuthSoap.class, features);
//    }

}
