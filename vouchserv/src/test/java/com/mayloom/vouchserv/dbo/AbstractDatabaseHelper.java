package com.mayloom.vouchserv.dbo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.hibernate.ejb.EntityManagerFactoryImpl;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.base.Optional;
import com.mayloom.vouchserv.api.VoucherPinAmount;
import com.mayloom.vouchserv.api.VoucherService;
import com.mayloom.vouchserv.man.api.req.BatchGenRequest;
import com.mayloom.vouchserv.man.api.req.VoucherBatchType;
import com.mayloom.vouchserv.dbo.enums.FetchRoles;
import com.mayloom.vouchserv.dbo.enums.FetchVBOs;
import com.mayloom.vouchserv.dbo.enums.FetchVoucherBatches;
import com.mayloom.vouchserv.imp.GenTaskInfo;
import com.mayloom.vouchserv.imp.VoucherGenSubtask;

abstract class AbstractDatabaseHelper {
	
	final Logger logger = LoggerFactory.getLogger(AbstractDatabaseHelper.class);
	
	static final int BIG_BATCH_SIZE = 200;
	
	@Autowired
	@Qualifier("databaseHelper")
	DatabaseHelper databaseHelper;
	
	@Autowired
	@Qualifier("voucherService")
	private VoucherService voucherService;
	
	@Autowired
	ApplicationContext applicationContext;
	
	static final String USERNAME = "root@vouchertool.com";
	
	public static void printStats(Statistics stats) {
		System.out.println(stats.toString());
		System.out.println("Second Level Cache Put Count ==> " + stats.getSecondLevelCachePutCount());
		System.out.println("Second Level Cache Hit Count ==> " + stats.getSecondLevelCacheHitCount());
		System.out.println("Second Level Cache Miss Count ==> " + stats.getSecondLevelCacheMissCount());
	}
	
	void createAndPersistNewUser() {
		String username = "foo";
		String password = "bar";
		databaseHelper.addNewOrdinaryUser(username, password);
		Optional<User> optUser = databaseHelper.getUser(username, FetchRoles.TRUE, FetchVBOs.TRUE);
		assertTrue(optUser.isPresent());
		User user = optUser.get();
		assertEquals(username, user.getUsername());
		assertEquals(password, user.getPassword());	
		assertTrue(user.isAccountNonExpired());
		assertTrue(user.isAccountNonLocked());
		assertTrue(user.isCredentialsNonExpired());
		assertTrue(user.isEnabled());
		assertNotNull(user.getAuthorities());
		assertEquals(1, user.getAuthorities().size());
		Iterator<GrantedAuthority> iter = user.getAuthorities().iterator();
		assertEquals(true, iter.hasNext());
		GrantedAuthority auth = iter.next();
		assertEquals(RoleType.ROLE_USER.toString(), auth.getAuthority());
		assertFalse(iter.hasNext());
		assertNotNull(user.getVoucherBatchOwners());
		assertEquals(0, user.getVoucherBatchOwners().size());
	}
	
	void doesUserExist() {
		String username = "auckland";
		databaseHelper.addNewOrdinaryUser(username, "password");
		boolean userExists = databaseHelper.doesUserExist(username);
		assertTrue(userExists);
	}
	
	void doesUserPasswordExist() {
		String username = "wellington@newzealand.com";
		databaseHelper.addNewOrdinaryUser(username, "password");
		boolean userExists = databaseHelper.doesUserExist(username, "password");
		assertTrue(userExists);
	}
	
