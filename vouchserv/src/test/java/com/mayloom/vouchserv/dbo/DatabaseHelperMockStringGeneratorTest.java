package com.mayloom.vouchserv.dbo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

import com.mayloom.vouchserv.api.VoucherPinAmount;
import com.mayloom.vouchserv.man.api.req.BatchGenRequest;
import com.mayloom.vouchserv.man.api.req.VoucherBatchType;
import com.mayloom.vouchserv.gen.RandomStringGeneratorImplMock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"../spring/main-test-spring-context.xml", "../spring/test-spring-context.xml"})
public class DatabaseHelperMockStringGeneratorTest {
	
	@Autowired
	@Qualifier("databaseHelper")
	private DatabaseHelper databaseHelper;
	
	private static String USERNAME = "foof@bar.com";
	
	private static boolean databaseSetup = false;
	
	@Before
	public void setUp() {
		if (!databaseSetup) {
			databaseHelper.addNewOrdinaryUser(USERNAME, "password");
			databaseSetup = true;
		}
	}
	
	@Test
	public void testGenerateVoucherBatch() {
		String vsvId = "VSV-ID20110523291332669";
		databaseHelper.createAndPersistVoucherBatchOwner(USERNAME, vsvId);
	
		BatchGenRequest req = new BatchGenRequest.Builder(null, vsvId, 1, VoucherBatchType.REGULAR).build();
		int batchNumber = databaseHelper.generateVoucherBatch(req, false);
		
		// since VoucherBatchOwner has generated first batch...
		assertEquals(1, batchNumber);
		
		VoucherBatch batch = databaseHelper.getVoucherBatch(vsvId, batchNumber);
		assertNotNull(batch);
		assertEquals(1, batch.getVouchers().size());
		assertEquals(vsvId, batch.getVoucherBatchOwner().getCode());
		for (Voucher voucher : batch.getVouchers()) {
			assertEquals(VoucherPinAmount.ONE_TRILLION.length(), voucher.getPin().length());
			assertEquals(null, voucher.getRedemptionDate());
		}
	}
	
	@Test
	public void testGenerateVoucherBatch2() {
		String vsvId = "VSV-ID20110530291332670";
		
		databaseHelper.createAndPersistVoucherBatchOwner(USERNAME, vsvId);
		String vsvId2 = "VSV-ID20110524291332679";
		databaseHelper.createAndPersistVoucherBatchOwner(USERNAME, vsvId2);
		/**
		 * RandomStringGeneratorImplMock will always return the same number, but below, we
		 * have a different vsvId in the second generate call, and so this must work...
		 */
		BatchGenRequest req = new BatchGenRequest.Builder(null, vsvId, 1, VoucherBatchType.REGULAR).build();
		int batchNumber =  databaseHelper.generateVoucherBatch(req, false);
		BatchGenRequest req2 = new BatchGenRequest.Builder(null, vsvId2, 1, VoucherBatchType.REGULAR).build();
		int batchNumber2 =  databaseHelper.generateVoucherBatch(req2, false);
		
		// since both VoucherBatchOwner's have generated their first batch...
		assertEquals(1, batchNumber);
		assertEquals(1, batchNumber2);
		
		VoucherBatch batch = databaseHelper.getVoucherBatch(vsvId, batchNumber);
		assertNotNull(batch);
		assertEquals(1, batch.getVouchers().size());
		assertEquals(vsvId, batch.getVoucherBatchOwner().getCode());
		for (Voucher voucher : batch.getVouchers()) {
			assertEquals(VoucherPinAmount.ONE_TRILLION.length(), voucher.getPin().length());
			assertEquals(null, voucher.getRedemptionDate());
		}
		
		VoucherBatch batch2 = databaseHelper.getVoucherBatch(vsvId2, batchNumber2);
		assertNotNull(batch2);
		assertEquals(1, batch2.getVouchers().size());
		assertEquals(vsvId2, batch2.getVoucherBatchOwner().getCode());
		for (Voucher voucher : batch2.getVouchers()) {
			assertEquals(VoucherPinAmount.ONE_TRILLION.length(), voucher.getPin().length());
			assertEquals(null, voucher.getRedemptionDate());
		}
	}
	
//	@Test(expected=java.lang.RuntimeException.class)
//	public void testGenerateVoucherBatch3() {
//		
//		String vsvId = "VSV-ID20110503291332781";
//		databaseHelper.createAndPersistVoucherBatchOwner(vsvId);
//		
//		BatchGenRequest req = new BatchGenRequest.Builder(vsvId, 1, VoucherBatchType.REGULAR).build();
//		int batchNumber =  databaseHelper.generateVoucherBatch(req);
//		assertEquals(1, batchNumber);
//		
//		VoucherBatch batch = databaseHelper.getVoucherBatch(vsvId, batchNumber);
//		assertNotNull(batch);
//		assertEquals(1, batch.getVouchers().size());
//		assertEquals(vsvId, batch.getVoucherBatchOwner().getCode());
//		for (Voucher voucher : batch.getVouchers()) {
//			assertEquals(VoucherPinAmount.ONE_TRILLION.length(), voucher.getPin().getPin().length());
//			assertEquals(null, voucher.getRedemptionDate());
//		}
//		
//		// should fail, since for one VoucherBatchOwner VoucherPins should be unique and 
//		// the generation method will timeout and throw RuntimeException as result
//		BatchGenRequest req2 = new BatchGenRequest.Builder(vsvId, 1, VoucherBatchType.REGULAR).build();
//		int batchNumber2 =  databaseHelper.generateVoucherBatch(req);
//		assertEquals(2, batchNumber2);
//		
//		VoucherBatch batch2 = databaseHelper.getVoucherBatch(vsvId, batchNumber2);
//		assertNotNull(batch2);
//		assertEquals(1, batch2.getVouchers().size());
//		assertEquals(vsvId, batch2.getVoucherBatchOwner().getCode());
//		for (Voucher voucher : batch2.getVouchers()) {
//			assertEquals(VoucherPinAmount.ONE_TRILLION.length(), voucher.getPin().getPin().length());
//			assertEquals(null, voucher.getRedemptionDate());
//		}
//	}
	
	@Test
	public void testGetVoucher() {
		String vsvId = "VSV-ID20110601291332782";
		databaseHelper.createAndPersistVoucherBatchOwner(USERNAME, vsvId);
		BatchGenRequest req = new BatchGenRequest.Builder(null, vsvId, 1, VoucherBatchType.REGULAR).build();
		int batchNumb = databaseHelper.generateVoucherBatch(req, false);
		assertEquals(1, batchNumb);
		Voucher voucher = databaseHelper.getVoucher(vsvId, RandomStringGeneratorImplMock.TWELVE);
		assertNotNull(voucher);
		assertEquals(RandomStringGeneratorImplMock.TWELVE, voucher.getPin());
		assertEquals(1, voucher.getSerialNumber());
	}
}
