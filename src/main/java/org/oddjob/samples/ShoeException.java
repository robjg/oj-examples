package org.oddjob.samples;

/**
 * An Exception for a Random Task.
 * 
 * @author rob
 *
 */
public class ShoeException extends Exception {
	private static final long serialVersionUID = 2010051300L;
	
	public ShoeException() {
		super("Who throws a shoe?");
	}
}
