/**
 * 
 */
package com.mayloom.vouchserv;

import static org.junit.Assert.*;

import java.util.List;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.common.base.Optional;
import com.mayloom.vouchserv.api.VoucherPinAmount;
import com.mayloom.vouchserv.api.VoucherService;
import com.mayloom.vouchserv.man.api.req.BatchGenRequest;
import com.mayloom.vouchserv.man.api.req.PinType;
import com.mayloom.vouchserv.man.api.req.VoucherBatchType;
import com.mayloom.vouchserv.api.res.ActivateVoucherBatchResult;
import com.mayloom.vouchserv.api.res.ActivateVoucherSerialRangeResult;
import com.mayloom.vouchserv.man.api.res.BatchGenResult;
import com.mayloom.vouchserv.api.res.DeactivateVoucherBatchResult;
import com.mayloom.vouchserv.api.res.GetVoucherResult;
import com.mayloom.vouchserv.man.api.res.RegisterResult;
import com.mayloom.vouchserv.api.res.VoucherRedeemResult;
import com.mayloom.vouchserv.api.res.code.ActivateVoucherSerialRangeResultErrorCode;
import com.mayloom.vouchserv.api.res.code.GeneralVoucherServErrorCode;
import com.mayloom.vouchserv.man.api.res.code.ResultStatusCode;
import com.mayloom.vouchserv.api.res.code.VoucherBatchActivationErrorCode;
import com.mayloom.vouchserv.api.res.code.VoucherRedeemErrorCode;
import com.mayloom.vouchserv.dbo.DatabaseHelper;
import com.mayloom.vouchserv.man.api.dto.Voucher;
import com.mayloom.vouchserv.man.api.dto.VoucherBatch;
import com.mayloom.vouchserv.dto.VoucherBatchGenStatus;
import com.mayloom.vouchserv.man.api.dto.VoucherBatchOwner;
import com.mayloom.vouchserv.imp.GenTaskInfo;
import com.mayloom.vouchserv.imp.VoucherGenSubtask;
import com.mayloom.vouchserv.imp.VoucherServiceImp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nico
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"spring/main-test-spring-context.xml"})
public class VoucherServiceTest {

	private final Logger logger = LoggerFactory.getLogger(VoucherServiceTest.class);
	
	@Autowired
	@Qualifier("voucherService")
	private VoucherService voucherService;
	
	@Autowired
	@Qualifier("databaseHelper")
	private DatabaseHelper databaseHelper;
	
	private static String correctLengthPin;
	
	private static String USERNAME = "foo@bar.com";
	private static String ANOTHER_USERNAME = "nico@haagen.com";
	
	private static boolean databaseSetup = false;
	
	@Before
	public void setUp() {
		if (!databaseSetup) {
			databaseHelper.addNewOrdinaryUser(USERNAME, "password");
			databaseHelper.addNewOrdinaryUser(ANOTHER_USERNAME, "password");
			databaseSetup = true;
		}
	}
	
	@BeforeClass public static void setUpOnce() {
		// prob better way to do this, as spring bean config dependent
		
		StringBuilder sb = new StringBuilder();
		for(int i=0; i < VoucherPinAmount.ONE_TRILLION.length(); ++i) {
			sb.append("1");
		}
		correctLengthPin = sb.toString();
	}

	/**
	 * Test method for {@link com.mayloom.vouchserv.api.VoucherService#register()}.
	 */
	@Test
	public void testRegister() {
		RegisterResult registerResult = voucherService.register(USERNAME);
		assertEquals(ResultStatusCode.SUCCESS, registerResult.getResultStatusCode());
		String vsvId = registerResult.getVsvId();
		assertNotNull(vsvId);	
		assertEquals(23, vsvId.length());
	}

	/**
	 * Test method for {@link com.mayloom.vouchserv.api.VoucherService#generateVoucherBatch(java.lang.String, int)}.
	 */
	@Test
	public void testGenerateRegularVoucherBatch() {
		RegisterResult registerResult = voucherService.register(USERNAME);
		assertEquals(ResultStatusCode.SUCCESS, registerResult.getResultStatusCode());
		String vsvId = registerResult.getVsvId();
		BatchGenRequest req = new BatchGenRequest.Builder(USERNAME, vsvId, 30, VoucherBatchType.REGULAR).
									pinLength(30).pinType(PinType.ALPHANUMERIC_MIXED_CASE).build();
		BatchGenResult batchGenResult = voucherService.generateVoucherBatch(USERNAME, req);
		assertNotNull(batchGenResult);
		assertEquals(1, batchGenResult.getBatchNumber());
		// TODO a lot more assertions!
		VoucherBatch voucherBatch = voucherService.getVoucherBatch(USERNAME, vsvId, batchGenResult.getBatchNumber());
		// REGULAR check ...
		assertFalse(voucherBatch.isActive());
		assertNull(voucherBatch.getExpiryDate());
		for (Voucher voucher : voucherBatch.getVouchers()) {
			// REGULAR check ...
			assertTrue(voucher.isActive());
			assertNull(voucher.getRedemptionDate());
			assertNotNull(voucher.getPin());
			assertTrue(voucher.getSerialNumber() > 0);
		}
	}
	
