package com.mayloom.vouchserv.imp;

import com.mayloom.vouchserv.man.api.req.PinType;
import com.mayloom.vouchserv.man.api.req.VoucherBatchType;

public final class VoucherGenSubtask {
	
	private final long batchId;
	private final VoucherBatchType batchType;
	private final int pinLength;
	private final PinType pinType; 
	private final long startSerialNumb;
	private final long endSerialNumb;
	
	public VoucherGenSubtask(long batchId, VoucherBatchType batchType, int pinLength, PinType pinType, long startSerialNumb, long endSerialNumb) {
		if (endSerialNumb < startSerialNumb) {
			throw new IllegalArgumentException("endSerialNumb " + endSerialNumb + " cannot be less that startSerialNumb " + startSerialNumb);
		}
		if (startSerialNumb <= 0) {
			throw new IllegalArgumentException("startSerialNumb must be > 0");
		}
		this.batchId = batchId;
		this.batchType = batchType;
		this.pinLength = pinLength;
		this.pinType = pinType;
		this.startSerialNumb = startSerialNumb;
		this.endSerialNumb = endSerialNumb;
	}

	/**
	 * @return the batchId
	 */
	public long getBatchId() {
		return batchId;
	}
	
	/**
	 * @return
	 */
	public VoucherBatchType getBatchType() {
		return batchType;
	}

	/**
	 * @return the pinLength
	 */
	public int getPinLength() {
		return pinLength;
	}

	/**
	 * @return the pinType
	 */
	public PinType getPinType() {
		return pinType;
	}

	/**
	 * @return the startSerialNumb
	 */
	public long getStartSerialNumb() {
		return startSerialNumb;
	}

	/**
	 * @return the endSerialNumb
	 */
	public long getEndSerialNumb() {
		return endSerialNumb;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VoucherGenSubtask [batchId=" + batchId + ", batchType="
				+ batchType + ", pinLength=" + pinLength + ", pinType="
				+ pinType + ", startSerialNumb=" + startSerialNumb
				+ ", endSerialNumb=" + endSerialNumb + "]";
	}

}
