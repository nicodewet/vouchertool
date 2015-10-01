/**
 * 
 */
package com.mayloom.vouchserv.imp.enums;

/**
 * @author Nico
 *
 */
public enum FormattedString {
	
	/**
	 * VSV-ID
	 */
	VSVID("VSV-ID"),
	
	/**
	 * VSV-ID followed by 9 digits
	 */
	VSVID_REG_EXP(VSVID.formatted + "\\d{17}"),

	/**
	 * yyyyMMdd
	 */
	DATE_FORMAT("yyyyMMdd"),
	
	/**
	 * %09d
	 */
	NINE_DECIMALS_FORMAT("%09d");
	
	private final String formatted;
	
	FormattedString(String formatted) { this.formatted = formatted; }
	
	@Override public String toString() { return formatted; }
}