	/**
	 * Test method for {@link com.mayloom.vouchserv.api.VoucherService#generateVoucherBatch(java.lang.String, int)}.
	 */
	@Test
	public void testGenerateJustInTimeVoucherBatch() {
		RegisterResult registerResult = voucherService.register(USERNAME);
		assertEquals(ResultStatusCode.SUCCESS, registerResult.getResultStatusCode());
		String vsvId = registerResult.getVsvId();
		BatchGenRequest req = new BatchGenRequest.Builder(USERNAME, vsvId, 5, VoucherBatchType.JUST_IN_TIME).
										pinLength(6).pinType(PinType.ALPHANUMERIC_UPPER_CASE).build();
		BatchGenResult batchGenResult = voucherService.generateVoucherBatch(USERNAME, req);
		assertNotNull(batchGenResult);
		assertEquals(1, batchGenResult.getBatchNumber());
		VoucherBatch voucherBatch = voucherService.getVoucherBatch(USERNAME, vsvId, batchGenResult.getBatchNumber());
		// JUST_IN_TIME check....
		assertTrue(voucherBatch.isActive());
		assertNull(voucherBatch.getExpiryDate());
		for (Voucher voucher : voucherBatch.getVouchers()) {
			// JUST_IN_TIME check....
			assertFalse(voucher.isActive());
			assertNull(voucher.getRedemptionDate());
			assertNotNull(voucher.getPin());
			assertTrue(voucher.getSerialNumber() > 0);
		}
	}
	
	
	/**
	 * Test method for {@link com.mayloom.vouchserv.api.VoucherService#getVoucherBatch(java.lang.String, int)}.
	 */
	@Test
	public void testGetVoucherBatch() {
		RegisterResult registerResult = voucherService.register(USERNAME);
		assertEquals(ResultStatusCode.SUCCESS, registerResult.getResultStatusCode());
		String vsvId = registerResult.getVsvId();
		BatchGenRequest req = new BatchGenRequest.Builder(USERNAME, vsvId, 10, VoucherBatchType.REGULAR).build();
		BatchGenResult batchGenResult = voucherService.generateVoucherBatch(USERNAME, req);
		assertEquals(ResultStatusCode.SUCCESS, 
				batchGenResult.getBatchGenResultStatus());
		VoucherBatch voucherBatch = voucherService.getVoucherBatch(USERNAME, vsvId, batchGenResult.getBatchNumber());
		assertNotNull(voucherBatch);
		assertNotNull(voucherBatch.getVouchers());
		assertNotNull(voucherBatch.getCreationDate());
		assertEquals(1, voucherBatch.getBatchNumber());
		assertEquals(10, voucherBatch.getVouchers().size());
		long previousSerialNumber = 0;
		for (Voucher voucher : voucherBatch.getVouchers()) {
			assertNotNull(voucher.getPin());
			assertNull(voucher.getExpiryDate());
			assertNull(voucher.getRedemptionDate());
			//logger.info(Long.toString(voucher.getSerialNumber()));
			assertTrue(voucher.getSerialNumber() > previousSerialNumber);
			++previousSerialNumber;
		}
	}

	/**
	 * Test method for {@link com.mayloom.vouchserv.api.VoucherService#redeemVoucher(java.lang.String, com.mayloom.vouchserv.dto.Voucher)}.
	 * 
	 * This test is to test validation of the Voucher.
	 */
	@Test
	public void testRedeemVoucher_InValidVoucherValidation() {
		
		String vsvId = null;
		Voucher voucher = null;
		assertEquals(GeneralVoucherServErrorCode.VSV_ID_INVALID, voucherService.redeemVoucher(USERNAME, vsvId, voucher).getVoucherRedeemErrorCode());
		
		RegisterResult registerResult = voucherService.register(USERNAME);
		assertEquals(ResultStatusCode.SUCCESS, registerResult.getResultStatusCode());
		vsvId = registerResult.getVsvId();
		assertEquals(VoucherRedeemErrorCode.VOUCHER_NOT_NULL, voucherService.redeemVoucher(USERNAME, vsvId, voucher).getVoucherRedeemErrorCode());
		
		voucher = new Voucher();
		assertEquals(VoucherRedeemErrorCode.PIN_NOT_NULL, voucherService.redeemVoucher(USERNAME, vsvId, voucher).getVoucherRedeemErrorCode());
		
		voucher.setActive(true);
		assertEquals(VoucherRedeemErrorCode.PIN_NOT_NULL, voucherService.redeemVoucher(USERNAME, vsvId, voucher).getVoucherRedeemErrorCode());
		
		String tooShortPin = "1";
		voucher.setPin(tooShortPin);
		assertEquals(VoucherRedeemErrorCode.VOUCHER_PIN_LENGTH_INCORRECT, voucherService.redeemVoucher(USERNAME, vsvId, voucher).getVoucherRedeemErrorCode());
		
		voucher.setPin(correctLengthPin);
		LocalDateTime now = new LocalDateTime();
		LocalDateTime yesterDaySameTime = now.minusDays(1);
		voucher.setExpiryDate(yesterDaySameTime.toDateTime().toDate());
		assertEquals(VoucherRedeemErrorCode.VOUCHER_EXPIRED, voucherService.redeemVoucher(USERNAME, vsvId, voucher).getVoucherRedeemErrorCode());
		
	}
	
