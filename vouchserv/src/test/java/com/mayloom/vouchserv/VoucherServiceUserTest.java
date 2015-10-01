package com.mayloom.vouchserv;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mayloom.vouchserv.api.VoucherService;
import com.mayloom.vouchserv.man.api.req.BatchGenRequest;
import com.mayloom.vouchserv.man.api.req.PinType;
import com.mayloom.vouchserv.man.api.req.VoucherBatchType;
import com.mayloom.vouchserv.api.res.ActivateVoucherBatchResult;
import com.mayloom.vouchserv.api.res.ActivateVoucherSerialRangeResult;
import com.mayloom.vouchserv.man.api.res.BatchGenResult;
import com.mayloom.vouchserv.api.res.DeactivateVoucherBatchResult;
import com.mayloom.vouchserv.man.api.res.RegisterResult;
import com.mayloom.vouchserv.api.res.VoucherRedeemResult;
import com.mayloom.vouchserv.api.res.code.GeneralVoucherServErrorCode;
import com.mayloom.vouchserv.man.api.res.code.ResultStatusCode;
import com.mayloom.vouchserv.man.api.res.code.VoucherServError;
import com.mayloom.vouchserv.dbo.DatabaseHelper;
import com.mayloom.vouchserv.man.api.dto.Voucher;
import com.mayloom.vouchserv.man.api.dto.VoucherBatch;

