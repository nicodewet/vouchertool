package com.mayloom.vouchserv.dbo;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType; 
import javax.persistence.JoinColumn;
import javax.persistence.Column;
import javax.persistence.NamedQuery;
import javax.persistence.NamedQueries;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import java.util.Date;
/**
 * @author Nico
 */
@Entity
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="\"VOUCHER\"")

@NamedQueries({
	@NamedQuery(
		name=Voucher.GET_VOUCHER_BY_USER_VBO_PIN_QUERY,
		query="SELECT voucher FROM Voucher voucher " + 
				"inner join voucher.voucherOwner as batch " + 
				"inner join batch.voucherBatchOwner as owner " + 
				"inner join owner.user as user " + 
				"where voucher.pin = :pin and user.username = :username and owner.code = :vsvId"
	),
	@NamedQuery(
			name=Voucher.GET_VOUCHER_BY_USER_VBO_SERIAL,
			query="SELECT voucher FROM Voucher voucher " + 
					"inner join voucher.voucherOwner as batch " + 
					"inner join batch.voucherBatchOwner as owner " + 
					"inner join owner.user as user " + 
					"where voucher.serialNumber = :serialNumber and user.username = :username and owner.code = :vsvId"
	),
	@NamedQuery(
			name=Voucher.GET_VOUCHER_BATCH_ID_BY_USERNAME_AND_VSV_ID_AND_PIN,
			query="SELECT batch.batchNumber FROM VoucherBatch batch " +   
					"inner join batch.vouchers as voucher " +
					"inner join voucher.pin as pin " + 
					"inner join batch.voucherBatchOwner as owner " + 
					"inner join owner.user as user " + 
					"where pin.pin = :pin and user.username = :username and owner.code = :vsvId"
	),
	@NamedQuery(
		name=Voucher.FIND_VOUCHER,
		query="SELECT voucher FROM Voucher voucher " +
				"WHERE voucher.pin = :pin " +
				"AND voucher.voucherOwner.voucherBatchOwner.code = :vsvId"
	),
	@NamedQuery(
		name=Voucher.GET_VOUCHER_COUNT_WITHIN_SERIAL_RANGE,
		query="SELECT COUNT(voucher) FROM Voucher voucher " + 
				"WHERE voucher.serialNumber >= :startSerialNumb AND voucher.serialNumber <= :endSerialNumb " +
				"AND voucher.voucherOwner.voucherBatchOwner.code = :vsvId"
	),
	@NamedQuery(
			name=Voucher.GET_ACTIVE_OR_INACTIVE_VOUCHER_COUNT_WITHIN_SERIAL_RANGE_QUERY,
			query="SELECT COUNT(voucher) FROM Voucher voucher " + 
					"WHERE voucher.serialNumber >= :startSerialNumb AND voucher.serialNumber <= :endSerialNumb " +
					" AND voucher.active = :active AND voucher.voucherOwner.voucherBatchOwner.code = :vsvId"
	),
	@NamedQuery(
			name=Voucher.GET_ACTIVE_OR_INACTIVE_VOUCHERS_WITHIN_SERIAL_RANGE_QUERY,
			query="SELECT voucher FROM Voucher voucher " + 
					"WHERE voucher.serialNumber >= :startSerialNumb AND voucher.serialNumber <= :endSerialNumb " +
					" AND voucher.active = :active AND voucher.voucherOwner.voucherBatchOwner.code = :vsvId"
	),
	@NamedQuery(
			name=Voucher.GET_VOUCHER_BY_VBO_AND_PIN_QUERY,
			query="select voucher from Voucher voucher " +
			"where voucher.voucherPK.pin = :pin and voucher.voucherPK.voucherBatchOwnerId = :voucherBatchOwnerId"), 
	@NamedQuery(
			name=Voucher.GET_VOUCHER_COUNT_BY_VBO_AND_PIN_QUERY,
			query="select count(voucher) from Voucher voucher " +
					"where voucher.voucherPK.pin = :pin and voucher.voucherPK.voucherBatchOwnerId = :voucherBatchOwnerId"), 
	@NamedQuery(
			name=Voucher.BULK_UPDATE_VOUCHER_ACTIVE_STATUS_QUERY,
			query="UPDATE Voucher voucher SET voucher.active = :active " +
					"WHERE voucher.serialNumber >= :startSerialNumb AND voucher.serialNumber <= :endSerialNumb"
	),
	/**
	 * The following query is not MySQL compatible, it will result in the following error message:
	 * 	"You can't specify target table 'Voucher' for update in FROM clause"
	 */
	@NamedQuery(
			name=Voucher.BULK_UPDATE_VOUCHER_ACTIVE_STATUS_CHOOSE_VSVID_QUERY,
			query="UPDATE Voucher voucher SET voucher.active = :active " +
					 "WHERE voucher.id IN " + 
					 	"(SELECT voucher.id FROM Voucher voucher " + 
					 	 "WHERE voucher.serialNumber >= :startSerialNumb AND voucher.serialNumber <= :endSerialNumb " +
					 	 "AND voucher.voucherOwner.voucherBatchOwner.code = :vsvId)"),
	/**
	 * Queries introduced to solve MySQL compatibility problem mentioned above. 
	 */
	@NamedQuery(
			name=Voucher.GET_VOUCHERS_IN_SERIAL_RANGE_CHOOSE_VSVID_QUERY,
			query="from Voucher voucher " +
				"inner join voucher.voucherOwner as batch " +
				"inner join batch.voucherBatchOwner as owner " +  
				"where voucher.serialNumber >= :startSerialNumb and voucher.serialNumber <= :endSerialNumb and owner.code = :vsvId"), 
	@NamedQuery(
			name=Voucher.GET_VOUCHER_IDS_IN_SERIAL_RANGE_CHOOSE_VSVID_QUERY,
			query="select voucher.id from Voucher voucher " +
				"inner join voucher.voucherOwner as batch " +
				"inner join batch.voucherBatchOwner as owner " +  
				"where voucher.serialNumber >= :startSerialNumb and voucher.serialNumber <= :endSerialNumb and owner.code = :vsvId"), 
	@NamedQuery(
			name=Voucher.BULK_UPDATE_VOUCHER_ACTIVE_STATUS_WITH_ID_LIST,
			query="UPDATE Voucher voucher SET voucher.active = :active WHERE voucher.id IN (:voucherIDList)"),
}) 
public class Voucher {
	