	/**
	 * Test method for {@link com.mayloom.vouchserv.api.VoucherService#redeemVoucher(java.lang.String, com.mayloom.vouchserv.dto.Voucher)}.
	 * 
	 * This test is to test validation of the Voucher.
	 */
	@Test
	public void testRedeemVoucher_InActiveVoucherValidation() {
		
		Voucher voucher = new Voucher();
		voucher.setActive(false);
		voucher.setPin(correctLengthPin);
		RegisterResult registerResult = voucherService.register(USERNAME);
		assertEquals(ResultStatusCode.SUCCESS, registerResult.getResultStatusCode());
		String vsvId = registerResult.getVsvId();
		assertEquals(VoucherRedeemErrorCode.VOUCHER_NOT_ACTIVE_OVERRIDE_USED, 
					voucherService.redeemVoucher(USERNAME, vsvId, voucher).getVoucherRedeemErrorCode());
	}
	
	/**
	 * Test method for {@link com.mayloom.vouchserv.api.VoucherService#redeemVoucher(java.lang.String, com.mayloom.vouchserv.dto.Voucher)}.
	 * 
	 * This test is to test redemption of a valid Voucher.
	 */
	@Test
	public void testRedeemVoucher_ValidVoucher() {
		
		final int BATCH_SIZE = 5;
		
		// REGISTER
		RegisterResult registerResult = voucherService.register(USERNAME);
		assertEquals(ResultStatusCode.SUCCESS, registerResult.getResultStatusCode());
		String vsvId = registerResult.getVsvId();
		
		// GENERATE REGULAR VOUCHER BATCH
		BatchGenRequest req = new BatchGenRequest.Builder(USERNAME, vsvId, BATCH_SIZE, VoucherBatchType.REGULAR).build();
		BatchGenResult batchGenResult = voucherService.generateVoucherBatch(USERNAME, req);
		assertEquals(ResultStatusCode.SUCCESS, batchGenResult.getBatchGenResultStatus());
		
		// GET VOUCHER BATCH
		VoucherBatch voucherBatch = voucherService.getVoucherBatch(USERNAME, vsvId, batchGenResult.getBatchNumber());
		assertFalse(voucherBatch.isActive());
		assertEquals(BATCH_SIZE, voucherBatch.getVouchers().size());
		Voucher voucher = voucherBatch.getVouchers().remove(0);
		assertEquals(1, voucherBatch.getBatchNumber());
		
		// TRY TO REDEEM VOUCHER
		VoucherRedeemResult redeemVoucherResult = voucherService.redeemVoucher(USERNAME, vsvId, voucher);
		assertEquals(ResultStatusCode.FAIL, redeemVoucherResult.getRedeemVoucherResultStatus());
		assertEquals(VoucherRedeemErrorCode.VOUCHER_BATCH_NOT_ACTIVE, redeemVoucherResult.getVoucherRedeemErrorCode());
		
		// ACTIVATE VOUCHER BATCH
		ActivateVoucherBatchResult voucherBatchActivationResult = voucherService.activateVoucherBatch(USERNAME, vsvId, voucherBatch.getBatchNumber());
		assertEquals(ResultStatusCode.SUCCESS, voucherBatchActivationResult.getActivateVoucherBatchResultStatus());
		
		// GET VOUCHER BATCH
		VoucherBatch batch = voucherService.getVoucherBatch(USERNAME, vsvId, batchGenResult.getBatchNumber());
		assertTrue(batch.isActive());
		
		// TRY TO REDEEM VOUCHER
		assertNull(voucher.getRedemptionDate());
		assertTrue(voucher.isActive());
		redeemVoucherResult = voucherService.redeemVoucher(USERNAME, vsvId, voucher);
		assertNotNull(redeemVoucherResult);
		assertEquals(voucher.getSerialNumber(), redeemVoucherResult.getSerialNumber());
		assertEquals(ResultStatusCode.SUCCESS, redeemVoucherResult.getRedeemVoucherResultStatus());
		
		// CHECK VOUCHER HAS BEEN REDEEMED
		VoucherBatch checkBatch = voucherService.getVoucherBatch(USERNAME, vsvId, batchGenResult.getBatchNumber());
		assertNotNull(checkBatch);
		assertNotNull(checkBatch.getVouchers());
		assertEquals(BATCH_SIZE, checkBatch.getVouchers().size());
		Voucher checkVoucher = checkBatch.getVouchers().remove(0);
		assertFalse(checkVoucher.isActive());
		assertNotNull(checkVoucher.getRedemptionDate());
		
		// TRY TO REDEEM VOUCHER AGAIN
		redeemVoucherResult = voucherService.redeemVoucher(USERNAME, vsvId, voucher.getPin());
		assertEquals(ResultStatusCode.FAIL, redeemVoucherResult.getRedeemVoucherResultStatus());
		assertEquals(VoucherRedeemErrorCode.VOUCHER_REDEEMED, redeemVoucherResult.getVoucherRedeemErrorCode());
	}
	
