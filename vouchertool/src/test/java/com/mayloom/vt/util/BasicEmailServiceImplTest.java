package com.mayloom.vt.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BasicEmailServiceImplTest {
	
	static BasicEmailService emailService; 

	@Before
	public void setUp() throws Exception {
		emailService = new BasicEmailServiceImpl(true);
	}

	@Test
	public void testSendBasicEmail() {
		String to = "nico@nicodewet.com";
		String from = "nico@vouchertool.com";
		String subject = "Welcome to VoucherTool";
		String bodyPlain = "You have registered as a VoucherTool user. Just a reminder that your email address is your username.";
		String bodyHtml = "You have registered as a VoucherTool user. Just a reminder that your email address is your username.";
		BasicEmail basicEmail = new BasicEmail(from, subject, bodyPlain, bodyHtml);
		basicEmail.setTo(to);
		assertFalse(emailService.sendBasicEmail(basicEmail));
	}

}