	public static final String GET_VOUCHER_BY_USER_VBO_PIN_QUERY = "Voucher.GetVoucherByUsernameAndVsvIDAndPIN";
	public static final String GET_VOUCHER_BY_USER_VBO_SERIAL = "Voucher.GetVoucherByUsernameAndVsvIDAndSerialNumber";
	public static final String GET_VOUCHER_COUNT_BY_VBO_AND_PIN_QUERY = "Voucher.GetCountByVBOAndPin"; 
	public static final String GET_VOUCHER_BATCH_ID_BY_USERNAME_AND_VSV_ID_AND_PIN = "Voucher.GetVoucherBatchIdByUsernameAndVsvIDAndPIN";
	public static final String FIND_VOUCHER = "Voucher.FindVoucher";
	public static final String GET_VOUCHER_COUNT_WITHIN_SERIAL_RANGE = "Voucher.GetVoucherCountWithinSerialRange";
	public static final String GET_ACTIVE_OR_INACTIVE_VOUCHER_COUNT_WITHIN_SERIAL_RANGE_QUERY = "Voucher.GetActiveOrInactiveVoucherCountWithinSerialRange";
	public static final String GET_ACTIVE_OR_INACTIVE_VOUCHERS_WITHIN_SERIAL_RANGE_QUERY = "Voucher.GetActiveOrInactiveVouchersWithinSerialRange";
	public static final String GET_VOUCHER_BY_VBO_AND_PIN_QUERY = "Voucher.GetVoucherByVBOAndPin";
	public static final String BULK_UPDATE_VOUCHER_ACTIVE_STATUS_QUERY = "Voucher.BulkUpdateVoucherActiveStatus";
	public static final String BULK_UPDATE_VOUCHER_ACTIVE_STATUS_CHOOSE_VSVID_QUERY = "Voucher.BulkUpdateVoucherActiveStatusChooseVSVID";
	public static final String GET_VOUCHERS_IN_SERIAL_RANGE_CHOOSE_VSVID_QUERY = "Voucher.GetVouchersInSerialRangeChooseVSVID";
	public static final String GET_VOUCHER_IDS_IN_SERIAL_RANGE_CHOOSE_VSVID_QUERY = "Voucher.GetVoucherIDsInSerialRangeChooseVSVID";
	public static final String BULK_UPDATE_VOUCHER_ACTIVE_STATUS_WITH_ID_LIST = "Voucher.BulkUpdateVoucherActiveStatusSpecifyIDList";
	