	void createAndPersistVoucherBatchOwner() {
		String vsvId = "VSV-ID20130205291332665";
		String username = "nico@miljoona.com";
		databaseHelper.addNewOrdinaryUser(username, "password");
		databaseHelper.createAndPersistVoucherBatchOwner(username, vsvId);
		VoucherBatchOwner vbOwner = databaseHelper.getVoucherBatchOwner(vsvId, FetchVoucherBatches.FALSE);
		assertNotNull(vbOwner);
		assertNotNull(vbOwner.getCreationDate());
		assertNull(vbOwner.getLastUpdate());
		assertEquals(vsvId, vbOwner.getCode());
		assertEquals(username, vbOwner.getUser().getUsername());
		Optional<List<String>> optVsvIdList = databaseHelper.getUserRegistrations(username);
		assertTrue(optVsvIdList.isPresent());
		List<String> vsvIdList = optVsvIdList.get();
		assertEquals(1, vsvIdList.size());
		assertEquals(vsvId, vsvIdList.get(0));
	}
	
	void createAndPersistVoucherBatchOwnerLastUpdate() {
		String vsvId = "VSV-ID20110427291332665";
		String username = "never@giveup.com";
		databaseHelper.addNewOrdinaryUser(username, "password");
		databaseHelper.createAndPersistVoucherBatchOwner(username, vsvId);
		VoucherBatchOwner vbOwner = databaseHelper.getVoucherBatchOwner(vsvId, FetchVoucherBatches.FALSE);
		assertNotNull(vbOwner);
		assertNotNull(vbOwner.getCreationDate());
		assertNull(vbOwner.getLastUpdate());
		assertEquals(vsvId, vbOwner.getCode());
		assertEquals(username, vbOwner.getUser().getUsername());
		Optional<List<String>> optVsvIdList = databaseHelper.getUserRegistrations(username);
		assertTrue(optVsvIdList.isPresent());
		List<String> vsvIdList = optVsvIdList.get();
		assertEquals(1, vsvIdList.size());
		assertEquals(vsvId, vsvIdList.get(0));
		// now create a voucherbatch as this will entail an update to the voucherbatchowner so we can
		// test that the lastUpdate Date is populated
		BatchGenRequest req = new BatchGenRequest.Builder(null, vsvId, 200, VoucherBatchType.REGULAR).build();
		int batchNumber = databaseHelper.generateVoucherBatch(req, false);
		assertEquals(1, batchNumber);
		vbOwner = databaseHelper.getVoucherBatchOwner(vsvId, FetchVoucherBatches.FALSE);
		assertNotNull(vbOwner.getCreationDate());
		assertNotNull(vbOwner.getLastUpdate());
		// System.out.println(vbOwner.getCreationDate().getTime());
		// System.out.println(vbOwner.getLastUpdate().getTime());
		assertTrue(vbOwner.getLastUpdate().getTime() > vbOwner.getCreationDate().getTime());
	}
	
