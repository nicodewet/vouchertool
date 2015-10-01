package com.mayloom.vouchserv.dbo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.OneToMany;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.List;
/**
 * A VoucherBatchOwner represents a unique PIN space, i.e. each PIN associated with a VoucherBatchOwner is 
 * unique, however, PINs are not unique when considering multiple VoucherBatchOwners.
 * 
 * The Vouchers associated with each VoucherBatchOwner (via VoucherBatches) each have a serial number which
 * is kept in sequence.
 * 
 * @author Nico
 */
@Entity
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(
        name="\"VOUCHER_BATCH_OWNER\"", 
        uniqueConstraints=
            @UniqueConstraint(columnNames={"CODE"})
      )
@NamedQueries({      
	@NamedQuery(
			name=VoucherBatchOwner.GET_VBO_CODES_QUERY, 
			query="SELECT vbo.code FROM VoucherBatchOwner vbo " +
					"WHERE vbo.user.username = :username"

	),
	@NamedQuery(
			name=VoucherBatchOwner.GET_USER_VBO_CODES_QUERY, 
			query="SELECT vbo FROM VoucherBatchOwner vbo " +
					"WHERE vbo.user.username = :username"

	),
	@NamedQuery(
			name=VoucherBatchOwner.GET_VBO_BY_CODE_QUERY,
			query="SELECT vbo FROM VoucherBatchOwner vbo WHERE vbo.code = :vsvId"	
	),
	@NamedQuery(
			name=VoucherBatchOwner.GET_NUMB_VBO_WITH_CODE,
			query="SELECT COUNT(vbo.code) FROM VoucherBatchOwner vbo WHERE vbo.code = :vsvId"
	),
	@NamedQuery(
			name=VoucherBatchOwner.GET_VBO_BATCHES,
			query="SELECT batch FROM VoucherBatch batch " +
			  "WHERE batch.voucherBatchOwner.code = :vsvId ORDER BY batch.creationDate DESC"
	)
})      
public class VoucherBatchOwner {
	
	public static final String GET_VBO_CODES_QUERY = "VoucherBatchOwner.GetUserVoucherBatchOwnerCodes";
	public static final String GET_USER_VBO_CODES_QUERY = "VoucherBatchOwner.GetFullUserVoucherBatchOwnerCodes";
	public static final String GET_VBO_BY_CODE_QUERY = "VoucherBatchOwner.GetVoucherBatchOwnerByCode";
	public static final String GET_NUMB_VBO_WITH_CODE = "VoucherBatchOwner.GetNumberOfVoucherBatchOwnersWithCode";
	public static final String GET_VBO_BATCHES = "VoucherBatchOwner.GetVoucherBatches";
	
	public static final int VBO_START_SERIAL_NO = 1;
	
	/**
	 * Must be unique. This is an identifier to serve as reference to identify the owner.
	 */
	@Id
	@Column(name="CODE", unique=true, nullable=false)
	private String code;
	
	// Many VoucherBatchOwners associated with One User
	@ManyToOne(fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name="USER_ID", nullable=false, updatable=false)
	private User user;
	
	// One VoucherBatchOwner associated with Many VoucherBatches
	@OneToMany(mappedBy="voucherBatchOwner", cascade=CascadeType.PERSIST)
	private List<VoucherBatch> voucherBatches = new ArrayList<VoucherBatch>();
	
	/**
	 * Vouchers generated, in batches, for the VoucherBatchOwner start
	 * at this value, which is incremented proportionally upon batch
	 * generation.
	 */
	@Column(name="NEXT_BATCH_START_SERIAL_NUMBER", nullable=false)
	long nextBatchStartSerialNumber = VBO_START_SERIAL_NO;
	
	
	@Column(name = "CREATION_DATE", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	Date creationDate;
	
	@Column(name = "LAST_UPDATE", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	Date lastUpdate;
	
	@OneToMany(mappedBy = "voucherBatchOwner", fetch = FetchType.LAZY)
	private Set<Voucher> vouchers = new HashSet<Voucher>();

	public VoucherBatchOwner() {
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
	
	@PreUpdate
	protected void onUpdate() {
		lastUpdate = new Date();
	}
	
	public Date getLastUpdate() {
		return lastUpdate;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public long getNextBatchStartSerialNumber() {
		return nextBatchStartSerialNumber;
	}

	public void setNextBatchStartSerialNumber(long nextBatchStartSerialNumber) {
		this.nextBatchStartSerialNumber = nextBatchStartSerialNumber;
	}
	
	/**
	 * Returns the string representation of this VoucherBatchOwner. It is intended to be a summary 
	 * and as such it does not contain all VoucherBatchOwner information, and the only ommission is
	 * the display of List<VoucherBatch> voucherBatches.   
	 * 
	 * The exact details of the summary representation are unspecified and subject to change, but 
	 * the following may be regarded as typical:
	 * 
	 * "[VoucherBatchOwner #9: code=VSV-ID20110420739451296, creationDate=2011-04-20, nextBatchStartSerialNumber=23]"
	 */
	@Override public String toString() {
		return String.format("[VoucherBatchOwner: code=%1$s, creationDate=%2$tY%2$tm%2$td, nextBatchStartSerialNumber=%3$s]",
				getCode(), getCreationDate(), getNextBatchStartSerialNumber());
		
	}

	public List<VoucherBatch> getVoucherBatches() {
		return voucherBatches;
	}

	public void addVoucher(Voucher voucher) {
		//this.uniqueVoucherPins.add(voucherPin);
		//voucherPin.getVoucherBatchOwners().add(this);
		this.vouchers.add(voucher);
	}
	
	public void addVoucherBatch(VoucherBatch voucherBatch) {
		this.voucherBatches.add(voucherBatch);
		voucherBatch.setVoucherBatchOwner(this);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * @return the vouchers
	 */
	public Set<Voucher> getVouchers() {
		return vouchers;
	}

	/**
	 * @param vouchers the vouchers to set
	 */
	public void setVouchers(Set<Voucher> vouchers) {
		this.vouchers = vouchers;
	}

}
