/**
 * 
 */
package com.mayloom.vouchserv.api.res;

import com.mayloom.vouchserv.man.api.res.VoucherServiceResult;
import com.mayloom.vouchserv.man.api.res.code.ResultStatusCode;
import com.mayloom.vouchserv.man.api.res.code.VoucherServError;

/**
 * @author Nico
 *
 */
public class ActivateVoucherSerialRangeResult extends VoucherServiceResult {
	
	private final long startSerialNumber; 
	private final long endSerialNumber;

	public ActivateVoucherSerialRangeResult(VoucherServError activeVoucherSerialRangeResultErrorCode) {
		super(ResultStatusCode.FAIL, activeVoucherSerialRangeResultErrorCode);
		this.startSerialNumber = -1L;
		this.endSerialNumber = -1L;
	}
	
	public ActivateVoucherSerialRangeResult(long startSerialNumber, long endSerialNumber) {
		super(ResultStatusCode.SUCCESS);
		this.startSerialNumber = startSerialNumber;
		this.endSerialNumber = endSerialNumber;
	}

	public long getStartSerialNumber() {
		return startSerialNumber;
	}

	public long getEndSerialNumber() {
		return endSerialNumber;
	}

}
