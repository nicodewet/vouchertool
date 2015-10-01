package com.mayloom.vouchserv.ws.intercept;

import java.util.Iterator;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Optional;
import com.mayloom.vouchserv.ws.hit.RequestHitCounter;

public abstract class AbstractUsernameRequestInterceptor {
	
	protected RequestHitCounter requestHitCounter;
	
	public RequestHitCounter getRequestHitCounter() {
		return requestHitCounter;
	}

	public void setRequestHitCounter(RequestHitCounter requestHitCounter) {
		this.requestHitCounter = requestHitCounter;
	}
	
	/**
	 * Get the Username, contained within a Security and in turn UsernameToken element.
	 * 
	 * @param soapMessage	the SoapMessage instance to be inspected
	 * @return the Optional<String> instance containing the SOAP Username				
	 */
	protected Optional<String> getSoapUserName(SoapMessage soapMessage) {
		
		Assert.notNull(soapMessage);
		
		if (soapMessage != null && soapMessage.getEnvelope() != null && soapMessage.getEnvelope().getHeader() != null) {
		
			Iterator<SoapHeaderElement> iterator = soapMessage.getEnvelope().getHeader().examineAllHeaderElements();
	        
			while(iterator.hasNext()) {
				
	        	SoapHeaderElement header = iterator.next();
	        	
	        	String headerName = header.getName().getLocalPart();
	        	    	
	        	if(headerName.equalsIgnoreCase("Security")) {
	        		
	        		DOMSource domSource = (DOMSource) header.getSource();
	        		
	        		Node securityNode = domSource.getNode();
	        		
	        		NodeList securityNodeChildren = securityNode.getChildNodes();
	        		
	        		for (int i = 0; i < securityNodeChildren.getLength(); i++) {
	        			
	        			Node childNode = securityNodeChildren.item(i);
	        			  
	        			if (childNode.getLocalName() != null && childNode.getLocalName().equalsIgnoreCase("UsernameToken")) {
	        				
	        				NodeList usernameTokenChildren = childNode.getChildNodes();
	        				
	        				for (int j = 0; j < usernameTokenChildren.getLength(); j++) {
	        					
	        					Node userNameTokenChild = usernameTokenChildren.item(j);
	        					
	        					if (userNameTokenChild.getLocalName() != null && userNameTokenChild.getLocalName().equalsIgnoreCase("Username")) {
	        						
	        						String userNameCandidate = userNameTokenChild.getTextContent();
	        						
	        						if (StringUtils.hasLength(userNameCandidate)) {
	        							
	        							return Optional.of(userNameCandidate);
	        							
	        						} else {
	        							
	        							return Optional.absent();
	        							
	        						}
	        						
	        					}
	        					
	        				}
	        				
	        			}
	        		}
	        		
	        	}
	        }
		}
		
        return Optional.absent();
	}
	
	protected Optional<String> getRequestLocalName(SoapMessage soapMessage) { 
		
		Assert.notNull(soapMessage);
		
		SoapBody soapBody = soapMessage.getSoapBody();
		Source soapBodySource = soapBody.getPayloadSource();
		DOMSource domSource = (DOMSource)soapBodySource;
		Node node = domSource.getNode();
		
		Assert.notNull(node);
		String requestLocalName = node.getLocalName(); 
		
		if (requestLocalName == null) {
			return Optional.absent();
		} else {
			return Optional.of(requestLocalName);
		}
	}

}
