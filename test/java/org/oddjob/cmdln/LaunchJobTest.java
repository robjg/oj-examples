package org.oddjob.cmdln;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;
import org.oddjob.Oddjob;
import org.oddjob.OjTestCase;
import org.oddjob.state.ParentState;
import org.oddjob.tools.ConsoleCapture;
import org.oddjob.tools.OurDirs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaunchJobTest extends OjTestCase {

	private static final Logger logger = LoggerFactory.getLogger(LaunchJobTest.class);
	
    @Test
    public void testLaunchAsjobInOddjob() throws IOException, URISyntaxException {
    	
    	ClassLoader existingContext = Thread.currentThread(
    			).getContextClassLoader();
    	if (existingContext == null) {
    		logger.warn("ContextClassLoader is null");
    	}
    	
    	try {
    		// Why do I set this to null?
    		Thread.currentThread().setContextClassLoader(null);
    	
			File oddjobDir = new OurDirs().relative("../oddjob");
			
			File config = new File(oddjobDir, 
					"src/test/resources/org/oddjob/jobs/LaunchExample.xml");
			
			Oddjob oddjob = new Oddjob();
			oddjob.setFile(config);
			oddjob.setArgs(new String[] { 
					oddjobDir.toString(),
					"org.oddjob.Main",
					OjTestCase.logConfig()} );
					
			ConsoleCapture console = new ConsoleCapture();
			try (ConsoleCapture.Close close = console.captureConsole()) {
			
				oddjob.run();
			}
			
			assertEquals(ParentState.COMPLETE, oddjob.lastStateEvent().getState());
			
			console.dump(logger);
			
			String[] lines = console.getLines();
			assertEquals(1, lines.length);
			assertTrue(lines[0].startsWith("URLClassLoader:"));
			
			oddjob.destroy();
		
    	}
    	finally {
        	Thread.currentThread().setContextClassLoader(existingContext);
    	}
    }
	
}
