package com.mayloom.vouchserv.api.res.code;

import com.mayloom.vouchserv.man.api.res.code.VoucherServError;


public enum GeneralVoucherServErrorCode implements VoucherServError {
	
	VSV_ID_INVALID(100);
	
	private int code;

	private GeneralVoucherServErrorCode(int c) {
	   code = c;
	}

	public int getCode() {
	   return code;
	}

}
