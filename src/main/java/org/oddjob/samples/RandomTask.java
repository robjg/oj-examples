package org.oddjob.samples;

import org.oddjob.Stoppable;
import org.oddjob.framework.extend.SimpleJob;


/**
 * Where as Oddjob was James Bond's villain, Austin Powers had
 * Random Task, who threw a shoe ("who throws a Shoe?").
 * 
 * @author rob
 *
 */
public class RandomTask extends SimpleJob 
implements Stoppable {

	private long delay;

	public long getDelay() {
	
		return delay;	
	}
	
	public void setDelay(long delay) {
		
		this.delay = delay;
	}
	

	public int execute() throws Exception {

		if (getDelay() == 0) {		
			setDelay(new Double(Math.random() * 10000).longValue());
		}
		synchronized (this) {
			this.wait(delay);
		}

		if (Math.random() > 0.9) {
			
			throw new Exception("Random Exception");
		}
		
		if (Math.random() > 0.5 ) {
		
			return 1;
		}
		else {
			
			return 0;
		}
	}
	
	public void onStop() {
		synchronized (this) {
			notifyAll();
		}
	}
}
