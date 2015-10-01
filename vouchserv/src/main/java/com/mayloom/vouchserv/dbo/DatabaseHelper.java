/**
 * 
 */
package com.mayloom.vouchserv.dbo;

import java.util.List;
import com.google.common.base.Optional;
import com.mayloom.vouchserv.man.api.req.BatchGenRequest;
import com.mayloom.vouchserv.api.res.ActivateVoucherBatchResult;
import com.mayloom.vouchserv.api.res.ActivateVoucherSerialRangeResult;
import com.mayloom.vouchserv.api.res.DeactivateVoucherBatchResult;
import com.mayloom.vouchserv.api.res.VoucherRedeemResult;
import com.mayloom.vouchserv.dbo.enums.FetchRoles;
import com.mayloom.vouchserv.dbo.enums.FetchVBOs;
import com.mayloom.vouchserv.dbo.enums.FetchVoucherBatches;
import com.mayloom.vouchserv.dto.VoucherBatchGenStatus;
import com.mayloom.vouchserv.imp.GenTaskInfo;
import com.mayloom.vouchserv.imp.VoucherGenSubtask;

/**
 * @author Nico
 */
public interface DatabaseHelper {
	
	/**
	 * Check whether a User with the specified username exists.
	 * 
	 * @param username	The username of the user, cannot be null.
	 * @return			true if a User with the specified username exists, false otherwise
	 */
	public boolean doesUserExist(String username);
	
	/**
	 * Add a User with role ROLE_USER.
	 * 
	 * Note, it is the caller's responsibility to ensure that the username is unique.
	 * 
	 * @param username		The unique username of the user, cannot be null.
	 * @param password		The password of the user, cannot be null.
	 * @throws				ConstraintViolationException is the specified username is not unique.
	 */
	public void addNewOrdinaryUser(String username, String password);
	
	/**
	 * Add a User with role ROLE_ADMIN.
	 * 
	 * Note, it is the caller's responsibility to ensure that the username is unique.
	 * 
	 * @param username		The unique username of the user, cannot be null.
	 * @param password		The password of the user, cannot be null.
	 * @throws				ConstraintViolationException is the specified username is not unique.
	 */
	public void addNewAdminUser(String username, String password);
	
	/**
	 * Determines whether the specified username and password combination exists.
	 * 
	 * This method can be used for authentication, but only if these are the only two User
	 * fields supported (i.e. if account expiry, account lock, credentials expiry, emabled not
	 * considered).
	 * 
	 * @param username	The username of the user. Cannot be null.
	 * @param password	The password of the user. Cannot be null.
	 * @return			true if the specified username / password combo exists, false otherwise
	 */
	public boolean doesUserExist(String username, String password);
	
	/**
	 * Fetch the requested User Entity based on username and optionally load the associated Role 
	 * and VoucherBatchOwners.
	 * 
	 * @param username The unique username of the user to be fetched.
	 * @param fetchRoles Whether the Role collection should be fetched.
	 * @param fetchVoucherBatchOwners Whether the VoucherBatchOwner collection should be fetched.
	 * @return An Optional object that may contain the requested User if it was found in the database.
	 */
	public Optional<User> getUser(String username, FetchRoles fetchRoles, FetchVBOs fetchVoucherBatchOwners);
	
	public Optional<List<String>> getUserRegistrations(String username);
	
	/**
	 * Get the complete list of registrations (VoucherBatchOwners) for the specified user. 
	 * 
	 * NOTE: Each VoucherBatchOwner also has a set of associated VoucherBatches but these
	 * are not returned and should be separately fetched.
	 * 
	 * @param username	The username of the user who's full registration details we wish to fetch. 
	 * @return			List of VoucherBatchOwners ordered by lastUpdated (most recently used)
	 */
	public Optional<List<VoucherBatchOwner>> getFullUserRegistrationDetails(String username);
	
	/**
	 * Get the complete list of VoucherBatches for the VoucherBatchOwner with the specified vsvId.
	 * 
	 * NOTE: Each VoucherBatch also has an associated set of Vouchers but these are not returned
	 * and should be separately fetched.
	 * 
	 * @param vsvId The identifier of the VoucherBatchOwner whos VoucherBatches we wish to fetch.
	 * @return The List of VoucherBatches ordered by creationDate (most recently created first)
	 */
	public Optional<List<VoucherBatch>> getVoucherBatches(String vsvId);
	
