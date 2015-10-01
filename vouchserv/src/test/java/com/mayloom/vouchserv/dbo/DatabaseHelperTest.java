/**
 * 
 */
package com.mayloom.vouchserv.dbo;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Nico
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"../spring/main-test-spring-context.xml"})
public class DatabaseHelperTest extends AbstractDatabaseHelper implements DatabaseHelperJUnitCases {

	@Test
	public void testCreateAndPersistNewUser() {
		super.createAndPersistNewUser();
	}
	
	@Test
	public void testDoesUserExist() {
		super.doesUserExist();
	}
	
	@Test
	public void testDoesUserPasswordExist() {
		super.doesUserPasswordExist();
	}

	/**
	 * Test method for {@link com.mayloom.vouchserv.dbo.DatabaseHelperImp#createAndPersistVoucherBatchOwner(java.lang.String)}.
	 */
	@Test
	public void testCreateAndPersistVoucherBatchOwner() {
		super.createAndPersistVoucherBatchOwner();
	}
	
	@Override
	public void testCreateAndPersistVoucherBatchOwnerLastUpdate() {
		super.createAndPersistVoucherBatchOwnerLastUpdate();	
	}
	
	/**
	 * Test method for {@link com.mayloom.vouchserv.dbo.DatabaseHelperImp#createAndPersistVoucherBatchOwner(java.lang.String)}.
	 */
	@Test(expected=javax.persistence.PersistenceException.class)
	public void testCreateAndPersistVoucherBatchOwnerUnique() {
		super.createAndPersistVoucherBatchOwnerUnique();
	}

	/**
	 * Test method for {@link com.mayloom.vouchserv.dbo.DatabaseHelperImp#getVoucherBatchOwner(java.lang.String)}.
	 */
	@Test
	public void testGetVoucherBatchOwner() {
		super.getVoucherBatchOwner();
	}
	
	/**
	 * Test method for {@link com.mayloom.vouchserv.dbo.DatabaseHelperImp#getVoucherBatchOwner(java.lang.String)}.
	 */
	@Test
	public void testGetVoucherBatchOwnerIncorrectVSVID() {
		super.getVoucherBatchOwnerIncorrectVSVID();
	}
	
	/**
	 * Test method for {@link com.mayloom.vouchserv.dbo.DatabaseHelperImp#doesVsvIdExist(String)}.
	 */
	@Test
	public void testValidateVsvId_ValidVSVID() {
		super.validateVsvId_ValidVSVID();
	}
	
	/**
	 * Test method for {@link com.mayloom.vouchserv.dbo.DatabaseHelperImp#doesVsvIdExist(String)}.
	 */
	@Test
	public void testValidateVsvId_InValidVSVID() {
		super.validateVsvId_InValidVSVID();
	}
	
	/**
	 * Test method for {@link com.mayloom.vouchserv.dbo.DatabaseHelperImp#doesVsvIdExist(String)}.
	 */
	@Test
	public void testValidateVsvId_InValidVSVID2() {
		super.validateVsvId_InValidVSVID2();
	}
	
	/**
	 * Test method for {@link com.mayloom.vouchserv.dbo.DatabaseHelperImp#generateVoucherBatch(String, int, com.mayloom.vouchserv.api.VoucherPinAmount)}
	 */
	@Test
	public void testGenerateVoucherBatch_TinyBatch() {
		super.generateVoucherBatch_TinyBatch();
	}
	
	@Test
	public void testGetVoucherByPinAndSerialNumber() {
		super.getVoucherByPinAndSerialNumber();
	}
	
	@Test
	public void testGenerateVoucherBatch_BigBatch() {
		String vsvId = "VSV-ID20120602291332769";
		String username = "daniel@vouchertool.com";
		logger.info("Generating {} vouchers...", AbstractDatabaseHelper.BIG_BATCH_SIZE);
		long startTime = System.currentTimeMillis();
		long endTime;
		super.generateVoucherBatch_BigBatch(vsvId, username, false);
		endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		long seconds = duration / 1000;
		long minutes = seconds / 60;
		logger.info("Duration: {}s = {}m", seconds, minutes);
		// 10 000 ... Duration: 647s = 10m
		// 1 000 ... Duration: 10s = 0m
		logger.info("Generating {} vouchers...", AbstractDatabaseHelper.BIG_BATCH_SIZE);
		startTime = System.currentTimeMillis();
		super.generateVoucherBatch_BigBatch(vsvId, username, true);
		endTime = System.currentTimeMillis();
		duration = endTime - startTime;
		seconds = duration / 1000;
		minutes = seconds / 60;
		logger.info("Duration: {}s = {}m", seconds, minutes);
		
		// first batch: 1 000 ... Duration: 8s, 8s
		// second batch: 1 000 ... Duration: 98s, 109s
		
		// skipping VBO-PIN unique check ... 
		// first batch: 1 000 ... Duration: 8s, 9s
		// second batch: 1 000 ... Duration: 52s, 51s ==> approx 100% improvement
	}
	
	@Test
	public void testGenerateVoucherBatch_OneVoucher() {
		super.generateVoucherBatch_OneVoucher();
	}
	
	@Test
	public void testGetVoucherBatches() {
		super.getVoucherBatches();
	}
	
	@Test
	public void testAddVoucherBatchWithoutVouchers() {
		super.addVoucherBatchWithoutVouchers();
	}
	
	@Test
	public void testGetIncompleteVoucherBatches() {
		super.getIncompleteVoucherBatches();
	}
	
	@Test
	public void testGenerateIncompleteBatches() throws InterruptedException {
		super.generateIncompleteBatches();
	}

}
