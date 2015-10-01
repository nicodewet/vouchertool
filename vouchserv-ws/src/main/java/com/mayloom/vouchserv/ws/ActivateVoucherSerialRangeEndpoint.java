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

import com.mayloom.vouchserv.api.res.ActivateVoucherSerialRangeResult;

@Endpoint
public class ActivateVoucherSerialRangeEndpoint extends AbstractVouchservEndpoint {
	
	private final Logger logger = LoggerFactory.getLogger(ActivateVoucherSerialRangeEndpoint.class);
	
	public static final String REQUEST_LOCAL_NAME = "ActivateVoucherSerialRangeRequest";
	
	public static final String START_SERIAL_NUM = "startSerialNumber"; 
	public static final String END_SERIAL_NUM = "endSerialNumber";
	
	public static final String RESPONSE_LOCAL_NAME = "ActivateVoucherSerialRangeResponse";
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = REQUEST_LOCAL_NAME)
	@ResponsePayload
	@PreAuthorize("hasRole('ROLE_USER')")
	public Element handleActivateVoucherSerialRangeRequest(@RequestPayload Element requestElement) throws ParserConfigurationException {
    	
		String username = getUsername();
		logger.info("username: {}", username);
		
    	// 1. Check request
    	checkRequest(requestElement, REQUEST_LOCAL_NAME);
    	
    	// 2. Act on message 
    	String reqVsvID = getDirectChildElementText(requestElement, VSV_ID);
    	logger.debug("REQ vsvID: {}", reqVsvID);
    	String reqStartSerialNumber = getDirectChildElementText(requestElement, START_SERIAL_NUM);
    	logger.debug("REQ startSerialNumber: {}", reqStartSerialNumber);
    	String reqEndSerialNumber = getDirectChildElementText(requestElement, END_SERIAL_NUM);
    	logger.debug("REQ endSerialNumber: {}", reqEndSerialNumber);
    	
    	long nReqStartSerialNumber = Long.parseLong(reqStartSerialNumber);
    	long nReqEndSerialNumber = Long.parseLong(reqEndSerialNumber);
    	ActivateVoucherSerialRangeResult activateVoucherSerialRangeResult = voucherService.activeVoucherSerialRange(username, reqVsvID, nReqStartSerialNumber, nReqEndSerialNumber);
    	
    	// 3. Return response
    	return formulateAndReturnResponse(activateVoucherSerialRangeResult);
	}
	
	private Element formulateAndReturnResponse(ActivateVoucherSerialRangeResult activateVouchServResult) throws ParserConfigurationException {
		
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		
    	Document respDoc = documentBuilder.newDocument();
    	Element respRoot = respDoc.createElementNS(NAMESPACE_URI, RESPONSE_LOCAL_NAME);
		
    	appendChildElementWithTextNode(respDoc, respRoot, RESP_RES_STAT_CODE, Integer.toString(activateVouchServResult.getResultStatusCode().getCode()));
    	appendChildElementWithTextNode(respDoc, respRoot, START_SERIAL_NUM, Long.toString(activateVouchServResult.getStartSerialNumber()));
    	appendChildElementWithTextNode(respDoc, respRoot, END_SERIAL_NUM, Long.toString(activateVouchServResult.getEndSerialNumber()));
    	
    	if (activateVouchServResult.getVoucherServError() != null) {
    		appendChildElementWithTextNode(respDoc, respRoot, ERR_CODE, Integer.toString(activateVouchServResult.getVoucherServError().getCode()));
    	}
    	
    	return respRoot;
	}
}
