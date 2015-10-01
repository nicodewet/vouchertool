package com.mayloom.vouchserv.ws;

import java.util.Date;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.mayloom.vouchserv.api.VoucherService;
import com.mayloom.vouchserv.dto.ContentEnricherVoucher;
import com.mayloom.vouchserv.man.api.dto.Voucher;
import com.mayloom.vouchserv.ws.util.IterableNodeList;

/**
 * @author Nico
 */
public abstract class AbstractVouchservEndpoint {
	
	/**
     * Namespace of both request and response.
     */
	static final String NAMESPACE_URI = "http://vouchertool.com/vouchserv/schemas";
	static final String NAMESPACE_PREFIX = "v";
	
	static final String NAMESPACE_NAME_URI = "http://www.w3.org/2000/xmlns/";
	static final String XSI_QUALIFIED_NAME = "xmlns:xsi";
	static final String XSI_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema-instance";
	
	/**
	 * Shared request / response elements.
	 */
	public static final String VSV_ID = "vsvID";
    public static final String BATCH_NUM = "batchNumber";
    public static final String RESP_RES_STAT_CODE = "resultStatusCode";
    public static final String VOUCHER_PIN = "pin";
    public static final String VOUCHER_SERIAL_NUM = "serialNumber";
    public static final String ERR_CODE = "errorCode";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String VOUCHER_RESP = "voucher";
    public static final String VOUCHER_REDEMPTION_DATE_RESP = "redemptionDate";
    public static final String EXPIRY_DATE_RESP = "expiryDate";
    public static final String ACTIVE_RESP = "active";
    
	static final DateTimeFormatter XML_DATE_TIME_FORMAT = ISODateTimeFormat.dateTimeNoMillis();
	
	private final Logger logger = LoggerFactory.getLogger(AbstractVouchservEndpoint.class);
	
	final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	
	@Autowired
	@Qualifier("voucherService")
	VoucherService voucherService;
	
	/**
	 * Append a child element with the specified <code>elementName</code> and with the specified
	 * <code>elementValue</code> as its own text node, to the specified <code>root</code>. 
	 * 
	 * i.e. Use this method to append, to <code>root</code>, this: <elementName>elementValue<elementName/>
	 * 
	 * @param doc
	 * @param root
	 * @param elementName
	 * @param elementValue
	 */
	void appendChildElementWithTextNode(Document doc, Element root, String elementName,
										String elementValue) {
		
		Element childToBeAppended = doc.createElementNS(NAMESPACE_URI,elementName); 
		childToBeAppended.setPrefix(NAMESPACE_PREFIX);
        Text textToBeAppended = doc.createTextNode(elementValue);
        childToBeAppended.appendChild(textToBeAppended);
        root.appendChild(childToBeAppended);
	}
	
	void appendNilChildElement(Document doc, Element root, String elementName) {
		Element childToBeAppended = doc.createElementNS(NAMESPACE_URI,elementName); 
		childToBeAppended.setPrefix(NAMESPACE_PREFIX);
		childToBeAppended.setAttributeNS(XSI_NAMESPACE_URI, "xsi:nil", Boolean.toString(true));
		root.appendChild(childToBeAppended);
	}
	
	/**
	 * Searches the direct children of <code>root</code> for the specified <code>elementName</code> and returns the containing Text. 
	 * If more than one node with the specified <code>elementName</code> exists, then the containing text of the first matching element found
	 * is returned.
	 * 
	 * @param root			the element who's children we wish to inspect
	 * @param elementName	the name of the element who's text we wish to return
	 * @return				null if no Node with <code>elementName</code> is found, or the text content of the found element (which could 
	 * 						also possibly be null) of the first child of the root with the specified <code>elementName</code> 
	 */
	String getDirectChildElementText(Element root, String elementName) {
		if (root == null) {
			throw new IllegalArgumentException();
		}
		if (elementName == null || elementName.length() == 0) {
			throw new IllegalArgumentException();
		}
		String result = null;
		
		NodeList nodeList = root.getChildNodes();
		
		for(final Node node : new IterableNodeList(nodeList)) {
			
			// We use endsWith below since the node name could be qualified with a namespace prefix, e.g. ns1:vsvID 
		  if (node.getNodeName().endsWith(elementName)) {
			  return node.getTextContent();
		  }
		  
		}
		
		return result;
	}
	
