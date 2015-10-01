package com.mayloom.vouchserv.ws;

import java.util.Date;
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
import com.mayloom.vouchserv.man.api.req.BatchGenRequest;
import com.mayloom.vouchserv.man.api.req.PinType;
import com.mayloom.vouchserv.man.api.req.VoucherBatchType;
import com.mayloom.vouchserv.man.api.res.BatchGenResult;
import com.mayloom.vouchserv.man.api.res.code.VoucherServError;

@Endpoint
public class VoucherBatchGenerationEndpoint extends AbstractVouchservEndpoint {

	/**
     * The local name of the expected request.
     */
    public static final String BATCH_GEN_REQ_LOCAL_NAME = "VoucherBatchGenerationRequest";
    
    /**
     * The local name of the created response.
     */
    public static final String BATCH_GEN_RESP_LOCAL_NAME = "VoucherBatchGenerationResponse";
    
    /**
     * Response elements.
     */
	public static final String BATCH_GEN_RESP_ERR_LIST = "batchGenerationErrorList";
	public static final String BATCH_GEN_RESP_ERROR_CODE = "batchGenerationErrorCode";
    
    /**
     * Request elements.
     */
    public static final String VOUCHER_NUMBER = "voucherNumber";
    public static final String BATCH_TYPE = "batchType";
    public static final String PIN_LENGTH = "pinLength";
    public static final String PIN_TYPE = "pinType";
    public static final String EXPIRY_DATE= "expiryDate";
    
    private final Logger logger = LoggerFactory.getLogger(VoucherBatchGenerationEndpoint.class);
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = BATCH_GEN_REQ_LOCAL_NAME)
	@ResponsePayload
	@PreAuthorize("hasRole('ROLE_USER')")
	public Element handleRegisterRequest(@RequestPayload Element requestElement) throws ParserConfigurationException {
    	
    	String username = getUsername();
		logger.info("username: {}", username);
    	
    	// 1. Check request
    	checkRequest(requestElement, BATCH_GEN_REQ_LOCAL_NAME);
    			
    	// 2. Act on message
    	String reqVsvID = getDirectChildElementText(requestElement, VSV_ID);
    	logger.debug("REQ vsvID: {}", reqVsvID);
    	String reqVoucherNumber = getDirectChildElementText(requestElement, VOUCHER_NUMBER);
    	logger.debug("REQ voucherNumber: {}", reqVoucherNumber);
        String reqBatchType = getDirectChildElementText(requestElement,BATCH_TYPE);
        logger.debug("REQ batchType: {}", reqBatchType);
        String reqPinLength = getDirectChildElementText(requestElement,PIN_LENGTH);
        logger.debug("REQ pinLength: {}", reqPinLength);
        String reqPinType = getDirectChildElementText(requestElement,PIN_TYPE);
        logger.debug("REQ pinType: {}", reqPinType);
        String reqExpiryDate = getDirectChildElementText(requestElement,EXPIRY_DATE);
        logger.debug("REQ expiryDate: {}", reqExpiryDate);

        /**
         * OPTIONAL ELEMENTS
         */
        int nReqPinLength = 12;
        if (reqPinLength != null) {
        	nReqPinLength = Integer.parseInt(reqPinLength);
        }
        
        PinType pinType = PinType.NUMERIC;
        if (reqPinType != null) {
        	pinType = PinType.valueOf(reqPinType);
        }
        
        Date dReqExpiryDate = getDate(reqExpiryDate);
                
        /**
         * NON-OPTIONAL ELEMENTS
         */
        int nVoucherNumber = Integer.valueOf(reqVoucherNumber).intValue();
        VoucherBatchType voucherBatchType = VoucherBatchType.valueOf(reqBatchType); 
        
        
        BatchGenRequest batchGenRequest = new BatchGenRequest.Builder(username, reqVsvID, nVoucherNumber, voucherBatchType)
        										.pinLength(nReqPinLength).pinType(pinType).expiryDate(dReqExpiryDate).build();
        
        BatchGenResult batchGenResult; 
        
        if (nVoucherNumber <= 1000) { // TODO store value in properties
        	logger.info("Synchronous generation...");
        	batchGenResult = voucherService.generateVoucherBatch(username, batchGenRequest);
        } else {
        	logger.info("Asynchronous generation...");
        	batchGenResult = voucherService.generateVoucherBatchAsync(username, batchGenRequest);
        }
    	    			
    	// 3. Return response
    	DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    	Document respDoc = documentBuilder.newDocument();
    	Element respRoot = respDoc.createElementNS(NAMESPACE_URI, BATCH_GEN_RESP_LOCAL_NAME);
    	respRoot.setPrefix(NAMESPACE_PREFIX);
    	appendChildElementWithTextNode(respDoc, respRoot, BATCH_NUM, Integer.toString(batchGenResult.getBatchNumber()));
    	appendChildElementWithTextNode(respDoc, respRoot, RESP_RES_STAT_CODE, Integer.toString(batchGenResult.getBatchGenResultStatus().getCode())); 
    	
    	VoucherServError voucherServError = batchGenResult.getBatchGenErrorCode();
    	Element respBatchGenerationErrorList = respDoc.createElementNS(NAMESPACE_URI, BATCH_GEN_RESP_ERR_LIST);
    	respBatchGenerationErrorList.setPrefix(NAMESPACE_PREFIX);
    	if (voucherServError != null) {
    		appendChildElementWithTextNode(respDoc, respBatchGenerationErrorList, BATCH_GEN_RESP_ERROR_CODE, Integer.toString(voucherServError.getCode()));
    	}
    	respRoot.appendChild(respBatchGenerationErrorList);
    	
    	return respRoot;
    	
    }
}
