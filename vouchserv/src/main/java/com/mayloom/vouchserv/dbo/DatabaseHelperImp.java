package com.mayloom.vouchserv.dbo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Optional;
import com.mayloom.vouchserv.man.api.req.BatchGenRequest;
import com.mayloom.vouchserv.man.api.req.PinType;
import com.mayloom.vouchserv.man.api.req.VoucherBatchType;
import com.mayloom.vouchserv.api.res.ActivateVoucherBatchResult;
import com.mayloom.vouchserv.api.res.ActivateVoucherSerialRangeResult;
import com.mayloom.vouchserv.api.res.DeactivateVoucherBatchResult;
import com.mayloom.vouchserv.api.res.VoucherRedeemResult;
import com.mayloom.vouchserv.api.res.code.ActivateVoucherSerialRangeResultErrorCode;
import com.mayloom.vouchserv.api.res.code.VoucherBatchActivationErrorCode;
import com.mayloom.vouchserv.api.res.code.VoucherRedeemErrorCode;
import com.mayloom.vouchserv.dbo.enums.FetchRoles;
import com.mayloom.vouchserv.dbo.enums.FetchVBOs;
import com.mayloom.vouchserv.dbo.enums.FetchVoucherBatches;
import com.mayloom.vouchserv.dto.VoucherBatchGenStatus;
import com.mayloom.vouchserv.gen.RandomStringGenerator;
import com.mayloom.vouchserv.imp.GenTaskInfo;
import com.mayloom.vouchserv.imp.VoucherGenSubtask;
import com.mayloom.vouchserv.util.DateUtil;
/**
 * @author Nico
 *
 */
public class DatabaseHelperImp implements DatabaseHelper {

	private static Logger logger = LoggerFactory.getLogger(DatabaseHelperImp.class);
	
	/********************************
	 * Boiler plate persistence setup
	 */
	
	@PersistenceContext(unitName = "vouchServ-pu")
	private EntityManager entityManager;

	private RandomStringGenerator randomStringGenerator;
	
	private long uniquePinGenerationTimeout;
	
