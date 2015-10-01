package com.mayloom.vouchserv.ws;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Optional;
import com.mayloom.vouchserv.api.res.GetVoucherResult;
import com.mayloom.vouchserv.man.api.dto.Voucher;
import com.mayloom.vouchserv.man.api.dto.VoucherBatch;

@Endpoint
public class GetVoucherEndpoint extends AbstractVouchservEndpoint {
	
	private final Logger logger = LoggerFactory.getLogger(GetVoucherEndpoint.class);
	
	/**
     * The local name of the expected request.
     */
    public static final String GET_VOUCHER_REQ_LOCAL_NAME = "GetVoucherRequest";
    
    /**
     * The local name of the created response.
     */
    public static final String GET_VOUCHER_RESP_LOCAL_NAME = "GetVoucherResponse";
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = GET_VOUCHER_REQ_LOCAL_NAME)
	@ResponsePayload
	@PreAuthorize("hasRole('ROLE_USER')")
	public Element handleGetVoucherRequest(@RequestPayload Element requestElement) throws ParserConfigurationException {
    	
    	String username = getUsername();
		logger.info("username: {}", username);
    	
    	// 1. Check request
    	checkRequest(requestElement, GET_VOUCHER_REQ_LOCAL_NAME);
    	
    	// 2. Act on message
    	String reqVsvID = getDirectChildElementText(requestElement, VSV_ID);
    	logger.debug("REQ vsvID: {}", reqVsvID);
    	
    	String reqVoucherPin = getDirectChildElementText(requestElement, VOUCHER_PIN);
    	logger.debug("REQ pin: {}", reqVoucherPin);
    	String reqVoucherSerialNo = getDirectChildElementText(requestElement, VOUCHER_SERIAL_NUM);
    	logger.debug("REQ serial no: {}", reqVoucherSerialNo);
    	
    	Optional<String> optVoucherPin = Optional.absent();
    	Optional<String> optVoucherSerialNo = Optional.absent();
    	
    	if (!StringUtils.isEmpty(reqVoucherPin)) {
    		optVoucherPin = Optional.of(reqVoucherPin);
    	}
    	
    	if (!StringUtils.isEmpty(reqVoucherSerialNo)) {
    		optVoucherSerialNo = Optional.of(reqVoucherSerialNo);
    	}
    	
    	GetVoucherResult result = voucherService.getVoucher(username, reqVsvID, optVoucherSerialNo, optVoucherPin);
    	    	
    	// 3. Return response
    	DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    	Document respDoc = documentBuilder.newDocument();
    	Element respRoot = respDoc.createElementNS(NAMESPACE_URI, GET_VOUCHER_RESP_LOCAL_NAME);
    	respRoot.setPrefix(NAMESPACE_PREFIX);
    	
    	// add additional namespace to the root element
    	respRoot.setAttributeNS(NAMESPACE_NAME_URI, XSI_QUALIFIED_NAME, XSI_NAMESPACE_URI);
    	
    	if (result.getVoucher() != null) {
    		Element respVoucher = buildVoucherElement(respDoc, result.getVoucher()); 
    		respRoot.appendChild(respVoucher);
    	}
    	if (result.getErrorCode() != null) {
        	appendChildElementWithTextNode(respDoc, respRoot, ERR_CODE, Integer.toString(result.getErrorCode().getCode()));
    	}
    	return respRoot;
    }
}
