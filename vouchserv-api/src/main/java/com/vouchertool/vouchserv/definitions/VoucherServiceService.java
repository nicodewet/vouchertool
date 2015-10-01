package com.vouchertool.vouchserv.definitions;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.7.0
 * 2012-11-10T21:56:39.563+13:00
 * Generated source version: 2.7.0
 * 
 */
@WebServiceClient(name = "VoucherServiceService", 
                  wsdlLocation = "https://www.vouchertool.com/vouchserv/vouchserv.wsdl",
                  targetNamespace = "http://vouchertool.com/vouchserv/definitions") 
public class VoucherServiceService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://vouchertool.com/vouchserv/definitions", "VoucherServiceService");
    public final static QName VoucherServiceSoap11 = new QName("http://vouchertool.com/vouchserv/definitions", "VoucherServiceSoap11");
    static {
        URL url = null;
        try {
            url = new URL("https://www.vouchertool.com/vouchserv/vouchserv.wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(VoucherServiceService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "https://www.vouchertool.com/vouchserv/vouchserv.wsdl");
        }
        WSDL_LOCATION = url;
    }

    public VoucherServiceService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public VoucherServiceService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public VoucherServiceService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public VoucherServiceService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public VoucherServiceService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public VoucherServiceService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     *
     * @return
     *     returns VoucherService
     */
    @WebEndpoint(name = "VoucherServiceSoap11")
    public VoucherService getVoucherServiceSoap11() {
        return super.getPort(VoucherServiceSoap11, VoucherService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns VoucherService
     */
    @WebEndpoint(name = "VoucherServiceSoap11")
    public VoucherService getVoucherServiceSoap11(WebServiceFeature... features) {
        return super.getPort(VoucherServiceSoap11, VoucherService.class, features);
    }

}