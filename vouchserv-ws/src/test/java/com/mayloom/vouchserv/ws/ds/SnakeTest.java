package com.mayloom.vouchserv.ws.ds;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnakeTest {
	
	private final Logger logger = LoggerFactory.getLogger(SnakeTest.class);
	
	private Snake<LocalDateTime> snake; 

	@Before
	public void setUp() throws Exception {
		snake = new SnakeImpl<LocalDateTime>(); 
	}

	@Test
	public void testIteratorIteratesFromTailToHeadOfSnake() throws InterruptedException {
		snake.eat(new LocalDateTime());
		Thread.sleep(5);
		snake.eat(new LocalDateTime());
		Thread.sleep(5);
		snake.eat(new LocalDateTime());
		Thread.sleep(5);
		Iterator<LocalDateTime> iter = snake.iterator();
		LocalDateTime previous = null;
		while (iter.hasNext()) {
			LocalDateTime current = iter.next();
			logger.info(current.toString());
			
			if (previous != null) {
				int compareResult = current.compareTo(previous);
				assertTrue(compareResult > 0);
			}
			// prep for next loop
			previous = current;
		}
	}

}