	// combo of pin and foreign key to VBO must be unique
	// http://dwuysan.wordpress.com/2012/02/22/joincolumn-is-part-of-the-composite-primary-keys/
	// http://stackoverflow.com/questions/11215494/composite-primary-key-foreign-key
	@EmbeddedId
	@AttributeOverrides({
        @AttributeOverride(name = "voucherBatchOwnerId", column = @Column(name = "VBO_ID")),
        @AttributeOverride(name = "pin", column = @Column(name = "PIN"))
    })
    private VoucherPK voucherPK;
	
	// Many Vouchers associated with One VoucherBatchOwner
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VBO_ID", insertable = false, updatable = false)
    private VoucherBatchOwner voucherBatchOwner;

	@Column(name="PIN", unique=false, nullable=false, insertable = false, updatable = false)
	private String pin;
	
	// Many Vouchers associated with One VoucherBatch
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="VOUCHER_BATCH_ID")
	private VoucherBatch voucherOwner;
	
	/**
	 * Not Id since serialNumber will not be unique.
	 */
	@Column(name="SERIAL_NUMBER", nullable=false)
	long serialNumber;
	
	/**
	 * Optional (hence we have nullable=true). Acts as parent voucherBatch override.
	 */
	@Column(name="EXPIRY_DATE", nullable=true)
	Date expiryDate; 
	
	/**
	 * Acts as parent active override, true by default.
	 */
	@Column(name="ACTIVE", nullable=true)
	boolean active;
	
	/**
	 * Whether a voucher is active of not is inferred
	 * on the basis of whether redemptionDate is null 
	 * or not. 
	 */
	@Column(name="REDEMPTION_DATE", nullable=true)
	Date redemptionDate;
	
	public Voucher() {
	}
	
	public Voucher(String pin, long serialNumber) {
		this.pin = pin;
		this.serialNumber = serialNumber;
	}
	
	public String toString() {
		
			return "TODO"; // TODO  
			// "voucher: $pin, serial number: $serialNumber, expiry date: $expiryDate, redemption date:, $redemptionDate" 
		}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public VoucherBatch getVoucherOwner() {
		return voucherOwner;
	}

	public void setVoucherOwner(VoucherBatch voucherOwner) {
		this.voucherOwner = voucherOwner;
	}

	public long getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(long serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getRedemptionDate() {
		return redemptionDate;
	}

	public void setRedemptionDate(Date redemptionDate) {
		this.redemptionDate = redemptionDate;
	}

	public VoucherBatchOwner getVoucherBatchOwner() {
		return voucherBatchOwner;
	}

	public void setVoucherBatchOwner(VoucherBatchOwner voucherBatchOwner) {
		this.voucherBatchOwner = voucherBatchOwner;
	}

	public VoucherPK getVoucherPK() {
		return voucherPK;
	}

	public void setVoucherPK(VoucherPK voucherPK) {
		this.voucherPK = voucherPK;
	}
}