	void createAndPersistVoucherBatchOwnerUnique() {
		String vsvId = "VSV-ID20110502291332665";
		String username = "nico@vouchertool.com";
		databaseHelper.addNewOrdinaryUser(username, "password");
		databaseHelper.createAndPersistVoucherBatchOwner(username, vsvId);
		VoucherBatchOwner vbOwner = databaseHelper.getVoucherBatchOwner(vsvId, FetchVoucherBatches.FALSE);
		assertNotNull(vbOwner);
		// Now try to create another VoucherBatchOwner with the same VSV-ID
		databaseHelper.createAndPersistVoucherBatchOwner(username, vsvId);
	}
	void getVoucherBatchOwner() {
		String vsvId = "VSV-ID20110430291332666";
		String username = "john@vouchertool.com";
		databaseHelper.addNewOrdinaryUser(username, "password");
		databaseHelper.createAndPersistVoucherBatchOwner(username, vsvId);
		VoucherBatchOwner vbOwner = databaseHelper.getVoucherBatchOwner(vsvId, FetchVoucherBatches.TRUE);
		assertNotNull(vbOwner);
		assertEquals(vsvId,vbOwner.getCode());
		assertNotNull(vbOwner.getCreationDate()); // more comprehensive testing needed for creationDate
		assertTrue(vbOwner.getCode() != null); // should change to checking for 1 but can only do this once tests independent
		assertEquals(1, vbOwner.getNextBatchStartSerialNumber());
		assertEquals(0, vbOwner.getVoucherBatches().size());
	}
	void getVoucherBatchOwnerIncorrectVSVID() {
		VoucherBatchOwner vbOwner = databaseHelper.getVoucherBatchOwner("VSV-ID20110430012375678", FetchVoucherBatches.FALSE);
		assertNull(vbOwner);
	}
	void validateVsvId_ValidVSVID() {
		String vsvId = "VSV-ID20110502123456799";
		String username = "rooti@vouchertool.com";
		databaseHelper.addNewOrdinaryUser(username, "password");
		databaseHelper.createAndPersistVoucherBatchOwner(username, vsvId);
		VoucherBatchOwner vbOwner = databaseHelper.getVoucherBatchOwner(vsvId, FetchVoucherBatches.FALSE);
		assertNotNull(vbOwner);
		assertTrue(databaseHelper.doesVsvIdExist(username, vsvId));
	}
	void validateVsvId_InValidVSVID() {
		String vsvId = "VSV-ID20110502123456888";
		assertFalse(databaseHelper.doesVsvIdExist("foo", vsvId));
	}
	void validateVsvId_InValidVSVID2() {
		String vsvId = "VSV-ID20120625123456888";
		databaseHelper.addNewOrdinaryUser("userOne", "password");
		databaseHelper.addNewOrdinaryUser("userTwo", "password");
		databaseHelper.createAndPersistVoucherBatchOwner("userOne", vsvId);
		assertFalse(databaseHelper.doesVsvIdExist("userTwo", vsvId));
		Optional<User> optUserOne = databaseHelper.getUser("userOne", FetchRoles.FALSE, FetchVBOs.TRUE);
		User userOne = optUserOne.get();
		assertEquals(1, userOne.getVoucherBatchOwners().size());
		VoucherBatchOwner vbo = userOne.getVoucherBatchOwners().get(0);
		logger.info(vbo.getCode());
		Optional<User> optUserTwo = databaseHelper.getUser("userTwo", FetchRoles.FALSE, FetchVBOs.TRUE);
		User userTwo = optUserTwo.get();
		assertEquals(0, userTwo.getVoucherBatchOwners().size());
	}
	void generateVoucherBatch_TinyBatch() {
		String vsvId = "VSV-ID20110503291332769";
		String username = "gina@vouchertool.com";
		databaseHelper.addNewOrdinaryUser(username, "password");
		databaseHelper.createAndPersistVoucherBatchOwner(username, vsvId);
		BatchGenRequest req = new BatchGenRequest.Builder(null, vsvId, 20, VoucherBatchType.REGULAR).build();
		int batchNumber = databaseHelper.generateVoucherBatch(req, false);
		assertEquals(1, batchNumber);
		VoucherBatch batch = databaseHelper.getVoucherBatch(vsvId, batchNumber);
		assertNotNull(batch);
		assertEquals(20, batch.getVouchers().size());
		assertEquals(vsvId, batch.getVoucherBatchOwner().getCode());
		for (Voucher voucher : batch.getVouchers()) {
			assertEquals(VoucherPinAmount.ONE_TRILLION.length(), voucher.getPin().length());
			assertEquals(null, voucher.getRedemptionDate());
		}
		
	}
	
