package com.mayloom.vt.bean;

import java.util.Date;

import com.mayloom.vouchserv.man.api.req.PinType;

/**
 * Used in forms to generate voucher batches by anonymous users.
 * 
 * Anonymous users are not registered, that is do not have an account and so no username and
 * also an API KEY (VSV-ID) is not relevant to the user in terms of form data.
 * 
 * The data in this form will eventually be used to populate a BatchGenRequest (voucherservice class)
 * and only REGULAR voucher batches will be generated.
 * 
 * @author Nico
 */
public class GenerateAnonymousVouchersBean {
	
	// TODO should we give user the option where they want to generate vouchers with unique pins
	//      , via a checkbox, or unique pin codes only (i.e. not wrapped in voucher construct)
	
	public static final String NUMERIC = "NUMERIC";
	public static final String ALPHA_UPPER = "ALPHANUMERIC UPPER CASE";
	public static final String ALPHA_MIXED = "ALPHANUMERIC MIXED CASE";

	// this can also be a drop down?
	private String voucherNumber = "";   
	
	// this need to be a drop down
	private String pinLength = ""; 
	
	// this needs to be a drop down
	private String pinType = ""; 

//	public String getVoucherNumber() {
//		return voucherNumber;
//	}
	
	public PinType getConvertedPinType() {
		if (NUMERIC.equals(pinType)) {
			return PinType.NUMERIC; 
		} else if (ALPHA_MIXED.equals(pinType)) {
			return PinType.ALPHANUMERIC_MIXED_CASE; 
		} else if (ALPHA_UPPER.equals(pinType)) {
			return PinType.ALPHANUMERIC_UPPER_CASE; 
		} else {
			return PinType.NUMERIC; // effective default
		}
	}
	
	public String getPinType() {
		return pinType;
	}
	
	public int getConvertedPinLength() {
		return Integer.parseInt(pinLength);
	}
	
	public String getPinLength() {
		return pinLength;
	}
	
	/**
	 * Return the int representation of our form data. Naturally we are assuming that sufficient validation
	 * has been done elsewhere to protect us against a NumberFormatException
	 * 
	 * @return the int representation of our form data
	 */
	public int getConvertedVoucherNumber() {
		return Integer.parseInt(voucherNumber);
	}
	
	public String getVoucherNumber() {
		return voucherNumber;
	}

	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}

//	public String getPinLength() {
//		return pinLength;
//	}

	public void setPinLength(String pinLength) {
		this.pinLength = pinLength;
	}

//	public String getPinType() {
//		return pinType;
//	}

	public void setPinType(String pinType) {
		this.pinType = pinType;
	}
}
