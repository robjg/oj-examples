package org.oddjob.devguide;

import org.junit.Test;

import java.io.File;

import org.junit.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.tools.ConsoleCapture;
import org.oddjob.tools.OurDirs;

public class ArooaExposedTest extends Assert {
	private static final Logger logger = LoggerFactory.getLogger(ArooaExposedTest.class);

	
    @Test
	public void testStandardAPExample() throws ArooaParseException {
		
		OurDirs dirs = new OurDirs();
		
		File config = dirs.relative("examples/devguide/exposed1.xml");
		
		ConsoleCapture console = new ConsoleCapture();
		try (ConsoleCapture.Close close = console.captureConsole()) {
			
			StandardAPExample.main(config.toString());		
		}
			
		console.dump(logger);
		
		String[] lines = console.getLines();
				
		assertEquals("Person Set.", lines[0].trim());
		
		assertEquals(1, lines.length);
	}	
}
