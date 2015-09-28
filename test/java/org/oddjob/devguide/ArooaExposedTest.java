package org.oddjob.devguide;

import java.io.File;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.tools.ConsoleCapture;
import org.oddjob.tools.OurDirs;

public class ArooaExposedTest extends TestCase {
	private static final Logger logger = Logger.getLogger(ArooaExposedTest.class);

	
	public void testStandardAPExample() throws ArooaParseException {
		
		OurDirs dirs = new OurDirs();
		
		File config = dirs.relative("examples/devguide/exposed1.xml");
		
		ConsoleCapture console = new ConsoleCapture();
		console.captureConsole();
			
		StandardAPExample.main(config.toString());		
		
		console.close();
		console.dump(logger);
		
		String[] lines = console.getLines();
				
		assertEquals("Person Set.", lines[0].trim());
		
		assertEquals(1, lines.length);
	}	
}
