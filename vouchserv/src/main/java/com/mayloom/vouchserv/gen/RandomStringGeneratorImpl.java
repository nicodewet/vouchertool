/**
 * 
 */
package com.mayloom.vouchserv.gen;

import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mayloom.vouchserv.gen.RandomStringGenerator;

/**
 * @author Nico
 *
 */
public class RandomStringGeneratorImpl implements RandomStringGenerator {
	
	private final Logger logger = LoggerFactory.getLogger(RandomStringGeneratorImpl.class);
	
	// 400ms slower than own implementation
	// NOTE: max length of String is Integer.MAX_VALUE, equal to max size of an array
	private String generateRandomNumberAnyLength(int length) {
		
		return RandomStringUtils.randomNumeric(length);
	}
	
	
	public String generateRandomNumber(int length) {
		
		// the limitation below is due to the fact that we generate a long below and 
		// we can only generate the full range of 0 to 9 for 18 digits
		if (length > 30 || length < 6) {
			return null;
		}
		
		if (length <= 18) {
			
			// long: -9,223,372,036,854,775,808 to 9,223,372,036,854,775,807
			long randomLong = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
		
			if (randomLong < 0)
				randomLong = -randomLong;
		
			String randomString = Long.toString(randomLong);
			
			if (randomString == null || randomString.length() == 0) {
				throw new IllegalStateException();
			}
		
			if (randomString.length() == length) {
			
				return randomString;
			
			} else if (randomString.length() > length) {
			
				return randomString.substring(0, length);
			
			} else if (randomString.length() < length) {
			
				return String.format(formatString(length), randomString);
			
			} else {
			
				throw new IllegalStateException("Reached impossible state");
	
			}
			
		} else {
			
			return generateRandomNumberAnyLength(length); 
			
		}
	}
	
	public String generateRandomAlphanumeric(int length, boolean capitalize) {
		
		if (length > 30 || length < 6) {
			
			return null;
			
		} else {
		
			String randomAlphaNumeric = RandomStringUtils.randomAlphanumeric(length);
			
			if (capitalize) {
				
				return StringUtils.upperCase(randomAlphaNumeric);
				
			} else {
				
				return randomAlphaNumeric;
			}
		}
	}
	
	private String formatString(int length) { 
		String format = "%0" + length + "d"; 
		logger.info(format);
		return format;
	}
}
