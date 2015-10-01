package com.mayloom.vouchserv.api;

/**
 * Represents voucher pins numbers (or ranges) in their printed / usable form rather than 
 * actual numeric amount starting from 0. For example, if we want want to represent 
 * ONE_MILLION, which would in fact be 1 000 000 as a number, we rather use 999 999 since
 * this is the format one would use on an actual voucher (digital or physical).
 * 
 * @author Nico
 */
public enum VoucherPinAmount {
	
	/**
	 * 1 000 000 unique values when including all 0s
	 * pin form: 999 999, decimal form: 999999
	 */
	ONE_MILLION(999999),
	
	/**
	 * 10 000 000 unique values when including all 0s
	 * pin form: 999 9 999, decimal form: 9 999 999
	 */
	TEN_MILLION(9999999),
	
	/**
	 * 100 000 000 unique values when including all 0s
	 * pin form: 9999 9999, decimal form: 99 999 999, 3.72 GB or 4 000 000 032 bytes
	 */
	ONE_HUNDRED_MILLION(99999999),
	
	/**
	 * 1 000 000 000 unique values when including all 0s
	 * pin form: 999 999 999, decimal form, 999 999 999
	 */
	ONE_BILLION(999999999),
	
	/**
	 * 1 000 000 000 000 unique values when including all 0s 
	 * pin form: 9999 9999 9999 OR 999 999 999 999, decimal form: 999 999 999 999
	 */
	ONE_TRILLION(999999999999L); 
	
	private final long amount;
	
	VoucherPinAmount(long amount) {
		this.amount = amount;
	}
	
	public long amount() { return amount; }
	
	/**
	 * Get the number of digits associated with this VoucherPinAmount
	 * 
	 * @return
	 */
	public int length() { return Long.toString(amount).length(); }
	
	/**
	 * Returns the format String for use in java.lang.String#format(String, Object...) with
	 * the format ensuring that left padding of 0s occurs up to the length of the 
	 * VoucherPinAmount length()
	 * 
	 * @return
	 * @see java.lang.String#format(String, Object...)
	 */
	public String formatString() { 
		String format = "%0" + length() + "d"; 
		return format;
	}
}
