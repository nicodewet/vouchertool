package com.mayloom.vouchserv.api.res.code;

import com.mayloom.vouchserv.man.api.res.code.VoucherServError;


/**
 * Error codes applicable to both activateVoucherbatch and deactivateVoucherBatch 
 * operations.
 * 
 * @author Nico
 */
public enum VoucherBatchActivationErrorCode implements VoucherServError {
	
	 INVALID_BATCH_NUMBER(400),
	 
	 BATCH_DOES_NOT_EXIST(401),
	 
	 BATCH_ALREADY_ACTIVE(402),
	 
	 BATCH_ALREADY_INACTIVE(403);

	 private int code;

	 private VoucherBatchActivationErrorCode(int c) {
	   code = c;
	 }

	 public int getCode() {
	   return code;
	 }
	
}
