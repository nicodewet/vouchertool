package com.mayloom.vt.util;

public interface BasicEmailService {
	
	/**
	 * Send an email with the basic attributes one would expect
	 * 
	 * The "send" could in fact only result in the email being logged if the service implementation
	 * has been configured as such (this is useful during development)
	 * 
	 * @param email
	 * @throws EmailServiceException TODO document underlying Exception types
	 * @return true if the email was sent, false if only the email was logged and no send was attempted
	 */
	public boolean sendBasicEmail(BasicEmail email);

}
