package org.oddjob.devguide;

public class NaughtyJob implements Runnable {

	public void run() {
		throw new RuntimeException("I won't run. I won't!");
	}
}
