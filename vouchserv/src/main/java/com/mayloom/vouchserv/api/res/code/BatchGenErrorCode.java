package com.mayloom.vouchserv.api.res.code;

import com.mayloom.vouchserv.man.api.res.code.VoucherServError;


/**
 * @author Nico
 *
 */
public enum BatchGenErrorCode implements VoucherServError {
	
	/**
	 * Batch size selected exceeds maximum allowable value.
	 */
	BATCH_SIZE_LIMIT_EXCEEDED(200),
	
	/**
	 * Batch size selected must exceed 0.
	 */
	BATCH_SIZE_MUST_BE_GREATER_THAN_ZERO(201),
	
	/**
	 * Batch expiry date in the past in terms of the system clock time, 
	 * must be in the future.
	 */
	BATCH_EXP_DATE_IN_PAST(202),
	
	/**
	 * Pin length must be between 6 and 30 inclusive.
	 */
	PIN_LENGTH(203),
	
	/**
	 * Unique pin space saturation.
	 */
	PIN_SPACE_SATURATION(204);

	 private int code;

	 private BatchGenErrorCode(int c) {
	   code = c;
	 }

	 public int getCode() {
	   return code;
	 }
}