	/**
	 * Test method for {@link com.mayloom.vouchserv.api.VoucherService#deactivateVoucherbatch(String, int)}.
	 * 
	 * This test is to test deactivation of a VoucherBatch.
	 */
	@Test
	public void testDeactivateVoucherBatch() {
		
		/***************
		 *  Generate REGULAR VoucherBatch, which we expect to be inactive, we should get fail result and an error code
		 */
		
		// 1. use random vsvId, random batchNumber
		
		RegisterResult registerResult = voucherService.register(USERNAME);
		assertEquals(ResultStatusCode.SUCCESS, registerResult.getResultStatusCode());
		String randomVsvId = "VSV-ID20110703123456789";
		BatchGenRequest req = new BatchGenRequest.Builder(USERNAME, randomVsvId, 1, VoucherBatchType.REGULAR).build();
		BatchGenResult batchGenResult = voucherService.generateVoucherBatch(USERNAME, req);
		assertEquals(ResultStatusCode.FAIL, batchGenResult.getBatchGenResultStatus());
		assertEquals(GeneralVoucherServErrorCode.VSV_ID_INVALID, batchGenResult.getBatchGenErrorCode());
		DeactivateVoucherBatchResult deactivateVoucherBatchResult = voucherService.deactivateVoucherbatch(USERNAME, randomVsvId, 134242);
		assertEquals(ResultStatusCode.FAIL, deactivateVoucherBatchResult.getResultStatusCode());
		assertEquals(GeneralVoucherServErrorCode.VSV_ID_INVALID, deactivateVoucherBatchResult.getVoucherServError());
		
		// 2. use valid vsvId
		RegisterResult registerResult2 = voucherService.register(USERNAME);
		assertEquals(ResultStatusCode.SUCCESS, registerResult2.getResultStatusCode());
		String vsvId = registerResult2.getVsvId();
		
		BatchGenRequest req2 = new BatchGenRequest.Builder(USERNAME, vsvId, 2, VoucherBatchType.REGULAR).build();
		BatchGenResult anotherBatchGenResult = voucherService.generateVoucherBatch(USERNAME, req2);
		assertEquals(ResultStatusCode.SUCCESS, anotherBatchGenResult.getBatchGenResultStatus());
		assertEquals(1, anotherBatchGenResult.getBatchNumber());
		assertEquals(null, anotherBatchGenResult.getBatchGenErrorCode());
		
		VoucherBatch batch = voucherService.getVoucherBatch(USERNAME, vsvId, anotherBatchGenResult.getBatchNumber());
		assertFalse(batch.isActive());
		deactivateVoucherBatchResult = voucherService.deactivateVoucherbatch(USERNAME, vsvId, anotherBatchGenResult.getBatchNumber());
		assertEquals(VoucherBatchActivationErrorCode.BATCH_ALREADY_INACTIVE, deactivateVoucherBatchResult.getVoucherServError());
		assertEquals(ResultStatusCode.FAIL, deactivateVoucherBatchResult.getResultStatusCode());
		assertEquals(1, deactivateVoucherBatchResult.getBatchNumber());
		batch = voucherService.getVoucherBatch(USERNAME, vsvId, anotherBatchGenResult.getBatchNumber());
		assertFalse(batch.isActive());
		
		// 3. use random batchNumber
		deactivateVoucherBatchResult = voucherService.deactivateVoucherbatch(USERNAME, vsvId, 100);
		assertEquals(ResultStatusCode.FAIL, deactivateVoucherBatchResult.getResultStatusCode());
		assertEquals(100, deactivateVoucherBatchResult.getBatchNumber());
		assertEquals(VoucherBatchActivationErrorCode.BATCH_DOES_NOT_EXIST, deactivateVoucherBatchResult.getVoucherServError());
	
		/***************
		 *  Generate JUST_IN_TIME VoucherBatch, which we expect to be active, we should get a success result
		 */
		
		// 1. use random vsvId, random voucherbatch
		
		BatchGenRequest req3 = new BatchGenRequest.Builder(USERNAME, randomVsvId, 1, VoucherBatchType.JUST_IN_TIME).build();
		batchGenResult = voucherService.generateVoucherBatch(USERNAME, req3);
		assertEquals(ResultStatusCode.FAIL, batchGenResult.getBatchGenResultStatus());
		assertEquals(GeneralVoucherServErrorCode.VSV_ID_INVALID, batchGenResult.getBatchGenErrorCode());
		deactivateVoucherBatchResult = voucherService.deactivateVoucherbatch(USERNAME, randomVsvId, 134242);
		assertEquals(ResultStatusCode.FAIL, deactivateVoucherBatchResult.getResultStatusCode());
		assertEquals(GeneralVoucherServErrorCode.VSV_ID_INVALID, deactivateVoucherBatchResult.getVoucherServError());
		assertEquals(134242, deactivateVoucherBatchResult.getBatchNumber());
		
		// 2. use valid vsvId
		BatchGenRequest req4 = new BatchGenRequest.Builder(USERNAME, vsvId, 2, VoucherBatchType.JUST_IN_TIME).build();
		anotherBatchGenResult = voucherService.generateVoucherBatch(USERNAME, req4);
		assertEquals(ResultStatusCode.SUCCESS, anotherBatchGenResult.getBatchGenResultStatus());
		assertEquals(2, anotherBatchGenResult.getBatchNumber());
		assertEquals(null, anotherBatchGenResult.getBatchGenErrorCode());
		
		batch = voucherService.getVoucherBatch(USERNAME, vsvId, anotherBatchGenResult.getBatchNumber());
		assertTrue(batch.isActive());
		deactivateVoucherBatchResult = voucherService.deactivateVoucherbatch(USERNAME, vsvId, anotherBatchGenResult.getBatchNumber());
		assertEquals(ResultStatusCode.SUCCESS, deactivateVoucherBatchResult.getResultStatusCode());
		assertEquals(anotherBatchGenResult.getBatchNumber(), deactivateVoucherBatchResult.getBatchNumber());
		batch = voucherService.getVoucherBatch(USERNAME, vsvId, anotherBatchGenResult.getBatchNumber());
		assertFalse(batch.isActive());
	}
	
