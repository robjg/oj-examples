package org.oddjob.devguide;

public class HelloPeopleJob implements Runnable {

	private String[] who;
	
	public void setWho(String[] who) {
		this.who = who; 
	}
	
	public String[] getWho() {
		return who;
	}
		
	public void run() {
		for (int i = 0; i < who.length; ++i) {
			System.out.println("Hello " + who[i] + "!");			
		}
	}
	
	public String toString() {
		return "Hello People";
	}
}
