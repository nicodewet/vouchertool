package com.mayloom.vouchserv.api;
import java.util.List;

import com.google.common.base.Optional;
import com.mayloom.vouchserv.man.api.req.BatchGenRequest;
import com.mayloom.vouchserv.api.res.ActivateVoucherBatchResult;
import com.mayloom.vouchserv.api.res.ActivateVoucherSerialRangeResult;
import com.mayloom.vouchserv.man.api.res.BatchGenResult;
import com.mayloom.vouchserv.api.res.DeactivateVoucherBatchResult;
import com.mayloom.vouchserv.api.res.GetVoucherResult;
import com.mayloom.vouchserv.man.api.res.RegisterResult;
import com.mayloom.vouchserv.api.res.VoucherRedeemResult;
import com.mayloom.vouchserv.man.api.dto.VoucherBatch;
import com.mayloom.vouchserv.man.api.dto.Voucher;
import com.mayloom.vouchserv.man.api.dto.VoucherBatchHeader;
import com.mayloom.vouchserv.dto.VoucherBatchGenStatus;
import com.mayloom.vouchserv.man.api.dto.VoucherBatchOwner;
import com.mayloom.vouchserv.imp.GenTaskInfo;
import com.mayloom.vouchserv.imp.VoucherGenSubtask;
/**
 * @author Nico
 * 
 * The VoucherService has a voucher pin amount, that is, the number of numeric
 * digits that constitute the number of digits in each voucher pin.
 * 
 * TODO: Should use some kind of VoucherServiceResponse to wrap responses since
 * this will contain generic error response messages (e.g. invalid VSV-ID
 * used) with for example an ErrorMessage enum (with code and associated message) 
 * that can be exposed at api level. As an example, see 
 * http://java.sun.com/webservices/docs/1.4/api/javax/servlet/http/HttpServletResponse.html 
 */
public interface VoucherService {
	
	void init();

	/**
	 * Methods for REGULAR VoucherBatch management:
	 * 
	 * 1. Register and get VSV-ID in order to perform operations.
	 * 2. Generate REGULAR VoucherBatch with or without expiry date.
	 * 3. Activate VoucherBatch.
	 * 4. Get VoucherBatch.
	 * 4. Redeem individual Vouchers.
	 *  
	 * Methods for JUST_IN_TIME VoucherBatch management:
	 * 
	 * 1. Register and get VSV-ID in order to perform operations.
	 * 2. Generate JUST_IN_TIME VoucherBatch with or without expiry date.
	 * 3. Get VoucherBatch.
	 * 4. Activate voucher serial range.
	 * 5. Redeem individual Vouchers within said serial range.
	 * 
	 * TODO refactor this interface to use the Response response doSomething(Request request); pattern.
	 */
	
	/**
	 * Returns a unique twenty three character identifier, called the VSV-ID, that serves
	 * as the service user's identifier. 
	 * 
	 * Format of returned VSV-ID: VSV-IDCCYYMMDDXXXXXXXXX
	 * 
	 * VSV-ID: All identifiers start with this. 6 characters.
	 * CCYYMMDD: This is the system / service date when registration took place. 8 characters.
	 * XXXXXXXXX: A random nine digit alpanumeric random character sequence. 9 characters. 
	 * 
	 * @param  The user making the request and with whom the generated VSV-ID will be associated.
	 * @return RegisterResult with a 23 character VSV-ID if the operation succeeded
	 */
	public RegisterResult register(String username);
	
	/**
	 * Generate a VoucherBatch of a particular size, of a particular type (REGULAR or JUST_IN_TIME), with the 
	 * maximum batch size of 1000 vouchers. 
	 * 
	 * The user may select the desired granularity of control when it comes to activation: based on the selected 
	 * VoucherBatchType, either the VoucherBatch will be active, with individual Vouchers inactive OR the 
	 * VoucherBatch will be inactive and individual vouchers in the VoucherBatch will be active, so if the 
	 * generated VoucherBatch is later activated any Voucher in the batch can be redeemed. NOTE: if the batch 
	 * had an expiry date set, and has expired, even if its active, Vouchers cannot be redeemed.
	 * 
	 * As a synchronous method the caller may be blocked for a significant time period while generation 
	 * takes place. The limitation in batch size stems is in place in order to limit the blocking time. 
	 * 
	 * @param username          The username of the user making the request.
	 * @param batchGenRequest   The BatchGenRequest with required and optional parameters.
	 * @return 					The BatchGenResult, if successful with a sequential value per service user that serves 
	 *         					as a batch identifier, that is returned upon successful batch generation. If unsuccessful,
	 *         					the BatchGenResult's BatchGenErrorCode will specify the nature of the error.
	 */
	public BatchGenResult generateVoucherBatch(String username, BatchGenRequest batchGenRequest);
	
