/**
 * 
 */
package com.mayloom.vouchserv.gen;

/**
 * @author Nico
 *
 */
public interface RandomStringGenerator {

	/**
	 * Generate a positive random number of between 6 and 30 digits. The generated number
	 * may be left padded with 0s. 
	 * 
	 * The generator is capable of generating the full range of digits for the specified length,
	 * e.g. with length 6, any value from 000000 to 999999 could be generated. 
	 * 
	 * Left padding example: users selects 10 as the desired length, the generator generates 
	 *          say a random number of 12345, then 0000012345 is returned 
	 * 
	 * @param length 	the length of the desired random number. Must be between 6 and 30
	 * @return       	the generated random number as a String, null if less than 6 or greater than 30
	 */
	String generateRandomNumber(int length);
	
	/**
	 * Generate a random alphanumeric string of characters of between 6 and 30. Alpha characters
	 * may optionally be capitalized.
	 * 
	 * @param length	  the length of the desired random alphanumeric. Must be between 6 and 30
	 * @param capitalize  if true, alpha characters ALL capitalized, if false alpha characters are RANDOMLY MIXED upper lower case
	 * @return			  the generated random alphanumeric as a String, null if less than 6 or greater than 30
	 */
	String generateRandomAlphanumeric(int length, boolean capitalize);
}