	Date getDate(String xmlDateTime) {
		Date dReqExpiryDate = null;
	    if (xmlDateTime != null) {
	    	DateTime dt = XML_DATE_TIME_FORMAT.parseDateTime(xmlDateTime);
	    	dReqExpiryDate = dt.toDate(); // lossy conversion
	    }
	    return dReqExpiryDate;
	}
	
	String getDate(Date date) {
		if (date == null) {
			return null;
		} else {
			DateTime dt = new DateTime(date);
			return XML_DATE_TIME_FORMAT.print(dt);
		}
		
	}
	
	/**
	 * Assertions that check that the specified Element has the expected NAMESPACE_URI
	 * and also had the expected Local Name
	 * 
	 * @param requestElement
	 * @param requestElementLocalName
	 */
	public void checkRequest(Element requestElement, String requestElementLocalName) {
		
		if (StringUtils.isEmpty(requestElementLocalName)) { 
			throw new IllegalArgumentException();
		}
		
		logger.debug("Handling element with namespace URI: {}", requestElement.getNamespaceURI());
		logger.debug("Handling element with local name: {}", requestElement.getLocalName());
		Assert.isTrue(NAMESPACE_URI.equals(requestElement.getNamespaceURI()), "Invalid namespace");
        Assert.isTrue(requestElementLocalName.equals(requestElement.getLocalName()), "Invalid local name");
		
	}
	
	/**
	 * Get the username of the authenticated principal from the SecurityContext.
	 * 
	 * @return The username
	 * @throws IllegalArgumentException if no username was found
	 */
	String getUsername() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = null;
		if (principal instanceof UserDetails) {
		  username = ((UserDetails)principal).getUsername();
		}
		if (username == null) {
			/** Should only get here in the event of misconfiguration, that is, if a security interceptor 
			 * (XwsSecurityInterceptor) did not do its job */
			throw new IllegalArgumentException("No UserDetails found");
		}
		return username;
	}
	
	Element buildVoucherElement(Document respDoc, Voucher voucher) {
		Element respVoucher = respDoc.createElementNS(NAMESPACE_URI, VOUCHER_RESP);
		respVoucher.setPrefix(NAMESPACE_PREFIX);
		appendChildElementWithTextNode(respDoc, respVoucher, VOUCHER_PIN, voucher.getPin());
		appendChildElementWithTextNode(respDoc, respVoucher, VOUCHER_SERIAL_NUM, Long.toString(voucher.getSerialNumber()));
		String xmlExpiryDateTime = getDate(voucher.getExpiryDate());
		if (xmlExpiryDateTime == null) { 
			appendNilChildElement(respDoc, respVoucher, EXPIRY_DATE_RESP);
		} else {
			appendChildElementWithTextNode(respDoc, respVoucher, EXPIRY_DATE_RESP, xmlExpiryDateTime);
		}    			
		appendChildElementWithTextNode(respDoc, respVoucher, ACTIVE_RESP, Boolean.toString(voucher.isActive()));
		String xmlRedemptionDateTime = getDate(voucher.getRedemptionDate());
		if (xmlRedemptionDateTime == null) { 
			appendNilChildElement(respDoc, respVoucher, VOUCHER_REDEMPTION_DATE_RESP);
		} else {
			appendChildElementWithTextNode(respDoc, respVoucher, VOUCHER_REDEMPTION_DATE_RESP, xmlRedemptionDateTime);
		}
		if (voucher instanceof ContentEnricherVoucher) {
			ContentEnricherVoucher contentEnricherVoucher = (ContentEnricherVoucher)voucher;
			String batchNumber = Integer.toString(contentEnricherVoucher.getBatchNumber());
			appendChildElementWithTextNode(respDoc, respVoucher, BATCH_NUM, batchNumber);
		}
		return respVoucher;
	}
}
