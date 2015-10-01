package com.mayloom.vouchserv.ws.hit;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Testharness {
	
	public long timeTasks(int nThreads, final Runnable task) throws InterruptedException {
		
		final CountDownLatch startGate = new CountDownLatch(1);
		final CountDownLatch endGate = new CountDownLatch(nThreads);
		
		for (int i = 0; i < nThreads; i++) {
			
			Thread t = new Thread() {
				
				public void run() {
					
					try {
						startGate.await();
						try {
							task.run();
						} finally {
							endGate.countDown();
						}
					} catch (InterruptedException ignored) { }
					
				}
			};
			t.start();
			
		}
		
		long start = System.nanoTime();
		startGate.countDown();
		endGate.await();
		long end = System.nanoTime();
		return end - start;
	}
	
	public long timeTasks(List<Runnable> tasks) throws InterruptedException {
		
		final CountDownLatch startGate = new CountDownLatch(1);
		final CountDownLatch endGate = new CountDownLatch(tasks.size());
		
		for (final Runnable task : tasks) { 
			
				Thread t = new Thread() {
				
				public void run() {
					
					try {
						startGate.await();
						try {
							task.run();
						} finally {
							endGate.countDown();
						}
					} catch (InterruptedException ignored) { }
					
				}
			};
			t.start();
			
		}
		
		long start = System.nanoTime();
		startGate.countDown();
		endGate.await();
		long end = System.nanoTime();
		return end - start;
		
	}

}