	/**
	 * Test method for {@link com.mayloom.vouchserv.api.VoucherService#activeVoucherSerialRange(String vsvId, int startSerialNumber, int endSerialNumber)}.
	 */
	@Test
	public void testActivateVoucherSerialRange_InActiveVouchers() {
		
		// generate VoucherBatch with 10 vouchers, all vouchers in inactive state after generation
		
		RegisterResult registerResult = voucherService.register(USERNAME);
		assertEquals(ResultStatusCode.SUCCESS, registerResult.getResultStatusCode());
		String vsvId = registerResult.getVsvId();
		
		BatchGenRequest req = new BatchGenRequest.Builder(USERNAME, vsvId, 10, VoucherBatchType.JUST_IN_TIME).build();
		BatchGenResult batchGenResult = voucherService.generateVoucherBatch(USERNAME, req);
		assertEquals(ResultStatusCode.SUCCESS, batchGenResult.getBatchGenResultStatus());
		
		// try to activate with range that is negative (endSerial before startSerial)
		
		ActivateVoucherSerialRangeResult result = voucherService.activeVoucherSerialRange(USERNAME, vsvId, 10L, 1L);
		assertEquals(ResultStatusCode.FAIL, result.getResultStatusCode());
		assertEquals(-1L, result.getStartSerialNumber());
		assertEquals(-1L, result.getEndSerialNumber());
		assertEquals(ActivateVoucherSerialRangeResultErrorCode.START_END_SERIAL_RANGE_VALIDATION_ERROR, result.getVoucherServError());
		
		// try to activate with negative value(s)
		
		// try to activate in range below
		
		// try to activate in range above
		result = voucherService.activeVoucherSerialRange(USERNAME, vsvId, 1L, 11L);
		assertEquals(ResultStatusCode.FAIL, result.getResultStatusCode());
		assertEquals(-1L, result.getStartSerialNumber());
		assertEquals(-1L, result.getEndSerialNumber());
		assertEquals(ActivateVoucherSerialRangeResultErrorCode.SERIAL_RANGE_PORTION_DOES_NOT_EXIST, result.getVoucherServError());
		
		// try to activate in range totally outside
		
		// activate half the range
		result = voucherService.activeVoucherSerialRange(USERNAME, vsvId, 1L, 5L);
		assertEquals(ResultStatusCode.SUCCESS, result.getResultStatusCode());
		assertEquals(1L, result.getStartSerialNumber());
		assertEquals(5L, result.getEndSerialNumber());
		
		// activate the other half of the range
		result = voucherService.activeVoucherSerialRange(USERNAME, vsvId, 6L, 10L);
		assertEquals(ResultStatusCode.SUCCESS, result.getResultStatusCode());
		assertEquals(6L, result.getStartSerialNumber());
		assertEquals(10L, result.getEndSerialNumber());
	}
	
