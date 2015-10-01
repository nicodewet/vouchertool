package com.mayloom.vouchserv.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

public class LoggingSoapFaultMappingExceptionResolver extends SoapFaultMappingExceptionResolver {
	
	private final Logger logger = LoggerFactory.getLogger(LoggingSoapFaultMappingExceptionResolver.class);
	
	@Override
	protected void logException(Exception ex, MessageContext messageContext) {
		logger.warn("ERROR", ex);
	}

}
