package com.mayloom.vouchserv.gen;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"../spring/main-test-spring-context.xml"})
public class IdentifierToolTest {

	@Autowired
	@Qualifier("identifierTool")
	private IdentifierTool identifierTool;
	
	@Test
	public void generateVSVIDTest() {
		String vsvId = identifierTool.generateVSVID();		
		assertNotNull(vsvId);
	}
	
	@Test
	public void validateVSVIDTest() {
		String vsvId = identifierTool.generateVSVID();		
		assertTrue(identifierTool.validateVSVID(vsvId));
		String invalidVsvId = "VSV-ID2010061212345678x";
		assertFalse(identifierTool.validateVSVID(invalidVsvId));
		String anotherInValidVsvId = "VSV-DI20100621123456789";
		assertFalse(identifierTool.validateVSVID(anotherInValidVsvId));
		String yetAnotherInvalidVsvId = "";
		assertFalse(identifierTool.validateVSVID(yetAnotherInvalidVsvId));
	}
}