/**
 * In this class we test that with all applicable VoucherService requests, a User can 
 * only perform operations using its own VoucherBatchOwners (via the VSV-ID) and not
 * that of another user, in which case we expect an Error with an appropriate code
 * returned informing the user that they have used an unknown VSV-ID.
 * 
 * It may seem odd that we do the above, but we do so since the User to VoucherBatchOwner
 * mapping was added long after the then root VoucherBatchOwner came into existence (i.e.
 * security aspects where added last).
 * 
 * @author Nico
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"spring/main-test-spring-context.xml"})
public class VoucherServiceUserTest {
	
	private final Logger logger = LoggerFactory.getLogger(VoucherServiceGenOneHundredVouchersTest.class);

	@Autowired
	@Qualifier("voucherService")
	private VoucherService voucherService;
	
	@Autowired
	@Qualifier("databaseHelper")
	private DatabaseHelper databaseHelper;
	
	private static boolean databaseSetup;
	
	private static final String FIRST_USERNAME = "tata@sasa.com";
	
	private static final String SECOND_USERNAME = "rara@qaqa.com";
	
	private static String firstVSVID;
	
	private static String secondVSVID;
	
	@Before
	public void setUp() throws Exception {
		/**
		 * Ok so here we need two Users, each with its own registration and thus VSV-ID,
		 * which we'll use to test that Users cannot transact with each others VSV-ID.
		 */
		if (!databaseSetup) {
			logger.debug("=========== SETUP ===================");
			databaseHelper.addNewOrdinaryUser(FIRST_USERNAME, "password");
			RegisterResult registerResult = voucherService.register(FIRST_USERNAME);
			assertEquals(ResultStatusCode.SUCCESS, registerResult.getResultStatusCode());
			firstVSVID = registerResult.getVsvId();
			
			databaseHelper.addNewOrdinaryUser(SECOND_USERNAME, "password");
			registerResult = voucherService.register(SECOND_USERNAME);
			assertEquals(ResultStatusCode.SUCCESS, registerResult.getResultStatusCode());
			secondVSVID = registerResult.getVsvId();
		}
		databaseSetup = true;
	}

	@Test
	public void testGenerateVoucherBatch() {
		/**
		 * Incorrect combination that should result in failure: SECOND_USERNAME, firstVSVID
		 */
		BatchGenRequest req = new BatchGenRequest.Builder(SECOND_USERNAME, firstVSVID, 30, VoucherBatchType.REGULAR).
				pinLength(30).pinType(PinType.ALPHANUMERIC_MIXED_CASE).build();
		BatchGenResult batchGenResult = voucherService.generateVoucherBatch(SECOND_USERNAME, req);
		
		ResultStatusCode resultStatusCode = batchGenResult.getBatchGenResultStatus();
		assertEquals(ResultStatusCode.FAIL, resultStatusCode);
		VoucherServError vouchServError = batchGenResult.getBatchGenErrorCode();
		assertEquals(GeneralVoucherServErrorCode.VSV_ID_INVALID.getCode(), vouchServError.getCode());
	}
	
	@Test
	public void testGetVoucherBatch() {
		
		/**
		 * Correct firstVSVID, FIRST_USERNAME combination 
		 */
		BatchGenRequest req = new BatchGenRequest.Builder(FIRST_USERNAME, firstVSVID, 30, VoucherBatchType.REGULAR).build();
		BatchGenResult batchGenResult = voucherService.generateVoucherBatch(FIRST_USERNAME, req);
		
		ResultStatusCode resultStatusCode = batchGenResult.getBatchGenResultStatus();
		assertEquals(ResultStatusCode.SUCCESS, resultStatusCode);
		
		/**
		 * Incorrect secondVSVID, FIRST_USERNAME combination in fetch ...
		 */
		VoucherBatch batch = voucherService.getVoucherBatch(FIRST_USERNAME, secondVSVID, batchGenResult.getBatchNumber());
		assertNull(batch);
		
		/**
		 * Incorrect firstVSVID, SECOND_USERNAME combination in fetch ...
		 */
		batch = voucherService.getVoucherBatch(SECOND_USERNAME, firstVSVID, batchGenResult.getBatchNumber());
		assertNull(batch);
		
		/**
		 * Correct firstVSVID, FIRST_USERNAME combination in fetch ...
		 */
		batch = voucherService.getVoucherBatch(FIRST_USERNAME, firstVSVID, batchGenResult.getBatchNumber());
		assertNotNull(batch); // no further testing done in this class, refer to VoucherServiceTest
	}

	@Test
	public void testActivateVoucherBatch() {
		
		BatchGenRequest req = new BatchGenRequest.Builder(FIRST_USERNAME, firstVSVID, 30, VoucherBatchType.REGULAR).build();
		BatchGenResult batchGenResult = voucherService.generateVoucherBatch(FIRST_USERNAME, req);
		ResultStatusCode resultStatusCode = batchGenResult.getBatchGenResultStatus();
		assertEquals(ResultStatusCode.SUCCESS, resultStatusCode);
		
		ActivateVoucherBatchResult res = voucherService.activateVoucherBatch(FIRST_USERNAME, secondVSVID, batchGenResult.getBatchNumber());
		ResultStatusCode resStatCode = res.getActivateVoucherBatchResultStatus();
		assertEquals(ResultStatusCode.FAIL, resStatCode);
		assertEquals(GeneralVoucherServErrorCode.VSV_ID_INVALID.getCode(), res.getActivateVoucherBatchErrorCode().getCode());
		
		res = voucherService.activateVoucherBatch(SECOND_USERNAME, firstVSVID, batchGenResult.getBatchNumber());
		resStatCode = res.getActivateVoucherBatchResultStatus();
		assertEquals(ResultStatusCode.FAIL, resStatCode);
		assertEquals(GeneralVoucherServErrorCode.VSV_ID_INVALID.getCode(), res.getActivateVoucherBatchErrorCode().getCode());
	}
	
	@Test
	public void testRedeemVoucherWithVoucher() {
		
		BatchGenRequest req = new BatchGenRequest.Builder(FIRST_USERNAME, firstVSVID, 30, VoucherBatchType.REGULAR).build();
		BatchGenResult batchGenResult = voucherService.generateVoucherBatch(FIRST_USERNAME, req);
		assertEquals(ResultStatusCode.SUCCESS, batchGenResult.getBatchGenResultStatus());
		ActivateVoucherBatchResult res = voucherService.activateVoucherBatch(FIRST_USERNAME, firstVSVID, batchGenResult.getBatchNumber());
		ResultStatusCode resStatCode = res.getActivateVoucherBatchResultStatus();
		resStatCode = res.getActivateVoucherBatchResultStatus();
		assertEquals(ResultStatusCode.SUCCESS, resStatCode);
		VoucherBatch batch = voucherService.getVoucherBatch(FIRST_USERNAME, firstVSVID, batchGenResult.getBatchNumber());
		assertNotNull(batch);
		Voucher voucher = batch.getVouchers().get(0);
		assertNotNull(voucher);
		
		VoucherRedeemResult vouchRedeemRes = voucherService.redeemVoucher(SECOND_USERNAME, firstVSVID, voucher);
		assertEquals(ResultStatusCode.FAIL, vouchRedeemRes.getRedeemVoucherResultStatus());
		assertEquals(GeneralVoucherServErrorCode.VSV_ID_INVALID.getCode(), vouchRedeemRes.getVoucherRedeemErrorCode().getCode());
		
		vouchRedeemRes = voucherService.redeemVoucher(FIRST_USERNAME, secondVSVID, voucher);
		assertEquals(ResultStatusCode.FAIL, vouchRedeemRes.getRedeemVoucherResultStatus());
		assertEquals(GeneralVoucherServErrorCode.VSV_ID_INVALID.getCode(), vouchRedeemRes.getVoucherRedeemErrorCode().getCode());
		
		vouchRedeemRes = voucherService.redeemVoucher(FIRST_USERNAME, firstVSVID, voucher);
		assertEquals(ResultStatusCode.SUCCESS, vouchRedeemRes.getRedeemVoucherResultStatus());
	}
	
	@Test
	public void testRedeemVoucherWithPin() {
		
		BatchGenRequest req = new BatchGenRequest.Builder(FIRST_USERNAME, firstVSVID, 30, VoucherBatchType.REGULAR).build();
		BatchGenResult batchGenResult = voucherService.generateVoucherBatch(FIRST_USERNAME, req);
		assertEquals(ResultStatusCode.SUCCESS, batchGenResult.getBatchGenResultStatus());
		ActivateVoucherBatchResult res = voucherService.activateVoucherBatch(FIRST_USERNAME, firstVSVID, batchGenResult.getBatchNumber());
		ResultStatusCode resStatCode = res.getActivateVoucherBatchResultStatus();
		resStatCode = res.getActivateVoucherBatchResultStatus();
		assertEquals(ResultStatusCode.SUCCESS, resStatCode);
		VoucherBatch batch = voucherService.getVoucherBatch(FIRST_USERNAME, firstVSVID, batchGenResult.getBatchNumber());
		assertNotNull(batch);
		Voucher voucher = batch.getVouchers().get(0);
		assertNotNull(voucher);
		String pin = voucher.getPin();
		
		VoucherRedeemResult vouchRedeemRes = voucherService.redeemVoucher(SECOND_USERNAME, firstVSVID, pin);
		assertEquals(ResultStatusCode.FAIL, vouchRedeemRes.getRedeemVoucherResultStatus());
		assertEquals(GeneralVoucherServErrorCode.VSV_ID_INVALID.getCode(), vouchRedeemRes.getVoucherRedeemErrorCode().getCode());
		
		vouchRedeemRes = voucherService.redeemVoucher(FIRST_USERNAME, secondVSVID, pin);
		assertEquals(ResultStatusCode.FAIL, vouchRedeemRes.getRedeemVoucherResultStatus());
		assertEquals(GeneralVoucherServErrorCode.VSV_ID_INVALID.getCode(), vouchRedeemRes.getVoucherRedeemErrorCode().getCode());
		
		vouchRedeemRes = voucherService.redeemVoucher(FIRST_USERNAME, firstVSVID, pin);
		assertEquals(ResultStatusCode.SUCCESS, vouchRedeemRes.getRedeemVoucherResultStatus());
	}

	@Test
	public void testDeactivateVoucherBatch() {
		
		DeactivateVoucherBatchResult res = voucherService.deactivateVoucherbatch(FIRST_USERNAME, secondVSVID, 7);
		assertEquals(ResultStatusCode.FAIL, res.getResultStatusCode());
		assertEquals(GeneralVoucherServErrorCode.VSV_ID_INVALID.getCode(), res.getVoucherServError().getCode());
		
		res = voucherService.deactivateVoucherbatch(SECOND_USERNAME, firstVSVID, 7);
		assertEquals(ResultStatusCode.FAIL, res.getResultStatusCode());
		assertEquals(GeneralVoucherServErrorCode.VSV_ID_INVALID.getCode(), res.getVoucherServError().getCode());
	}
	
	@Test
	public void testActiveVoucherSerialRange() {
		
		ActivateVoucherSerialRangeResult res = voucherService.activeVoucherSerialRange(FIRST_USERNAME, secondVSVID, 1, 10);
		assertEquals(ResultStatusCode.FAIL, res.getResultStatusCode());
		assertEquals(GeneralVoucherServErrorCode.VSV_ID_INVALID.getCode(), res.getVoucherServError().getCode());
		
		res = voucherService.activeVoucherSerialRange(SECOND_USERNAME, firstVSVID, 1, 10);
		assertEquals(ResultStatusCode.FAIL, res.getResultStatusCode());
		assertEquals(GeneralVoucherServErrorCode.VSV_ID_INVALID.getCode(), res.getVoucherServError().getCode());
	}
}
