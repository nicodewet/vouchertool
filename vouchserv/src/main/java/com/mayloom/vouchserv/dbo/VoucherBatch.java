package com.mayloom.vouchserv.dbo;

import javax.persistence.Entity;
import javax.persistence.CascadeType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * @author Nico
 */
@Entity
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="\"VOUCHER_BATCH\"")
@NamedQueries({
@NamedQuery(
		name=VoucherBatch.GET_BATCH_GEN_STATUS_QUERY,
		query="SELECT batch.batchNumber, batch.requestedSize, batch.generatedSize FROM VoucherBatch batch " +
				"inner join batch.voucherBatchOwner as owner " + 
				"inner join owner.user as user " + 
				"where user.username = :username and owner.code = :vsvId and batch.batchNumber = :batchNumber"
),	
@NamedQuery(
		name=VoucherBatch.GET_BATCH_WITHOUT_VOUCHERS_QUERY,
		query="SELECT batch FROM VoucherBatch batch " +
			  "WHERE batch.voucherBatchOwner.code = :vsvId AND batch.batchNumber = :batchNumber"
),
@NamedQuery(
		name=VoucherBatch.GET_MOST_RECENTLY_CREATED_BATCH_QUERY,
		query="SELECT vb FROM VoucherBatch vb " +
			  "WHERE vb.creationDate = (SELECT MAX(vb.creationDate) FROM VoucherBatch vb WHERE vb.voucherBatchOwner.code = :vsvId) " +
			  "AND vb.voucherBatchOwner.code = :vsvId"
),
@NamedQuery(
		name=VoucherBatch.GET_ALL_BATCH_PINS_QUERY,
		query="SELECT pin FROM Voucher voucher " + 
				"INNER JOIN batch.vouchers as voucher " + 
				"WHERE batch.id = :id"
),
@NamedQuery(
		name=VoucherBatch.GET_INCOMPLETE_BATCHES,
		query="SELECT vb FROM VoucherBatch vb " +
				"WHERE vb.generatedSize < vb.requestedSize"
),
@NamedQuery(
		name=VoucherBatch.GET_MAX_BATCH_VOUCHER_SERIAL_NO,
		query="SELECT MAX(voucher.serialNumber) FROM Voucher voucher " +
				"INNER JOIN voucher.voucherOwner as batch " +
				"WHERE batch.id = :id"
)

})
public class VoucherBatch {
	
	public static final String GET_BATCH_GEN_STATUS_QUERY = "VoucherBatch.GetVoucherBatchGenStatus";
	public static final String GET_MOST_RECENTLY_CREATED_BATCH_QUERY = "VoucherBatch.GetMostRecentlyCreatedVoucherBatchPerOwner";
	public static final String GET_ALL_BATCH_PINS_QUERY = "VoucherBatch.GetAllAssociatedPins";
	public static final String GET_BATCH_WITHOUT_VOUCHERS_QUERY = "VoucherBatch.GetWithAllVouchers";
	public static final String GET_INCOMPLETE_BATCHES = "VoucherBatch.GetIncompleteBatches";
	public static final String GET_MAX_BATCH_VOUCHER_SERIAL_NO = "VoucherBatch.GetVoucherBatchMaxSerialNo";
	
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private long id;
	
	// One VoucherBatch associated with Many Vouchers
	@OneToMany(mappedBy="voucherOwner", cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
	@OrderBy("serialNumber ASC")
	private List<Voucher> vouchers = new ArrayList<Voucher>();
	
	// Many VoucherBatches associated with One VoucherBatchOwner
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="VBO_ID", nullable=false)
	private VoucherBatchOwner voucherBatchOwner;
	
	/**
	 * This field must be sequential per parent VoucherBatchOwner. The
	 * question is how to enforce this constraint.
	 */
	@Column(name="BATCH_NUMBER", nullable=false)
	private int batchNumber;
	
