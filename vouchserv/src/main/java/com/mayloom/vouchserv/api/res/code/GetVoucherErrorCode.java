package com.mayloom.vouchserv.api.res.code;

import com.mayloom.vouchserv.man.api.res.code.VoucherServError;

public enum GetVoucherErrorCode implements VoucherServError {
	
	VOUCHER_DOES_NOT_EXIST(305);
	
	 private int code;

	 private GetVoucherErrorCode(int c) {
	   code = c;
	 }

	 public int getCode() {
	   return code;
	 }
	
}
