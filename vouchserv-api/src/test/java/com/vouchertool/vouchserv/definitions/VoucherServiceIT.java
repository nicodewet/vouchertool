package com.vouchertool.vouchserv.definitions;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.vouchertool.vouchserv.header.HeaderHandlerResolver;
import com.vouchertool.vouchserv.schemas.RegisterRequest;
import com.vouchertool.vouchserv.schemas.RegisterResponse;
import com.vouchertool.vouchserv.schemas.VoucherBatchGenerationRequest;
import com.vouchertool.vouchserv.schemas.VoucherBatchGenerationResponse;
import com.vouchertool.vouchserv.schemas.VoucherBatchType;

public class VoucherServiceIT {

	private final String LIVE_WSDL = "https://www.vouchertool.com/vouchserv/vouchserv.wsdl";
	private final String LOCAL_WSDL = "http://localhost:8080/vouchserv/vouchserv.wsdl";
	
	//@Test
	public void test() {
		URL url = null;
		try {
			url = new URL(LIVE_WSDL);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		
		VoucherServiceService voucherServiceService = new VoucherServiceService(url);
		HeaderHandlerResolver handlerResolver = new HeaderHandlerResolver();
		voucherServiceService.setHandlerResolver(handlerResolver);
		VoucherService voucherService = voucherServiceService.getVoucherServiceSoap11();
		RegisterRequest registerRequest = new RegisterRequest();
		RegisterResponse registerResponse = voucherService.register(registerRequest);
		assertEquals("0", registerResponse.getResultStatusCode());
		assertNotNull(registerResponse.getVsvID());
		assertTrue(StringUtils.startsWith(registerResponse.getVsvID(), "VSV-ID"));
		
		VoucherBatchGenerationRequest voucherBatchGenerationRequest = new VoucherBatchGenerationRequest();
		voucherBatchGenerationRequest.setVsvID(registerResponse.getVsvID());
		voucherBatchGenerationRequest.setVoucherNumber(new BigInteger("100"));
		voucherBatchGenerationRequest.setBatchType(VoucherBatchType.REGULAR);
		
		Date before = new Date();
		System.out.println(before.toString());
		long startTime = System.nanoTime();
		for (int i = 0; i < 1; i++) {
			VoucherBatchGenerationResponse resp = voucherService.voucherBatchGeneration(voucherBatchGenerationRequest);
			assertEquals("0", resp.getResultStatusCode());
			System.out.println(i);
		}
		long endTime = System.nanoTime();
		long durationNanos = endTime - startTime;
		long durationSecs = durationNanos / 1000000000L;
		System.out.println(durationSecs); // 158  noatime, nodiratime 162, query cache vals 136, 160, 131, 133, 138
		Date after = new Date();
		System.out.println(after.toString());	
	}
}