	/**
	 * @param username
	 * @param batchGenRequest	No limitation of the number of vouchers that can be generated.
	 * @return					The BatchGenResult with the batchNumber of the generated batch.
	 */
	public BatchGenResult generateVoucherBatchAsync(String username,  BatchGenRequest batchGenRequest);
	
	/**
	 * Retrieve a previously generated voucher batch. 
	 * 
	 * @param username      The username of the user making the request.
	 * @param vsvId 		The VSV-ID, service user identifier, that uniquely identifies the user, and 
	 * 	            		is generated upon registration.
	 * @param batchNumber 	The identifier of the previously successfully generated voucher batch owned
	 *                    	by service user with VSV-ID vsvId. 
	 * @return 				The requested batch. If ANY error occurred (e.g. invalid VSD-ID, invalid batchNumber, 
	 * 		   				system error) null is returned, or null may be returned if the batch is not found.
	 */
	public VoucherBatch getVoucherBatch(String username, String vsvId, int batchNumber);
	
	/**
	 * Get the generation status of a VoucherBatch, encapsulating the requested batch size and how many 
	 * vouchers have been generated.
	 * 
	 * @param username		The username of the user making the request.
	 * @param vsvId			The VSV-ID, service user identifier, that uniquely identifies the user, and 
	 * 	            		is generated upon registration.
	 * @param batchNumber	The identifier of the previously successfully generated voucher batch owned
	 *                    	by service user with VSV-ID vsvId.
	 * @return				If no batch exists an empty Optional, else a VoucherBatchGenStatus element with the requested
	 * 						size and the generated size.
	 */
	public Optional<VoucherBatchGenStatus> getVoucherBatchStatus(String username, String vsvId, int batchNumber);
	
	/**
	 * Activate a VoucherBatch, known to be in an inactive state, so that its Vouchers could possibly 
	 * be redeemed. It is not possible to redeem a Voucher unless its parent Voucherbatch is active.
	 * 
	 * @param username      The username of the user making the request.
	 * @param  vsvId		The VSV-ID, service user identifier, that uniquely identifies the user 
	 * 						(VoucherBatchOwner), and is generated upon registration.
	 * @param  batchNumber  The number of the batch known to be associated with the specified VoucherBatchOwner.
	 * @return 				ActivateVoucherBatchResult with th result of the VoucherBatch activation.
	 */
	public ActivateVoucherBatchResult activateVoucherBatch(String username, String vsvId, int batchNumber);
	
	
	/**
	 * Deactivate a VoucherBatch that is known to be in an active state.
	 * 
	 * @param username      The username of the user making the request.
	 * @param vsvId			The VSV-ID, service user identifier, that uniquely identifies the user 
	 * 						(VoucherBatchOwner), and is generated upon registration.
	 * @param batchNumber	The number of the batch known to be associated with the specified VoucherBatchOwner.
	 * @return				DeactivateVoucherBatchResult with the result of the deactivation.
	 */
	public DeactivateVoucherBatchResult deactivateVoucherbatch(String username, String vsvId, int batchNumber);
	
	/**
	 * Mark a voucher as having been redeemed. 
	 * 
	 * Both the associated VoucherBatch and the Voucher must be marked as active in 
	 * order for this operation to succeed. Parent VoucherBatch must not have expired.
	 * 
	 * NOTE: Marking a voucher as having been redeemed will normally be a single step in
	 * a broader transaction involving 1. the value of the voucher being distributed in 
	 * some way 2. marking the voucher as having been redeemed. Step 1 is BEYOND THE SCOPE
	 * of what this method does.
	 * 
	 * @param username  The username of the user making the request.
	 * @param vsvId 	The VSV-ID, service user identifier, that uniquely identifies the user, and 
	 * 					is generated upon registration.
	 * @param voucher 	The Voucher that has to be redeemed (marked as being redeemed).
	 * @return 			VoucherRedeemResult with the result of the redemption embedded.			 
	 */
	public VoucherRedeemResult redeemVoucher(String username, String vsvId, Voucher voucher);
	
