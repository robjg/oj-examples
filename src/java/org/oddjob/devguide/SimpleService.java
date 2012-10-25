package org.oddjob.devguide;

import java.util.concurrent.CountDownLatch;

public class SimpleService {

	private volatile boolean stop;

	private volatile Thread thread;
	
	public void start() throws InterruptedException {
		
		final CountDownLatch serviceStarted = new CountDownLatch(1);
		
		thread = new Thread(new Runnable() {
			public void run() {
				while (!stop) {
					System.out.println("I could be useful.");
					serviceStarted.countDown();
					synchronized (SimpleService.this) {
						try {
							SimpleService.this.wait();
						} catch (InterruptedException e) {
							break;
						}
					}
				}
				System.out.println("Service Stopping.");
			}});
		thread.start();
		
		serviceStarted.await();
	}

	public void stop() throws InterruptedException {
		synchronized(this) {
			stop = true;
			notifyAll();
		}
		if (thread != null) {
			thread.join();
		}
	}
}
