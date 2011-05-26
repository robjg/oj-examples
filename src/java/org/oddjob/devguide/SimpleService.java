package org.oddjob.devguide;

public class SimpleService {

	private Thread t;

	public void start() {
		
		t = new Thread(new Runnable() {
			public void run() {
				while (true) {
					System.out.println("I could be useful.");
					synchronized (this) {
						try {
							wait(5000);
						} catch (InterruptedException e) {
							System.out.println("OK - I'll stop.");
							break;
						}
					}
				}
			}});
		t.start();
	}

	public void stop() {
		t.interrupt();
	}
}
