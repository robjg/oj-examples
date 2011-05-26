package org.oddjob.dummy;

import java.io.File;

import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.framework.SimpleJob;


public class DummyExistsJob extends SimpleJob {

	@ArooaAttribute
	public void setFile(File file) { }
	
	public int execute() throws Exception {

		if (Math.random() > 0.9) {
			
			throw new Exception("Pretend File Exception");
		}
		
		if (Math.random() > 0.5 ) {
		
			return 1;
		}
		else {
			
			return 0;
		}
	}
}
