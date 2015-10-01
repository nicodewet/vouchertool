package com.mayloom.vouchserv.ws;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mayloom.vouchserv.man.api.dto.Voucher;
import com.mayloom.vouchserv.man.api.dto.VoucherBatch;

@Endpoint
public class GetVoucherBatchEndpoint extends AbstractVouchservEndpoint {
	
	private final Logger logger = LoggerFactory.getLogger(GetVoucherBatchEndpoint.class);
	
	/**
     * The local name of the expected request.
     */
    public static final String GET_BATCH_REQ_LOCAL_NAME = "GetVoucherBatchRequest";
    
    /**
     * The local name of the created response.
     */
    public static final String GET_BATCH_RESP_LOCAL_NAME = "GetVoucherBatchResponse";
    
    /**
     * Response elements.
     */
    public static final String VOUCHER_BATCH_RESP = "voucherBatch";
    public static final String VOUCHER_LIST_RESP = "voucherList";
    
    
    public static final String CREATION_DATE_RESP = "creationDate";
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = GET_BATCH_REQ_LOCAL_NAME)
	@ResponsePayload
	@PreAuthorize("hasRole('ROLE_USER')")
	public Element handleGetVoucherBatchRequest(@RequestPayload Element requestElement) throws ParserConfigurationException {
    	
    	String username = getUsername();
		logger.info("username: {}", username);
    	
    	// 1. Check request
    	checkRequest(requestElement, GET_BATCH_REQ_LOCAL_NAME);
    	
    	// 2. Act on message
    	String reqVsvID = getDirectChildElementText(requestElement, VSV_ID);
    	logger.debug("REQ vsvID: {}", reqVsvID);
    	String reqBatchNumber = getDirectChildElementText(requestElement, BATCH_NUM);
    	logger.debug("REQ batchNumber: {}", reqBatchNumber);
    	
    	VoucherBatch voucherBatch = voucherService.getVoucherBatch(username, reqVsvID, Integer.parseInt(reqBatchNumber));
    	
    	// 3. Return response
    	DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    	Document respDoc = documentBuilder.newDocument();
    	Element respRoot = respDoc.createElementNS(NAMESPACE_URI, GET_BATCH_RESP_LOCAL_NAME);
    	respRoot.setPrefix(NAMESPACE_PREFIX);
    	
    	// add additional namespace to the root element
    	respRoot.setAttributeNS(NAMESPACE_NAME_URI, XSI_QUALIFIED_NAME, XSI_NAMESPACE_URI);
    	
    	Element respVoucherBatch = respDoc.createElementNS(NAMESPACE_URI, VOUCHER_BATCH_RESP);
    	respVoucherBatch.setPrefix(NAMESPACE_PREFIX);
    	respRoot.appendChild(respVoucherBatch);
    	
    	if (voucherBatch != null) {
    		Element respVoucherList = respDoc.createElementNS(NAMESPACE_URI, VOUCHER_LIST_RESP);
    		respVoucherList.setPrefix(NAMESPACE_PREFIX);
    		for (Voucher voucher : voucherBatch.getVouchers()) {
    			
    			Element respVoucher = buildVoucherElement(respDoc, voucher);
    			respVoucherList.appendChild(respVoucher);
    		}
    		respVoucherBatch.appendChild(respVoucherList);
    		
    		appendChildElementWithTextNode(respDoc, respVoucherBatch, BATCH_NUM, Integer.toString(voucherBatch.getBatchNumber()));
    		String xmlExpiryDateTime = getDate(voucherBatch.getExpiryDate());
    		if (xmlExpiryDateTime == null) { 
    			appendNilChildElement(respDoc, respVoucherBatch, EXPIRY_DATE_RESP);
    		} else {
    			appendChildElementWithTextNode(respDoc, respVoucherBatch, EXPIRY_DATE_RESP, xmlExpiryDateTime);
    		}
    		
    		appendChildElementWithTextNode(respDoc, respVoucherBatch, ACTIVE_RESP, Boolean.toString(voucherBatch.isActive()));
    		String xmlCreationDateTime = getDate(voucherBatch.getCreationDate());
    		if (xmlCreationDateTime == null) { 
    			appendNilChildElement(respDoc, respVoucherBatch, CREATION_DATE_RESP);
    		} else { 
    			appendChildElementWithTextNode(respDoc, respVoucherBatch, CREATION_DATE_RESP, xmlCreationDateTime);
    		}
    		
    		
    	} else {
    		logger.debug("No matching voucherBatch found with vsvID {} and batchNumber {}", reqVsvID, reqBatchNumber);
    	}
    	
    	
    	return respRoot;
    	
    }

	

}
