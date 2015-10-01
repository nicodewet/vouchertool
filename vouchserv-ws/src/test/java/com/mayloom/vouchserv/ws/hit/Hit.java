package com.mayloom.vouchserv.ws.hit;

public class Hit implements Runnable {
	
	private RequestHitCounter hitCounter; 
	private String requestName;
	
	Hit(RequestHitCounter hitCounter, String requestName) {
		this.hitCounter = hitCounter;
		this.requestName = requestName;
	}

	public void run() {			
		int hitsPerSec = hitCounter.registerHit(requestName);
		// System.out.println(hitsPerSec);
	}
	
}
