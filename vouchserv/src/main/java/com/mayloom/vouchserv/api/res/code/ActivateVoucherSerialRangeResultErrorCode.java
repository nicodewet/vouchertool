package com.mayloom.vouchserv.api.res.code;

import com.mayloom.vouchserv.man.api.res.code.VoucherServError;

public enum ActivateVoucherSerialRangeResultErrorCode implements VoucherServError {

	START_END_SERIAL_RANGE_VALIDATION_ERROR(500),
	
	SERIAL_RANGE_PORTION_DOES_NOT_EXIST(501),
	 
	SERIAL_RANGE_PORTION_ALREADY_ACTIVE(502),
	
	TEMP(503);
	
	private int code;

	private ActivateVoucherSerialRangeResultErrorCode(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
}