	public void createAndPersistVoucherBatchOwner(String username, String vsvId);
	
	/**
	 * Get the VoucherBatchOwner and optionally its voucherBatches collection.
	 * 
	 * @param vsvId The VSV-ID used to identify the VoucherBatchOwner.
	 * @param fetchVoucherBatches Whether to fetch the VoucherBatchOwner's voucherBatches collection or not.
	 * @return null, if no VoucherBatchOwner is found, or the 
	 *         VoucherBatchOwner with the specified VSV-ID.
	 */
	public VoucherBatchOwner getVoucherBatchOwner(String vsvId, FetchVoucherBatches fetchVoucherBatches);
	
	/**
	 * Determine whether, for the specified user, a VoucherBatchOwner with the supplied VSV-ID exists.
	 * 
	 * TODO: This method is a legacy method (from when we did not have usernames) and needs to be removed.
	 * 
	 * @param username The username of the user.
	 * @param vsvId The VSV-ID that we wish to validate. 
	 * @return true if a a VoucherBatchOwner with the supplied VSV-ID exists, false otherwise.
	 * @see com.mayloom.vouchserv.dbo.VoucherBatchOwner#getCode()
	 */
	public boolean doesVsvIdExist(String username, String vsvId);
	
	/**
	 * Generated a new VoucherBatch owned by the VoucherBatchOwner with the
	 * specified vsvId. 
	 * 
	 * @param batchGenRequest   		The BatchGenRequest.                         
	 * @param skipPinVBOUniqueCheck 	If true then we save having to check that every generated voucher is unique and so get a massive performance gain BUT
	 * 									we risk a ConstraintViolationException
	 * @return 							The batchNumber, a sequential voucher batch identifier. 
	 * @see com.mayloom.vouchserv.dbo.VoucherBatch#getBatchNumber()
	 * TODO document Exceptions that may be thrown when using skipPinVBOUniqueCheck
	 */
	public int generateVoucherBatch(BatchGenRequest batchGenRequest, boolean skipPinVBOUniqueCheck);
	
	/**********************************************************************************************
	 * ********************************************************************************************
	 * The following two methods splits the above into one call to add the VoucherBatch and one or
	 * many more calls to generate the actual vouchers in batches of a configurable amount per 
	 * transaction, say 100 at a time.
	 * 
	 * NOTE: Using the methods below, rather than the all-in-one method above, has implications for
	 * getVoucherBatch since getVoucherBatch will have to have the ability to let the user know that
	 * the batch has not yet been completed and also what the status is in terms of progress, so this
	 * means that the method becomes a polling method 
	 **********************************************************************************************/
	 
	
	/**
	 * Persists the VoucherBatch without generating any vouchers.
	 * 
	 * When the persistence is conducted the VoucherBatchOwner's nextBatchStartSerialNumber is updated 
	 * to in effect RESERVE the serial range from its starting point to the reserved level.
	 * 
	 * @param batchGenRequest	The BatchGenRequest.
	 * @return					A GenTaskInfo instance, which cannot be null, with information, in addition
	 * 							to the batchGenRequest, that will be required by generation methods in 
	 * 							generating vouchers in effect scheduled by this method.
	 */
	public GenTaskInfo addVoucherBatchWithoutVouchers(BatchGenRequest batchGenRequest);
	
	/**
	 * Generates a specified set of vouchers.
	 * 
	 * The expectation is that the batch in question would not be complete in terms of vouchers generated
	 * (this is validated by this method) and that the task has the correct pinLength and pinType (checking
	 * the correctness of these fields is the responsibility of the caller).
	 * 
	 * @param task		The VoucherGenSubtask to be completed.
	 * @return			The number of vouchers generated
	 * @throws 			IllegalArgumentException if the specified batch has already been completely generated
	 * @throws 			IllegalArgumentException if the specified batch does not exist 
	 */
	public int generateAdditionalVoucherBatchVouchers(VoucherGenSubtask task);
	
	/**********************************************************************************************
	 * ********************************************************************************************
	 */
	