	void getVoucherByPinAndSerialNumber() {
		
		String vsvId = "VSV-ID20120909291332769";
		String username = "nico@kiwiinternational.co.nz";
		databaseHelper.addNewOrdinaryUser(username, "password");
		databaseHelper.createAndPersistVoucherBatchOwner(username, vsvId);
		BatchGenRequest req = new BatchGenRequest.Builder(null, vsvId, 10, VoucherBatchType.REGULAR).build();
		int batchNumber = databaseHelper.generateVoucherBatch(req, false);
		assertEquals(1, batchNumber);
		VoucherBatch batch = databaseHelper.getVoucherBatch(vsvId, batchNumber);
		assertNotNull(batch);
		assertEquals(10, batch.getVouchers().size());
		assertEquals(vsvId, batch.getVoucherBatchOwner().getCode());
		
		for (int i = 0; i < 10; i++) {
			
			int expectedVoucherSerialNo = i + 1;
			
			Voucher voucher = batch.getVouchers().get(i);
			assertTrue(batch == voucher.getVoucherOwner());
			assertEquals(12, voucher.getPin().length());
			assertEquals((long)expectedVoucherSerialNo, voucher.getSerialNumber());
		
			Optional<Voucher> optSameVoucherByPin = databaseHelper.getVoucherByPin(username, vsvId, voucher.getPin());
			assertEquals(voucher.getPin(), optSameVoucherByPin.get().getPin());
			assertEquals((long)expectedVoucherSerialNo, optSameVoucherByPin.get().getSerialNumber());
			assertEquals(1, optSameVoucherByPin.get().getVoucherOwner().getBatchNumber());
			
			Optional<Voucher> optSameVoucherBySerial = databaseHelper.getVoucherBySerialNumber(username, vsvId, Long.toString(voucher.getSerialNumber()));
			assertEquals(voucher.getPin(), optSameVoucherBySerial.get().getPin());
			assertEquals((long)expectedVoucherSerialNo, optSameVoucherBySerial.get().getSerialNumber());
			assertEquals(1, optSameVoucherBySerial.get().getVoucherOwner().getBatchNumber());
		}
		
	}
	
	void generateVoucherBatch_BigBatch(String vsvId, String username, boolean rerun) {
		if (!rerun) {
			databaseHelper.addNewOrdinaryUser(username, "password");
			databaseHelper.createAndPersistVoucherBatchOwner(username, vsvId);
		}
		BatchGenRequest req = new BatchGenRequest.Builder(null, vsvId, BIG_BATCH_SIZE, VoucherBatchType.REGULAR).build();
		int batchNumber = databaseHelper.generateVoucherBatch(req, false);
		assertTrue(batchNumber >= 1);
		VoucherBatch batch = databaseHelper.getVoucherBatch(vsvId, batchNumber);
		EntityManagerFactoryInfo entityManagerFactoryInfo = (EntityManagerFactoryInfo) applicationContext.getBean("entityManagerFactory");
		EntityManagerFactory emf = entityManagerFactoryInfo.getNativeEntityManagerFactory();
		EntityManagerFactoryImpl emfImp = (EntityManagerFactoryImpl)emf;
		Statistics stats = emfImp.getSessionFactory().getStatistics();
		printStats(stats);
		//assertTrue(stats.getSecondLevelCacheHitCount() > 0);
		assertEquals(BIG_BATCH_SIZE, batch.getVouchers().size());
		assertEquals(BIG_BATCH_SIZE, batch.getRequestedSize());
		assertEquals(BIG_BATCH_SIZE, batch.getGeneratedSize());
	}
	
	void generateVoucherBatch_OneVoucher() {
		String vsvId = "VSV-ID20110523291532769";
		String username = "christiaan@vouchertool.com";
		databaseHelper.addNewOrdinaryUser(username, "password");
		databaseHelper.createAndPersistVoucherBatchOwner(username, vsvId);
		BatchGenRequest req = new BatchGenRequest.Builder(null, vsvId, 1, VoucherBatchType.REGULAR).build();
		int batchNumber = databaseHelper.generateVoucherBatch(req, false);
		VoucherBatch batch = databaseHelper.getVoucherBatch(vsvId, batchNumber);
		assertNotNull(batch);
		assertEquals(1, batch.getVouchers().size());
		assertEquals(1, batch.getRequestedSize());
		assertEquals(1, batch.getGeneratedSize());
	}
	
