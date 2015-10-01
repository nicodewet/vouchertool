package com.mayloom.vouchserv.ws.hit;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.Queue;
import java.util.LinkedList;

import org.joda.time.Duration;
import org.joda.time.LocalDateTime;

import com.google.common.base.Optional;
import com.mayloom.vouchserv.ws.ds.Snake;
import com.mayloom.vouchserv.ws.ds.SnakeImpl;

public class RequestHitCounterImpl implements RequestHitCounter {
	
	// Not applicable: http://ria101.wordpress.com/2011/12/12/concurrenthashmap-avoid-a-common-misuse/
	private final ConcurrentHashMap<String, Counter> records = new ConcurrentHashMap<String, Counter>();
	
	// http://dmy999.com/article/34/correct-use-of-concurrenthashmap
	public int registerHit(String requestName) {
		
		Counter hitCounter = getOrCreate(requestName);
		synchronized(hitCounter) {
			return hitCounter.incrementCount();
		}
		
	}
	
	public int getHitCount(String requestName) {
		
		Counter counter = records.get(requestName);
		if (counter != null) {
			return counter.getCount();
		} else {
			return 0;
		}
	}
	
	public int registerUserHit(String userName, String requestName) {
		
		Assert.hasText(userName);
		Assert.hasText(requestName);
		
		String key = userName + requestName;
		
		return registerHit(key);
	}

	public int getHitCount(String userName, String requestName) {
		
		Assert.hasText(userName);
		Assert.hasText(requestName);
		
		String key = userName + requestName;
		
		return getHitCount(key);
		
	}
	
	private Counter getOrCreate(String requestName) {
	    Counter rec = records.get(requestName);
	    if (rec == null) {
	        // record does not yet exist
	        Counter newRec = new Counter();
	        rec = records.putIfAbsent(requestName, newRec);
	        if (rec == null) {
	            // put succeeded, use new value
	            rec = newRec;
	        }
	    }
	    return rec;
	}

}

