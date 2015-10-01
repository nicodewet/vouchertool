/**
 * 
 */
package com.mayloom.vouchserv.gen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nico
 * 
 */
public class RandomStringGeneratorImplMock implements RandomStringGenerator {

	private final Logger logger = LoggerFactory.getLogger(RandomStringGeneratorImplMock.class);
	
	/* (non-Javadoc)
	 * @see com.mayloom.vouchserv.gen.RandomStringGenerator#generateRandomNumber(int)
	 */
	public static final String TWELVE = "935623497456";
	
	public String generateRandomNumber(int length) {	
		
		if (length == TWELVE.length()) {
			logger.debug("Generated random number {}", TWELVE);
			return TWELVE;
		} else 
			throw new RuntimeException("can only generate for length 12");
	}

	public String generateRandomAlphanumeric(int length, boolean capitalize) {
		// TODO Auto-generated method stub
		return null;
	}

}
