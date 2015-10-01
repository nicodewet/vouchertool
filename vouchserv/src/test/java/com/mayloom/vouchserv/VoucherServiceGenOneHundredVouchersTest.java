/**
 * 
 */
package com.mayloom.vouchserv;

import static org.junit.Assert.assertEquals;

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
import com.mayloom.vouchserv.man.api.req.VoucherBatchType;
import com.mayloom.vouchserv.man.api.res.BatchGenResult;
import com.mayloom.vouchserv.man.api.res.RegisterResult;
import com.mayloom.vouchserv.man.api.res.code.ResultStatusCode;
import com.mayloom.vouchserv.dbo.DatabaseHelper;

/**
 * @author Nico
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"spring/main-test-spring-context.xml"})
public class VoucherServiceGenOneHundredVouchersTest {
	
	private final Logger logger = LoggerFactory.getLogger(VoucherServiceGenOneHundredVouchersTest.class);
	
	private static final String USERNAME = "nicolo@vouchertool.com";
	
	@Autowired
	@Qualifier("voucherService")
	private VoucherService voucherService;
	
	@Autowired
	@Qualifier("databaseHelper")
	private DatabaseHelper databaseHelper;
	
	private static String vsvId;
	
	private static boolean databaseSetup = false;
	
	@Before
	public void setUp() {
		if (!databaseSetup) {
			logger.debug("=========== SETUP ===================");
			// We use database interface below to simulate a user registration which would
			// ordinarily occur using this interface and a separate web application
			databaseHelper.addNewOrdinaryUser(USERNAME, "password");
			RegisterResult registerResult = voucherService.register(USERNAME);
			assertEquals(ResultStatusCode.SUCCESS, registerResult.getResultStatusCode());
			vsvId = registerResult.getVsvId();
		}
		databaseSetup = true;
	}
	
	@Test
	public void testGenHundredVouchers() {
		logger.debug("=========== TEST CASE ===================");
		long startTime = System.currentTimeMillis();
		BatchGenRequest req = new BatchGenRequest.Builder(USERNAME, vsvId, 100, VoucherBatchType.JUST_IN_TIME).build();
		BatchGenResult anotherBatchGenResult = voucherService.generateVoucherBatch(USERNAME, req);
		assertEquals(ResultStatusCode.SUCCESS, anotherBatchGenResult.getBatchGenResultStatus());
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		logger.debug("Duration: 100 vouchers generated in {} ms", duration);
		boolean lessThanTwoSeconds = duration < 2000L;
		assertEquals(true, lessThanTwoSeconds);
		assertEquals(1, anotherBatchGenResult.getBatchNumber());
		assertEquals(null, anotherBatchGenResult.getBatchGenErrorCode());
	}

}
