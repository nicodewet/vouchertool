package com.mayloom.vouchserv.ws.intercept;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.SoapMessage;
import com.google.common.base.Optional;

public class UsernameRequestLoggingInterceptor extends AbstractUsernameRequestInterceptor implements EndpointInterceptor {
	
	private final Logger logger = LoggerFactory.getLogger(UsernameRequestLoggingInterceptor.class);
	
	public boolean handleRequest(MessageContext messageContext, Object endpoint) throws Exception {
		
		if (logger.isInfoEnabled()) {
		
			WebServiceMessage message = messageContext.getRequest();
			
			if (message instanceof SoapMessage) {
	            
				SoapMessage soapMessage = (SoapMessage)message;
				
				Optional<String> soapUserName = getSoapUserName(soapMessage);
				
				if (soapUserName.isPresent() && StringUtils.hasText(soapUserName.get())) {
					
					Optional<String> requestName = getRequestLocalName(soapMessage);
					
					if (requestName.isPresent() && StringUtils.hasText(requestName.get())) {
						
						requestHitCounter.registerUserHit(soapUserName.get(), requestName.get());
						
						String userNameAndRequest = soapUserName.get() + " " + requestName.get();
						
						logger.info(">>> {} PAIR HIT #{}", userNameAndRequest, requestHitCounter.getHitCount(soapUserName.get(), requestName.get()));
						
					}
				} 
	        }
		}
		return true;
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
