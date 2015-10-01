package com.mayloom.vouchserv.ws.hit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import junit.framework.TestCase;

public class RequestHitCounterTest extends TestCase {
	
	private final Logger logger = LoggerFactory.getLogger(RequestHitCounterTest.class);
	
	protected RequestHitCounter hitCounter;
	
	private static final String REQUEST_NAME = "RequestFoo";
	private static final String ANOTHER_REQUEST_NAME = "RequestBar";
	
	protected void setUp() {
		hitCounter = new RequestHitCounterImpl();		
	}
	
	public void testGetHitCount_Positive() throws InterruptedException {
		
		logger.info("{}: {}", REQUEST_NAME, hitCounter.registerHit(REQUEST_NAME));
		Thread.sleep(1001);
		logger.info("{}: {}", REQUEST_NAME, hitCounter.registerHit(REQUEST_NAME));
		logger.info("{}: {}", ANOTHER_REQUEST_NAME, hitCounter.registerHit(ANOTHER_REQUEST_NAME));
		logger.info("{}: {}", REQUEST_NAME, hitCounter.registerHit(REQUEST_NAME));
		logger.info("{}: {}", REQUEST_NAME, hitCounter.registerHit(REQUEST_NAME));
		assertEquals(4, hitCounter.getHitCount(REQUEST_NAME));
		assertEquals(1, hitCounter.getHitCount(ANOTHER_REQUEST_NAME));
	}
	
	public void testGetHitCount_Negative() {
		assertEquals(0, hitCounter.getHitCount(REQUEST_NAME));
	}
	
	public void testConcurrentSameHits() throws InterruptedException {
		 
		Testharness testharness = new Testharness();
		long elapsedTime = testharness.timeTasks(100, new Hit(hitCounter, REQUEST_NAME));
		System.out.println(elapsedTime);
		double seconds = (double)elapsedTime / 1000000000.0;
		System.out.println(seconds);
		assertEquals(100, hitCounter.getHitCount(REQUEST_NAME));
	}
	
	public void testConcurrentSameDiffHits() throws InterruptedException {
		 
		Testharness testharness = new Testharness();
		
		List<String> requestNames = new ArrayList<String>();
		requestNames.add(REQUEST_NAME);
		requestNames.add(ANOTHER_REQUEST_NAME);
		List<String> randomRequestSequence = generateRandomRequestSequence(100, requestNames);
		assertEquals(100, randomRequestSequence.size());
		List<Runnable> hitList = new ArrayList<Runnable>(100);
		for (String requestName : randomRequestSequence) {
			hitList.add(new Hit(hitCounter,requestName));
		}
		assertEquals(100, hitList.size());
		long elapsedTime = testharness.timeTasks(hitList);
		System.out.println(elapsedTime);
		double seconds = (double)elapsedTime / 1000000000.0;
		System.out.println(seconds);
	}
	
	private List<String> generateRandomRequestSequence(int numberOfRequests, List<String> requestNames) {
		Random rand = new Random();
		List<String> requestSequence = new ArrayList<String>(100);
		for (int i = 0 ; i < numberOfRequests ; i++) {
			int choice  = rand.nextInt(requestNames.size());
			requestSequence.add(requestNames.get(choice));
			
		}
		return requestSequence;
	}

}

