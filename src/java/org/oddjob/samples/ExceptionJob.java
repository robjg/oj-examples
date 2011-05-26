package org.oddjob.samples;

import org.oddjob.framework.SimpleJob;

public class ExceptionJob extends SimpleJob {

	public int execute() throws Exception {
		
		synchronized (this) {
			this.wait(3000);
		}

		throw new Exception("This is a deliberately naughty job.");
	}
}
