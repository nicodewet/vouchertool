package com.mayloom.vouchserv.api.res.code;

import com.google.common.base.Optional;
import com.mayloom.vouchserv.man.api.res.code.VoucherServError;

public enum VoucherRedeemErrorCode implements VoucherServError {
	
	VOUCHER_NOT_NULL(300),
	
	PIN_NOT_NULL(301),
	
	VOUCHER_NOT_ACTIVE_OVERRIDE_USED(302),
	
	VOUCHER_PIN_LENGTH_INCORRECT(303),
	
	VOUCHER_EXPIRED(304),
	
	VOUCHER_DOES_NOT_EXIST(305),
	
	VOUCHER_BATCH_NOT_ACTIVE(306),
	
	VOUCHER_BATCH_EXPIRED(307),
	
	VOUCHER_REDEEMED(308);

     private int code;

	 private VoucherRedeemErrorCode(int c) {
	   code = c;
	 }

	 public int getCode() {
	   return code;
	 }
	 
	 public Optional<GetVoucherErrorCode> toGetVoucherErrorCode() {
		 for (GetVoucherErrorCode code : GetVoucherErrorCode.values()) {
			 if (code.getCode() == this.getCode()) {
				 return Optional.of(code);
			 }
		 }
		 return Optional.absent();
	 }
}
