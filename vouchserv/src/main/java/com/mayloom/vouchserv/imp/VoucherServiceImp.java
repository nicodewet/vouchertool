package com.mayloom.vouchserv.imp;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.google.common.base.Optional;
import com.mayloom.vouchserv.dto.ContentEnricherVoucher;
import com.mayloom.vouchserv.man.api.dto.Voucher;
import com.mayloom.vouchserv.man.api.dto.VoucherBatch;
import com.mayloom.vouchserv.man.api.dto.VoucherBatchHeader;
import com.mayloom.vouchserv.dto.VoucherBatchGenStatus;
import com.mayloom.vouchserv.man.api.dto.VoucherBatchOwner;
import com.mayloom.vouchserv.api.GenerationServer;
import com.mayloom.vouchserv.api.VoucherPinAmount;
import com.mayloom.vouchserv.api.VoucherService;
import com.mayloom.vouchserv.man.api.req.BatchGenRequest;
import com.mayloom.vouchserv.man.api.req.PinType;
import com.mayloom.vouchserv.man.api.req.VoucherBatchType;
import com.mayloom.vouchserv.api.res.ActivateVoucherBatchResult;
import com.mayloom.vouchserv.api.res.ActivateVoucherSerialRangeResult;
import com.mayloom.vouchserv.man.api.res.BatchGenResult;
import com.mayloom.vouchserv.api.res.DeactivateVoucherBatchResult;
import com.mayloom.vouchserv.api.res.GetVoucherResult;
import com.mayloom.vouchserv.man.api.res.RegisterResult;
import com.mayloom.vouchserv.api.res.VoucherRedeemResult;
import com.mayloom.vouchserv.api.res.code.ActivateVoucherSerialRangeResultErrorCode;
import com.mayloom.vouchserv.api.res.code.GetVoucherErrorCode;
import com.mayloom.vouchserv.api.res.code.VoucherBatchActivationErrorCode;
import com.mayloom.vouchserv.api.res.code.BatchGenErrorCode;
import com.mayloom.vouchserv.api.res.code.GeneralVoucherServErrorCode;
import com.mayloom.vouchserv.man.api.res.code.ResultStatusCode;
import com.mayloom.vouchserv.api.res.code.VoucherRedeemErrorCode;
import com.mayloom.vouchserv.gen.IdentifierTool;
import com.mayloom.vouchserv.imp.enums.VoucherServiceConfig;
import com.mayloom.vouchserv.util.DateUtil;
import com.mayloom.vouchserv.dbo.BulkOpDatabaseHelper;
import com.mayloom.vouchserv.dbo.DatabaseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.dozer.Mapper;
import org.joda.time.LocalDateTime;

public final class VoucherServiceImp implements VoucherService {
	
	private final VoucherPinAmount voucherPinAmount;
	
	private final Logger logger = LoggerFactory.getLogger(VoucherServiceImp.class);
	
	private DatabaseHelper databaseHelper;
	
	private BulkOpDatabaseHelper bulkOpDatabaseHelper;
	
	private Mapper mapper;
	
	private IdentifierTool vsvIdTool;
	
	private GenerationServer generationServer; 
	
	public static final int MAX_VOUCHERS_PER_TASK = 1000;
	
	/**
	 * Not sure about including this anymore.
	 */
	private VoucherServiceImp(VoucherPinAmount voucherPinAmount) {
		this.voucherPinAmount = voucherPinAmount;
	}
	
	@PostConstruct
	public void init() {
		
		logger.info("==================== VoucherServiceImp =========================");
        if (this.databaseHelper == null) {
            throw new IllegalStateException("The [databaseHelper] property must be set.");
        }
		
		logger.info("VoucherService initialization: looking for unfinished VoucherBatches");
		
		generateIncompleteBatches();
					
		logger.info("====================+++++++++++++++++++++=======================");
	}
	
