package com.mayloom.vouchserv.api;

import java.util.List;

import com.mayloom.vouchserv.imp.VoucherGenSubtask;

/**
 * After VoucherBatches have been "registered", the generation is subdivided into tasks which are executed
 * by this server.
 * 
 * @see TaskExecutionWebServer
 * 
 * @author Nico
 */
public interface GenerationServer {
	
	/**
	 * Schedule a voucher generation task for processing. This method should NOT
	 * block the current thread, under any circumstances.
	 * 
	 * @param genTask The voucher generation task, cannot be null.
	 */
	public void processTask(VoucherGenSubtask genTask);
	
	public void processTasks(List<VoucherGenSubtask> genTask);
	
}
