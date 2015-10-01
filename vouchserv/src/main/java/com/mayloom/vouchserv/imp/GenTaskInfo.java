package com.mayloom.vouchserv.imp;

import com.mayloom.vouchserv.dbo.VoucherBatchOwner;

public final class GenTaskInfo {
	
	private final long startSerialNo;
	private final long batchId;
	private final int batchNumber;
	
	/**
	 * Represents supplementary information required to continue to generate Vouchers
	 * for a particular BatchGenRequest, the information in this class represents information
	 * that we will only know once the VoucherBatch has been persisted (but the Vouchers
	 * not necessarily generated when performing asynchronous batch generation).
	 * 
	 * @param startSerialNo the start serial number for a generation task
	 * @param batchId the VoucherBatch Id
	 * @param batchNumber the VoucherBatch batchNumber
	 */
	public GenTaskInfo(long startSerialNo, long batchId, int batchNumber) {
		if (startSerialNo < VoucherBatchOwner.VBO_START_SERIAL_NO) {
			throw new IllegalArgumentException();
		}
		this.startSerialNo = startSerialNo;
		this.batchId = batchId;
		this.batchNumber = batchNumber;
	}

	public long getStartSerialNo() {
		return startSerialNo;
	}

	public long getBatchId() {
		return batchId;
	}

	public int getBatchNumber() {
		return batchNumber;
	}
	
}
