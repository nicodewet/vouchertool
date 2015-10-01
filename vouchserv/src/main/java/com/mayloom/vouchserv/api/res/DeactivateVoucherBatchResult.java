package com.mayloom.vouchserv.api.res;

import com.mayloom.vouchserv.man.api.res.VoucherServiceResult;
import com.mayloom.vouchserv.man.api.res.code.ResultStatusCode;
import com.mayloom.vouchserv.man.api.res.code.VoucherServError;

/**
 * @author Nico
 *
 */
public class DeactivateVoucherBatchResult extends VoucherServiceResult {
	
	private final int batchNumber;

	public DeactivateVoucherBatchResult(VoucherServError activateVoucherBatchErrorCode, int batchNumber) {
		super(ResultStatusCode.FAIL, activateVoucherBatchErrorCode);
		this.batchNumber = batchNumber;
	}
	
	public DeactivateVoucherBatchResult(int batchNumber) {
		super(ResultStatusCode.SUCCESS);
		this.batchNumber = batchNumber;
	}
	
	public int getBatchNumber() {
		return batchNumber;
	}

}
