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

import com.mayloom.vouchserv.api.res.VoucherRedeemResult;
import com.mayloom.vouchserv.man.api.dto.Voucher;

@Endpoint
public class RedeemVoucherEndpoint extends AbstractVouchservEndpoint {
	
	private final Logger logger = LoggerFactory.getLogger(RedeemVoucherEndpoint.class);
	
	/**
     * The local name of the expected request.
     */
	public static final String REDEEM_V_REQ_LOCAL_NAME = "RedeemVoucherRequest";
    
    /**
     * The local name of the created response.
     */
    public static final String REDEEM_V_RESP_LOCAL_NAME = "RedeemVoucherResponse";
    
    public static final String REDEEM_V_ERROR_CODE = "redeemVoucherErrorCode";
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = REDEEM_V_REQ_LOCAL_NAME)
	@ResponsePayload
	@PreAuthorize("hasRole('ROLE_USER')")
	public Element handleRedeemVoucherRequest(@RequestPayload Element requestElement) throws ParserConfigurationException {
    	
    	String username = getUsername();
		logger.info("username: {}", username);
    	
    	// 1. Check request
    	checkRequest(requestElement, REDEEM_V_REQ_LOCAL_NAME);
    	
    	// 2. Act on message 
    	
    	String reqVsvID = getDirectChildElementText(requestElement, VSV_ID);
    	logger.debug("REQ vsvID: {}", reqVsvID);
    	String reqPin = getDirectChildElementText(requestElement, VOUCHER_PIN);
    	logger.debug("REQ pin: {}", reqPin);
    	
    	VoucherRedeemResult voucherRedeemResult = voucherService.redeemVoucher(username, reqVsvID, reqPin);
    	
    	// 3. Return response
    	DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    	Document respDoc = documentBuilder.newDocument();
    	Element respRoot = respDoc.createElementNS(NAMESPACE_URI, REDEEM_V_RESP_LOCAL_NAME);
    	String resultStatusCode = Integer.toString(voucherRedeemResult.getRedeemVoucherResultStatus().getCode());
    	appendChildElementWithTextNode(respDoc, respRoot, RESP_RES_STAT_CODE, resultStatusCode);
    	if (voucherRedeemResult.getVoucherRedeemErrorCode() != null) {
    		String errorCode = Integer.toString(voucherRedeemResult.getVoucherRedeemErrorCode().getCode()); 
    		appendChildElementWithTextNode(respDoc, respRoot, REDEEM_V_ERROR_CODE, errorCode);
    	}
    	return respRoot;
    }

}
