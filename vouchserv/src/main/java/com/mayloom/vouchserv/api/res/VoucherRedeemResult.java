package com.mayloom.vouchserv.api.res;

import com.mayloom.vouchserv.man.api.res.code.ResultStatusCode;
import com.mayloom.vouchserv.man.api.res.code.VoucherServError;


public class VoucherRedeemResult {
	
	private ResultStatusCode redeemVoucherResultStatus;
	
	private long serialNumber;
	
	private VoucherServError voucherRedeemErrorCode; 
	
	/**
	 * Use this constructor if NO ERRORS occurred.
	 * 
	 * @param serialNumber The serial number of the redeemed voucher.
	 */
	public VoucherRedeemResult(long serialNumber) {
		this.redeemVoucherResultStatus = ResultStatusCode.SUCCESS;
		this.serialNumber = serialNumber;
	}
	
	/**
	 * Use this constructor if AN ERROR occurred, since the detail
	 * of the error can be specified using the specified VoucherRedeemErrorCode.
	 * 
	 * @param errorCode If voucher redemption was unsuccessful, used to specify the error code. 
	 */
	public VoucherRedeemResult(VoucherServError errorCode) {
		this.redeemVoucherResultStatus = ResultStatusCode.FAIL;
		this.voucherRedeemErrorCode = errorCode;
	}

	public ResultStatusCode getRedeemVoucherResultStatus() {
		return redeemVoucherResultStatus;
	}

	/**
	 * @return The serial number of the redeemed voucher.
	 */
	public long getSerialNumber() {
		return serialNumber;
	}

	public VoucherServError getVoucherRedeemErrorCode() {
		return voucherRedeemErrorCode;
	}

}
