package com.mayloom.vouchserv.ws.intercept;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.SoapMessage;
import com.google.common.base.Optional;

/**
 * Checks how many Concurrent requests are in progress for username-Request pairs. If
 * the number of requests in progress exceeds the designated limit, then a SOAP error
 * is returned.
 * 
 * @author Nico
 */
public class UserRequestRateControlInterceptor extends AbstractUsernameRequestInterceptor implements EndpointInterceptor {
	
	private final Logger logger = LoggerFactory.getLogger(UserRequestRateControlInterceptor.class);
	
	private final int MAX_REQUESTS_PER_SECOND = 20;
	
	public boolean handleRequest(MessageContext messageContext, Object endpoint) throws Exception {
	
		boolean concurrencyLevelsAcceptable = true;
		
		WebServiceMessage message = messageContext.getRequest();
		
		if (message instanceof SoapMessage) {
            
			SoapMessage soapMessage = (SoapMessage)message;
			
			Optional<String> soapUserName = getSoapUserName(soapMessage);
			
			if (soapUserName.isPresent() && StringUtils.hasText(soapUserName.get())) {
			
				int currentHitRate = requestHitCounter.registerHit(soapUserName.get());
				
				logger.info("User {} hit rate: {}", soapUserName.get(), currentHitRate);
				
				if (currentHitRate > MAX_REQUESTS_PER_SECOND) {
					
					concurrencyLevelsAcceptable = false;
					
				}
			
			} 
        }
		
		return concurrencyLevelsAcceptable;
	}
	
	public boolean handleResponse(MessageContext messageContext, Object endpoint) throws Exception {
		return true;
	}

	public boolean handleFault(MessageContext messageContext, Object endpoint) throws Exception {
		return true;
	}

	public void afterCompletion(MessageContext messageContext, Object endpoint, Exception ex) throws Exception {		
	}

}
