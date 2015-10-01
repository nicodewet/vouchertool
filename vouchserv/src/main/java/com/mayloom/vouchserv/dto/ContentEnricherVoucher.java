package com.mayloom.vouchserv.dto;

import com.mayloom.vouchserv.man.api.dto.Voucher;

public class ContentEnricherVoucher extends Voucher {
	
	private int batchNumber;

	/**
	 * @return the batchNumber
	 */
	public int getBatchNumber() {
		return batchNumber;
	}

	/**
	 * @param batchNumber the batchNumber to set
	 */
	public void setBatchNumber(int batchNumber) {
		this.batchNumber = batchNumber;
	}

}
