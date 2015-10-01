package com.mayloom.vouchserv;

import static org.junit.Assert.assertEquals;

import java.util.Date;

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
import com.mayloom.vouchserv.util.DateUtil;

/**
 * @author Nico
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"spring/main-test-spring-context.xml"})
public class VoucherServiceExpiryTest {

	private final Logger logger = LoggerFactory.getLogger(VoucherServiceTest.class);
	
	@Autowired
	@Qualifier("voucherService")
	private VoucherService voucherService;
	
	@Autowired
	@Qualifier("databaseHelper")
	private DatabaseHelper databaseHelper;
	
	/**
	 * VoucherBatch expiryDate specific test for {@link com.mayloom.vouchserv.api.VoucherService#generateVoucherBatch(java.lang.String, int)}
	 */
	@Test
	public void testGenerateExpiringVoucherBatch() {
		
		/**
		 * Get a Date a year from now.
		 */
		Date yearFromNow = DateUtil.getDateYearFromNow();
		
		logger.debug("Date {}", yearFromNow.toString());
		
		String username = "retief@vouchertool.com";
		databaseHelper.addNewOrdinaryUser(username, "password");
		RegisterResult registerResult = voucherService.register(username);
		assertEquals(ResultStatusCode.SUCCESS, registerResult.getResultStatusCode());
		String vsvId = registerResult.getVsvId();
		BatchGenRequest req = new BatchGenRequest.Builder(username, vsvId, 5, VoucherBatchType.JUST_IN_TIME).expiryDate(yearFromNow).build();
		BatchGenResult batchGenResult = voucherService.generateVoucherBatch(username, req);
		
		logger.debug("Result {}", batchGenResult.getBatchGenErrorCode());
		
		/**
		 * Check that the user did not enter a nonsense date (already expired)
		 */
		assertEquals(ResultStatusCode.SUCCESS, batchGenResult.getBatchGenResultStatus());
		
		
		
		// TODO now get the batch and check its expirydate is what we expected...
		
	}

}
