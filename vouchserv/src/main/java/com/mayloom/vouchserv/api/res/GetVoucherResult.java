package com.mayloom.vouchserv.api.res;

import com.mayloom.vouchserv.api.res.code.GeneralVoucherServErrorCode;
import com.mayloom.vouchserv.api.res.code.GetVoucherErrorCode;
import com.mayloom.vouchserv.man.api.res.code.VoucherServError;
import com.mayloom.vouchserv.dto.ContentEnricherVoucher;

public final class GetVoucherResult {
	
	private final ContentEnricherVoucher voucher;
	private final VoucherServError errorCode;
	
	private GetVoucherResult(ContentEnricherVoucher voucher) {
		this.voucher = voucher;
		this.errorCode = null;
	}
	
	private GetVoucherResult(ContentEnricherVoucher voucher, GetVoucherErrorCode errorCode) {
		if ( (voucher != null && errorCode != null) || (voucher == null && errorCode == null) ) {
			throw new IllegalArgumentException("Both voucher and erroCode cannot be absent or present simultaneously");
		} 
		this.voucher = voucher;
		this.errorCode = errorCode;
	}
	
	private GetVoucherResult(ContentEnricherVoucher voucher, GeneralVoucherServErrorCode errorCode) {
		if ( (voucher != null && errorCode != null) || (voucher == null && errorCode == null) ) {
			throw new IllegalArgumentException("Both voucher and erroCode cannot be absent or present simultaneously");
		} 
		this.voucher = voucher;
		this.errorCode = errorCode;
	}
	
	public static GetVoucherResult createNoErrorInstance(ContentEnricherVoucher voucher) {
		return new GetVoucherResult(voucher);
	}
	
	public static GetVoucherResult createGetVoucherErrorInstance(GetVoucherErrorCode errorCode) {
		return new GetVoucherResult(null, errorCode);
	}
	
	public static GetVoucherResult createGeneralErrorInstance(GeneralVoucherServErrorCode errorCode) {
		return new GetVoucherResult(null, errorCode);
	}

	/**
	 * @return the errorCode or null if there was no error in which case a voucher should be present
	 */
	public VoucherServError getErrorCode() {
		return errorCode;
	}

	/**
	 * @return the voucher or null if there was no voucher, in which case an error code should be present
	 */
	public ContentEnricherVoucher getVoucher() {
		return voucher;
	}
	
}
