package com.mayloom.vouchserv.ws.hit;

import java.util.Iterator;

import org.joda.time.Duration;
import org.joda.time.LocalDateTime;

import com.google.common.base.Optional;
import com.mayloom.vouchserv.ws.ds.Snake;
import com.mayloom.vouchserv.ws.ds.SnakeImpl;

public final class Counter {
	
	private int count;
	
	private Snake<LocalDateTime> snake; 
	
	public Counter() {
		count = 0;
		snake = new SnakeImpl<LocalDateTime>(); 
	}
	
	public int getCount() {
		return count;
	}
	
	public int incrementCount() {
		
		count++;
		
		snake.eat(new LocalDateTime());
		
		return calculateCurrentHitsPerSecond();
	}
	
	private int calculateCurrentHitsPerSecond() {
		if (snake.size() == 0) {
			return 0;
		}
		LocalDateTime end = new LocalDateTime();
		Iterator<LocalDateTime> iter = snake.iterator();
		int hits = 0;
		while (iter.hasNext()) {
			LocalDateTime start = iter.next();
			Duration duration = new Duration(start.toDateTime(), end.toDateTime());
			if (duration.getMillis() >= 1000L) {
				Optional dropped = snake.excrete();
			} else {
				hits++;
			}
		}
		return hits;
	}

}