	void generateIncompleteBatches() {
		
		Optional<List<com.mayloom.vouchserv.dbo.VoucherBatch>> optIncompleteVoucherBatches = databaseHelper.getIncompleteVoucherBatches();
		
		if (optIncompleteVoucherBatches.isPresent()) {
			logger.info("Found {} incomplete VoucherBatches", optIncompleteVoucherBatches.get().size());
			for (com.mayloom.vouchserv.dbo.VoucherBatch batch: optIncompleteVoucherBatches.get()) {
				
				long startSerialNo = -1;
				int sizeToGenerate = -1;
				if (batch.getGeneratedSize() != 0) {
					// TODO must calculate batchGenRequest's size
					sizeToGenerate = batch.getRequestedSize() - batch.getGeneratedSize();
					logger.info("Some VoucherBatch vouchers have already been generated, calculating start serial no...");
					Optional<Long> optMaxGeneratedVoucherSerialNo = databaseHelper.getMaxVoucherBatchVoucherSerialNo(batch.getId());
					if (optMaxGeneratedVoucherSerialNo.isPresent()) {
						startSerialNo = optMaxGeneratedVoucherSerialNo.get().longValue() + 1;
						logger.info("Calculated start serial no: {}", startSerialNo);
					} else {
						throw new IllegalStateException("If some vouchers have been generated we MUST be able to find an associated Voucher which must have a serial no");
					}
				} else if (batch.getGeneratedSize() == 0) {
					sizeToGenerate = batch.getRequestedSize();
					logger.info("No VoucherBatch vouchers have been generated");
					startSerialNo = batch.getRequestedBatchStartSerialNo();
				} else {
					throw new IllegalStateException("batch genereated size must be >= 0");
				}
				
				if (startSerialNo == -1) {
					throw new IllegalStateException("cannot have negative start serial number");
				}
				
				long batchId = batch.getId();
				int batchNumber = batch.getBatchNumber();
				GenTaskInfo genTaskInfo = new GenTaskInfo(startSerialNo, batchId, batchNumber);
				VoucherBatchType reqVoucherBatchType = VoucherBatchType.valueOf(batch.getRequestedBatchType());
				PinType reqPinType = PinType.valueOf(batch.getRequestedPinType());
				
				BatchGenRequest batchGenRequest = new BatchGenRequest.Builder("NOT RELEVANT","NOT RELEVANT", sizeToGenerate ,reqVoucherBatchType)
														.pinLength(batch.getRequestedPinLength()).pinType(reqPinType).build();
				
				List<VoucherGenSubtask> taskList = splitIntoSubtasks(genTaskInfo, batchGenRequest);
				generationServer.processTasks(taskList);
			}
		} else {
			logger.info("Found ZERO incomplete VoucherBatches");
		}
	
		
	}
	
	public RegisterResult register(String username) {
		
		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>> VoucherServiceImp.register");
		
		String vsvId = getVsvIdTool().generateVSVID();
		
		logger.debug("VSVID: " + vsvId);
		
		// TODO return errors that may occur here 
		
		databaseHelper.createAndPersistVoucherBatchOwner(username, vsvId);
		
		return new RegisterResult(vsvId);
	}
	
