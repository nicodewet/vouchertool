package com.mayloom.vouchserv.ws;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mayloom.vouchserv.api.res.ActivateVoucherBatchResult;
import com.mayloom.vouchserv.api.res.DeactivateVoucherBatchResult;

@Endpoint
public class ActivateVoucherBatchEndpoint extends AbstractVouchservEndpoint {
	
	private final Logger logger = LoggerFactory.getLogger(ActivateVoucherBatchEndpoint.class);
	
	/**
     * The local name of the expected request(s).
     */
	public static final String ACTIVATE_VB_REQUEST_LOCAL_NAME = "ActivateVoucherBatchRequest";
	public static final String DEACTIVATE_VB_REQUEST_LOCAL_NAME = "DeactivateVoucherBatchRequest";
    
    /**
     * The local name of the created response(s).
     */
    public static final String ACTIVATE_VB_RESPONSE_LOCAL_NAME = "ActivateVoucherBatchResponse";
    public static final String DEACTIVATE_VB_RESPONSE_LOCAL_NAME = "DeactivateVoucherBatchResponse";
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = ACTIVATE_VB_REQUEST_LOCAL_NAME)
	@ResponsePayload
	@PreAuthorize("hasRole('ROLE_USER')")
	public Element handleActivateVoucherBatchRequest(@RequestPayload Element requestElement) throws ParserConfigurationException {
    	
    	String username = getUsername();
		logger.info("username: {}", username);
    	
    	// 1. Check request
    	checkRequest(requestElement, ACTIVATE_VB_REQUEST_LOCAL_NAME);
    	
    	// 2. Act on message 
    	String reqVsvID = getDirectChildElementText(requestElement, VSV_ID);
    	logger.debug("REQ vsvID: {}", reqVsvID);
    	String reqBatchNumber = getDirectChildElementText(requestElement, BATCH_NUM);
    	logger.debug("REQ batchNumber: {}", reqBatchNumber);
    	
    	ActivateVoucherBatchResult activateVoucherBatchResult = voucherService.activateVoucherBatch(username, reqVsvID, Integer.parseInt(reqBatchNumber));
    	
    	// 3. Return response
    	return formulateAndReturnResponse(activateVoucherBatchResult, null);
    	
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = DEACTIVATE_VB_REQUEST_LOCAL_NAME)
	@ResponsePayload
	@PreAuthorize("hasRole('ROLE_USER')")
    public Element handleDeactivateVoucherBatchRequest(@RequestPayload Element requestElement) throws ParserConfigurationException {
    	
    	String username = getUsername();
		logger.info("username: {}", username);
    	
    	// 1. Check request
    	checkRequest(requestElement, DEACTIVATE_VB_REQUEST_LOCAL_NAME);
    	
    	// 2. Act on message 
    	String reqVsvID = getDirectChildElementText(requestElement, VSV_ID);
    	logger.debug("REQ vsvID: {}", reqVsvID);
    	String reqBatchNumber = getDirectChildElementText(requestElement, BATCH_NUM);
    	logger.debug("REQ batchNumber: {}", reqBatchNumber);
    	
    	DeactivateVoucherBatchResult deactivateVoucherBatchResult = voucherService.deactivateVoucherbatch(username, reqVsvID, Integer.parseInt(reqBatchNumber));
    	
    	// 3. Return response
    	return formulateAndReturnResponse(null, deactivateVoucherBatchResult);
    }

	private Element formulateAndReturnResponse(ActivateVoucherBatchResult activateVoucherBatchResult, DeactivateVoucherBatchResult deactivateVoucherBatchResult) throws ParserConfigurationException {
		
		if ((activateVoucherBatchResult == null && deactivateVoucherBatchResult == null) || (activateVoucherBatchResult != null && deactivateVoucherBatchResult != null)) {
			throw new IllegalArgumentException();
		}
		
		Element respRoot = null;
		
		String responseQualifiedName = null;
		String batchNumber = null;
		String resultStatus = null;
		boolean addErrorCode = false;
		String errorCode = null;
		// 1. Generic setup
		if (activateVoucherBatchResult != null) {
			responseQualifiedName = ACTIVATE_VB_RESPONSE_LOCAL_NAME;
			batchNumber = Integer.toString(activateVoucherBatchResult.getBatchNumber()); // error prone ref since as per api may be null
			resultStatus = Integer.toString(activateVoucherBatchResult.getActivateVoucherBatchResultStatus().getCode());
			if (activateVoucherBatchResult.getActivateVoucherBatchErrorCode() != null) {
				addErrorCode = true;
	    		errorCode = Integer.toString(activateVoucherBatchResult.getActivateVoucherBatchErrorCode().getCode());
	    	}
		} else if (deactivateVoucherBatchResult != null) {
			responseQualifiedName = DEACTIVATE_VB_RESPONSE_LOCAL_NAME;
			batchNumber = Integer.toString(deactivateVoucherBatchResult.getBatchNumber()); // error prone ref since as per api may be null
			logger.debug("batch numb {}", batchNumber);
			resultStatus = Integer.toString(deactivateVoucherBatchResult.getResultStatusCode().getCode());
			logger.debug("result status {}", resultStatus);
			if (deactivateVoucherBatchResult.getVoucherServError() != null) {
				addErrorCode = true;
				errorCode = Integer.toString(deactivateVoucherBatchResult.getVoucherServError().getCode()); 
				logger.debug("error code {}", errorCode);
			}
		}
		
		// 2. Build response
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    	Document respDoc = documentBuilder.newDocument();
    	respRoot = respDoc.createElementNS(NAMESPACE_URI, responseQualifiedName);
    	appendChildElementWithTextNode(respDoc, respRoot, BATCH_NUM, batchNumber);
    	appendChildElementWithTextNode(respDoc, respRoot, RESP_RES_STAT_CODE, resultStatus);
    	
    	if (addErrorCode) {
    		appendChildElementWithTextNode(respDoc, respRoot, ERR_CODE, errorCode);
    	}
    	
		if (respRoot == null) { 
			throw new RuntimeException();
		}
		
    	return respRoot;
	}

}
