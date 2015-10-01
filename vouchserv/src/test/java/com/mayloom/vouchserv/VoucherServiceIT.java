/**
 * 
 */
package com.mayloom.vouchserv;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mayloom.vouchserv.api.VoucherService;
import com.mayloom.vouchserv.man.api.req.BatchGenRequest;
import com.mayloom.vouchserv.man.api.req.VoucherBatchType;
import com.mayloom.vouchserv.man.api.res.BatchGenResult;
import com.mayloom.vouchserv.man.api.res.RegisterResult;
import com.mayloom.vouchserv.man.api.res.code.ResultStatusCode;
import com.mayloom.vouchserv.dbo.DatabaseHelper;
import com.mayloom.vouchserv.man.api.dto.VoucherBatch;

/**
 * @author Nico
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"spring/postgresql-integration-spring-context.xml"})
public class VoucherServiceIT {
	
	@Autowired
	@Qualifier("voucherService")
	private VoucherService voucherService;
	
	@Autowired
	@Qualifier("databaseHelper")
	private DatabaseHelper databaseHelper;
	
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
	
	/**
	 * 17:29:47.509 [pool-1-thread-1] INFO  c.m.v.imp.GenerationServerImp - Task execution being handled, end serial 100
	 * 17:34:49.138 [pool-1-thread-1] INFO  c.m.v.imp.GenerationServerImp - Task execution being handled, end serial 100000
	 * 22:19:19.624 [pool-1-thread-1] INFO  c.m.v.imp.GenerationServerImp - Task execution being handled, end serial 100
	 * 22:25:23.956 [pool-1-thread-1] INFO  c.m.v.imp.GenerationServerImp - Task execution being handled, end serial 100000
	 */
	@Test
	public void testAsyncBatchGen() throws InterruptedException { 
		RegisterResult registerResult = voucherService.register(USERNAME);
		assertEquals(ResultStatusCode.SUCCESS, registerResult.getResultStatusCode());
		String vsvId = registerResult.getVsvId();
		
		int VOUCHER_NUMB = 5000;
		BatchGenResult batchGenResult = null;
		for (int i=0; i < 6; i++) {
			BatchGenRequest req = new BatchGenRequest.Builder(USERNAME, vsvId, VOUCHER_NUMB, VoucherBatchType.REGULAR).build();
			batchGenResult = voucherService.generateVoucherBatchAsync(USERNAME, req);	
		}
		Thread.sleep(60000); // 30 s 
		VoucherBatch batch = voucherService.getVoucherBatch(USERNAME, vsvId, batchGenResult.getBatchNumber());
		assertEquals(VOUCHER_NUMB, batch.getVouchers().size());
	}

}