	/**
	 * Test method for {@link com.mayloom.vouchserv.api.VoucherService#activeVoucherSerialRange(String vsvId, int startSerialNumber, int endSerialNumber)}.
	 */
	@Test
	public void testActivateVoucherSerialRange_ActiveVouchers() {
		
		// generate VoucherBatch with 10 vouchers, all vouchers in active state after generation
		RegisterResult registerResult = voucherService.register(USERNAME);
		assertEquals(ResultStatusCode.SUCCESS, registerResult.getResultStatusCode());
		String vsvId = registerResult.getVsvId();
		
		BatchGenRequest req = new BatchGenRequest.Builder(USERNAME, vsvId, 10, VoucherBatchType.REGULAR).build();
		BatchGenResult batchGenResult = voucherService.generateVoucherBatch(USERNAME, req);
		assertEquals(ResultStatusCode.SUCCESS, batchGenResult.getBatchGenResultStatus());
		
		// try to activate with range that is negative (endSerial before startSerial)
		
		ActivateVoucherSerialRangeResult result = voucherService.activeVoucherSerialRange(USERNAME, vsvId, 10L, 1L);
		assertEquals(ResultStatusCode.FAIL, result.getResultStatusCode());
		assertEquals(-1L, result.getStartSerialNumber());
		assertEquals(-1L, result.getEndSerialNumber());
		assertEquals(ActivateVoucherSerialRangeResultErrorCode.START_END_SERIAL_RANGE_VALIDATION_ERROR, result.getVoucherServError());
		
		// try to activate with negative value(s)
		
		// try to activate in range below
		
		// try to activate in range above
		result = voucherService.activeVoucherSerialRange(USERNAME, vsvId, 1L, 11L);
		assertEquals(ResultStatusCode.FAIL, result.getResultStatusCode());
		assertEquals(-1L, result.getStartSerialNumber());
		assertEquals(-1L, result.getEndSerialNumber());
		assertEquals(ActivateVoucherSerialRangeResultErrorCode.SERIAL_RANGE_PORTION_DOES_NOT_EXIST, result.getVoucherServError());
		
		// try to active within range, but as we know vouchers are already active
		result = voucherService.activeVoucherSerialRange(USERNAME, vsvId, 1L, 10L);
		assertEquals(ResultStatusCode.FAIL, result.getResultStatusCode());
		assertEquals(-1L, result.getStartSerialNumber());
		assertEquals(-1L, result.getEndSerialNumber());
		assertEquals(ActivateVoucherSerialRangeResultErrorCode.SERIAL_RANGE_PORTION_ALREADY_ACTIVE, result.getVoucherServError());
		
		// try to activate in range totally outside
		
	}
	
	@Test
	public void testGetVoucher() {
		
		RegisterResult registerResult = voucherService.register(USERNAME);
		assertEquals(ResultStatusCode.SUCCESS, registerResult.getResultStatusCode());
		String apiKey = registerResult.getVsvId();
		BatchGenRequest req = new BatchGenRequest.Builder(USERNAME, apiKey, 10, VoucherBatchType.REGULAR).build();
		BatchGenResult batchGenResult = voucherService.generateVoucherBatch(USERNAME, req);
		assertEquals(ResultStatusCode.SUCCESS, batchGenResult.getBatchGenResultStatus());
		VoucherBatch batch = voucherService.getVoucherBatch(USERNAME, apiKey, batchGenResult.getBatchNumber());
		Voucher firstVoucher = batch.getVouchers().get(0);
		
		GetVoucherResult getVoucherResult = voucherService.getVoucher(USERNAME, apiKey, Optional.<String>absent(), Optional.of(firstVoucher.getPin()));
		assertEquals(null, getVoucherResult.getErrorCode());
		assertNotNull(getVoucherResult.getVoucher());
		assertEquals(firstVoucher.getSerialNumber(), getVoucherResult.getVoucher().getSerialNumber());
		assertEquals(firstVoucher.getPin(), getVoucherResult.getVoucher().getPin());
		assertEquals(batchGenResult.getBatchNumber(), getVoucherResult.getVoucher().getBatchNumber());
		
		getVoucherResult = voucherService.getVoucher(USERNAME, apiKey, Optional.of(Long.toString(firstVoucher.getSerialNumber())), Optional.<String>absent());
		assertEquals(null, getVoucherResult.getErrorCode());
		assertNotNull(getVoucherResult.getVoucher());
		assertEquals(firstVoucher.getSerialNumber(), getVoucherResult.getVoucher().getSerialNumber());
		assertEquals(firstVoucher.getPin(), getVoucherResult.getVoucher().getPin());
		assertEquals(batchGenResult.getBatchNumber(), getVoucherResult.getVoucher().getBatchNumber());
	}
	
