package com.mayloom.vouchserv.ws;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import com.mayloom.vouchserv.api.VoucherService;
import com.mayloom.vouchserv.man.api.res.RegisterResult;
import com.mayloom.vouchserv.dbo.DatabaseHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Endpoint
public class RegisterEndpoint extends AbstractVouchservEndpoint {
	
	/**
     * The local name of the expected request.
     */
    public static final String REGISTER_REQUEST_LOCAL_NAME = "RegisterRequest";
    
    /**
     * The local name of the created response.
     */
    public static final String REGISTER_RESPONSE_LOCAL_NAME = "RegisterResponse";
	
	private final Logger logger = LoggerFactory.getLogger(RegisterEndpoint.class);
	
	@Autowired
	@Qualifier("databaseHelper")
	DatabaseHelper databaseHelper;
		
	 /**
     * Reads the given <code>requestElement</code>, and sends a response back.
     *
     * @param requestElement the contents of the SOAP message as DOM elements
     * @return the response element
	 * @throws ParserConfigurationException 
     * @throws IllegalArgumentException if <code>requestElement</code> does not have namespace URI <code>NAMESPACE_URI</code>
     * 		   or if said element does not have local name <code>REGISTER_REQUEST_LOCAL_NAME</code> 
     */
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = REGISTER_REQUEST_LOCAL_NAME)
	@ResponsePayload
	@PreAuthorize("hasRole('ROLE_USER')")
	public Element handleRegisterRequest(@RequestPayload Element requestElement) throws ParserConfigurationException {
		
		String username = getUsername();
		logger.info("username: {}", username);
		
		// 1. Check request
		checkRequest(requestElement, REGISTER_REQUEST_LOCAL_NAME);
		
        // 2. Act on message
		RegisterResult registerResult = voucherService.register(username);
		logger.debug("VSV-ID: {}", registerResult.getVsvId());
		
		// 3. Return response
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.newDocument();
        //doc.setPrefix(NAMESPACE_PREFIX);
        Element root = doc.createElementNS(NAMESPACE_URI, REGISTER_RESPONSE_LOCAL_NAME);
        root.setPrefix(NAMESPACE_PREFIX);
        appendChildElementWithTextNode(doc, root, "vsvID", registerResult.getVsvId());
        appendChildElementWithTextNode(doc, root, "resultStatusCode", ""+registerResult.getResultStatusCode().getCode());
        
        return root;
	}

	// Spring bean initialization callback method
	public void init() {
		logger.info("==================== VouchservEndpoint =========================");
		if (this.voucherService == null) {
			throw new IllegalStateException("The [voucherService] property must be set.");
		}
	}
}