	/**
	 * Mark a voucher, with the specified pin, as having been redeemed. 
	 * 
	 * Both the associated VoucherBatch and the Voucher must be marked as active in 
	 * order for this operation to succeed. Parent VoucherBatch must not have expired.
	 * 
	 * NOTE: Marking a voucher as having been redeemed will normally be a single step in
	 * a broader transaction involving 1. the value of the voucher being distributed in 
	 * some way 2. marking the voucher as having been redeemed. Step 1 is BEYOND THE SCOPE
	 * of what this method does.
	 * 
	 * @param username  The username of the user making the request.
	 * @param vsvId 	The VSV-ID, service user identifier, that uniquely identifies the user, and 
	 * 					is generated upon registration.
	 * @param pin 		The pin of the Voucher that has to be redeemed (marked as being redeemed).
	 * @return 			VoucherRedeemResult with the result of the redemption embedded.			 
	 */
	public VoucherRedeemResult redeemVoucher(String username, String vsvId, String pin);
	
	/**
	 * Activate a sequential set of Vouchers, known to be inactive, and identified by a specified serial range.
	 * 
	 * @param username  			The username of the user making the request.
	 * @param vsvId					The VSV-ID, service user identifier, that uniquely identifies the user 
	 * 								(VoucherBatchOwner), and is generated upon registration.
	 * @param startSerialNumber     The start of the sequential list of vouchers to be activated (i.e. Voucher 
	 * 								with startSerialNumber will also be activated). Must be greater than 0. 
	 * 								May be equal to endSerialNumber (which means single voucher activation).
	 * @param endSerialNumber       The end of the sequential list of vouchers to be activated (i.e. Voucher 
	 * 								with endSerialNumber will also be activated). Must be greater than 0.
	 * 								May be equal to startSerialNumber (which means single voucher activation).
	 * @return						ActivateVoucherSerialRangeResult with the result of the activation.
	 */
	public ActivateVoucherSerialRangeResult activeVoucherSerialRange(String username, String vsvId, long startSerialNumber, long endSerialNumber);
	
	/**
	 * Get a Voucher from persistent storage.
	 * 
	 * @param username
	 * @param vsvId
	 * @param serialNo Optional, but if not present then pin MUST be present. If both seriaNo AND pin are present then serialNo will not be used to find the 
	 * 				   voucher (using one field is sufficient).
	 * @param pin Optional, but if not present then serialNo MUST be present. If both seriaNo AND pin are present then serialNo will not be used find the voucher 
	 *            (using one field is sufficient).
	 * @return
	 * @throws IllegalArgumentException if both parameters are absent
	 */
	public GetVoucherResult getVoucher(String username, String vsvId, Optional<String> serialNo, Optional<String> pin);
	
	/**
	 * Get all the voucher service registrations associated with the specified user. 
	 * 
	 * @param username  The user who's registrations we wish to retrieve. 
	 * @return			The registrations of the user, there may be none.
	 */
	public Optional<List<VoucherBatchOwner>> getRegistrations(String username);
	
	/**
	 * Get the complete list of VoucherBatches for the VoucherBatchOwner with the specified vsvId.
	 * 
	 * NOTE: Each VoucherBatch also has an associated set of Vouchers but these are not returned
	 * and should be separately fetched.
	 * 
	 * @param vsvId The identifier of the VoucherBatchOwner whos VoucherBatches we wish to fetch.
	 * @return The List of VoucherBatches ordered by creationDate (most recently created first)
	 */
	public Optional<List<VoucherBatchHeader>> getVoucherBatches(String vsvId);
	
	/**
	 * TEMPORARILY IN PUBLIC API
	 */
	public List<VoucherGenSubtask> splitIntoSubtasks(GenTaskInfo genTaskInfo, BatchGenRequest req);
}