	public BatchGenResult generateVoucherBatch(String username, BatchGenRequest batchGenRequest) {
		
		if (getVsvIdTool().validateVSVID(batchGenRequest.getVsvId())) {
		
			// validate the supplied vsvId i.e. determine whether there is a VoucherBatchOwner with such a vsvId
			// TODO this is highly inefficient, a legacy of not having usernames
			boolean isVsvIdValid = databaseHelper.doesVsvIdExist(username, batchGenRequest.getVsvId());
		
			if (isVsvIdValid) {
		
				// voucherNumber validation
				if ( batchGenRequest.getVoucherNumber() <= 0) { 
			
					BatchGenResult batchGenResult = 
						new BatchGenResult(ResultStatusCode.FAIL, BatchGenErrorCode.BATCH_SIZE_MUST_BE_GREATER_THAN_ZERO);
					return batchGenResult; 
			
				} else if (batchGenRequest.getVoucherNumber() > VoucherServiceConfig.SYNC_VOUCHER_BATCH_SIZE_GENERATION_LIMIT.getValue()) {
			
					BatchGenResult batchGenResult = new BatchGenResult(ResultStatusCode.FAIL, BatchGenErrorCode.BATCH_SIZE_LIMIT_EXCEEDED);
					return batchGenResult; 
				
				} else if ((batchGenRequest.getExpiryDate() != null) && (DateUtil.isDateInPast(batchGenRequest.getExpiryDate()))) { 
				
					return new BatchGenResult(ResultStatusCode.FAIL, BatchGenErrorCode.BATCH_EXP_DATE_IN_PAST);
					
				} else if ((batchGenRequest.getPinLength() < 6) || (batchGenRequest.getPinLength() > 30)) 
				
					return new BatchGenResult(ResultStatusCode.FAIL, BatchGenErrorCode.PIN_LENGTH);
				
				else {
				
					// now generate the batch
					int batchNumber = databaseHelper.generateVoucherBatch(batchGenRequest, false);
			
					BatchGenResult batchGenResult = new BatchGenResult(ResultStatusCode.SUCCESS, batchNumber);
					return batchGenResult;
				}	
			
			} else {
			
				BatchGenResult batchGenResult = new BatchGenResult(ResultStatusCode.FAIL, GeneralVoucherServErrorCode.VSV_ID_INVALID);
				return batchGenResult; 
			
			}
		} else {
			
			return new BatchGenResult(ResultStatusCode.FAIL, GeneralVoucherServErrorCode.VSV_ID_INVALID);
			
		}
		
	}
	
	@Override
	public BatchGenResult generateVoucherBatchAsync(String username, BatchGenRequest batchGenRequest) {
		
		// TODO add in generic validation for batchGenRequest
		if (batchGenRequest.getPinType() == PinType.NUMERIC && batchGenRequest.getPinLength() == 6 && batchGenRequest.getVoucherNumber() == 1000000) {
			return new BatchGenResult(ResultStatusCode.FAIL, BatchGenErrorCode.PIN_SPACE_SATURATION);
		}
		
		// Step 1: generate the empty batch
		GenTaskInfo genTaskInfo = databaseHelper.addVoucherBatchWithoutVouchers(batchGenRequest);
		
		// Step 2: split the voucher generation into subtasks
		List<VoucherGenSubtask> taskList = splitIntoSubtasks(genTaskInfo, batchGenRequest);
		generationServer.processTasks(taskList);
		//for (VoucherGenSubtask task: taskList) {
			// Step 2.2: let the multi-threaded GenerationServer handle the generation
			//generationServer.processTask(task);
		//}
		BatchGenResult batchGenResult = new BatchGenResult(ResultStatusCode.SUCCESS, genTaskInfo.getBatchNumber());
		return batchGenResult;
		
	}
	
