package org.oddjob.devguide;

public class DifferentPropertiesJob implements Runnable {

	public void setSimple(Object something) {
		System.out.println("Set simple with: " + something);
	}
	
	public void setIndexed(int index, Object something) {
		System.out.println("Set indexed " + index + " with: " + something);
	}

	public void setMapped(String key, Object something) {
		System.out.println("Set mapped " + key + " with: " + something);
	}
	
	public void run() {
	}
}
