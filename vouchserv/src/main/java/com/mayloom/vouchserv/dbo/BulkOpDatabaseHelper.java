package com.mayloom.vouchserv.dbo;

import com.mayloom.vouchserv.api.res.ActivateVoucherSerialRangeResult;

public interface BulkOpDatabaseHelper {

	/**
	 * WARNING This method is for testing purposes only, as it does not respect the 
	 * data model (Voucher being owned by VoucherBatchOwner).
	 * 
	 * @param startSerialNumb	operation applies to all vouchers >= startSerialNumb
	 * @param endSerialNumb     operation applies to all vouchers <= endSerialNumb
	 * @param activeStatus		true if we wish to update all Vouchers with active as true, false otherwise
	 * @return					number of Vouchers updated
	 */
	@Deprecated
	public int bulkUpdateVoucherActiveStatus(long startSerialNumb, long endSerialNumb, boolean activeStatus);
	
	public ActivateVoucherSerialRangeResult bulkUpdateVoucherActivate(String vsvId, long startSerialNumb, long endSerialNumb);
	
}