	/******************************************************************************************
	 * == PROCESS ENGINE START ==
	 * 
	 * REQ_SIZE, GEN_SIZE, REQ_BATCH_TYPE, REQ_PIN_LENGTH, REQ_PIN_TYPE are task oriented attributes 
	 * and essential for managing asyn voucher generation tasks in particular. Building them into 
	 * VoucherBatch as we don't want to have a third party process engine at this stage.
	 */
	@Column(name = "REQ_SIZE", nullable = false)
	private int requestedSize;
	
	@Column(name = "GEN_SIZE", nullable = false)
	private int generatedSize = 0;
	
	@Column(name = "REQ_BATCH_TYPE", nullable = false)
	private String requestedBatchType; 
	
	@Column(name = "REQ_PIN_LENGTH", nullable = false)
	private int requestedPinLength;
	
	@Column(name = "REQ_PIN_TYPE", nullable = false)
	private String requestedPinType;
	
	@Column(name = "REQ_START_SERIAL", nullable = false)
	private long requestedBatchStartSerialNo;
	
	/*
	 * == PROCESS ENGINE END == 
	 *****************************************************************************************/
	
	/**
	 * @return the requestedBatchStartSerialNo
	 */
	public long getRequestedBatchStartSerialNo() {
		return requestedBatchStartSerialNo;
	}

	/**
	 * @param requestedBatchStartSerialNo the requestedBatchStartSerialNo to set
	 */
	public void setRequestedBatchStartSerialNo(long requestedBatchStartSerialNo) {
		this.requestedBatchStartSerialNo = requestedBatchStartSerialNo;
	}


	@Column(name="EXPIRY_DATE", nullable=true)
	Date expiryDate;
	
	@Column(name="ACTIVE", nullable=false)
	boolean active;
	
	@Column(name = "CREATION_DATE", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	Date creationDate;
	
	public VoucherBatch() {
	}
	
	public List<Voucher> getVouchers() {
		return vouchers;
	}

	public void setVouchers(List<Voucher> vouchers) {
		this.vouchers = vouchers;
	}

	public VoucherBatchOwner getVoucherBatchOwner() {
		return voucherBatchOwner;
	}

	public void setVoucherBatchOwner(VoucherBatchOwner voucherBatchOwner) {
		this.voucherBatchOwner = voucherBatchOwner;
	}

	public int getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(int batchNumber) {
		this.batchNumber = batchNumber;
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

	public long getId() {
		return id;
	}

	@PrePersist
	protected void onCreate() {
		creationDate = new Date();
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	/**
	 * @return the requestedSize
	 */
	public int getRequestedSize() {
		return requestedSize;
	}

	/**
	 * @param requestedSize the requestedSize to set
	 */
	public void setRequestedSize(int requestedSize) {
		this.requestedSize = requestedSize;
	}

	/**
	 * @return the generatedSize
	 */
	public int getGeneratedSize() {
		return generatedSize;
	}

	/**
	 * @param generatedSize the generatedSize to set
	 */
	public void setGeneratedSize(int generatedSize) {
		this.generatedSize = generatedSize;
	}
	
	/**
	 * @return the requestedBatchType
	 */
	public String getRequestedBatchType() {
		return requestedBatchType;
	}

	/**
	 * @param requestedBatchType the requestedBatchType to set
	 */
	public void setRequestedBatchType(String requestedBatchType) {
		this.requestedBatchType = requestedBatchType;
	}

	/**
	 * @return the requestedPinLength
	 */
	public int getRequestedPinLength() {
		return requestedPinLength;
	}

	/**
	 * @param requestedPinLength the requestedPinLength to set
	 */
	public void setRequestedPinLength(int requestedPinLength) {
		this.requestedPinLength = requestedPinLength;
	}

	/**
	 * @return the requestedPinType
	 */
	public String getRequestedPinType() {
		return requestedPinType;
	}

	/**
	 * @param requestedPinType the requestedPinType to set
	 */
	public void setRequestedPinType(String requestedPinType) {
		this.requestedPinType = requestedPinType;
	}

	
	public void addVoucher(Voucher voucher) {
		this.getVouchers().add(voucher);
		voucher.setVoucherOwner(this);
	}
}