	/**
	 * Get a particular Voucher owned by a specified VoucherBatchOwner based on the unique pin (unique
	 * per VoucherBatchOwner that is) of the wanted Voucher.
	 * 
	 * @param vsvId      the identifier of the VoucherBatchOwner who owns the voucher     
	 * @param voucherPin the voucher pin
	 * @return           the voucher, if it exists for the specified VoucherBatchOwner, null otherwise
	 */
	public Voucher getVoucher(String vsvId, String voucherPin);
	
	/**
	 * Get a particular VoucherBatch owned by a specific VoucherBatchOwner based on the VoucherBatch's
	 * batchNumber
	 * 
	 * @param vsvId         the identifier of the VoucherBatchOwner who owns the VoucherBatch
	 * @param batchNumber   the batchNumber, a unique sequential number per VoucherBatchOwner
	 * @return 				null if no VoucherBatch found or the VoucherBatch with all associated Vouchers and VoucherPins 
	 */
	public VoucherBatch getVoucherBatch(String vsvId, int batchNumber);
	
	/**
	 * Get a batch using a database identifier.	
	 * 
	 * @param batchId	The underlying (database layer) primary id. 
	 * @return			The VoucherBatch if possible else.
	 */
	public Optional<VoucherBatch> getVoucherBatch(long batchId);
	
	/**
	 * Get the status of the specified batch in terms of vouchers generated at the time the 
	 * transaction takes place. 
	 * 
	 * @param username	the username that owns the vsvId
	 * @param vsvId	the vsvId of the user
	 * @param batchNumber the number of the batch 
	 * @return	an empty optional if the batch is not found, else an Optional with a VoucherBatchGenStatus
	 */
	Optional<VoucherBatchGenStatus> getVoucherBatchStatus(String username, String vsvId, int batchNumber);
	
	/**
	 * Activate a particular VoucherBatch.
	 * 
	 * @param vsvId			the identifier of the VoucherBatchOwner who owns the VoucherBatch
	 * @param batchNumber   the batchNumber, a unique sequential number per VoucherBatchOwner
	 * @return 				ActivateVoucherBatchResult
	 */
	public ActivateVoucherBatchResult activateVoucherBatch(String vsvId, int batchNumber);
	
	/**
	 * Activate Voucher serial range.
	 * 
	 * @param vsvId
	 * @param startSerialNumb
	 * @param endSerialNumb
	 * @return					ActivateVoucherSerialRangeResult
	 */
	public ActivateVoucherSerialRangeResult activeVoucherSerialRange(String vsvId, long startSerialNumb, long endSerialNumb);
	
	/**
	 * Deactivate a particular VoucherBatch.
	 * 
	 * @param vsvId			the identifier of the VoucherBatchOwner who owns the VoucherBatch
	 * @param batchNumber   the batchNumber, a unique sequential number per VoucherBatchOwner
	 * @return 				DeactivateVoucherBatchResult
	 */
	public DeactivateVoucherBatchResult deactivateVoucherbatch(String vsvId, int batchNumber);
	
	/**
	 * Redeem a Voucher owned by a specific VoucherBatchOwner and this entails 
	 * setting the redemptionDate of the Voucher if possible.
	 * 
	 * Note, it may be prudent to precede a call to redeemVoucher with getVoucher()
	 * to ensure that the Voucher is active and has not already been redeemed. 
	 * 
	 * @param vsvId		the identifier of the VoucherBatchOwner who owns the voucher
	 * @param pin	    the voucher pin of the voucher we wish to redeem
	 * @return          VoucherRedeemResult indicating success or failure
	 */
	public VoucherRedeemResult redeemVoucher(String vsvId, String voucherPin);
	
	public Optional<Voucher> getVoucherByPin(String username, String vsvId, String pin);
	
	public Optional<Voucher> getVoucherBySerialNumber(String username, String vsvId, String serialNumber);
	
	public Optional<Integer> getBatchNumberByPin(String username, String vsvId, String pin);

	public Optional<List<VoucherBatch>> getIncompleteVoucherBatches();
	
	// return type Optional since there may be 0 Vouchers associated with the VoucherBatch 
	public Optional<Long> getMaxVoucherBatchVoucherSerialNo(long voucherBatchId);
}
