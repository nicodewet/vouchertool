package com.mayloom.vt.spring;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.remoting.RemoteLookupFailureException;

import com.mayloom.vouchserv.man.api.VoucherServiceManagement;
import com.vaadin.Application;

public class DAO {

	public static final String GENERATOR_USERNAME = "nico@vouchertool.com";
	private static VoucherServiceManagement voucherServiceManagement;
	private static final String GENERATOR_PASSWORD = "rodyahov";
	// TODO move URL below to external configuration
	private static final String VOUCHER_SERVICE_WSDL = "http://localhost/vouchserv/vouchserv.wsdl";
	private static final String VOUCHER_SERVICE_ERROR = "Voucher service not responding. Sanity checked with HTTP GET to " + VOUCHER_SERVICE_WSDL;
	
	private static boolean initDone = false;
	
	public static final void initialize(Application application) {
		
		if (!initDone) {
			SpringContextHelper springContextHelper = new SpringContextHelper(application);
			voucherServiceManagement = springContextHelper.getBean(VoucherServiceManagement.class);
			
			checkVoucherServiceIsRunning();
			
			/**
			 * NOTE: The call below can result in a org.springframework.remoting.RemoteLookupFailureException:
			 * 
			 * javax.servlet.ServletException: org.springframework.remoting.RemoteLookupFailureException: Lookup of RMI stub failed; nested exception is java.rmi.ConnectException: Connection refused to host: localhost; nested exception is: 
			 * java.net.ConnectException: Connection refused
			 * 
			 * CAUSE: The reason for this is because vouchserv may not have started up. A hack around this is to execute an HTTP GET
			 * request on https://www.vouchertool.com/vouchserv/vouchserv.wsdl
			 */
			try {
				if (!voucherServiceManagement.doesUserExist(DAO.GENERATOR_USERNAME)) {
					voucherServiceManagement.addNewOrdinaryUser(DAO.GENERATOR_USERNAME, GENERATOR_PASSWORD);
				}
			} catch (RemoteLookupFailureException e) {
				throw new DependantComponentInitializationException("Voucher Service RMI Lookup Failure. Is the component running and initialized?", e);
			}
		}
		
		initDone = true;
    }

	public static VoucherServiceManagement getVoucherServiceManagement() {
		return voucherServiceManagement;
	}
	
	public static void checkVoucherServiceIsRunning() {
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(VOUCHER_SERVICE_WSDL);

//            System.out.println("executing request " + httpget.getURI());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            String responseBody = null;
			try {
				responseBody = httpclient.execute(httpget, responseHandler);
			} catch (ClientProtocolException e) {
				throw new DependantComponentInitializationException(VOUCHER_SERVICE_ERROR, e);
			} catch (IOException e) {
				throw new DependantComponentInitializationException(VOUCHER_SERVICE_ERROR, e);
			}
//            System.out.println("----------------------------------------");
//            System.out.println(responseBody);
//            System.out.println("----------------------------------------");

        } finally {
            try {
				httpclient.close();
			} catch (IOException e) {
				// Not much we can do here!
				System.out.println(e);
			}
        }
	}
}