	@Test
	public void testGetRegistrations() {
		
		Optional<List<VoucherBatchOwner>> optRegistrations = voucherService.getRegistrations(ANOTHER_USERNAME);
		assertFalse(optRegistrations.isPresent());
		
		RegisterResult registerResult = voucherService.register(ANOTHER_USERNAME);
		assertEquals(ResultStatusCode.SUCCESS, registerResult.getResultStatusCode());
		optRegistrations = voucherService.getRegistrations(ANOTHER_USERNAME);
		assertTrue(optRegistrations.isPresent());
		assertEquals(1, optRegistrations.get().size());
		assertNull(optRegistrations.get().get(0).getLastUpdate());
		assertNotNull(optRegistrations.get().get(0).getCode());
		assertNotNull(optRegistrations.get().get(0).getCreationDate());
		assertEquals(1l,optRegistrations.get().get(0).getNextBatchStartSerialNumber());
		
		BatchGenRequest req = new BatchGenRequest.Builder(USERNAME, registerResult.getVsvId(), 1, VoucherBatchType.REGULAR).build();
		BatchGenResult batchGenResult = voucherService.generateVoucherBatch(ANOTHER_USERNAME, req);
		assertEquals(ResultStatusCode.SUCCESS, batchGenResult.getBatchGenResultStatus());
		optRegistrations = voucherService.getRegistrations(ANOTHER_USERNAME);
		assertTrue(optRegistrations.isPresent());
		assertEquals(1, optRegistrations.get().size());
		assertNotNull(optRegistrations.get().get(0).getLastUpdate());
		logger.info(optRegistrations.get().get(0).getLastUpdate().toString());
		assertNotNull(optRegistrations.get().get(0).getCode());
		logger.info(optRegistrations.get().get(0).getCode());
		assertNotNull(optRegistrations.get().get(0).getCreationDate());
		logger.info(optRegistrations.get().get(0).getCreationDate().toString());
		assertEquals(2l,optRegistrations.get().get(0).getNextBatchStartSerialNumber());
	}
	
	/**
	 * NOTE: The intention behind the test case below is so that it will still work even when
	 * the VoucherServiceImp.MAX_VOUCHERS_PER_TASK changes, e.g. from 100 to 1000 or back
	 */
	@Test
	public void testTaskSplitter() {
		
		int BATCH_GEN_REQ_SIZE = 5 * VoucherServiceImp.MAX_VOUCHERS_PER_TASK + 1;
		
		BatchGenRequest req = new BatchGenRequest.Builder(USERNAME, "vsvid", BATCH_GEN_REQ_SIZE, VoucherBatchType.REGULAR).build();
		GenTaskInfo genTaskInfo = new GenTaskInfo(1,1L,1);
		
		List<VoucherGenSubtask> tasks = voucherService.splitIntoSubtasks(genTaskInfo, req);
		
		int expected_number_of_tasks = (BATCH_GEN_REQ_SIZE / VoucherServiceImp.MAX_VOUCHERS_PER_TASK) + 1; 
		
		assertEquals(expected_number_of_tasks, tasks.size());
		
		for (VoucherGenSubtask task : tasks) {
			assertEquals(genTaskInfo.getBatchId(), task.getBatchId());
			assertEquals(task.getBatchType(), req.getBatchType());
			assertEquals(task.getPinLength(), req.getPinLength());
			assertEquals(task.getPinType(), req.getPinType());
			logger.debug("{} {}", task.getStartSerialNumb(), task.getEndSerialNumb());
			// task.getStartSerialNumb()
			// task.getEndSerialNumb()
		}
		VoucherGenSubtask task = tasks.get(0);
		assertEquals(1, task.getStartSerialNumb());
		assertEquals(VoucherServiceImp.MAX_VOUCHERS_PER_TASK, task.getEndSerialNumb());
		
		task = tasks.get(1);
		assertEquals((VoucherServiceImp.MAX_VOUCHERS_PER_TASK + 1), task.getStartSerialNumb());
		assertEquals((VoucherServiceImp.MAX_VOUCHERS_PER_TASK * 2), task.getEndSerialNumb());
		
		task = tasks.get(2);
		assertEquals(((VoucherServiceImp.MAX_VOUCHERS_PER_TASK * 2) + 1), task.getStartSerialNumb());
		assertEquals((VoucherServiceImp.MAX_VOUCHERS_PER_TASK * 3), task.getEndSerialNumb());
		
		task = tasks.get(3);
		assertEquals(((VoucherServiceImp.MAX_VOUCHERS_PER_TASK * 3) + 1), task.getStartSerialNumb());
		assertEquals((VoucherServiceImp.MAX_VOUCHERS_PER_TASK * 4), task.getEndSerialNumb());
		
		task = tasks.get(4);
		assertEquals(((VoucherServiceImp.MAX_VOUCHERS_PER_TASK * 4) + 1), task.getStartSerialNumb());
		assertEquals((VoucherServiceImp.MAX_VOUCHERS_PER_TASK * 5), task.getEndSerialNumb());
		
		task = tasks.get(05);
		assertEquals(((VoucherServiceImp.MAX_VOUCHERS_PER_TASK * 5) + 1), task.getStartSerialNumb());
		assertEquals(((VoucherServiceImp.MAX_VOUCHERS_PER_TASK * 5) + 1), task.getEndSerialNumb());
		
		req = new BatchGenRequest.Builder(USERNAME, "vsvid", 50, VoucherBatchType.REGULAR).build();
		tasks = voucherService.splitIntoSubtasks(genTaskInfo, req);
		assertEquals(1, tasks.size());
		task = tasks.get(0);
		assertEquals(1, task.getStartSerialNumb());
		assertEquals(50, task.getEndSerialNumb());
		
		BATCH_GEN_REQ_SIZE = VoucherServiceImp.MAX_VOUCHERS_PER_TASK + 10;
		req = new BatchGenRequest.Builder(USERNAME, "vsvid", BATCH_GEN_REQ_SIZE, VoucherBatchType.REGULAR).build();
		tasks = voucherService.splitIntoSubtasks(genTaskInfo, req);
		assertEquals(2, tasks.size());
		task = tasks.get(0);
		assertEquals(1, task.getStartSerialNumb());
		assertEquals(VoucherServiceImp.MAX_VOUCHERS_PER_TASK, task.getEndSerialNumb());
		task = tasks.get(1);
		assertEquals((VoucherServiceImp.MAX_VOUCHERS_PER_TASK + 1), task.getStartSerialNumb());
		assertEquals((VoucherServiceImp.MAX_VOUCHERS_PER_TASK + 10), task.getEndSerialNumb());
		
		req = new BatchGenRequest.Builder(USERNAME, "vsvid", 1, VoucherBatchType.REGULAR).build();
		tasks = voucherService.splitIntoSubtasks(genTaskInfo, req);
		assertEquals(1, tasks.size());
		task = tasks.get(0);
		assertEquals(1, task.getStartSerialNumb());
		assertEquals(1, task.getEndSerialNumb());
	}
	