	void getVoucherBatches() {
		
		String vsvId = "VSV-ID20130153291532769";
		String username = "may2103@vouchertool.com";
		databaseHelper.addNewOrdinaryUser(username, "password");
		databaseHelper.createAndPersistVoucherBatchOwner(username, vsvId);
		BatchGenRequest req = new BatchGenRequest.Builder(null, vsvId, 10, VoucherBatchType.REGULAR).build();
		databaseHelper.generateVoucherBatch(req, false);
		databaseHelper.generateVoucherBatch(req, false);
		databaseHelper.generateVoucherBatch(req, false);
		
		// testing databaseHelper.getVoucherBatches(vsvId)
		Optional<List<VoucherBatch>> vbList = databaseHelper.getVoucherBatches(vsvId);
		assertNotNull(vbList);
		assertTrue(vbList.isPresent());
		assertEquals(3, vbList.get().size());
		assertEquals(10, vbList.get().get(0).getRequestedSize());
		assertEquals(10, vbList.get().get(0).getGeneratedSize());
		assertEquals(vsvId, vbList.get().get(0).getVoucherBatchOwner().getCode());
		
		for (VoucherBatch batch : vbList.get()) {
			// NOTE: call batch.getVouchers().size() and you'll get a LazyInitializationException
			// this is why this method is best exposed via the VoucherService with DTOs
			assertNotNull(batch.getVouchers());
		}
	}
	
	void addVoucherBatchWithoutVouchers() {
		int REQ_SIZE = 20;
		String vsvId = "VSV-ID20130625291532769";
		String username = "doss@vouchertool.com";
		databaseHelper.addNewOrdinaryUser(username, "password");
		databaseHelper.createAndPersistVoucherBatchOwner(username, vsvId);
		// TODO: no reason to keep on adding users and vsvids
		BatchGenRequest req = new BatchGenRequest.Builder(username, vsvId, REQ_SIZE, VoucherBatchType.REGULAR).build();
		GenTaskInfo genTaskInfo = databaseHelper.addVoucherBatchWithoutVouchers(req);
		
		Optional<VoucherBatch> optBatch = databaseHelper.getVoucherBatch(genTaskInfo.getBatchId()); 
		assertTrue(optBatch.isPresent());
		VoucherBatch batch = optBatch.get();
		assertNotNull(batch);
		assertNotNull(batch.getVouchers());
		assertEquals(0,batch.getVouchers().size());
		assertEquals(REQ_SIZE, batch.getRequestedSize());
		assertEquals(0, batch.getGeneratedSize());
		
		// Ok, so now lets generate 10/20 vouchers...
		VoucherGenSubtask task = new VoucherGenSubtask(genTaskInfo.getBatchId(), req.getBatchType(), req.getPinLength(), req.getPinType(), 1L, 10L);
		int numberGenerated = databaseHelper.generateAdditionalVoucherBatchVouchers(task);
		assertEquals(10, numberGenerated);
		
		optBatch = databaseHelper.getVoucherBatch(genTaskInfo.getBatchId()); 
		assertTrue(optBatch.isPresent());
		batch = optBatch.get();
		assertNotNull(batch);
		assertNotNull(batch.getVouchers());
		assertEquals(10,batch.getVouchers().size());
		assertEquals(REQ_SIZE, batch.getRequestedSize());
		assertEquals(10, batch.getGeneratedSize());
		for (Voucher voucher: batch.getVouchers()) {
			assertEquals(req.getPinLength(), voucher.getPin().length());
		}
		
		// Ok, so now lets generate 20/20 vouchers...
		task = new VoucherGenSubtask(genTaskInfo.getBatchId(), req.getBatchType(), req.getPinLength(), req.getPinType(), 11L, 20L);
		numberGenerated = databaseHelper.generateAdditionalVoucherBatchVouchers(task);
		assertEquals(10, numberGenerated);
		optBatch = databaseHelper.getVoucherBatch(genTaskInfo.getBatchId()); 
		assertTrue(optBatch.isPresent());
		batch = optBatch.get();
		assertNotNull(batch);
		assertNotNull(batch.getVouchers());
		assertEquals(20,batch.getVouchers().size());
		assertEquals(REQ_SIZE, batch.getRequestedSize());
		assertEquals(20, batch.getGeneratedSize());
		for (Voucher voucher: batch.getVouchers()) {
			assertEquals(req.getPinLength(), voucher.getPin().length());
		}
	}
	
