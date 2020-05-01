package org.oddjob.devguide;

public class HelloPersonJob implements Runnable {

	private Person person;
	private boolean formal;
	
	public void setPerson(Person person) {
		this.person = person; 
	}
	
	public Person getPerson() {
		return person;
	}
		
	public boolean isFormal() {
		return formal;
	}
	
	public void setFormal(boolean formal) {
		this.formal = formal;
	}
	
	public void run() {
		if (formal) {
			System.out.println("Hello " + 
					person.getTitle() + " " + 
					person.getSurname() + ".");
		}
		else {
			System.out.println("Hello " + 
					person.getFirstname() + "!");
		}
	}
	
	public String toString() {
		return (formal ? "Formal " : "Friendly ") + "Hello Person";
	}
}