	@Test
	public void testAsyncBatchGen() throws InterruptedException { 
		RegisterResult registerResult = voucherService.register(USERNAME);
		assertEquals(ResultStatusCode.SUCCESS, registerResult.getResultStatusCode());
		String vsvId = registerResult.getVsvId();
		
		BatchGenRequest req = new BatchGenRequest.Builder(USERNAME, vsvId, 200, VoucherBatchType.REGULAR).build();
		BatchGenResult batchGenRes = voucherService.generateVoucherBatchAsync(USERNAME, req);
		Thread.sleep(5000);
		VoucherBatch batch = voucherService.getVoucherBatch(USERNAME, vsvId, batchGenRes.getBatchNumber());
		assertEquals(200, batch.getVouchers().size());
		Optional<VoucherBatchGenStatus> optStatus = voucherService.getVoucherBatchStatus(USERNAME, vsvId, batchGenRes.getBatchNumber());
		assertTrue(optStatus.isPresent());
		VoucherBatchGenStatus voucherBatchGenStatus = optStatus.get();
		
		assertEquals(batchGenRes.getBatchNumber(), voucherBatchGenStatus.getBatchNumber());
		assertEquals(200, voucherBatchGenStatus.getGeneratedSize());
		assertEquals(200, voucherBatchGenStatus.getRequestedSize());
		
	}
	
	@Test
	public void testAsyncBatchGenOnInit() throws InterruptedException {
		
		int REQ_SIZE = 200;
		String vsvId = "VSV-ID24052013291532769";
		String username = "24052013@vouchertool.com";
		databaseHelper.addNewOrdinaryUser(username, "password");
		databaseHelper.createAndPersistVoucherBatchOwner(username, vsvId);
		// TODO: no reason to keep on adding users and vsvids
		BatchGenRequest req = new BatchGenRequest.Builder(username, vsvId, REQ_SIZE, VoucherBatchType.REGULAR).build();
		GenTaskInfo genTaskInfo = databaseHelper.addVoucherBatchWithoutVouchers(req);
		
		voucherService.init();
		Thread.sleep(5000);
		Optional<VoucherBatchGenStatus> optStatus = voucherService.getVoucherBatchStatus(username, vsvId, 1);
		assertTrue(optStatus.isPresent());
		VoucherBatchGenStatus voucherBatchGenStatus = optStatus.get();
		
		assertEquals(1, voucherBatchGenStatus.getBatchNumber());
		assertEquals(200, voucherBatchGenStatus.getGeneratedSize());
		assertEquals(200, voucherBatchGenStatus.getRequestedSize());
		
		// TODO now fetch the actual batch and make sure the task oriented attributes are correct and
		// THEN, if time allows, check that the generated Vouchers have been correctly generated as 
		// specified
	}

}
