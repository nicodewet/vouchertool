package com.mayloom.vouchserv.gen;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.apache.commons.lang.StringUtils;

import com.mayloom.vouchserv.api.VoucherPinAmount;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"../spring/main-test-spring-context.xml"})
public class RandomStringGeneratorTest {
	
	private final Logger logger = LoggerFactory.getLogger(RandomStringGeneratorTest.class);
	
	@Autowired
	@Qualifier("randomStringGenerator")
	private RandomStringGenerator randomStringGenerator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void generateRandomNumberTest() {
		final String DIGITS = "0123456789";
		String randomString = randomStringGenerator.generateRandomNumber(10);
		assertEquals(10, randomString.length());
		assertEquals(true, StringUtils.containsOnly(randomString, DIGITS));
		randomString = randomStringGenerator.generateRandomNumber(VoucherPinAmount.ONE_TRILLION.length());
		assertEquals(VoucherPinAmount.ONE_TRILLION.length(), randomString.length());
		assertEquals(true, StringUtils.containsOnly(randomString, DIGITS));
	}
	
	@Test 
	public void generateRandomNumberRangeTest() {
		String randomDigits = randomStringGenerator.generateRandomNumber(0);
		assertNull(randomDigits);
		String moreRandomDigits = randomStringGenerator.generateRandomNumber(31);
		assertNull(moreRandomDigits);
		String maxLength = randomStringGenerator.generateRandomNumber(30);
		logger.debug(maxLength);
		assertEquals(30, maxLength.length());
		String minLength = randomStringGenerator.generateRandomNumber(6);
		logger.debug(minLength);
		assertEquals(6, minLength.length());
	}
	
	@Test
	public void generateUpperCaseAlphaNumericRandomTest() {
		String randomStrUpperCase = randomStringGenerator.generateRandomAlphanumeric(30, true);
		assertEquals(30, randomStrUpperCase.length());
		logger.debug(randomStrUpperCase);
		// TODO add in check that all alpha's are actually upper case
	}
	
	@Test
	public void generateMixedUpperLowerCaseAlphaNumericRandomTest() {
		String randomStrMixedUpperLowerCase = randomStringGenerator.generateRandomAlphanumeric(30, false);
		assertEquals(30, randomStrMixedUpperLowerCase.length());
		logger.debug(randomStrMixedUpperLowerCase);
		// TODO add in check that all alpha's are actually upper case
	}

}
