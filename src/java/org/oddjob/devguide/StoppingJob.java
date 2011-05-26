package org.oddjob.devguide;

public class StoppingJob implements Runnable {

	public void run() {
		synchronized(this) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println("OK - I'll stop.");
			}
		}
	}
}