	// TODO make private and rather test using ReflectionUtils
	public List<VoucherGenSubtask> splitIntoSubtasks(GenTaskInfo genTaskInfo, BatchGenRequest req) {
		
		if (req.getVoucherNumber() == 0) {
			return null;
		}
		List<VoucherGenSubtask> tasks = new ArrayList<VoucherGenSubtask>();
		if (req.getVoucherNumber() <= MAX_VOUCHERS_PER_TASK) {
			// TODO how do we know some vouchers have not already been generated?
			VoucherGenSubtask task = new VoucherGenSubtask(genTaskInfo.getBatchId(), req.getBatchType(), req.getPinLength(), req.getPinType(), 
															genTaskInfo.getStartSerialNo(), (genTaskInfo.getStartSerialNo() + req.getVoucherNumber() - 1));
			logger.info(task.toString());
			tasks.add(task);
		} else {
			long genStartSerialNumb = genTaskInfo.getStartSerialNo(); 
			long genEndSerialNumb = genTaskInfo.getStartSerialNo() + req.getVoucherNumber(); 
			long currentStartSerialNumb = genStartSerialNumb;
			int pseudoGeneratedVouchers = 0;
			while (pseudoGeneratedVouchers < req.getVoucherNumber()) {
				if (currentStartSerialNumb == genStartSerialNumb) {
					// first task
					logger.info("first task");
					VoucherGenSubtask task = new VoucherGenSubtask(genTaskInfo.getBatchId(), req.getBatchType(), req.getPinLength(), req.getPinType(), 
																	currentStartSerialNumb, currentStartSerialNumb + MAX_VOUCHERS_PER_TASK - 1); // TODO endSerialNumber is wrong
					tasks.add(task);
					logger.info(task.toString());
					currentStartSerialNumb += MAX_VOUCHERS_PER_TASK; // prepare for next iteration
					long serialRange = task.getEndSerialNumb() - task.getStartSerialNumb() + 1;
					logger.info(""+serialRange);
					pseudoGeneratedVouchers += MAX_VOUCHERS_PER_TASK;
				} else { 
					int vouchersLeft = req.getVoucherNumber() - pseudoGeneratedVouchers;
					if (vouchersLeft < MAX_VOUCHERS_PER_TASK) {
						// final task
						logger.info("final task");
						VoucherGenSubtask task = new VoucherGenSubtask(genTaskInfo.getBatchId(), req.getBatchType(), req.getPinLength(), req.getPinType(), 
								currentStartSerialNumb, genEndSerialNumb - 1);
						tasks.add(task);
						logger.info(task.toString());
						pseudoGeneratedVouchers += vouchersLeft; // prepare to escape from loop
					} else {
						logger.info(".");
						long endSerial = currentStartSerialNumb + MAX_VOUCHERS_PER_TASK;  
						VoucherGenSubtask task = new VoucherGenSubtask(genTaskInfo.getBatchId(), req.getBatchType(), req.getPinLength(), req.getPinType(), 
								currentStartSerialNumb, endSerial - 1);
						tasks.add(task);
						logger.info(task.toString());
						currentStartSerialNumb += MAX_VOUCHERS_PER_TASK; // prepare for next iteration
						pseudoGeneratedVouchers += MAX_VOUCHERS_PER_TASK;
					}
				}
				
			}
		}
		return tasks;
	}

	public VoucherBatch getVoucherBatch(String username, String vsvId, int batchNumber) {
		
		if (getVsvIdTool().validateVSVID(vsvId)) {
			
			boolean isVsvIdValid = databaseHelper.doesVsvIdExist(username, vsvId);
			
			if (isVsvIdValid) {
				
				com.mayloom.vouchserv.dbo.VoucherBatch dboBatch = databaseHelper.getVoucherBatch(vsvId, batchNumber);
				
				if (dboBatch == null) {
					
					return null;
					
				} else {
					
					VoucherBatch voucherBatch =  mapper.map(dboBatch, VoucherBatch.class);
					
					return voucherBatch;
				}
			} else {
				return null;
			}
			
		} else {
			return null;
		}
	}
	
	@Override
	public Optional<VoucherBatchGenStatus> getVoucherBatchStatus(String username, String vsvId, int batchNumber) {
		if (getVsvIdTool().validateVSVID(vsvId) && batchNumber > 0) {
			Optional<VoucherBatchGenStatus> optVoucherBatchGenStatus = databaseHelper.getVoucherBatchStatus(username, vsvId, batchNumber);
			return optVoucherBatchGenStatus;
		} else {
			return Optional.absent();
		}
	}
	
	@Override
	public GetVoucherResult getVoucher(String username, String vsvId, Optional<String> serialNo, Optional<String> pin) {
		
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(vsvId) || (!serialNo.isPresent() && !pin.isPresent())) {
			throw new IllegalArgumentException();
		}
		
