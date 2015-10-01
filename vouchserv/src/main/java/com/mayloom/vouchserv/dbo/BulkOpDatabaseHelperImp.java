package com.mayloom.vouchserv.dbo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mayloom.vouchserv.api.res.ActivateVoucherSerialRangeResult;
import com.mayloom.vouchserv.api.res.code.ActivateVoucherSerialRangeResultErrorCode;

/**
 * @author Nico
 */
public class BulkOpDatabaseHelperImp implements BulkOpDatabaseHelper {
	
private static Logger logger = LoggerFactory.getLogger(BulkOpDatabaseHelper.class);
	
	/********************************
	 * Boiler plate persistence setup
	 */
	
	@PersistenceContext(unitName = "vouchServ-pu")
	private EntityManager entityManager;

	@Transactional(readOnly=false, propagation=Propagation.REQUIRES_NEW)
	public ActivateVoucherSerialRangeResult bulkUpdateVoucherActivate(String vsvId, long startSerialNumb, long endSerialNumb) {
		
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
			
			Query queryVoucherIDsInSerialRange = getEntityManager().createNamedQuery(Voucher.GET_VOUCHER_IDS_IN_SERIAL_RANGE_CHOOSE_VSVID_QUERY);
			queryVoucherIDsInSerialRange.setParameter("vsvId", vsvId);
			queryVoucherIDsInSerialRange.setParameter("startSerialNumb", startSerialNumb);
			queryVoucherIDsInSerialRange.setParameter("endSerialNumb", endSerialNumb);
			List<Long> voucherIDs = (List<Long>)queryVoucherIDsInSerialRange.getResultList();
			
			Query queryUpdateVouchers = getEntityManager().createNamedQuery(Voucher.BULK_UPDATE_VOUCHER_ACTIVE_STATUS_WITH_ID_LIST);
			queryUpdateVouchers.setParameter("voucherIDList", voucherIDs);
			queryUpdateVouchers.setParameter("active", true);
			
			int numberRowsUpdated = queryUpdateVouchers.executeUpdate();
			
			assert(numberRowsUpdated == safeLongToInt(expectedNumberOfInactiveVouchers));
			
			return new ActivateVoucherSerialRangeResult(startSerialNumb, endSerialNumb);
			
		} else {
			
			throw new RuntimeException("Unexpected condition");
			
		}
	}
	
	@Deprecated
	@Transactional(readOnly=false, propagation=Propagation.REQUIRES_NEW)
	public int bulkUpdateVoucherActiveStatus(long startSerialNumb, long endSerialNumb, boolean activeStatus) {
		
		Query queryGetInactiveVoucherCount = getEntityManager().createNamedQuery(Voucher.BULK_UPDATE_VOUCHER_ACTIVE_STATUS_QUERY);
		queryGetInactiveVoucherCount.setParameter("startSerialNumb", startSerialNumb);
		queryGetInactiveVoucherCount.setParameter("endSerialNumb", endSerialNumb);
		queryGetInactiveVoucherCount.setParameter("active", activeStatus);
		
		return queryGetInactiveVoucherCount.executeUpdate();
	}
	
	private int safeLongToInt(long l) {
	    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
	        throw new IllegalArgumentException
	            (l + " cannot be cast to int without changing its value.");
	    }
	    return (int) l;
	}

	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}
