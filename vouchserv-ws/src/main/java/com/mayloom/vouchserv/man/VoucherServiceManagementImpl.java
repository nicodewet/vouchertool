package com.mayloom.vouchserv.man;

import java.util.List;

import com.google.common.base.Optional;
import com.mayloom.vouchserv.api.VoucherService;
import com.mayloom.vouchserv.man.api.req.BatchGenRequest;
import com.mayloom.vouchserv.man.api.res.BatchGenResult;
import com.mayloom.vouchserv.man.api.res.RegisterResult;
import com.mayloom.vouchserv.dbo.DatabaseHelper;
import com.mayloom.vouchserv.man.api.dto.VoucherBatch;
import com.mayloom.vouchserv.man.api.dto.VoucherBatchHeader;
import com.mayloom.vouchserv.man.api.dto.VoucherBatchOwner;
import com.mayloom.vouchserv.man.api.VoucherServiceManagement;

public class VoucherServiceManagementImpl implements VoucherServiceManagement {
	
	private DatabaseHelper databaseHelper;
	private VoucherService voucherService;
	
	public VoucherServiceManagementImpl(DatabaseHelper databaseHelper, VoucherService voucherService) {
		this.databaseHelper = databaseHelper;
		this.voucherService = voucherService;
	}
	
	@Override
	public boolean doesUserExist(String username) {
		return databaseHelper.doesUserExist(username);
	}
	
	@Override
	public boolean doesUserExist(String username, String password) {
		return databaseHelper.doesUserExist(username, password);
	}

	@Override
	public void addNewOrdinaryUser(String username, String password) {
		databaseHelper.addNewOrdinaryUser(username, password);
	}

	@Override
	public Optional<List<VoucherBatchHeader>> getVoucherBatches(String vsvId) {
		return voucherService.getVoucherBatches(vsvId);
	}

	@Override
	public Optional<List<VoucherBatchOwner>> getRegistrations(String username) {
		return voucherService.getRegistrations(username);
	}

	@Override
	public RegisterResult register(String username) {
		return voucherService.register(username);
	}

	@Override
	public BatchGenResult generateVoucherBatch(String username, BatchGenRequest batchGenRequest) {
		return voucherService.generateVoucherBatch(username, batchGenRequest);
	}

	@Override
	public VoucherBatch getVoucherBatch(String username, String vsvId, int batchNumber) {
		return voucherService.getVoucherBatch(username, vsvId, batchNumber);
	}

}
