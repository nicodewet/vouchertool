package com.mayloom.vouchserv.ws.hit;

public interface RequestHitCounter {

	public int registerHit(String hitName);
		
	public int getHitCount(String hitName);
	
	/**
	 * Convenience alias for {@link #registerHit(String)}
	 */
	public int registerUserHit(String userName, String requestName);
	
	/**
	 * Convenience alias for {@link #getHitCount(String)}
	 */
	public int getHitCount(String userName, String requestName);
}
