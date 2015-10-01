package com.mayloom.vouchserv.gen;

public interface IdentifierTool {
	
	/**
	 * Generates a 23 character identifier formatted as a single String composed of three parts:
	 * 
	 * VSV-ID - 6 characters: included in all generated identifiers
	 * 20110703 - 8 digits: the date when the identifier was generated
	 * 123456789 - 9 digits: randomly generated 
	 * 
	 * @return the generated identifier
	 */
	public String generateVSVID();
	
	/**
	 * Checks that the specified String conforms to:
	 * 
	 * 1. Starts with VSV-ID
	 * 2. Followed by 17 digits
	 * 
	 * @param vsvId the String to be validated
	 * @return      true if the String is a valid vsvId, false otherwise
	 */
	public boolean validateVSVID(String vsvId);

}