		if (getVsvIdTool().validateVSVID(vsvId)) {
			
			if (pin.isPresent()) {
				
				Optional<com.mayloom.vouchserv.dbo.Voucher> optDboVoucher = databaseHelper.getVoucherByPin(username, vsvId, pin.get());
				
				if (optDboVoucher.isPresent()) {
					
					ContentEnricherVoucher voucher = mapper.map(optDboVoucher.get(), ContentEnricherVoucher.class);
					if (voucher.getPin() == null) { 
						throw new RuntimeException();
					}
					return GetVoucherResult.createNoErrorInstance(voucher);
					
				} else { 
					
					return GetVoucherResult.createGetVoucherErrorInstance(GetVoucherErrorCode.VOUCHER_DOES_NOT_EXIST);
				}
				
			} else if (serialNo.isPresent()) {
				
				Optional<com.mayloom.vouchserv.dbo.Voucher> optDboVoucher = databaseHelper.getVoucherBySerialNumber(username, vsvId, serialNo.get());
				
				if (optDboVoucher.isPresent()) {
					
					ContentEnricherVoucher voucher = mapper.map(optDboVoucher.get(), ContentEnricherVoucher.class);
					return GetVoucherResult.createNoErrorInstance(voucher);
					
				} else { 
					
					return GetVoucherResult.createGetVoucherErrorInstance(GetVoucherErrorCode.VOUCHER_DOES_NOT_EXIST);
				}
				
			} else { 
				
				throw new IllegalArgumentException();
				
			}
			
		} else {
			
			return GetVoucherResult.createGeneralErrorInstance(GeneralVoucherServErrorCode.VSV_ID_INVALID);
			
		}
		
	}
	
	public ActivateVoucherBatchResult activateVoucherBatch(String username, String vsvId, int batchNumber) {
		
		if (getVsvIdTool().validateVSVID(vsvId)) {
			
			boolean isVsvIdValid = databaseHelper.doesVsvIdExist(username, vsvId);
			
			if (isVsvIdValid) {
			
				if (batchNumber > 0) {
				
					return databaseHelper.activateVoucherBatch(vsvId, batchNumber);
				
				} else {
				
					return new ActivateVoucherBatchResult(VoucherBatchActivationErrorCode.INVALID_BATCH_NUMBER, batchNumber);
				
				}
				
			} else {
				
				return new ActivateVoucherBatchResult(GeneralVoucherServErrorCode.VSV_ID_INVALID, batchNumber);
			}
			
		} else {
			
			return new ActivateVoucherBatchResult(GeneralVoucherServErrorCode.VSV_ID_INVALID, batchNumber);
			
		}
	}
	
	public ActivateVoucherSerialRangeResult activeVoucherSerialRange(String username, String vsvId, long startSerialNumb, long endSerialNumb) {
		
		if (getVsvIdTool().validateVSVID(vsvId)) {
			
			if ((startSerialNumb > 0) && (endSerialNumb > 0) && (endSerialNumb >= startSerialNumb)) {
				
				boolean isVsvIdValid = databaseHelper.doesVsvIdExist(username, vsvId);
				
				if (isVsvIdValid) {
								
					return bulkOpDatabaseHelper.bulkUpdateVoucherActivate(vsvId, startSerialNumb, endSerialNumb);
				
				} else {
					
					return new ActivateVoucherSerialRangeResult(GeneralVoucherServErrorCode.VSV_ID_INVALID);
					
				}
				
			} else {
				
				return new ActivateVoucherSerialRangeResult(ActivateVoucherSerialRangeResultErrorCode.START_END_SERIAL_RANGE_VALIDATION_ERROR);
				
			}
			
		} else {
			
			return new ActivateVoucherSerialRangeResult(GeneralVoucherServErrorCode.VSV_ID_INVALID);
			
		}
		
	}
	
	public VoucherRedeemResult redeemVoucher(String username, String vsvId, Voucher voucher) {
		
		if (getVsvIdTool().validateVSVID(vsvId)) {
			
			boolean isVsvIdValid = databaseHelper.doesVsvIdExist(username, vsvId);
			
			if (isVsvIdValid) {
			
				VoucherRedeemErrorCode redeemValidationErrorCode = isVoucherRedeemable(voucher);
			
				if ( redeemValidationErrorCode != null) {
				
					logger.debug("VOUCHER NOT REDEEMABLE: SEE ERROR CODE FOR DETAILS");
				
					return new VoucherRedeemResult(redeemValidationErrorCode);
				
				} else {
				
					logger.debug("VOUCHER REDEEMABLE");
				
					return databaseHelper.redeemVoucher(vsvId, voucher.getPin()); 
				
				}
				
			} else {
				return new VoucherRedeemResult(GeneralVoucherServErrorCode.VSV_ID_INVALID);
			}
		} else {
			return new VoucherRedeemResult(GeneralVoucherServErrorCode.VSV_ID_INVALID);
		}
	}
	
	@Override
	public VoucherRedeemResult redeemVoucher(String username, String vsvId, String pin) {
		
		boolean isVsvIdValid = databaseHelper.doesVsvIdExist(username, vsvId);
		
		if (isVsvIdValid) {
			return databaseHelper.redeemVoucher(vsvId, pin);
		} else {
			return new VoucherRedeemResult(GeneralVoucherServErrorCode.VSV_ID_INVALID);
		}
		
		
	}
	
	public DeactivateVoucherBatchResult deactivateVoucherbatch(String username, String vsvId, int batchNumber) {
		
		if (getVsvIdTool().validateVSVID(vsvId)) { 
			
			if (batchNumber > 0) {
				
				boolean isVsvIdValid = databaseHelper.doesVsvIdExist(username, vsvId);
			
				if (isVsvIdValid) {
				
					return databaseHelper.deactivateVoucherbatch(vsvId, batchNumber);
				
				} else {
					
					return new DeactivateVoucherBatchResult(GeneralVoucherServErrorCode.VSV_ID_INVALID, batchNumber);
					
				}
			
			} else {
							
				return new DeactivateVoucherBatchResult(VoucherBatchActivationErrorCode.INVALID_BATCH_NUMBER, batchNumber);
			
			}
			
		} else {
			
			return new DeactivateVoucherBatchResult(GeneralVoucherServErrorCode.VSV_ID_INVALID, batchNumber);
			
		}
		
	}
		
	/**
	 * Voucher validation function to determine if it can be redeemed based 
	 * on supplied fields.
	 * 
	 * TODO we may wish the refactor with a list of errors, rather than a single result with 
	 * a hierarchy as is currently done
	 * 
	 * TODO with the below, we assume that the voucher's time, in terms of relevant dates 
     * such as below, is relevant to system time (LocalDateTime in Joda Time), this is most 
     * likely incorrect, it should be based on the local time / timezone of the 
     * VoucherBatchOwner or VoucherBatch
	 * 
	 * @param voucher	The Voucher we wish the validate in terms of redeemability
	 * @return 			null if the specified Voucher can be redeemed, VoucherRedeemErrorCode otherwise
	 */
	private VoucherRedeemErrorCode isVoucherRedeemable(Voucher voucher) {
		
		// most basic validation checks, in order of priority
		if (voucher == null) {
			
			return VoucherRedeemErrorCode.VOUCHER_NOT_NULL;
			
		} else if (voucher.getPin() == null) {
			
			return VoucherRedeemErrorCode.PIN_NOT_NULL;
			
		} else if (voucher.isActive() == false) {
			
			return VoucherRedeemErrorCode.VOUCHER_NOT_ACTIVE_OVERRIDE_USED;
			
		} else if (voucher.getPin().length() != voucherPinAmount.length()) {
			
			return VoucherRedeemErrorCode.VOUCHER_PIN_LENGTH_INCORRECT;
			
		} else if (voucher.getExpiryDate() != null) {
					
			LocalDateTime now = new LocalDateTime();
			LocalDateTime expiry = LocalDateTime.fromDateFields(voucher.getExpiryDate());
					
			if (now.isAfter(expiry)) {
						
				return VoucherRedeemErrorCode.VOUCHER_EXPIRED;
						
			} 
				
		} 
				
		// if we get here, no validation errors have been picked up
		return null;	
		
	}
	
	@Override
	public Optional<List<VoucherBatchOwner>> getRegistrations(String username) {
		
		Optional<List<com.mayloom.vouchserv.dbo.VoucherBatchOwner>> optVoucherBatchOwnerList = getDatabaseHelper().getFullUserRegistrationDetails(username);
		
		if (optVoucherBatchOwnerList.isPresent() && optVoucherBatchOwnerList.get().size() > 0) {
			
			List<VoucherBatchOwner> vboList = new ArrayList<VoucherBatchOwner>();
			
			for (com.mayloom.vouchserv.dbo.VoucherBatchOwner dboVbo : optVoucherBatchOwnerList.get()) {
				
				VoucherBatchOwner voucherBatchOwner =  mapper.map(dboVbo, VoucherBatchOwner.class);
				vboList.add(voucherBatchOwner);
				
			}
			
			return Optional.of(vboList);
			
			
		} else {
			
			return Optional.absent();
			
		}
	}
	
	@Override
	public Optional<List<VoucherBatchHeader>> getVoucherBatches(String vsvId) {
		Optional<List<com.mayloom.vouchserv.dbo.VoucherBatch>> optVoucherBatchList = databaseHelper.getVoucherBatches(vsvId);
		if (optVoucherBatchList.isPresent()) {
			if (optVoucherBatchList.get().size() > 0) {
				List<com.mayloom.vouchserv.dbo.VoucherBatch> vbList = optVoucherBatchList.get();
				List<VoucherBatchHeader> voucherBatches = new ArrayList<VoucherBatchHeader>(vbList.size());
				for (com.mayloom.vouchserv.dbo.VoucherBatch dboVoucherBatch : vbList) {
					// org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: com.mayloom.vouchserv.dbo.VoucherBatch.vouchers, no session or session was
					// closed
					VoucherBatchHeader voucherBatchHeader =  mapper.map(dboVoucherBatch, VoucherBatchHeader.class);
					voucherBatches.add(voucherBatchHeader);
				}
				return Optional.of(voucherBatches);
			} else {
				return Optional.absent();
			}
		} else {
			return Optional.absent();
		}
	}
	
	public DatabaseHelper getDatabaseHelper() {
		return databaseHelper;
	}

	public void setDatabaseHelper(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public VoucherPinAmount getVoucherPinAmount() {
		return voucherPinAmount;
	}

	public Mapper getMapper() {
		return mapper;
	}

	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	public IdentifierTool getVsvIdTool() {
		return vsvIdTool;
	}

	public void setVsvIdTool(IdentifierTool vsvIdTool) {
		this.vsvIdTool = vsvIdTool;
	}

	public BulkOpDatabaseHelper getBulkOpDatabaseHelper() {
		return bulkOpDatabaseHelper;
	}

	public void setBulkOpDatabaseHelper(BulkOpDatabaseHelper bulkOpDatabaseHelper) {
		this.bulkOpDatabaseHelper = bulkOpDatabaseHelper;
	}
	
	/**
	 * @return the generationServer
	 */
	public GenerationServer getGenerationServer() {
		return generationServer;
	}

	/**
	 * @param generationServer the generationServer to set
	 */
	public void setGenerationServer(GenerationServer generationServer) {
		this.generationServer = generationServer;
	}

	

}