	/*
	* Boiler plate persistence setup
	********************************/
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public boolean doesUserExist(String username) {
		
		Query getUserCountQuery = getEntityManager().createNamedQuery(User.GET_USER_COUNT_QUERY);
		getUserCountQuery.setParameter("username", username);
		Number userCount = (Number)getUserCountQuery.getSingleResult();
		if (userCount.intValue() == 0) {
			return false;
		} else if (userCount.intValue() == 1) {
			return true;
		} else {
			throw new RuntimeException("logic error");
		}
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public boolean doesUserExist(String username, String password) {
		if (username == null) { throw new IllegalArgumentException(); }
		if (password == null) { throw new IllegalArgumentException(); }
		Query getUserPasswordCountQuery = getEntityManager().createNamedQuery(User.GET_USER_PASSWORD_COUNT_QUERY);
		getUserPasswordCountQuery.setParameter("username", username);
		getUserPasswordCountQuery.setParameter("password", password);
		Number userCount = (Number)getUserPasswordCountQuery.getSingleResult();
		if (userCount.intValue() == 0) {
			return false;
		} else if (userCount.intValue() == 1) {
			return true;
		} else {
			throw new RuntimeException("logic error");
		}
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRES_NEW)
	public void addNewOrdinaryUser(String username, String password) {
		addNewUser(username, password, RoleType.ROLE_USER);
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRES_NEW)
	public void addNewAdminUser(String username, String password) {
		addNewUser(username, password, RoleType.ROLE_ADMIN);
	}
	
	public void addNewUser(String username, String password, RoleType roleType) {
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		Role role = new Role();
		role.setAuthority(roleType.toString());
		user.addRole(role);
		getEntityManager().persist(user);
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public Optional<User> getUser(String username, FetchRoles fetchRoles, FetchVBOs fetchVoucherBatchOwners) {
		
		boolean bFetchRoles = (fetchRoles == FetchRoles.TRUE) ? true : false;
		boolean bFetchVoucherBatchOwners = (fetchVoucherBatchOwners == FetchVBOs.TRUE) ? true : false;
		
		Query getUserQuery = getEntityManager().createNamedQuery(User.GET_USER_QUERY);
		getUserQuery.setParameter("username", username);
		List<User> resultList = (List<User>)getUserQuery.getResultList();
		
		if (resultList.size() == 1) {
			User user = resultList.get(0);
			if (bFetchRoles && !Hibernate.isInitialized(user.getRoles())) {
				Hibernate.initialize(user.getRoles());
			}
			if (bFetchVoucherBatchOwners && !Hibernate.isInitialized(user.getVoucherBatchOwners())) {
				Hibernate.initialize(user.getVoucherBatchOwners());
			}
			return Optional.of(user);
		} else if (resultList.size() == 0) {
			return Optional.absent();
		} else {
			throw new RuntimeException("programming error");
		}
	}
	
	public Optional<List<String>> getUserRegistrations(String username) {
		
		Query getVsvIdsQuery = getEntityManager().createNamedQuery(VoucherBatchOwner.GET_VBO_CODES_QUERY);
		getVsvIdsQuery.setParameter("username", username);
		List<String> resultList = (List<String>)getVsvIdsQuery.getResultList();
		
		if (resultList.size() == 0) {
			return Optional.absent();
		} else {
			return Optional.of(resultList);
		}
		
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRES_NEW)
	public void createAndPersistVoucherBatchOwner(String username, String vsvId) {
		
		Optional<User> optionalUser = getUser(username, FetchRoles.FALSE, FetchVBOs.TRUE);
		
		if (!optionalUser.isPresent()) {
			throw new RuntimeException("No user with username " + username);
		} else {
		
			User user = optionalUser.get();
			
			VoucherBatchOwner voucherBatchOwner = new VoucherBatchOwner();
			voucherBatchOwner.setCode(vsvId);
			voucherBatchOwner.setNextBatchStartSerialNumber(1);
			
			voucherBatchOwner.setUser(user);
			
			// javax.persistence.PersistenceException: org.hibernate.exception.ConstraintViolationException
			getEntityManager().persist(voucherBatchOwner);
		}
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public VoucherBatchOwner getVoucherBatchOwner(String vsvId, FetchVoucherBatches fetchVoucherBatches) {
		
		List<VoucherBatchOwner> resultList = getEntityManager()
        	.createQuery("SELECT vbo FROM VoucherBatchOwner vbo WHERE vbo.code = :vsvId")
        	.setParameter("vsvId", vsvId).getResultList();
		
		logger.debug(" >>>>>>> Found {} VoucherBatchOwners", resultList.size());
		
		if (resultList.size() == 0) {
            return null;
        } else if (resultList.size() == 1){
        	VoucherBatchOwner voucherBatchOwner = resultList.get(0);
        	
        	logger.debug(" >>>>>>> {}", voucherBatchOwner.toString());
        	
        	if (fetchVoucherBatches == FetchVoucherBatches.TRUE) {
        		Hibernate.initialize(voucherBatchOwner.getVoucherBatches());
        	}
        	
            return voucherBatchOwner;
            
        } else {
        	
        	throw new RuntimeException("Implementation error, more than one VoucherBatchOwner found with vsvId: " + vsvId);
        	
        }		
	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRES_NEW)
	public int generateVoucherBatch(BatchGenRequest batchGenRequest, boolean skipPinVBOUniqueCheck) {
		
		assert(batchGenRequest.getVoucherNumber() > 0);
		
		// Step 1: Determine what the startSerialNumber for Voucher and batchNumber for batch 
		// must be - get the VoucherBatchOwner & voucherbatchNumber of last generatedBatch 
		// for the VoucherBatchOwner 
		
		Query getVoucherBatchOwner = getEntityManager().createNamedQuery(VoucherBatchOwner.GET_VBO_BY_CODE_QUERY); // TODO: should use username in this query !!!!
		getVoucherBatchOwner.setParameter("vsvId", batchGenRequest.getVsvId());
		VoucherBatchOwner voucherBatchOwner = (VoucherBatchOwner)getVoucherBatchOwner.getSingleResult();
			
		VoucherBatchOwner mergedBatchOwner = getEntityManager().merge(voucherBatchOwner);
						
		Query queryGetMostRecentVoucherBatch = getEntityManager().createNamedQuery(VoucherBatch.GET_MOST_RECENTLY_CREATED_BATCH_QUERY); // TODO: should use username in this query !!!!
		queryGetMostRecentVoucherBatch.setParameter("vsvId", batchGenRequest.getVsvId());
		List<VoucherBatch> voucherBatchList = (List<VoucherBatch>)queryGetMostRecentVoucherBatch.getResultList();
			
		int batchNumber = 1;
			
		if (voucherBatchList.size() == 0) {
				
			logger.debug("VoucherBatch #{} being generated for VoucherbatchOwner with vsvId {}", batchNumber, batchGenRequest.getVsvId());
				
		} else if (voucherBatchList.size() == 1) {
				
			VoucherBatch batchNumberSource = voucherBatchList.get(0);
			batchNumber = batchNumberSource.getBatchNumber() + 1;
			
		// possible, but unlikely, to get VoucherBatches with the same MAX(vb.creationDate) with the same vsvId	
		} else if (voucherBatchList.size() > 1) {
				
			long maxId = -1;
			for (VoucherBatch batchNumberSource: voucherBatchList) {
				if (batchNumberSource.getId() > maxId) {
					batchNumber =  batchNumberSource.getBatchNumber() + 1;
				}
				maxId = batchNumberSource.getId();
			}
				
		} else {
				
			throw new RuntimeException("Query result should not return more than one VoucherBatch by design.");
				
		}
			
		// Step 2: Generate the pins...
			
		HashMap<String, Voucher> vouchers = new HashMap<String, Voucher>();
			
		long serialNumber = mergedBatchOwner.getNextBatchStartSerialNumber();
		long startSerialNumber = serialNumber;
		
		boolean isFirstBatch = false;
		if (serialNumber == 1) {
			isFirstBatch = true;
		}
			
		logger.debug("Batch start serial number: {}", serialNumber);
			
		for (int i = 0; i < batchGenRequest.getVoucherNumber(); i++) {
				
			boolean generateAnotherPin = true;
				
			String strGeneratedVoucherPin = null;
				
			long uniquePinGenStartTime = System.currentTimeMillis();
				
			while (generateAnotherPin) {
					
				if (batchGenRequest.getPinType().equals(PinType.NUMERIC)) {
					strGeneratedVoucherPin = randomStringGenerator.generateRandomNumber(batchGenRequest.getPinLength());
				} else if (batchGenRequest.getPinType().equals(PinType.ALPHANUMERIC_UPPER_CASE)) {
					strGeneratedVoucherPin = randomStringGenerator.generateRandomAlphanumeric(batchGenRequest.getPinLength(), true);
				} else if (batchGenRequest.getPinType().equals(PinType.ALPHANUMERIC_MIXED_CASE)) {
					strGeneratedVoucherPin = randomStringGenerator.generateRandomAlphanumeric(batchGenRequest.getPinLength(), false);
				}
				
				if (strGeneratedVoucherPin == null) {
					throw new RuntimeException("null pin genererated");
				}
				
				logger.debug("Generated VoucherPin {}, length {}", strGeneratedVoucherPin, strGeneratedVoucherPin.length());
				
				// Now determine if this strGeneratedVoucherPin is unique for this voucherBatchOwner 1. in terms of 
				// values already generated in this batch 2. VoucherPins associated with Vouchers and VoucherBatches already
				// persisted with the VoucherBatches owned by this (with supplied vsvId) ID
				
				// unique check #1 - batch uniqueness
				if (vouchers.get(strGeneratedVoucherPin) == null) {
						
					generateAnotherPin = false;
						
				} 
						
				// unique check #2 - vbo uniqueness
				// we only need this check if this is NOT the first batch we are generating and we'll know whether its the first batch based on the serialNumber
				
				if (!isFirstBatch & !skipPinVBOUniqueCheck) {
					// unique check #2 - vbo uniqueness
					Query q = getEntityManager().createNamedQuery(Voucher.GET_VOUCHER_COUNT_BY_VBO_AND_PIN_QUERY);
					q.setParameter("pin", strGeneratedVoucherPin);
					q.setParameter("voucherBatchOwnerId", mergedBatchOwner.getCode());
						
					Number countResult=(Number) q.getSingleResult();
					boolean exists = countResult.intValue() == 1;
					
					if(!exists) {
							
						generateAnotherPin = false;
						logger.debug(">>>>>>> VoucherPin {} entirely unique.", strGeneratedVoucherPin);
		
					} else {
							
						logger.debug(">>>>>>> VoucherBatchOwner {} is already associated with Voucher with Pin {}", mergedBatchOwner.getCode(), strGeneratedVoucherPin);
						      
						generateAnotherPin = true;
							
					} 
				}
				
				long timeSnapshot = System.currentTimeMillis();				
				long uniquePinGendurationThusFar = timeSnapshot - uniquePinGenStartTime;
					
				if (uniquePinGendurationThusFar > this.getUniquePinGenerationTimeout()) {
					
					String debugMessage = "Unique Pin Generation Timeout Exceeded. Took " + uniquePinGendurationThusFar + 
											" ms thus far to generate unique pin, max time is " + getUniquePinGenerationTimeout() + " ms.";	
					logger.warn(debugMessage);
						
				}
			} // while (generateAnotherPin)
				
			if (strGeneratedVoucherPin == null) {
				throw new RuntimeException("strGeneratedVoucherPin cannot be null");
			}
						
			Voucher voucher = new Voucher();
			voucher.setVoucherPK(new VoucherPK(mergedBatchOwner.getCode(), strGeneratedVoucherPin));
			voucher.setPin(strGeneratedVoucherPin);
			voucher.setVoucherBatchOwner(mergedBatchOwner);
			voucher.setSerialNumber(serialNumber);
			setActiveBasedOnVoucherBatchType(batchGenRequest.getBatchType(), voucher);
				
			logger.debug("Voucher serial number: {}", serialNumber);
				
			serialNumber = serialNumber + 1;
				
			vouchers.put(strGeneratedVoucherPin, voucher);
				
		}
			
		assert(vouchers.size() == batchGenRequest.getVoucherNumber());
			
		VoucherBatch batch = new VoucherBatch();
		batch.setRequestedSize(batchGenRequest.getVoucherNumber());
			
		for (Voucher voucher: vouchers.values()) {
			batch.addVoucher(voucher);
			// Persist redundant VoucherBatchOwner to VoucherPin links for performance reasons
			// We are NOT expecting any conflicts below, as conflicts should have been resolved
			// in the "generateAnotherPin" pin generation loop, here we persist to protect against 
			// conflicts when generating the NEXT batch
			mergedBatchOwner.addVoucher(voucher);
		}
		
		batch.setRequestedBatchStartSerialNo(startSerialNumber);
		batch.setGeneratedSize(vouchers.size());
		batch.setRequestedBatchType(batchGenRequest.getBatchType().toString());
		batch.setRequestedPinType(batchGenRequest.getPinType().toString());
		batch.setRequestedPinLength(batchGenRequest.getPinLength());
		batch.setBatchNumber(batchNumber); 
		batch.setExpiryDate(batchGenRequest.getExpiryDate());
			
		setActiveBasedOnVoucherBatchType(batchGenRequest.getBatchType(), batch);
			
		logger.debug("Next batch start serial number: {}", serialNumber);
			
		mergedBatchOwner.addVoucherBatch(batch);
		mergedBatchOwner.setNextBatchStartSerialNumber(serialNumber);
			
		getEntityManager().persist(batch); 
			 
		assert(batch.getId() > 0);
			
		return batch.getBatchNumber(); 
			
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRES_NEW)
	@Override
	public GenTaskInfo addVoucherBatchWithoutVouchers(BatchGenRequest batchGenRequest) {
		
		logger.info(batchGenRequest.toString());
		
		// Step 1: Determine what the startSerialNumber for Voucher and batchNumber for batch 
		// must be - get the VoucherBatchOwner & voucherbatchNumber of last generatedBatch 
		// for the VoucherBatchOwner 
		
		Query getVoucherBatchOwner = getEntityManager().createNamedQuery(VoucherBatchOwner.GET_VBO_BY_CODE_QUERY);
		getVoucherBatchOwner.setParameter("vsvId", batchGenRequest.getVsvId());
		VoucherBatchOwner voucherBatchOwner = (VoucherBatchOwner)getVoucherBatchOwner.getSingleResult();
		
		long generationStartSerialNumber = voucherBatchOwner.getNextBatchStartSerialNumber(); 			
		VoucherBatchOwner mergedBatchOwner = getEntityManager().merge(voucherBatchOwner);
						
		Query queryGetMostRecentVoucherBatch = getEntityManager().createNamedQuery(VoucherBatch.GET_MOST_RECENTLY_CREATED_BATCH_QUERY);
		queryGetMostRecentVoucherBatch.setParameter("vsvId", batchGenRequest.getVsvId());
		List<VoucherBatch> voucherBatchList = (List<VoucherBatch>)queryGetMostRecentVoucherBatch.getResultList();
			
		int batchNumber = 1;
			
		if (voucherBatchList.size() == 0) {
				
			logger.debug("VoucherBatch #{} being generated for VoucherbatchOwner with vsvId {}", batchNumber, batchGenRequest.getVsvId());
				
		} else if (voucherBatchList.size() == 1) {
				
			VoucherBatch batchNumberSource = voucherBatchList.get(0);
			batchNumber = batchNumberSource.getBatchNumber() + 1;
			
		// possible, but unlikely, to get VoucherBatches with the same MAX(vb.creationDate) with the same vsvId	
		} else if (voucherBatchList.size() > 1) {
				
			long maxId = -1;
			for (VoucherBatch batchNumberSource: voucherBatchList) {
				if (batchNumberSource.getId() > maxId) {
					batchNumber =  batchNumberSource.getBatchNumber() + 1;
				}
				maxId = batchNumberSource.getId();
			}
				
		} else {
				
			throw new RuntimeException("Query result should not return more than one VoucherBatch by design.");
				
		}

		VoucherBatch batch = new VoucherBatch();
		batch.setRequestedBatchStartSerialNo(generationStartSerialNumber); 
		batch.setRequestedSize(batchGenRequest.getVoucherNumber());
		batch.setGeneratedSize(0);
		batch.setBatchNumber(batchNumber); 
		batch.setExpiryDate(batchGenRequest.getExpiryDate());
		batch.setRequestedBatchType(batchGenRequest.getBatchType().toString());
		batch.setRequestedPinType(batchGenRequest.getPinType().toString());
		batch.setRequestedPinLength(batchGenRequest.getPinLength());
			
		setActiveBasedOnVoucherBatchType(batchGenRequest.getBatchType(), batch);
		
		long serialNumber = mergedBatchOwner.getNextBatchStartSerialNumber() + batchGenRequest.getVoucherNumber();
		
		logger.debug("Next batch start serial number: {}", serialNumber);
			
		mergedBatchOwner.addVoucherBatch(batch);
		mergedBatchOwner.setNextBatchStartSerialNumber(serialNumber); // the range reserve
			
		getEntityManager().persist(batch); 
			 
		assert(batch.getId() > 0);
		
		return new GenTaskInfo(generationStartSerialNumber, batch.getId(), batch.getBatchNumber()); 
	}
	
	/**
	 * TODO fix: some of the logic in this method also exists in VoucherServiceImp.generateIncompleteBatches 
	 */
	@Transactional(readOnly=false)
	@Override
	public int generateAdditionalVoucherBatchVouchers(VoucherGenSubtask task) {
		
		if (task.getEndSerialNumb() <= task.getStartSerialNumb()) {
			String errorMessage = "task cannot have endSerialNumber [" + task.getEndSerialNumb() + "] <= startSerialNumber [" + task.getStartSerialNumb() + "]";
			throw new IllegalArgumentException(errorMessage);
		}
		
		// Step 1: get the voucherbatch based on the specified id and validate it
		VoucherBatch batch = getEntityManager().find(VoucherBatch.class, Long.valueOf(task.getBatchId()));
		
		if (batch == null) {
			throw new IllegalArgumentException("batch does not exist");
		}
		
		if (batch.getRequestedSize() == batch.getGeneratedSize()) {
			throw new IllegalArgumentException("batch has already been completely generated"); 
		}
			
		// Step 2: generate the requested (unique) pins and vouchers 
		long numberOfVouchersToGenerate = task.getEndSerialNumb() - task.getStartSerialNumb() + 1;
		logger.trace("Generating {} vouchers ...", numberOfVouchersToGenerate);
		
		// Step 3: Generate the pins...
		int numbGeneratedVouchers = 0;
		
		List<Voucher> vouchers = new ArrayList<Voucher>((int)(task.getEndSerialNumb() - task.getStartSerialNumb()));
		
		for (long serialNumber = task.getStartSerialNumb(); serialNumber <= task.getEndSerialNumb(); serialNumber++) {
			
			boolean generateAnotherPin = true;
			
			String strGeneratedVoucherPin = null;
			
			long uniquePinGenStartTime = System.currentTimeMillis();
			int loopNo = 0;
			
			while (generateAnotherPin) {
				loopNo++;
				if (task.getPinType().equals(PinType.NUMERIC)) {
					strGeneratedVoucherPin = randomStringGenerator.generateRandomNumber(task.getPinLength());
				} else if (task.getPinType().equals(PinType.ALPHANUMERIC_UPPER_CASE)) {
					strGeneratedVoucherPin = randomStringGenerator.generateRandomAlphanumeric(task.getPinLength(), true);
				} else if (task.getPinType().equals(PinType.ALPHANUMERIC_MIXED_CASE)) {
					strGeneratedVoucherPin = randomStringGenerator.generateRandomAlphanumeric(task.getPinLength(), false);
				}
			
				if (strGeneratedVoucherPin == null) {
					throw new RuntimeException("null pin genererated");
				}
			
				logger.trace("Generated VoucherPin {}, length {}", strGeneratedVoucherPin, strGeneratedVoucherPin.length());
				
				// unique check #2 - vbo uniqueness
				Query q = getEntityManager().createNamedQuery(Voucher.GET_VOUCHER_COUNT_BY_VBO_AND_PIN_QUERY);
				q.setParameter("pin", strGeneratedVoucherPin);
				q.setParameter("voucherBatchOwnerId", batch.getVoucherBatchOwner().getCode());
					
				Number countResult=(Number) q.getSingleResult();
				boolean exists = countResult.intValue() == 1;
				
				if(!exists) {
						
					generateAnotherPin = false;
					logger.trace(">>>>>>> VoucherPin {} entirely unique.", strGeneratedVoucherPin);
	
				} else {
						
					logger.trace(">>>>>>> VoucherBatchOwner {} is already associated with Voucher with Pin {}", batch.getVoucherBatchOwner().getCode(), strGeneratedVoucherPin);
					      
					generateAnotherPin = true;
						
				} 
			
				long timeSnapshot = System.currentTimeMillis();				
				long uniquePinGendurationThusFar = timeSnapshot - uniquePinGenStartTime;
				
				if (uniquePinGendurationThusFar > this.getUniquePinGenerationTimeout()) {
					String debugMessage = "Unique Pin Generation Timeout Exceeded. Took " + uniquePinGendurationThusFar + 
							" ms thus far to generate unique pin, max time is " + getUniquePinGenerationTimeout() + " ms.";	
					if (generateAnotherPin == false) {
						debugMessage = "NO LOOP: " + debugMessage;	
					} else {
						debugMessage = loopNo + debugMessage;
					}
					
					logger.warn(debugMessage);
						
				}
			} // while (generateAnotherPin)
			
			if (strGeneratedVoucherPin == null) {
				throw new RuntimeException("strGeneratedVoucherPin cannot be null");
			}
			
			numbGeneratedVouchers++;
			
			Voucher voucher = new Voucher();
			voucher.setPin(strGeneratedVoucherPin);
			voucher.setVoucherBatchOwner(batch.getVoucherBatchOwner());
			voucher.setSerialNumber(serialNumber);
			voucher.setVoucherPK(new VoucherPK(batch.getVoucherBatchOwner().getCode(), strGeneratedVoucherPin));
			setActiveBasedOnVoucherBatchType(task.getBatchType(), voucher);
			
			logger.trace("Voucher serial number: {}", serialNumber);
			vouchers.add(voucher);
			
			if (numberOfVouchersToGenerate == numbGeneratedVouchers) {
				logger.trace("Goal was to gen {} vouchers and done, exiting gen loop...", numbGeneratedVouchers);
				break;
			}
		}
		
		for (Voucher voucher: vouchers) {
			batch.addVoucher(voucher);
		}
		batch.setGeneratedSize(batch.getGeneratedSize() + numbGeneratedVouchers);
		
		logger.trace("Added {} vouchers to batch with size {}", numbGeneratedVouchers, batch.getGeneratedSize());
		
		return numbGeneratedVouchers;
	}
	
	/**
	 * Sets the VoucherBatch as being active if the batchType indicates JUST_IN_TIME and false
	 * if the batchType indicates REGULAR
	 * 
	 * @param batchType
	 * @param batch
	 * @throws RuntimeException
	 */
	private void setActiveBasedOnVoucherBatchType(VoucherBatchType batchType, VoucherBatch batch) throws RuntimeException {
		
		if (batchType.equals(VoucherBatchType.JUST_IN_TIME)) {
			
			batch.setActive(true);
			
		} else if (batchType.equals(VoucherBatchType.REGULAR)) {
			
			batch.setActive(false);
			
		} else {
			
			throw new RuntimeException("unsupported option");
			
		}
	}

	/**
	 * Sets the Voucher as being active if the batchType indicates REGULAR and false
	 * if the batchType indicates JUST_IN_TIME
	 * 
	 * @param batchType
	 * @param voucher
	 * @throws RuntimeException
	 */
	private void setActiveBasedOnVoucherBatchType(VoucherBatchType batchType, Voucher voucher) throws RuntimeException {
		
		if (batchType.equals(VoucherBatchType.JUST_IN_TIME)) {
			
			voucher.setActive(false);
			
		} else if (batchType.equals(VoucherBatchType.REGULAR)) {
			
			voucher.setActive(true);
			
		} else {
			
			throw new RuntimeException("unsupported option");
			
		}
	}

	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public boolean doesVsvIdExist(String username, String vsvId) {
		
		Query query = getEntityManager().createNamedQuery(User.GET_USER_COUNT_WITH_ASSOCIATED_VBO_CODE_QUERY);
		query.setParameter("vsvId", vsvId);
		query.setParameter("username", username);
		Long userCount = (Long)query.getSingleResult();
		
		if (userCount.intValue() == 1) {
			
			return true;
			
		} else if (userCount.intValue() == 0) {
			
			return false;
			
		} else {
			
			throw new RuntimeException("Implementation error, more than one VoucherBatchOwner found with vsvId: " + vsvId);   
			
		}
		
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public Voucher getVoucher(String vsvId, String voucherPin) {
		
		List<Voucher> voucherList = getEntityManager().createQuery(
		"SELECT voucher " +
		"FROM " +
		    "Voucher voucher " +
		"WHERE " + 
		    "voucher.pin.pin = :pin " +
		    "AND voucher.voucherOwner.voucherBatchOwner.code = :vsvId")
		    .setParameter("pin", voucherPin)
		    .setParameter("vsvId", vsvId).getResultList();
		
		if (voucherList.size() > 0) {
			return voucherList.get(0);
		} else {
			return null;
		}
		
	}

	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public VoucherBatch getVoucherBatch(String vsvId, int batchNumber) {
		
		Query queryGetVoucherBatch = getEntityManager().createNamedQuery(VoucherBatch.GET_BATCH_WITHOUT_VOUCHERS_QUERY);
		queryGetVoucherBatch.setParameter("vsvId", vsvId);
		queryGetVoucherBatch.setParameter("batchNumber", batchNumber);
		
		VoucherBatch batch;
		
		List<VoucherBatch> voucherBatchList = (List<VoucherBatch>)queryGetVoucherBatch.getResultList(); 
		
		if (voucherBatchList != null && voucherBatchList.size() == 0) {
			
			return null;
			
		} else if (voucherBatchList.size() == 1) { 
			
			batch = voucherBatchList.get(0);
			// NOTE: retrieving items from persistent storage as a side-effect below, but this is all we 
			//       really want to do, the logging is not the important part
			logger.info("Fetched {} vouchers", batch.getVouchers().size()); 
			
		} else { 
			
			throw new IllegalArgumentException();
			
		}
		
		return batch;
	}
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public Optional<VoucherBatch> getVoucherBatch(long batchId) {
		
		VoucherBatch batch = getEntityManager().find(VoucherBatch.class, Long.valueOf(batchId));
		
		if (batch == null) { 
			
			return Optional.absent();
			
		} else { 
			
			batch.getVouchers().size();
			return Optional.of(batch);
			
		}
		
	}
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public Optional<VoucherBatchGenStatus> getVoucherBatchStatus(
			String username, String vsvId, int batchNumber) {
		
		Query query = getEntityManager().createNamedQuery(VoucherBatch.GET_BATCH_GEN_STATUS_QUERY);
		query.setParameter("username", username);
		query.setParameter("vsvId", vsvId);
		query.setParameter("batchNumber", batchNumber);
		
		List<Object> resultList = (List<Object>)query.getResultList();
		
		if (resultList != null && resultList.size() == 1) {
			
			Object[] res = (Object[]) resultList.get(0);
			int returnedBatchNumber = ((Integer)res[0]).intValue();
			int requestedSize = ((Integer)res[1]).intValue();
			int generatedSize = ((Integer)res[2]).intValue();
			VoucherBatchGenStatus voucherBatchGenStatus = new VoucherBatchGenStatus(returnedBatchNumber, requestedSize, generatedSize);
			return Optional.of(voucherBatchGenStatus);
			
		} else {
			
			return Optional.absent();
			
		}
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRES_NEW)
	public ActivateVoucherBatchResult activateVoucherBatch(String vsvId, int batchNumber) {
		
		Query queryGetVoucherBatch = getEntityManager().createNamedQuery(VoucherBatch.GET_BATCH_WITHOUT_VOUCHERS_QUERY);
		queryGetVoucherBatch.setParameter("vsvId", vsvId);
		queryGetVoucherBatch.setParameter("batchNumber", batchNumber);
		
		try {
			
			VoucherBatch batch;
			
			List<VoucherBatch> voucherBatchList = (List<VoucherBatch>)queryGetVoucherBatch.getResultList(); 
			
			if (voucherBatchList != null && voucherBatchList.size() == 0) {
				
				return new ActivateVoucherBatchResult(VoucherBatchActivationErrorCode.BATCH_DOES_NOT_EXIST, batchNumber);
				
			} else if (voucherBatchList.size() == 1) { 
				
				batch = voucherBatchList.get(0); 
				
			} else { 
				
				throw new IllegalArgumentException();
				
			}
			
			if (batch.isActive()) {
				
				return new ActivateVoucherBatchResult(VoucherBatchActivationErrorCode.BATCH_ALREADY_ACTIVE, batchNumber);
				
			} else {
				
				batch.setActive(true);
				
				getEntityManager().merge(batch);
				
				return new ActivateVoucherBatchResult(batch.getBatchNumber());
			}
			
		} catch (EntityNotFoundException e) {
			
			return new ActivateVoucherBatchResult(VoucherBatchActivationErrorCode.BATCH_DOES_NOT_EXIST, batchNumber);
			
		}
		
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRES_NEW)
	public ActivateVoucherSerialRangeResult activeVoucherSerialRange(String vsvId, long startSerialNumb, long endSerialNumb) {
		
		long expectedNumberOfInactiveVouchers = endSerialNumb - (startSerialNumb - 1);
		
		/**
		 * Validation prep: get a count of the number of Vouchers, owned by VoucherBatchOwner with vsvId, between and 
		 * including startSerialNumb and endSerialNumb, regardless of whether they are INACTIVE or ACTIVE.
		 */
		
		Query queryGetVoucherCount = getEntityManager().createNamedQuery(Voucher.GET_VOUCHER_COUNT_WITHIN_SERIAL_RANGE);
		queryGetVoucherCount.setParameter("startSerialNumb", startSerialNumb);
		queryGetVoucherCount.setParameter("endSerialNumb", endSerialNumb);
		queryGetVoucherCount.setParameter("vsvId", vsvId);
		
		Long foundNumberOfVouchersObj = (Long)queryGetVoucherCount.getSingleResult();
		
		logger.debug("Found {} vouchers within specified range.", foundNumberOfVouchersObj.toString());
		
		long foundNumberOfVouchers = 0;
		
		if (foundNumberOfVouchersObj != null) {
			
			foundNumberOfVouchers = foundNumberOfVouchersObj.longValue();
			
		} 
		
		/**
		 * Validation prep: get a count of the number of Vouchers, owned by VoucherBatchOwner with vsvId, between and 
		 * including startSerialNumb and endSerialNumb, that are INACTIVE.
		 */
		Query queryGetInactiveVoucherCount = getEntityManager().createNamedQuery(Voucher.GET_ACTIVE_OR_INACTIVE_VOUCHER_COUNT_WITHIN_SERIAL_RANGE_QUERY);
		queryGetInactiveVoucherCount.setParameter("startSerialNumb", startSerialNumb);
		queryGetInactiveVoucherCount.setParameter("endSerialNumb", endSerialNumb);
		queryGetInactiveVoucherCount.setParameter("vsvId", vsvId);
		queryGetInactiveVoucherCount.setParameter("active", false);
		
		Long foundNumberOfInactiveVouchersObj = (Long)queryGetInactiveVoucherCount.getSingleResult();
		
		logger.debug("Found {} inactive vouchers within specified range.", foundNumberOfInactiveVouchersObj.toString());
		
		long foundNumberOfInactiveVouchers = 0;
		
		if (foundNumberOfInactiveVouchersObj != null) {
			
			foundNumberOfInactiveVouchers = foundNumberOfInactiveVouchersObj.longValue();
			
		} 
		
		/**
		 * Now do our validation...
		 */
		
		if (expectedNumberOfInactiveVouchers != foundNumberOfVouchers) { 
			
			return new ActivateVoucherSerialRangeResult(ActivateVoucherSerialRangeResultErrorCode.SERIAL_RANGE_PORTION_DOES_NOT_EXIST);
			
		} else if (expectedNumberOfInactiveVouchers != foundNumberOfInactiveVouchers) {
			
			return new ActivateVoucherSerialRangeResult(ActivateVoucherSerialRangeResultErrorCode.SERIAL_RANGE_PORTION_ALREADY_ACTIVE);
			
		} else if (expectedNumberOfInactiveVouchers == foundNumberOfInactiveVouchers) {
	
			// ok, so VoucherBatchOwner has an exact match of inactive Vouchers in the specified serial range, now try to activate them
			Query getInActiveVouchersQuery = getEntityManager().createNamedQuery(Voucher.GET_ACTIVE_OR_INACTIVE_VOUCHERS_WITHIN_SERIAL_RANGE_QUERY);
			getInActiveVouchersQuery.setParameter("startSerialNumb", startSerialNumb);
			getInActiveVouchersQuery.setParameter("endSerialNumb", endSerialNumb);
			getInActiveVouchersQuery.setParameter("vsvId", vsvId);
			getInActiveVouchersQuery.setParameter("active", false);
			
		    List<Voucher> inActiveVouchers = (List<Voucher>)getInActiveVouchersQuery.getResultList();
		    
		    for (Voucher voucher : inActiveVouchers) {
		    	
		    	assert(voucher.isActive() == false);
		    	
		    	voucher.setActive(true);
		    	
		    	getEntityManager().merge(voucher);
		    	
		    }
			
			return new ActivateVoucherSerialRangeResult(startSerialNumb, endSerialNumb);
			
		} else {
			
			throw new RuntimeException("Unexpected condition");
			
		}
	
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRES_NEW)
	public DeactivateVoucherBatchResult deactivateVoucherbatch(String vsvId, int batchNumber) {
		
		Query queryGetVoucherBatch = getEntityManager().createNamedQuery(VoucherBatch.GET_BATCH_WITHOUT_VOUCHERS_QUERY);
		queryGetVoucherBatch.setParameter("vsvId", vsvId);
		queryGetVoucherBatch.setParameter("batchNumber", batchNumber);
		
		try {
			
			//List<VoucherBatch> voucherBatchList = (List<VoucherBatch>)queryGetVoucherBatch.getSingleResult();
			VoucherBatch batch = (VoucherBatch)queryGetVoucherBatch.getSingleResult();
			
			if (batch.isActive()) {
				
				batch.setActive(false);
				
				getEntityManager().merge(batch);
				
				return new DeactivateVoucherBatchResult(batch.getBatchNumber());
				
			} else {
				
				return new DeactivateVoucherBatchResult(VoucherBatchActivationErrorCode.BATCH_ALREADY_INACTIVE, batchNumber);
				
			}
			
		} catch (EntityNotFoundException e) {
			
			return new DeactivateVoucherBatchResult(VoucherBatchActivationErrorCode.BATCH_DOES_NOT_EXIST, batchNumber);
			
		} catch (NoResultException e) {
			
			return new DeactivateVoucherBatchResult(VoucherBatchActivationErrorCode.BATCH_DOES_NOT_EXIST, batchNumber);
			
		}
		
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRES_NEW)
	public VoucherRedeemResult redeemVoucher(String vsvId, String voucherPin) {
		
		Query queryGetVoucher = getEntityManager().createNamedQuery(Voucher.FIND_VOUCHER);
		queryGetVoucher.setParameter("vsvId", vsvId);
		queryGetVoucher.setParameter("pin", voucherPin);
		
		try {
			
			List<Voucher> voucherList = (List<Voucher>)queryGetVoucher.getResultList(); 
			
			Voucher voucher;
			
			if (voucherList != null && voucherList.size() == 0) {
				
				return new VoucherRedeemResult(VoucherRedeemErrorCode.VOUCHER_DOES_NOT_EXIST);
				
			} else if (voucherList.size() == 1) {
				
				voucher = voucherList.get(0);
				
			} else {
				
				throw new IllegalStateException();
				
			}
			
			if (voucher.getVoucherOwner().isActive()) {
				
				if ( !DateUtil.isDateInPast(voucher.getVoucherOwner().getExpiryDate()) ) {
				
					if (voucher.isActive() && voucher.getRedemptionDate() == null) {
						
						voucher.setActive(false);
					
						voucher.setRedemptionDate(new Date());
			
						getEntityManager().merge(voucher);
			
						return new VoucherRedeemResult(voucher.getSerialNumber());
				
					} else if ((voucher.isActive() == false) && voucher.getRedemptionDate() == null) {
					
						return new VoucherRedeemResult(VoucherRedeemErrorCode.VOUCHER_NOT_ACTIVE_OVERRIDE_USED);
					
					} else if ((voucher.isActive() == false) && (voucher.getRedemptionDate() != null)) {
						
						return new VoucherRedeemResult(VoucherRedeemErrorCode.VOUCHER_REDEEMED);
						
					} else if (voucher.isActive() && voucher.getRedemptionDate() != null) {
						
						throw new IllegalStateException("Illegal State");
						
					} else {
						
						throw new RuntimeException("Should never get here");
						
					}
				
				} else {
					
					return new VoucherRedeemResult(VoucherRedeemErrorCode.VOUCHER_BATCH_EXPIRED);
					
				}
				
			} else {
				
				return new VoucherRedeemResult(VoucherRedeemErrorCode.VOUCHER_BATCH_NOT_ACTIVE);
				
			}
			
			
		} catch (EntityNotFoundException e) {
			
			return new VoucherRedeemResult(VoucherRedeemErrorCode.VOUCHER_DOES_NOT_EXIST);
			
		}
	}
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public Optional<Voucher> getVoucherByPin(String username, String vsvId, String pin) {
		
		logger.debug(username);
		logger.debug(vsvId);
		logger.debug(pin);
		
		Query queryGetVoucher = getEntityManager().createNamedQuery(Voucher.GET_VOUCHER_BY_USER_VBO_PIN_QUERY);
		queryGetVoucher.setParameter("username", username);
		queryGetVoucher.setParameter("vsvId", vsvId);
		queryGetVoucher.setParameter("pin", pin);
		
		List<Voucher> voucherList = (List<Voucher>)queryGetVoucher.getResultList();
		
		if (voucherList.size() == 1) {
			return Optional.of(voucherList.get(0));
		} else if (voucherList.size() == 0) { 
			return Optional.absent();
		} else {
			throw new IllegalStateException();
		}
		
	}
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public Optional<Voucher> getVoucherBySerialNumber(String username, String vsvId, String serialNumber) {
		
		Long qSerialNumber = Long.valueOf(serialNumber);
		if (qSerialNumber == null || qSerialNumber.longValue() <= 0L) { 
			throw new IllegalArgumentException("serialNumber cannot be null and must be greater than 0");
		}
		
		Query queryGetVoucher = getEntityManager().createNamedQuery(Voucher.GET_VOUCHER_BY_USER_VBO_SERIAL);
		queryGetVoucher.setParameter("username", username);
		queryGetVoucher.setParameter("vsvId", vsvId);
		queryGetVoucher.setParameter("serialNumber", qSerialNumber);
		
		List<Voucher> voucherList = (List<Voucher>)queryGetVoucher.getResultList();
		
		if (voucherList.size() == 1) {
			return Optional.of(voucherList.get(0));
		} else if (voucherList.size() == 0) { 
			return Optional.absent();
		} else {
			throw new IllegalStateException();
		}
		
	}

	@Override
	public Optional<Integer> getBatchNumberByPin(String username, String vsvId, String pin) {
		
		Query queryGetVoucher = getEntityManager().createNamedQuery(Voucher.GET_VOUCHER_BATCH_ID_BY_USERNAME_AND_VSV_ID_AND_PIN);
		queryGetVoucher.setParameter("username", username);
		queryGetVoucher.setParameter("vsvId", vsvId);
		queryGetVoucher.setParameter("pin", pin);
		
		List<Integer> batchNumberList = (List<Integer>)queryGetVoucher.getResultList();
		if (batchNumberList.size() == 1) {
			return Optional.of(batchNumberList.get(0));
		} else if (batchNumberList.size() == 0) {
			return Optional.absent();
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public Optional<List<VoucherBatchOwner>> getFullUserRegistrationDetails(String username) {
		
		Query getVbosQuery = getEntityManager().createNamedQuery(VoucherBatchOwner.GET_USER_VBO_CODES_QUERY);
		getVbosQuery.setParameter("username", username);
		List<VoucherBatchOwner> resultList = (List<VoucherBatchOwner>)getVbosQuery.getResultList();
		
		if (resultList.size() == 0) {
			return Optional.absent();
		} else {
			return Optional.of(resultList);
		}
		
	}
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public Optional<List<VoucherBatch>> getVoucherBatches(String vsvId) {
		Query getBatchesQuery = getEntityManager().createNamedQuery(VoucherBatchOwner.GET_VBO_BATCHES);
		getBatchesQuery.setParameter("vsvId", vsvId);
		List<VoucherBatch> resultList = (List<VoucherBatch>)getBatchesQuery.getResultList();
		
		if (resultList.size() == 0) {
			return Optional.absent();
		} else {
			return Optional.of(resultList);
		}
	}
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public Optional<List<VoucherBatch>> getIncompleteVoucherBatches() {
		Query getBatchesQuery = getEntityManager().createNamedQuery(VoucherBatch.GET_INCOMPLETE_BATCHES);
		List<VoucherBatch> resultList = (List<VoucherBatch>)getBatchesQuery.getResultList();
		
		if (resultList.size() == 0) {
			return Optional.absent();
		} else {
			return Optional.of(resultList);
		}
	}
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public Optional<Long> getMaxVoucherBatchVoucherSerialNo(long voucherBatchId) {
	
		/**
		 * Caused by: java.lang.IllegalStateException: No data type for node: org.hibernate.hql.ast.tree.AggregateNode
 			\-[AGGREGATE] AggregateNode: 'MAX'
    		\-[IDENT] IdentNode: 'serialNumber' {originalText=serialNumber}
		 */
		Query getBatchesQuery = getEntityManager().createNamedQuery(VoucherBatch.GET_MAX_BATCH_VOUCHER_SERIAL_NO);
		getBatchesQuery.setParameter("id", voucherBatchId);
		List<Long> resultList = (List<Long>)getBatchesQuery.getResultList();
		
		if (resultList.size() == 0) {
			return Optional.absent();
		} else if (resultList.size() == 1) {
			return Optional.of(resultList.get(0));
		} else {
			throw new IllegalStateException();
		}
	
	}
	
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	public RandomStringGenerator getRandomStringGenerator() {
		return randomStringGenerator;
	}

	public void setRandomStringGenerator(RandomStringGenerator randomStringGenerator) {
		this.randomStringGenerator = randomStringGenerator;
	}

	public long getUniquePinGenerationTimeout() {
		return uniquePinGenerationTimeout;
	}

	public void setUniquePinGenerationTimeout(long uniquePinGenerationTimeout) {
		this.uniquePinGenerationTimeout = uniquePinGenerationTimeout;
	}

	
}
