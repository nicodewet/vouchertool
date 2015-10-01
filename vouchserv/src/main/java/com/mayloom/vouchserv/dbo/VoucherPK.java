package com.mayloom.vouchserv.dbo;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class VoucherPK implements Serializable {

	// http://dwuysan.wordpress.com/2012/02/22/joincolumn-is-part-of-the-composite-primary-keys/
	// http://schuchert.wikispaces.com/JPA+Tutorial+1+-+Embedded+Entity
	// 
	private static final long serialVersionUID = 6891255171574332263L;
	
	private String voucherBatchOwnerId;
	private String pin;
	
	public VoucherPK() {
		
	}
	
	public VoucherPK(String voucherBatchOwnerId, String pin) {
		this.voucherBatchOwnerId = voucherBatchOwnerId;
		this.pin = pin;
	}
	
	/**
	 * @return the voucherBatchOwnerId
	 */
	public String getVoucherBatchOwnerId() {
		return voucherBatchOwnerId;
	}
	/**
	 * @param voucherBatchOwnerId the voucherBatchOwnerId to set
	 */
	public void setVoucherBatchOwnerId(String voucherBatchOwnerId) {
		this.voucherBatchOwnerId = voucherBatchOwnerId;
	}
	/**
	 * @return the pin
	 */
	public String getPin() {
		return pin;
	}
	/**
	 * @param pin the pin to set
	 */
	public void setPin(String pin) {
		this.pin = pin;
	}
}
