package com.mayloom.vouchserv.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mayloom.vouchserv.api.GenerationServer;
import com.mayloom.vouchserv.dbo.DatabaseHelper;

/**
 * TODO must document this
 * TODO must make # threads configurable
 * TODO must have some kind of monitoring to overload (requests waiting in unbounded queue for "too long")
 * 
 * http://stackoverflow.com/a/11655199/433900
 * http://stackoverflow.com/questions/7192223/ensuring-task-execution-order-in-threadpool?rq=1
 * 
 * @author Nico
 */
final class GenerationServerImp implements GenerationServer {
	
	private final Logger logger = LoggerFactory.getLogger(GenerationServerImp.class);
	
	private DatabaseHelper databaseHelper;
	
	/**
	 * Creates a thread pool that reuses a fixed number of threads operating off a shared unbounded queue. 
	 * At any point, at most nThreads threads will be active processing tasks. If additional tasks are 
	 * submitted when all threads are active, they will wait in the queue until a thread is available. 
	 * If any thread terminates due to a failure during execution prior to shutdown, a new one will take 
	 * its place if needed to execute subsequent tasks. The threads in the pool will exist until it is 
	 * explicitly shutdown.
	 */
	private final Executor concurrentTaskExecutor;
		
	private List<Executor> sequentialTaskExecutorList;
	
	private int roundRobinSequentialTaskExecutorIndex = 0;
	
	public GenerationServerImp(int numberOfConcurrentTaskExecutors, int numberOfSequentialExecutors) {
		
		concurrentTaskExecutor = Executors.newFixedThreadPool(5);
		
		sequentialTaskExecutorList = new ArrayList<Executor>(5);
		
		for (int i=0; i < numberOfSequentialExecutors; i++) {
			sequentialTaskExecutorList.add(Executors.newSingleThreadExecutor());
		}
		
	}
	
	/**
	 * Get the next Executor in a round-robin fashion.
	 * 
	 * @return The next Executor which we expect will be single threaded to ensure sequential task execution.
	 */
	private synchronized Executor getNextSequentialTaskExecutor() {
		int currentIndex = roundRobinSequentialTaskExecutorIndex;
		if (roundRobinSequentialTaskExecutorIndex == (sequentialTaskExecutorList.size() - 1)) {
			roundRobinSequentialTaskExecutorIndex = 0;
		} else {
			roundRobinSequentialTaskExecutorIndex++;
		}
		logger.info("Fetching sequential task executor #{}", currentIndex);
		logger.info("Will next fetch sequential task executor #{}", roundRobinSequentialTaskExecutorIndex);
		return sequentialTaskExecutorList.get(currentIndex);
	}

	@Override
	public void processTask(final VoucherGenSubtask genTask) {
		
		Runnable task = new Runnable() {
			public void run() {
				handleTask(genTask);
			}
		};
		concurrentTaskExecutor.execute(task);
	}
	
	@Override
	public void processTasks(final List<VoucherGenSubtask> genTasks) {
		
		Runnable task = new Runnable() {
			public void run() {
				handleTasks(genTasks);
			}
		};
		getNextSequentialTaskExecutor().execute(task);	
	}
	
	private void handleTask(VoucherGenSubtask genTask) {
		logger.info("Task execution request being handled by thread {}", Thread.currentThread().getId());
		getDatabaseHelper().generateAdditionalVoucherBatchVouchers(genTask);
	}
	
	private void handleTasks(List<VoucherGenSubtask> genTaskList) {
		logger.info("Task execution request being handled by thread {}", Thread.currentThread().getId());
		for (VoucherGenSubtask genTask: genTaskList) {
			getDatabaseHelper().generateAdditionalVoucherBatchVouchers(genTask);
		}
	}
		
	public DatabaseHelper getDatabaseHelper() {
		return databaseHelper;
	}

	public void setDatabaseHelper(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

}
