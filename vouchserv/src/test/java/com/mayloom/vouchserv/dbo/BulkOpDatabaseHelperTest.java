package com.mayloom.vouchserv.dbo;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mayloom.vouchserv.man.api.req.BatchGenRequest;
import com.mayloom.vouchserv.man.api.req.VoucherBatchType;
import com.mayloom.vouchserv.api.res.ActivateVoucherSerialRangeResult;
import com.mayloom.vouchserv.man.api.res.code.ResultStatusCode;

/**
 * @author Nico
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"../spring/main-test-spring-context.xml"})
public class BulkOpDatabaseHelperTest {
	
	@Autowired
	@Qualifier("bulkOpDatabaseHelper")
	private BulkOpDatabaseHelper bulkOpDatabaseHelper;
	
	@Autowired
	@Qualifier("databaseHelper")
	private DatabaseHelper databaseHelper;
	
	private String vsvId;
	
	@Before
	public void setUp() throws Exception {
		vsvId = "VSV-ID20110708291332769";
		String username = "root@vouchertool.com";
		databaseHelper.addNewOrdinaryUser(username, "password");
		databaseHelper.createAndPersistVoucherBatchOwner(username, vsvId);
		BatchGenRequest req = new BatchGenRequest.Builder(null, vsvId, 20, VoucherBatchType.JUST_IN_TIME).build();
		int batchNumber = databaseHelper.generateVoucherBatch(req, false);
		VoucherBatch batch = databaseHelper.getVoucherBatch(vsvId, batchNumber);
		assertNotNull(batch);
		assertEquals(20, batch.getVouchers().size());
		assertEquals(vsvId, batch.getVoucherBatchOwner().getCode());
	}
	
	@Test
	public void testBulkUpdateVoucherActiveStatus() {
		assertNotNull(vsvId);
		ActivateVoucherSerialRangeResult result = bulkOpDatabaseHelper.bulkUpdateVoucherActivate(vsvId, 1, 20);
		assertEquals(ResultStatusCode.SUCCESS, result.getResultStatusCode());
	}
}
