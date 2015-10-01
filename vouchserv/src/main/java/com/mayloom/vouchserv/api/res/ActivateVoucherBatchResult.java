package com.mayloom.vouchserv.api.res;

import com.mayloom.vouchserv.man.api.res.code.ResultStatusCode;
import com.mayloom.vouchserv.man.api.res.code.VoucherServError;


/**
 * @author Nico
 *
 */
public class ActivateVoucherBatchResult {

	private ResultStatusCode activateVoucherBatchResultStatus;
	
	private int batchNumber;
	
	private VoucherServError activateVoucherBatchErrorCode;
	
	public ActivateVoucherBatchResult(int batchNumber) {
		
		this.batchNumber = batchNumber;
		this.activateVoucherBatchResultStatus = ResultStatusCode.SUCCESS;
		
	}
	
	public ActivateVoucherBatchResult(VoucherServError activateVoucherBatchErrorCode) {
		this.activateVoucherBatchResultStatus = ResultStatusCode.FAIL;
		this.activateVoucherBatchErrorCode = activateVoucherBatchErrorCode;
		
	}
	
	public ActivateVoucherBatchResult(VoucherServError activateVoucherBatchErrorCode, int batchNumber) {
		this.activateVoucherBatchResultStatus = ResultStatusCode.FAIL;
		this.activateVoucherBatchErrorCode = activateVoucherBatchErrorCode;
		this.batchNumber = batchNumber;
		
	}

	public ResultStatusCode getActivateVoucherBatchResultStatus() {
		return activateVoucherBatchResultStatus;
	}

	public int getBatchNumber() {
		return batchNumber;
	}

	public VoucherServError getActivateVoucherBatchErrorCode() {
		return activateVoucherBatchErrorCode;
	}
}
