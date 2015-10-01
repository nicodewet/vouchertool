/**
 * 
 */
package com.mayloom.vouchserv.imp;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import com.mayloom.vouchserv.api.VoucherPinAmount;

/**
 * @author Nico
 *
 */
public class VoucherPinAmountTest {

	/**
	 * Test method for {@link com.mayloom.vouchserv.api.VoucherPinAmount#amount()}.
	 */
	@Test
	public void testAmount() {
		long one_billion_less_one = 1000000000 - 1;
		assertEquals(one_billion_less_one, VoucherPinAmount.ONE_BILLION.amount());
	}

	/**
	 * Test method for {@link com.mayloom.vouchserv.api.VoucherPinAmount#length()}.
	 */
	@Test
	public void testLength() {
		// Physical count: 999 999 999 = 9
		assertEquals(9, VoucherPinAmount.ONE_BILLION.length());
	}
	
	/**
	 * Test method for {@link com.mayloom.vouchserv.api.VoucherPinAmount#formatString()}.
	 */
	@Test
	public void testFormatStringONE_TRILLION() {
		
		Random random = new Random();
		
		long generatedVoucherPin = random.nextLong();
		
		if (generatedVoucherPin < 0)
			generatedVoucherPin = -generatedVoucherPin;
		
		Long generatedVoucherPinObj = new Long(generatedVoucherPin);
		
		String strGeneratedVoucherPin = generatedVoucherPinObj.toString(); 
		
		if (strGeneratedVoucherPin.length() > VoucherPinAmount.ONE_TRILLION.length()) {
			
			strGeneratedVoucherPin = strGeneratedVoucherPin.substring(0, VoucherPinAmount.ONE_TRILLION.length());
			
		} else {
		
			strGeneratedVoucherPin = String.format(VoucherPinAmount.ONE_TRILLION.formatString(), strGeneratedVoucherPin);
			
		}
		
		assertEquals(VoucherPinAmount.ONE_TRILLION.length(),strGeneratedVoucherPin.length());
	}

}
