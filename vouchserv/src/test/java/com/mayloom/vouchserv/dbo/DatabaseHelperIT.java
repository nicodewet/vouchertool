package com.mayloom.vouchserv.dbo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"../spring/postgresql-integration-spring-context.xml"})
public class DatabaseHelperIT extends AbstractDatabaseHelper implements DatabaseHelperJUnitCases {

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
	//@Test(expected=org.springframework.dao.DataIntegrityViolationException.class)
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
		String vsvId = "VSV-ID20120721291332769";
		String username = "root@vouchertool.com";
		super.generateVoucherBatch_BigBatch(vsvId, username, false);
	}
	
	/**
	 * This test doesn't add much value, consider removing.
	 */
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
		
}