	void getIncompleteVoucherBatches() {
		
		int REQ_SIZE = 20;
		String vsvId = "VSV-ID20130624291532769";
		String username = "20130624@vouchertool.com";
		databaseHelper.addNewOrdinaryUser(username, "password");
		databaseHelper.createAndPersistVoucherBatchOwner(username, vsvId);
		BatchGenRequest req = new BatchGenRequest.Builder(username, vsvId, REQ_SIZE, VoucherBatchType.REGULAR).build();
		GenTaskInfo genTaskInfo = databaseHelper.addVoucherBatchWithoutVouchers(req);
		Optional<List<VoucherBatch>> optBatchList = databaseHelper.getIncompleteVoucherBatches();
		assertTrue(optBatchList.isPresent());
		assertEquals(1, optBatchList.get().size());
		assertEquals(req.getVoucherNumber(), optBatchList.get().get(0).getRequestedSize());
		assertEquals(0, optBatchList.get().get(0).getGeneratedSize());
		assertEquals(req.getBatchType().toString(), optBatchList.get().get(0).getRequestedBatchType());
		assertEquals(req.getPinType().toString(), optBatchList.get().get(0).getRequestedPinType());
		assertEquals(req.getPinLength(), optBatchList.get().get(0).getRequestedPinLength());
	}
	
	void generateIncompleteBatches() throws InterruptedException {
		// generate complete batch normally but async
		int REQ_SIZE = 50;
		String vsvId = "VSV-ID20130709291532769";
		String username = "09072013@vouchertool.com";
		databaseHelper.addNewOrdinaryUser(username, "password");
		databaseHelper.createAndPersistVoucherBatchOwner(username, vsvId);
		BatchGenRequest req = new BatchGenRequest.Builder(username, vsvId, REQ_SIZE, VoucherBatchType.REGULAR).build();
		GenTaskInfo genTaskInfo = databaseHelper.addVoucherBatchWithoutVouchers(req);
		
		VoucherGenSubtask task = new VoucherGenSubtask(genTaskInfo.getBatchId(), req.getBatchType(), req.getPinLength(), req.getPinType(), 1L, REQ_SIZE);
		int numberGenerated = databaseHelper.generateAdditionalVoucherBatchVouchers(task);
		assertEquals(REQ_SIZE, numberGenerated);
		Optional<VoucherBatch> optBatch = databaseHelper.getVoucherBatch(genTaskInfo.getBatchId()); 
		assertTrue(optBatch.isPresent());
		VoucherBatch batch = optBatch.get();
		assertNotNull(batch);
		assertNotNull(batch.getVouchers());
		assertEquals(REQ_SIZE,batch.getVouchers().size());
		assertEquals(REQ_SIZE, batch.getRequestedSize());
		assertEquals(REQ_SIZE, batch.getGeneratedSize());
		
		REQ_SIZE = 250;
		// add incomplete batch
		req = new BatchGenRequest.Builder(username, vsvId, REQ_SIZE, VoucherBatchType.REGULAR).build();
		genTaskInfo = databaseHelper.addVoucherBatchWithoutVouchers(req);
		task = new VoucherGenSubtask(genTaskInfo.getBatchId(), req.getBatchType(), req.getPinLength(), req.getPinType(), 1L, 25);
		numberGenerated = databaseHelper.generateAdditionalVoucherBatchVouchers(task);
		assertEquals(25, numberGenerated);
		
		// simulate a restart and finish incomplete batch async
		voucherService.init();
		Thread.sleep(1000);
		optBatch = databaseHelper.getVoucherBatch(genTaskInfo.getBatchId()); 
		assertTrue(optBatch.isPresent());
		batch = optBatch.get();
		assertNotNull(batch);
		assertNotNull(batch.getVouchers());
		assertEquals(REQ_SIZE,batch.getVouchers().size());
		assertEquals(REQ_SIZE, batch.getRequestedSize());
		assertEquals(REQ_SIZE, batch.getGeneratedSize());
		// generate another batch async
	}
}
