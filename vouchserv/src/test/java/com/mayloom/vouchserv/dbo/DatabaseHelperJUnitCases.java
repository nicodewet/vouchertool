package com.mayloom.vouchserv.dbo;

public interface DatabaseHelperJUnitCases {

	public void testCreateAndPersistNewUser();
	public void testDoesUserExist();
	public void testCreateAndPersistVoucherBatchOwner();
	public void testCreateAndPersistVoucherBatchOwnerLastUpdate();
	public void testCreateAndPersistVoucherBatchOwnerUnique();
	public void testGetVoucherBatchOwner();
	public void testGetVoucherBatchOwnerIncorrectVSVID();
	public void testValidateVsvId_ValidVSVID();
	public void testValidateVsvId_InValidVSVID();
	public void testValidateVsvId_InValidVSVID2();
	public void testGenerateVoucherBatch_TinyBatch();
	public void testGenerateVoucherBatch_BigBatch();
	public void testGenerateVoucherBatch_OneVoucher();
}
