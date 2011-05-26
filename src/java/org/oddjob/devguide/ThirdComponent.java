package org.oddjob.devguide;

public class ThirdComponent {

	private Person person;
	
	public void setPerson(Person person) {
		this.person = person;
		System.out.println("Person Set.");
	}
	
	public Person getPerson() {
		return person;
	}			
}
