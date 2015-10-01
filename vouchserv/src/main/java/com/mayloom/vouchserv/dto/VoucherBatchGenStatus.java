package com.mayloom.vouchserv.dto;

public final class VoucherBatchGenStatus {

	private final int batchNumber;
	private final int requestedSize;
	private final int generatedSize;
	
	/**
	 * The status of a specific VoucherBatch in terms of voucher generation.
	 * 
	 * @param batchNumber the batchNumber of the VoucherBatch in question
	 * @param requestedSize the number of vouchers originally requested in terms of generation size	
	 * @param generatedSize	the number of vouchers that have been generated
	 */
	public VoucherBatchGenStatus(int batchNumber, int requestedSize, int generatedSize) {
		this.batchNumber = batchNumber;
		this.requestedSize = requestedSize;
		this.generatedSize = generatedSize;
	}

	public int getRequestedSize() {
		return requestedSize;
	}

	public int getGeneratedSize() {
		return generatedSize;
	}

	public int getBatchNumber() {
		return batchNumber;
	}
	
}
