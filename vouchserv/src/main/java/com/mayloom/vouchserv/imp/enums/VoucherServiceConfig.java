/**
 * 
 */
package com.mayloom.vouchserv.imp.enums;

/**
 * VoucherServiceImp configuration name - int value pairs.
 * 
 * @author Nico
 */
public enum VoucherServiceConfig {
	
	/**
	 * Maximum batch size of 1000 vouchers.
	 */
	SYNC_VOUCHER_BATCH_SIZE_GENERATION_LIMIT(1000);
	
	private int value;

	 private VoucherServiceConfig(int c) {
	   value = c;
	 }

	 public int getValue() {
	   return value;
	 }

}
